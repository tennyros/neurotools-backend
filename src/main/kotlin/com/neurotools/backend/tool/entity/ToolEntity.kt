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
    var updatedAt: Instant = Instant.now()
)
