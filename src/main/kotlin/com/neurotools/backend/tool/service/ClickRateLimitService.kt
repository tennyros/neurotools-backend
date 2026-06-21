package com.neurotools.backend.tool.service

import com.neurotools.backend.tool.exception.TooManyClickRequestsException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class ClickRateLimitService(
    @Value("\${neurotools.security.click-rate-limit.max-attempts:5}") private val maxAttempts: Int,
    @Value("\${neurotools.security.click-rate-limit.window-seconds:60}") windowSeconds: Long
) {
    private val window = Duration.ofSeconds(windowSeconds)
    private val buckets = ConcurrentHashMap<String, ClickBucket>()

    fun assertAllowed(slug: String, clientKey: String) {
        val now = Instant.now()
        val key = "$clientKey:$slug"
        val bucket = buckets.compute(key) { _, current ->
            val existing = current ?: ClickBucket(now, 0)
            if (Duration.between(existing.windowStart, now) >= window) {
                ClickBucket(now, 1)
            } else {
                existing.copy(attempts = existing.attempts + 1)
            }
        } ?: throw TooManyClickRequestsException("Rate limit check failed")

        if (Duration.between(bucket.windowStart, now) < window && bucket.attempts > maxAttempts) {
            throw TooManyClickRequestsException("Too many click requests")
        }

        cleanup(now)
    }

    private fun cleanup(now: Instant) {
        buckets.entries.removeIf { (_, bucket) ->
            Duration.between(bucket.windowStart, now) >= window
        }
    }

    private data class ClickBucket(
        val windowStart: Instant,
        val attempts: Int
    )
}
