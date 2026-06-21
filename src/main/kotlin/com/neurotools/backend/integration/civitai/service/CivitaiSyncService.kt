package com.neurotools.backend.integration.civitai.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import com.neurotools.backend.integration.SyncResult
import com.neurotools.backend.integration.civitai.client.CivitaiClient
import com.neurotools.backend.integration.civitai.dto.CivitaiModel
import com.neurotools.backend.tool.entity.ToolEntity
import com.neurotools.backend.tool.model.ToolPricing
import com.neurotools.backend.tool.repository.ToolRepository

@Service
class CivitaiSyncService(
    private val client: CivitaiClient,
    private val repository: ToolRepository,
    @Value("\${neurotools.integration.civitai.max-models:20}") private val maxModels: Int
) {
    private val log = LoggerFactory.getLogger(CivitaiSyncService::class.java)

    @Transactional
    fun syncCategory(category: String): SyncResult {
        val models = client.listModels(listOf("Checkpoint", "LORA", "ControlNet", "TextualInversion"), maxModels)

        var added = 0
        var updated = 0
        var failed = 0
        val errors = mutableListOf<String>()

        models.filterNot { it.nsfw }.forEach { model ->
            try {
                val existing = repository.findByExternalSourceAndExternalId("civitai", model.id.toString())
                if (existing == null) {
                    repository.save(createEntity(category, model))
                    added += 1
                } else {
                    updateEntity(existing, category, model)
                    updated += 1
                }
            } catch (exception: Exception) {
                failed += 1
                errors += "${model.id}: ${exception.message}"
                log.warn("Failed to sync Civitai model {}", model.id, exception)
            }
        }

        return SyncResult(source = "civitai", added = added, updated = updated, failed = failed, errors = errors)
    }

    private fun createEntity(category: String, model: CivitaiModel): ToolEntity =
        ToolEntity(
            slug = "civitai-${model.id}",
            name = model.name,
            category = category,
            rating = BigDecimal.valueOf(model.stats.rating).setScale(1, java.math.RoundingMode.HALF_UP),
            votes = model.stats.favoriteCount,
            description = model.description.orEmpty(),
            fullDescription = model.description.orEmpty(),
            affiliateLink = "https://civitai.com/models/${model.id}",
            pricing = ToolPricing.FREE,
            pros = emptyArray(),
            cons = emptyArray(),
            externalSource = "civitai",
            externalId = model.id.toString(),
            provider = model.creator?.username,
            downloads = model.stats.downloadCount,
            likes = model.stats.favoriteCount,
            ratingExternal = BigDecimal.valueOf(model.stats.rating).setScale(2, java.math.RoundingMode.HALF_UP),
            tags = model.tags.toTypedArray(),
            previewImages = extractPreviewImages(model),
            metadata = buildMap<String, Any> {
                put("nsfw", model.nsfw)
                model.type?.let { put("type", it) }
                val baseModels = model.modelVersions.mapNotNull { it.baseModel }.distinct()
                if (baseModels.isNotEmpty()) {
                    put("baseModels", baseModels)
                }
            },
            lastSyncAt = Instant.now()
        )

    private fun updateEntity(entity: ToolEntity, category: String, model: CivitaiModel) {
        entity.name = model.name
        entity.category = category
        entity.description = model.description ?: entity.description
        entity.fullDescription = model.description ?: entity.fullDescription
        entity.provider = model.creator?.username ?: entity.provider
        entity.downloads = model.stats.downloadCount
        entity.likes = model.stats.favoriteCount
        entity.rating = BigDecimal.valueOf(model.stats.rating).setScale(1, java.math.RoundingMode.HALF_UP)
        entity.ratingExternal = BigDecimal.valueOf(model.stats.rating).setScale(2, java.math.RoundingMode.HALF_UP)
        entity.votes = model.stats.favoriteCount
        entity.tags = model.tags.toTypedArray()
        entity.previewImages = extractPreviewImages(model)
        entity.metadata = buildMap<String, Any> {
            put("nsfw", model.nsfw)
            model.type?.let { put("type", it) }
            val baseModels = model.modelVersions.mapNotNull { it.baseModel }.distinct()
            if (baseModels.isNotEmpty()) {
                put("baseModels", baseModels)
            }
        }
        entity.lastSyncAt = Instant.now()
        entity.updatedAt = Instant.now()
        repository.save(entity)
    }

    private fun extractPreviewImages(model: CivitaiModel): Array<String> =
        model.modelVersions
            .flatMap { it.images }
            .filter { !it.nsfw }
            .mapNotNull { it.url }
            .distinct()
            .take(5)
            .toTypedArray()
}
