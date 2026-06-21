package com.neurotools.backend.tool.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import com.neurotools.backend.tool.dto.ToolResponse
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
}
