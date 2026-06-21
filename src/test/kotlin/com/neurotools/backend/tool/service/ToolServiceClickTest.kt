package com.neurotools.backend.tool.service

import com.neurotools.backend.tool.entity.ToolEntity
import com.neurotools.backend.tool.model.ToolPricing
import com.neurotools.backend.tool.repository.ToolRepository
import com.neurotools.backend.config.ClickTokenProperties
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class ToolServiceClickTest {
    private val repository: ToolRepository = Mockito.mock(ToolRepository::class.java)
    private val clickTokenService = ClickTokenService(ClickTokenProperties(secret = "test-secret"))
    private val service = ToolService(repository, clickTokenService)

    @Test
    fun `registerAffiliateClick increments stored click count`() {
        val tool = tool(affiliateClicks = 2)
        Mockito.`when`(repository.findBySlug("chatgpt")).thenReturn(tool)
        Mockito.`when`(repository.save(Mockito.any(ToolEntity::class.java))).thenAnswer { it.arguments[0] as ToolEntity }

        service.registerAffiliateClick("chatgpt")

        assertEquals(3, tool.affiliateClicks)
        Mockito.verify(repository).save(tool)
    }

    private fun tool(affiliateClicks: Int): ToolEntity =
        ToolEntity(
            id = UUID.randomUUID(),
            slug = "chatgpt",
            name = "ChatGPT",
            category = "Text",
            rating = BigDecimal("4.8"),
            votes = 100,
            description = "Description",
            fullDescription = "Full description",
            affiliateLink = "https://chat.openai.com",
            affiliateClicks = affiliateClicks,
            pricing = ToolPricing.FREEMIUM,
            pros = arrayOf("pro"),
            cons = arrayOf("con"),
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-01T00:00:00Z")
        )
}
