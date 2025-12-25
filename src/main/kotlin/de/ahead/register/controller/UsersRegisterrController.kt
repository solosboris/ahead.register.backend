package de.ahead.register.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody;
import de.ahead.register.dto.*
import de.ahead.register.model.UserEntry
import de.ahead.register.service.IUserService

@Tag(name = "Users API", description = "Users register endpoints management")
@RestController
@RequestMapping("/users")
class UsersRegisterrController(
    private val userService: IUserService
) {

    private val log = KotlinLogging.logger {}

    @Operation(
        summary = "Accepts first name, last name, email; stores in the user repository",
        description = "Returns the produced creation acknowledgement",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "produced user Id",
                content = [Content(schema = Schema(implementation = String::class))]
            ),
            ApiResponse(
                responseCode = "406",
                description = "the email had been registered",
                content = [Content(schema = Schema(implementation = String::class))]
            )
        ]
    )
    @PostMapping("/register")
    fun register(
        @RequestBody user: UserRegister
    ): ResponseEntity<String> =
        if (userService.storeUser(user))
            ResponseEntity.status(HttpStatus.OK).body("OK")
        else
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body("the email had not been registered")

    @Operation(
        summary = "Creates a 6-digit verification code in the RAM",
        description = "Outputs the code in the console",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "produced code",
                content = [Content(schema = Schema(implementation = Int::class))]
            ),
            ApiResponse(
                responseCode = "406",
                description = "no such email",
                content = [Content(schema = Schema(implementation = Int::class))]
            )
        ]
    )
    @PostMapping("/request")
    fun register(
        @RequestBody emailRequest: EmailRequest
    ): ResponseEntity<Int> {
        val userCode = userService.storeEmail(emailRequest.email)
        if ((100000 <= userCode) && (userCode <= 999999)) {
            log.info { "6-digit verification code succeeded: $userCode" }
            return ResponseEntity.status(HttpStatus.OK).body(userCode)
        } else {
            log.info { "6-digit verification code failed: $userCode" }
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                                    .body(-1)
        }
    }

    @Operation(
        summary = "Accepts an email and a 6-digit code",
        description = "Checks the code against the one in the system",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "success: 200 status code, user ID, first name and last name",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "code mismatch",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            )
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<UserResponse> {
        val userEntry: UserEntry? = userService.login(
            request.email,
            request.code
        )
        return if (userEntry != null)
                    ResponseEntity.status(HttpStatus.OK)
                                    .body(
                                        UserResponse(
                                            id = userEntry.id,
                                            firstName = userEntry.firstName,
                                            lastName = userEntry.lastName
                                        )
                                    )
                else
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                    .body(
                                        UserResponse(id = "", firstName = "", lastName = "")
                                    )
    }

}