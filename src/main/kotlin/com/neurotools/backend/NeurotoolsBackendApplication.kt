package com.neurotools.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NeurotoolsBackendApplication

fun main(args: Array<String>) {
    runApplication<NeurotoolsBackendApplication>(*args)
}
