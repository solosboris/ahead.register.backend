package de.ahead.register.service

import de.ahead.register.dto.User
import de.ahead.register.model.UserEntry

interface IUserService {

    fun storeUser(user: User): Boolean

    fun storeEmail(email: String): Int

    fun login(email: String, code: Int): UserEntry?

}