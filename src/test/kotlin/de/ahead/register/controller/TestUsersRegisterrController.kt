package de.ahead.register.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.ahead.register.dto.UserRegister
import de.ahead.register.dto.EmailRequest
import de.ahead.register.dto.LoginRequest
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

    private val userRegister: UserRegister =
                UserRegister("John", "Doe", "john.doe@test.com")
    private val registerCode: Int = 123456

    @Test
    fun testRegisterUser() {
        `when`(
            userService.storeUser(userRegister)
        ).thenReturn(true)

        mockMvc.post("/users/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper
                .writeValueAsString(userRegister)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun testCodeVerification() {
        `when`(
            userService.storeEmail(
                userRegister.email
            )
        ).thenReturn(registerCode)

        mockMvc.post("/users/request") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(EmailRequest(userRegister.email))
        }.andExpect {
            status { isOk() }
            content { registerCode.toString() }
        }
    }

    @Test
    fun testLogin() {
        var userEntry: UserEntry = UserEntry(
                id = UUID.randomUUID().toString(),
                firstName = userRegister.firstName,
                lastName = userRegister.lastName
            ).apply{ code = 1 }


        `when`(
            userService.login(
                userRegister.email,
                registerCode
            )
        ).thenReturn( userEntry )

        mockMvc.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper
                .writeValueAsString(
                    LoginRequest(
                        userRegister.email,
                        registerCode
                    )
                )
        }.andExpect {
            status { isOk() }
            jsonPath("$.firstName") {
                value(userRegister.firstName)
            }
            jsonPath("$.lastName") {
                value(userRegister.lastName)
            }
            jsonPath("$.id") {
                value(userEntry.id)
            }
        }
    }

    @Test
    fun testRegisteredMail() {
        `when`(
            userService.storeUser(userRegister)
        ).thenReturn(false)

        mockMvc.post("/users/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper
                .writeValueAsString(userRegister)
        }.andExpect {
            status { isNotAcceptable() }
        }
    }

    @Test
    fun testNonExistingEmail() {
        val emailRequest = EmailRequest(
            userRegister.email + ".lom"
        )

        `when`(
            userService.storeEmail(emailRequest.email)
        ).thenReturn(-1)

        mockMvc.post("/users/request") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper
                .writeValueAsString(emailRequest)
        }.andExpect {
            status { isNotAcceptable() }
        }
    }

    @Test
    fun testLoginInvalidCode() {
        `when`(
            userService.login(
                userRegister.email,
                registerCode + 1
            )
        ).thenReturn(null)

        mockMvc.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper
                .writeValueAsString(
                    LoginRequest(
                        userRegister.email,
                        registerCode + 1
                    )
                )
        }.andExpect {
            status { isUnauthorized() }
        }
    }

}