package com.neurotools.backend.tool.service

import com.neurotools.backend.config.ClickTokenProperties
import com.neurotools.backend.tool.exception.InvalidClickTokenException
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class ClickTokenService(
    private val properties: ClickTokenProperties
) {
    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()

    fun generate(slug: String, issuedAt: Instant = Instant.now()): String {
        val expiresAt = issuedAt.plus(Duration.ofHours(properties.ttlHours))
        val payload = payload(slug, expiresAt.epochSecond)
        return "$payload.${sign(payload)}"
    }

    fun verify(slug: String, token: String, now: Instant = Instant.now()) {
        val parts = token.split('.')
        if (parts.size != 3) {
            throw InvalidClickTokenException("Invalid click token")
        }

        val tokenSlug = decode(parts[0])
        val expiresAt = decode(parts[1]).toLongOrNull()
            ?: throw InvalidClickTokenException("Invalid click token")

        if (tokenSlug != slug) {
            throw InvalidClickTokenException("Invalid click token")
        }

        if (now.epochSecond > expiresAt) {
            throw InvalidClickTokenException("Click token expired")
        }

        val payload = "${parts[0]}.${parts[1]}"
        val expected = sign(payload)
        if (!constantTimeEquals(expected, parts[2])) {
            throw InvalidClickTokenException("Invalid click token")
        }
    }

    private fun payload(slug: String, expiresAtEpochSecond: Long): String =
        "${encode(slug)}.${encode(expiresAtEpochSecond.toString())}"

    private fun sign(payload: String): String {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(properties.secret.toByteArray(StandardCharsets.UTF_8), HMAC_ALGORITHM))
        return encoder.encodeToString(mac.doFinal(payload.toByteArray(StandardCharsets.UTF_8)))
    }

    private fun encode(value: String): String =
        encoder.encodeToString(value.toByteArray(StandardCharsets.UTF_8))

    private fun decode(value: String): String =
        String(decoder.decode(value), StandardCharsets.UTF_8)

    private fun constantTimeEquals(expected: String, actual: String): Boolean {
        val expectedBytes = expected.toByteArray(StandardCharsets.UTF_8)
        val actualBytes = actual.toByteArray(StandardCharsets.UTF_8)
        return java.security.MessageDigest.isEqual(expectedBytes, actualBytes)
    }

    private companion object {
        const val HMAC_ALGORITHM = "HmacSHA256"
    }
}
