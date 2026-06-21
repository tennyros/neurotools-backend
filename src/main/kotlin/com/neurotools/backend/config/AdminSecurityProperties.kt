package com.neurotools.backend.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "neurotools.security")
data class AdminSecurityProperties(
    @field:NotBlank
    val adminUsername: String = "",

    @field:NotBlank
    val adminPassword: String = ""
)
