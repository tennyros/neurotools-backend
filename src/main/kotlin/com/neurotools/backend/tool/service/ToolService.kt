package com.neurotools.backend.tool.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.neurotools.backend.tool.dto.ToolResponse
import com.neurotools.backend.tool.entity.ToolEntity
import com.neurotools.backend.tool.mapper.toResponse
import com.neurotools.backend.tool.repository.ToolRepository
import com.neurotools.backend.tool.summary.ToolCatalogSummaryResponse
import com.neurotools.backend.tool.summary.toCatalogSummary

@Service
class ToolService(
    private val repository: ToolRepository,
    private val clickTokenService: ClickTokenService
) {
    @Transactional(readOnly = true)
    fun findAll(category: String?, query: String?): List<ToolResponse> {
        val normalizedCategory = category?.trim()?.takeIf(String::isNotBlank)
        val normalizedQuery = query?.trim()?.lowercase()?.takeIf(String::isNotBlank)

        return repository.findAll()
            .asSequence()
            .filter { normalizedCategory == null || it.category == normalizedCategory }
            .filter {
                normalizedQuery == null ||
                    it.name.lowercase().contains(normalizedQuery) ||
                    it.description.lowercase().contains(normalizedQuery)
            }
            .sortedWith(compareByDescending<ToolEntity> { it.rating }.thenBy { it.name })
            .map { it.toResponse(clickTokenService.generate(it.slug)) }
            .toList()
    }

    @Transactional(readOnly = true)
    fun findBySlug(slug: String): ToolResponse =
        (repository.findBySlug(slug) ?: throw EntityNotFoundException("Tool '$slug' not found"))
            .toResponse(clickTokenService.generate(slug))

    @Transactional
    fun registerAffiliateClick(slug: String) {
        val tool = repository.findBySlug(slug)
            ?: throw EntityNotFoundException("Tool '$slug' not found")

        tool.affiliateClicks += 1
        repository.save(tool)
    }

    @Transactional(readOnly = true)
    fun getCatalogSummary(featuredLimit: Int = 3): ToolCatalogSummaryResponse =
        repository.findAll().toCatalogSummary(featuredLimit) { entity ->
            entity.toResponse(clickTokenService.generate(entity.slug))
        }
}
