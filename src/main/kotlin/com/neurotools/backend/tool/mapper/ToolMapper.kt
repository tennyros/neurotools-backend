package com.neurotools.backend.tool.mapper

import java.time.Instant
import com.neurotools.backend.tool.dto.ToolCreateRequest
import com.neurotools.backend.tool.dto.ToolResponse
import com.neurotools.backend.tool.dto.ToolUpdateRequest
import com.neurotools.backend.tool.entity.ToolEntity
import com.neurotools.backend.tool.model.ToolPricing

fun ToolEntity.toResponse(): ToolResponse =
    ToolResponse(
        id = requireNotNull(id),
        slug = slug,
        name = name,
        category = category,
        rating = rating,
        votes = votes,
        description = description,
        fullDescription = fullDescription,
        affiliateLink = affiliateLink,
        affiliateClicks = affiliateClicks,
        pricing = pricing,
        pros = pros.toList(),
        cons = cons.toList(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        externalSource = externalSource,
        externalId = externalId,
        provider = provider,
        downloads = downloads,
        likes = likes,
        ratingExternal = ratingExternal,
        tags = tags?.toList(),
        previewImages = previewImages?.toList(),
        metadata = metadata,
        lastSyncAt = lastSyncAt
    )

fun ToolCreateRequest.toEntity(): ToolEntity =
    ToolEntity(
        slug = slug.trim(),
        name = name.trim(),
        category = category.trim(),
        rating = rating,
        votes = votes,
        description = description.trim(),
        fullDescription = fullDescription.trim(),
        affiliateLink = affiliateLink.trim(),
        affiliateClicks = 0,
        pricing = pricing,
        pros = pros.map(String::trim).filter(String::isNotBlank).toTypedArray(),
        cons = cons.map(String::trim).filter(String::isNotBlank).toTypedArray()
    )

fun ToolEntity.applyUpdate(request: ToolUpdateRequest): ToolEntity {
    name = request.name.trim()
    category = request.category.trim()
    rating = request.rating
    votes = request.votes
    description = request.description.trim()
    fullDescription = request.fullDescription.trim()
    affiliateLink = request.affiliateLink.trim()
    pricing = request.pricing
    pros = request.pros.map(String::trim).filter(String::isNotBlank).toTypedArray()
    cons = request.cons.map(String::trim).filter(String::isNotBlank).toTypedArray()
    updatedAt = Instant.now()
    return this
}
