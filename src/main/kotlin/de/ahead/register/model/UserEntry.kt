package de.ahead.register.model

data class UserEntry(
    val id: String,
    val firstName: String,
    val lastName: String
) {
    var code: Int = 0
        get() { return field }
        set(value) { field = value }
}