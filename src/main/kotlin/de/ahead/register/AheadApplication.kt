package de.ahead.register

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AheadApplication

fun main(args: Array<String>) {
    runApplication<AheadApplication>(*args)
}