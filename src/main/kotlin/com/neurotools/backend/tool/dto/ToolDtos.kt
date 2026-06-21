package com.neurotools.backend.tool.dto

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
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
    val affiliateClicks: Int,
    val pricing: ToolPricing,
    val pros: List<String>,
    val cons: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class ToolCreateRequest(
    @field:NotBlank
    @field:Size(max = 120)
    val slug: String,

    @field:NotBlank
    @field:Size(max = 180)
    val name: String,

    @field:NotBlank
    @field:Size(max = 120)
    val category: String,

    @field:DecimalMin("0.0")
    @field:DecimalMax("5.0")
    val rating: BigDecimal = BigDecimal("0.0"),

    @field:Min(0)
    val votes: Int = 0,

    @field:NotBlank
    @field:Size(max = 500)
    val description: String,

    @field:NotBlank
    val fullDescription: String,

    @field:NotBlank
    @field:Size(max = 1000)
    val affiliateLink: String,

    val pricing: ToolPricing,

    @field:NotEmpty
    val pros: List<String>,

    @field:NotEmpty
    val cons: List<String>
)

data class ToolUpdateRequest(
    @field:NotBlank
    @field:Size(max = 180)
    val name: String,

    @field:NotBlank
    @field:Size(max = 120)
    val category: String,

    @field:DecimalMin("0.0")
    @field:DecimalMax("5.0")
    val rating: BigDecimal,

    @field:Min(0)
    val votes: Int,

    @field:NotBlank
    @field:Size(max = 500)
    val description: String,

    @field:NotBlank
    val fullDescription: String,

    @field:NotBlank
    @field:Size(max = 1000)
    val affiliateLink: String,

    val pricing: ToolPricing,

    @field:NotEmpty
    val pros: List<String>,

    @field:NotEmpty
    val cons: List<String>
)
