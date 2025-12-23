package de.ahead.register.config

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.bind.annotation.RestController

@SpringBootTest
@AutoConfigureMockMvc
class TestCorsIntegration {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var context: ApplicationContext

    private val log = KotlinLogging.logger {}

    private val methods = listOf(
        HttpMethod.GET,
        HttpMethod.POST,
        HttpMethod.PUT,
        HttpMethod.DELETE
    )

    private fun sanitizeEndpoints(endpoints: Set<String>): Set<String> =
        endpoints
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .filter { it.startsWith("/") }
            .filterNot { it.contains("{") } // exclude path variables
            .filterNot { it.contains("}") }
            .map { it.replace("//", "/") }
            .toSet()

    private fun discoverEndpoints(): Set<String> {
        val endpoints = mutableSetOf<String>()

        val controllers =
            context.getBeansWithAnnotation(RestController::class.java).values +
            context.getBeansWithAnnotation(Controller::class.java).values

        controllers.forEach { bean ->
            val clazz = bean::class.java

            val basePaths =
                clazz.getAnnotation(RequestMapping::class.java)?.value ?: arrayOf("")

            clazz.methods.forEach { method ->
                val mappings = listOfNotNull(
                    method.getAnnotation(GetMapping::class.java)?.value?.toList(),
                    method.getAnnotation(PostMapping::class.java)?.value?.toList(),
                    method.getAnnotation(PutMapping::class.java)?.value?.toList(),
                    method.getAnnotation(DeleteMapping::class.java)?.value?.toList(),
                    method.getAnnotation(RequestMapping::class.java)?.value?.toList()
                ).flatten()

                for (base in basePaths) {
                    for (path in mappings) {
                        endpoints.add((base + path).replace("//", "/"))
                    }
                }
            }
        }

        return endpoints
    }

    @Test
    fun testPreflightRequestsAllEndpoints() {
        val allowedOrigins = listOf(
            "http://localhost:5173",
            "http://localhost:8081"
        )

        val endpoints = sanitizeEndpoints(
            discoverEndpoints()
        )

        endpoints.forEach { endpoint ->
            allowedOrigins.forEach { origin ->
                methods.forEach { method ->
                    log.info { "CORS test: OPTIONS $endpoint from $origin for ${method.name()}" }

                    mockMvc.perform(
                        options(endpoint)
                            .header(HttpHeaders.ORIGIN, origin)
                            .header(
                                HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                                method.name()
                            )
                    ).andExpect(status().isOk
                    ).andExpect(
                        header().string(
                            HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                            origin
                        )
                    )
                }
            }
        }
    }

    @Test
    fun testPreflightRequestsDisallowedOrigin() {
        val endpoints = sanitizeEndpoints(
            discoverEndpoints()
        )

        endpoints.forEach { endpoint ->
            methods.forEach { method ->
                mockMvc.perform(
                    options(endpoint)
                        .header(HttpHeaders.ORIGIN, "http://evil.com")
                        .header(
                            HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                            method.name()
                        )
                ).andExpect(status().isForbidden)
            }
        }
    }

}