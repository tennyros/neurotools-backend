package com.neurotools.backend.tool

import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

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
