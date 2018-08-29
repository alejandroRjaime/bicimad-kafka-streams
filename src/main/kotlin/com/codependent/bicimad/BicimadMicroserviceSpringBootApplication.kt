package com.codependent.bicimad

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class BiciMadMicroserviceSpringBootApplication

fun main(args: Array<String>) {
    SpringApplication.run(BiciMadMicroserviceSpringBootApplication::class.java, *args)
}