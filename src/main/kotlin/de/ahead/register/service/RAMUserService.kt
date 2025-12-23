package de.ahead.register.service

import de.ahead.register.dto.User
import de.ahead.register.model.UserEntry
import java.util.Random
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class RAMUserSerice : IUserService {

    private val random: Random = Random()
    private val users = mutableMapOf<String, UserEntry>()

    override fun storeUser(user: User): Boolean {
        if (users.containsKey(user.email)) {
            return false
        }

        users.put(
            user.email,
            UserEntry(
                id = UUID.randomUUID().toString(),
                firstName = user.firstName,
                lastName = user.lastName
            )
        )
        return true
    }

    override fun storeEmail(email: String): Int {
        val userEntry: UserEntry? = users[email]
        if (userEntry == null) {
            return -1
        }

        userEntry.code = random.nextInt(999999 - 100000 + 1) + 100000
        return userEntry.code
    }

    override fun login(email: String, code: Int): UserEntry? {
        val userEntry: UserEntry? = users[email]
        return if ((userEntry != null) && (userEntry.code == code))
                    userEntry
                else
                    null
    }

}