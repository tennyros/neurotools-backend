package com.neurotools.backend.tool.service

import com.neurotools.backend.config.ClickTokenProperties
import com.neurotools.backend.tool.exception.InvalidClickTokenException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class ClickTokenServiceTest {
    private val service = ClickTokenService(
        ClickTokenProperties(
            secret = "test-secret",
            ttlHours = 1
        )
    )

    @Test
    fun `generated token verifies for the matching slug`() {
        val issuedAt = Instant.parse("2024-01-01T00:00:00Z")
        val token = service.generate("chatgpt", issuedAt = issuedAt)

        assertDoesNotThrow {
            service.verify("chatgpt", token, now = issuedAt.plus(Duration.ofMinutes(5)))
        }
    }

    @Test
    fun `generated token rejects a different slug`() {
        val token = service.generate("chatgpt", issuedAt = Instant.parse("2024-01-01T00:00:00Z"))

        assertThrows(InvalidClickTokenException::class.java) {
            service.verify("claude", token, now = Instant.parse("2024-01-01T00:05:00Z"))
        }
    }
}
