package com.neurotools.backend.tool.mapper

import java.time.Instant
import com.neurotools.backend.tool.dto.ToolResponse
import com.neurotools.backend.tool.entity.ToolEntity

fun ToolEntity.toResponse(clickToken: String): ToolResponse =
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
        clickToken = clickToken,
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
