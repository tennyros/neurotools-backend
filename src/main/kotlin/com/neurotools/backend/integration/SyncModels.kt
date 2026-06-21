package com.neurotools.backend.integration

import java.time.Instant

data class SyncResult(
    val source: String,
    val added: Int = 0,
    val updated: Int = 0,
    val failed: Int = 0,
    val errors: List<String> = emptyList(),
    val timestamp: Instant = Instant.now()
)

data class SyncStatus(
    val lastSyncAt: Instant?,
    val totalModels: Int,
    val huggingFaceModels: Int,
    val civitaiModels: Int,
    val modelsWithoutSource: Int
)
