package com.neurotools.backend.tool.service

import com.neurotools.backend.tool.exception.TooManyClickRequestsException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ClickRateLimitServiceTest {
    private val service = ClickRateLimitService(
        maxAttempts = 2,
        windowSeconds = 60
    )

    @Test
    fun `allows requests within the configured limit`() {
        assertDoesNotThrow {
            service.assertAllowed("chatgpt", "127.0.0.1")
            service.assertAllowed("chatgpt", "127.0.0.1")
        }
    }

    @Test
    fun `rejects requests beyond the configured limit`() {
        service.assertAllowed("chatgpt", "127.0.0.1")
        service.assertAllowed("chatgpt", "127.0.0.1")

        assertThrows(TooManyClickRequestsException::class.java) {
            service.assertAllowed("chatgpt", "127.0.0.1")
        }
    }
}
