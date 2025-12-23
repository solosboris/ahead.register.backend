package de.ahead.register.model

data class UserEntry(
    val id: String,
    val firstName: String,
    val lastName: String
) {
    var code: Int = 0
        set(value) { field = value }
}