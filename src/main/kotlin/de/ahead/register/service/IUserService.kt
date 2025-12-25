package de.ahead.register.service

import de.ahead.register.dto.UserRegister
import de.ahead.register.model.UserEntry

interface IUserService {

    fun storeUser(user: UserRegister): Boolean

    fun storeEmail(email: String): Int

    fun login(email: String, code: Int): UserEntry?

}