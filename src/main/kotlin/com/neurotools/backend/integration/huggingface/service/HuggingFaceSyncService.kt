package com.neurotools.backend.integration.huggingface.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import com.neurotools.backend.integration.SyncResult
import com.neurotools.backend.integration.huggingface.client.HuggingFaceClient
import com.neurotools.backend.integration.huggingface.dto.HuggingFaceModel
import com.neurotools.backend.tool.entity.ToolEntity
import com.neurotools.backend.tool.model.ToolPricing
import com.neurotools.backend.tool.repository.ToolRepository

@Service
class HuggingFaceSyncService(
    private val client: HuggingFaceClient,
    private val repository: ToolRepository,
    @Value("\${neurotools.integration.huggingface.max-models:20}") private val maxModels: Int
) {
    private val log = LoggerFactory.getLogger(HuggingFaceSyncService::class.java)

    private val categoryMapping = mapOf(
        "Текстовые" to listOf("text-generation", "conversational", "text2text-generation"),
        "Код" to listOf("text-generation", "code-generation", "text2text-generation")
    )

    @Transactional
    fun syncCategory(category: String): SyncResult {
        val tags = categoryMapping[category] ?: return SyncResult(source = "huggingface", failed = 1, errors = listOf("Unknown category: $category"))
        val models = tags.flatMap { client.searchModels(it, maxModels) }.distinctBy { it.id }

        var added = 0
        var updated = 0
        var failed = 0
        val errors = mutableListOf<String>()

        models.forEach { model ->
            try {
                val existing = repository.findByExternalSourceAndExternalId("huggingface", model.id)
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
                log.warn("Failed to sync Hugging Face model {}", model.id, exception)
            }
        }

        return SyncResult(source = "huggingface", added = added, updated = updated, failed = failed, errors = errors)
    }

    private fun createEntity(category: String, model: HuggingFaceModel): ToolEntity =
        ToolEntity(
            slug = sanitizeSlug(model.id),
            name = model.name ?: model.id.substringAfterLast('/'),
            category = category,
            rating = BigDecimal("0.0"),
            votes = 0,
            description = model.description.orEmpty(),
            fullDescription = model.description.orEmpty(),
            affiliateLink = "https://huggingface.co/${model.id}",
            pricing = ToolPricing.FREE,
            pros = emptyArray(),
            cons = emptyArray(),
            externalSource = "huggingface",
            externalId = model.id,
            provider = model.author,
            downloads = model.downloads,
            likes = model.likes,
            tags = model.tags.toTypedArray(),
            metadata = buildMap<String, Any> {
                model.pipelineTag?.let { put("pipelineTag", it) }
                model.libraryName?.let { put("libraryName", it) }
                model.createdAt?.let { put("createdAt", it) }
            },
            lastSyncAt = Instant.now()
        )

    private fun updateEntity(entity: ToolEntity, category: String, model: HuggingFaceModel) {
        entity.name = model.name ?: entity.name
        entity.category = category
        entity.description = model.description ?: entity.description
        entity.fullDescription = model.description ?: entity.fullDescription
        entity.provider = model.author ?: entity.provider
        entity.downloads = model.downloads
        entity.likes = model.likes
        entity.tags = model.tags.toTypedArray()
        entity.metadata = buildMap<String, Any> {
            model.pipelineTag?.let { put("pipelineTag", it) }
            model.libraryName?.let { put("libraryName", it) }
            model.createdAt?.let { put("createdAt", it) }
        }
        entity.lastSyncAt = Instant.now()
        entity.updatedAt = Instant.now()
        repository.save(entity)
    }

    private fun sanitizeSlug(value: String): String =
        value.lowercase()
            .replace('/', '-')
            .replace('_', '-')
            .replace(Regex("[^a-z0-9-]+"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')
}
