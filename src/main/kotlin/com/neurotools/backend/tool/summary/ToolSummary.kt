package com.neurotools.backend.tool.summary

import java.math.BigDecimal
import java.math.RoundingMode
import com.neurotools.backend.tool.dto.ToolResponse
import com.neurotools.backend.tool.entity.ToolEntity
import com.neurotools.backend.tool.mapper.toResponse

data class ToolCatalogSummaryResponse(
    val totalTools: Int,
    val categoriesCount: Int,
    val averageRating: BigDecimal,
    val featuredTools: List<ToolResponse>
)

fun List<ToolEntity>.toCatalogSummary(featuredLimit: Int = 3): ToolCatalogSummaryResponse {
    val normalizedLimit = featuredLimit.coerceAtLeast(1)
    val featuredTools = sortedFeaturedTools(normalizedLimit).map(ToolEntity::toResponse)
    val averageRating = if (isEmpty()) {
        BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP)
    } else {
        fold(BigDecimal.ZERO) { total, tool -> total + tool.rating }
            .divide(BigDecimal.valueOf(size.toLong()), 1, RoundingMode.HALF_UP)
    }

    return ToolCatalogSummaryResponse(
        totalTools = size,
        categoriesCount = map { it.category.trim() }
            .filter(String::isNotBlank)
            .toSet()
            .size,
        averageRating = averageRating,
        featuredTools = featuredTools
    )
}

private fun List<ToolEntity>.sortedFeaturedTools(limit: Int): List<ToolEntity> =
    sortedWith(
        compareByDescending<ToolEntity> { it.rating }
            .thenByDescending { it.votes }
            .thenBy { it.name }
    ).take(limit)
