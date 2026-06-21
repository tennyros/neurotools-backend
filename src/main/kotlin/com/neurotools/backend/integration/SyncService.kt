package com.neurotools.backend.integration

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.neurotools.backend.integration.civitai.service.CivitaiSyncService
import com.neurotools.backend.integration.huggingface.service.HuggingFaceSyncService
import com.neurotools.backend.tool.repository.ToolRepository

@Service
class SyncService(
    private val huggingFaceSyncService: HuggingFaceSyncService,
    private val civitaiSyncService: CivitaiSyncService,
    private val repository: ToolRepository
) {
    @Transactional
    fun syncAll(): List<SyncResult> =
        listOf(
            huggingFaceSyncService.syncCategory("Текстовые"),
            huggingFaceSyncService.syncCategory("Код"),
            civitaiSyncService.syncCategory("Изображения")
        )

    @Transactional
    fun syncByCategory(category: String): SyncResult =
        when (category.trim()) {
            "Текстовые", "Код" -> huggingFaceSyncService.syncCategory(category)
            "Изображения" -> civitaiSyncService.syncCategory(category)
            else -> throw IllegalArgumentException("Unknown category: $category")
        }

    @Transactional(readOnly = true)
    fun getStatus(): SyncStatus {
        val tools = repository.findAll()
        return SyncStatus(
            lastSyncAt = tools.mapNotNull { it.lastSyncAt }.maxOrNull(),
            totalModels = tools.size,
            huggingFaceModels = tools.count { it.externalSource == "huggingface" },
            civitaiModels = tools.count { it.externalSource == "civitai" },
            modelsWithoutSource = tools.count { it.externalSource == null }
        )
    }
}
