package de.ahead.register.config

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // CORS preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // backend API
                    .requestMatchers("/users/**").permitAll()
                    // Swagger, OpenAPI
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                    ).permitAll()
                    // static resources (WebJars, CSS, JS)
                    .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations()
                    ).permitAll()
                    // other is secured
                    .anyRequest().authenticated()
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = listOf(
                "http://localhost:5173",
                "http://localhost:8081"
            )
            allowedMethods = listOf(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
            )
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("*")
            allowCredentials = false
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }

}