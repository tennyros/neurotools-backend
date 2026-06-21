package com.neurotools.backend.tool.dto

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import com.neurotools.backend.tool.model.ToolPricing

data class ToolResponse(
    val id: UUID,
    val slug: String,
    val name: String,
    val category: String,
    val rating: BigDecimal,
    val votes: Int,
    val description: String,
    val fullDescription: String,
    val affiliateLink: String,
    val clickToken: String,
    val affiliateClicks: Int,
    val pricing: ToolPricing,
    val pros: List<String>,
    val cons: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val externalSource: String?,
    val externalId: String?,
    val provider: String?,
    val downloads: Int,
    val likes: Int,
    val ratingExternal: BigDecimal?,
    val tags: List<String>?,
    val previewImages: List<String>?,
    val metadata: Map<String, Any>?,
    val lastSyncAt: Instant?
)
