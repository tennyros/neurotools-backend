package com.neurotools.backend.tool.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import com.neurotools.backend.tool.dto.ToolCreateRequest
import com.neurotools.backend.tool.dto.ToolResponse
import com.neurotools.backend.tool.dto.ToolUpdateRequest
import com.neurotools.backend.tool.service.ToolService
import com.neurotools.backend.tool.summary.ToolCatalogSummaryResponse

@RestController
@RequestMapping("/api/tools")
class ToolController(
    private val service: ToolService
) {
    @GetMapping
    fun listTools(
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) query: String?
    ): List<ToolResponse> = service.findAll(category, query)

    @GetMapping("/summary")
    fun getSummary(
        @RequestParam(defaultValue = "3") featuredLimit: Int
    ): ToolCatalogSummaryResponse = service.getCatalogSummary(featuredLimit)

    @GetMapping("/{slug}")
    fun getTool(@PathVariable slug: String): ToolResponse = service.findBySlug(slug)

    @PostMapping("/{slug}/click")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun recordAffiliateClick(@PathVariable slug: String) = service.registerAffiliateClick(slug)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTool(@Valid @RequestBody request: ToolCreateRequest): ToolResponse =
        service.create(request)

    @PutMapping("/{id}")
    fun updateTool(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ToolUpdateRequest
    ): ToolResponse = service.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTool(@PathVariable id: UUID) = service.delete(id)
}
