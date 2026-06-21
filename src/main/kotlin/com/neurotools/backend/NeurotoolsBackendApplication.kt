package com.neurotools.backend

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableScheduling
class NeurotoolsBackendApplication

fun main(args: Array<String>) {
    runApplication<NeurotoolsBackendApplication>(*args)
}
