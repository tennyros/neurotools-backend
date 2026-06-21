package com.neurotools.backend.tool.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import com.neurotools.backend.tool.model.ToolPricing

@Entity
@Table(name = "ai_tools")
class ToolEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, unique = true)
    var slug: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var category: String,

    @Column(nullable = false, precision = 2, scale = 1)
    var rating: BigDecimal = BigDecimal("0.0"),

    @Column(nullable = false)
    var votes: Int = 0,

    @Column(nullable = false, length = 500)
    var description: String,

    @Column(name = "full_description", nullable = false, columnDefinition = "text")
    var fullDescription: String,

    @Column(name = "affiliate_link", nullable = false, length = 1000)
    var affiliateLink: String,

    @Column(name = "affiliate_clicks", nullable = false)
    var affiliateClicks: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var pricing: ToolPricing,

    @Column(nullable = false, columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    var pros: Array<String> = emptyArray(),

    @Column(nullable = false, columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    var cons: Array<String> = emptyArray(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(name = "external_source", length = 20)
    var externalSource: String? = null,

    @Column(name = "external_id", length = 255)
    var externalId: String? = null,

    @Column(length = 100)
    var provider: String? = null,

    @Column(nullable = false)
    var downloads: Int = 0,

    @Column(nullable = false)
    var likes: Int = 0,

    @Column(name = "rating_external", precision = 3, scale = 2)
    var ratingExternal: BigDecimal? = null,

    @Column(columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    var tags: Array<String>? = null,

    @Column(name = "preview_images", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    var previewImages: Array<String>? = null,

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    var metadata: Map<String, Any>? = null,

    @Column(name = "last_sync_at")
    var lastSyncAt: Instant? = null
)
