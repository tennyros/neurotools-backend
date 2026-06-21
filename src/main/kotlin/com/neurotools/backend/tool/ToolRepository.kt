package com.neurotools.backend.tool

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ToolRepository : JpaRepository<ToolEntity, UUID> {
    fun findBySlug(slug: String): ToolEntity?
    fun existsBySlug(slug: String): Boolean
}
