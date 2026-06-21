package com.neurotools.backend.tool.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import com.neurotools.backend.tool.dto.ToolCreateRequest
import com.neurotools.backend.tool.dto.ToolResponse
import com.neurotools.backend.tool.dto.ToolUpdateRequest
import com.neurotools.backend.tool.entity.ToolEntity
import com.neurotools.backend.tool.mapper.applyUpdate
import com.neurotools.backend.tool.mapper.toEntity
import com.neurotools.backend.tool.mapper.toResponse
import com.neurotools.backend.tool.repository.ToolRepository
import com.neurotools.backend.tool.summary.ToolCatalogSummaryResponse
import com.neurotools.backend.tool.summary.toCatalogSummary

@Service
class ToolService(
    private val repository: ToolRepository
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
            .map(ToolEntity::toResponse)
            .toList()
    }

    @Transactional(readOnly = true)
    fun findBySlug(slug: String): ToolResponse =
        (repository.findBySlug(slug) ?: throw EntityNotFoundException("Tool '$slug' not found"))
            .toResponse()

    @Transactional
    fun registerAffiliateClick(slug: String) {
        val tool = repository.findBySlug(slug)
            ?: throw EntityNotFoundException("Tool '$slug' not found")

        tool.affiliateClicks += 1
        repository.save(tool)
    }

    @Transactional(readOnly = true)
    fun getCatalogSummary(featuredLimit: Int = 3): ToolCatalogSummaryResponse =
        repository.findAll().toCatalogSummary(featuredLimit)

    @Transactional
    fun create(request: ToolCreateRequest): ToolResponse {
        if (repository.existsBySlug(request.slug.trim())) {
            throw DataIntegrityViolationException("Tool slug '${request.slug}' already exists")
        }

        return repository.save(request.toEntity()).toResponse()
    }

    @Transactional
    fun update(id: UUID, request: ToolUpdateRequest): ToolResponse {
        val tool = repository.findById(id)
            .orElseThrow { EntityNotFoundException("Tool '$id' not found") }

        return repository.save(tool.applyUpdate(request)).toResponse()
    }

    @Transactional
    fun delete(id: UUID) {
        if (!repository.existsById(id)) {
            throw EntityNotFoundException("Tool '$id' not found")
        }
        repository.deleteById(id)
    }
}
