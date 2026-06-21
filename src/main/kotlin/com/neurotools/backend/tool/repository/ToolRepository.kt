package com.neurotools.backend.tool.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import com.neurotools.backend.tool.entity.ToolEntity

interface ToolRepository : JpaRepository<ToolEntity, UUID> {
    fun findBySlug(slug: String): ToolEntity?
    fun existsBySlug(slug: String): Boolean
    fun findByExternalSourceAndExternalId(externalSource: String, externalId: String): ToolEntity?
}
