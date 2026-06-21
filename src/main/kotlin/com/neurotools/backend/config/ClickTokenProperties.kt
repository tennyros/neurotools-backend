package com.neurotools.backend.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "neurotools.security.click-token")
data class ClickTokenProperties(
    @field:NotBlank
    val secret: String = "",

    val ttlHours: Long = 24
)
