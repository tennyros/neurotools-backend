package com.neurotools.backend.tool.summary

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import com.neurotools.backend.tool.dto.ToolResponse
import com.neurotools.backend.tool.entity.ToolEntity
import com.neurotools.backend.tool.model.ToolPricing
import com.neurotools.backend.tool.summary.toCatalogSummary

class ToolSummaryTest {
    @Test
    fun `catalog summary derives totals average and featured tools from entities`() {
        val tools = listOf(
            tool("alpha", "Alpha", "Text", "4.8", 120),
            tool("beta", "Beta", "Image", "4.7", 240),
            tool("gamma", "Gamma", "Code", "4.5", 300)
        )

        val summary = tools.toCatalogSummary(featuredLimit = 2)

        assertEquals(3, summary.totalTools)
        assertEquals(3, summary.categoriesCount)
        assertEquals(BigDecimal("4.7"), summary.averageRating)
        assertEquals(listOf("alpha", "beta"), summary.featuredTools.map { it.slug })
    }

    @Test
    fun `catalog summary handles empty tools list`() {
        val summary = emptyList<ToolEntity>().toCatalogSummary()

        assertEquals(0, summary.totalTools)
        assertEquals(0, summary.categoriesCount)
        assertEquals(BigDecimal("0.0"), summary.averageRating)
        assertEquals(emptyList<ToolResponse>(), summary.featuredTools)
    }

    private fun tool(
        slug: String,
        name: String,
        category: String,
        rating: String,
        votes: Int
    ): ToolEntity =
        ToolEntity(
            id = UUID.randomUUID(),
            slug = slug,
            name = name,
            category = category,
            rating = BigDecimal(rating),
            votes = votes,
            description = "$name description",
            fullDescription = "$name full description",
            affiliateLink = "https://example.com/$slug",
            pricing = ToolPricing.FREE,
            pros = arrayOf("pro"),
            cons = arrayOf("con"),
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-01T00:00:00Z")
        )
}
