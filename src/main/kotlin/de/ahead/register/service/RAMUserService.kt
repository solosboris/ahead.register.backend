package de.ahead.register.service

import de.ahead.register.dto.UserRegister
import de.ahead.register.model.UserEntry
import java.util.Random
import java.util.UUID
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class RAMUserSerice : IUserService {

    private val random: Random = Random()
    private val users = mutableMapOf<String, UserEntry>()
	private val log = KotlinLogging.logger {}

    override fun storeUser(user: UserRegister): Boolean {
		log.info { "storeUser: $user" }
        val userEntry: UserEntry? = users[user.email]
        if (userEntry == null) {
            users.put(
                user.email,
                UserEntry(
                    id = UUID.randomUUID().toString(),
                    firstName = user.firstName,
                    lastName = user.lastName
                ).apply{ code = 0 }
            )
            log.info { "storeUser: #1 user had been created and stored; success" }
            return true
        } else if (userEntry.code != 1) {
            userEntry.code = 0
            log.info { "storeUser: #2 user code has been zeroed anew; success" }
            return true
        } else {
            log.info { "storeUser: #3 user had been committed; failure" }
            return false   
        }
    }

    override fun storeEmail(email: String): Int {
        val userEntry: UserEntry? = users[email]
        if (userEntry == null) {
            log.info { "storeEmail: #1 no user; failure" }
            return -1
        } else if (userEntry.code != 1) {
            userEntry.code = random.nextInt(999999 - 100000 + 1) + 100000
            log.info { "storeEmail: #2 user code has been set/reset; success" }
            return userEntry.code
        } else {
            log.info { "storeEmail: #3 user had been committed; failure" }
            return -1   
        }        
    }

    override fun login(email: String, code: Int): UserEntry? {
        val userEntry: UserEntry? = users[email]
        if ((userEntry != null) && (userEntry.code == code)) {
            userEntry.code = 1
            return userEntry
        }
        return null
    }

}