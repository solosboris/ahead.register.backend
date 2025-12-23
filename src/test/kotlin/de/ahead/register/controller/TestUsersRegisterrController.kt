package de.ahead.register.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.ahead.register.dto.*
import de.ahead.register.model.UserEntry
import de.ahead.register.service.IUserService
import java.util.UUID
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(UsersRegisterrController::class)
@AutoConfigureMockMvc(addFilters = false)
class UsersRegisterControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var userService: IUserService

    private val registerCode = 123456

    @Test
    fun testRegisterUser() {
        val user = User("", "John", "Doe", "john@test.com")

        `when`(userService.storeUser(user))
            .thenReturn(true)

        mockMvc.post("/users/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(user)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun testCodeVerification() {
        val request = EmailRequest("john@test.com")

        `when`(userService.storeEmail(request.email))
            .thenReturn(registerCode)

        mockMvc.post("/users/request") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            content { string(registerCode.toString()) }
        }
    }

    @Test
    fun testLogin() {
        val loginRequest = LoginRequest("john@test.com", registerCode)

        val userEntry = UserEntry(
            id = UUID.randomUUID().toString(),
            firstName = "John",
            lastName = "Doe"
        ).apply {
            code = registerCode
        }

        `when`(
            userService.login(loginRequest.email, loginRequest.code)
        ).thenReturn(userEntry)

        mockMvc.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.firstName") { value("John") }
            jsonPath("$.lastName") { value("Doe") }
        }
    }

    @Test
    fun testRegisteredMail() {
        val user = User("1", "John", "Doe", "john@test.com")

        `when`(userService.storeUser(user)).thenReturn(false)

        mockMvc.post("/users/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(user)
        }.andExpect {
            status { isNotAcceptable() }
        }
    }

    @Test
    fun testNonExistingEmail() {
        val request = EmailRequest("unknown@test.com")

        `when`(userService.storeEmail(request.email)).thenReturn(-1)

        mockMvc.post("/users/request") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNotAcceptable() }
        }
    }

    @Test
    fun testLoginInvalidCode() {
        val loginRequest = LoginRequest("john@test.com", 111111)

        `when`(
            userService.login(loginRequest.email, loginRequest.code)
        ).thenReturn(null)

        mockMvc.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isUnauthorized() }
        }
    }

}