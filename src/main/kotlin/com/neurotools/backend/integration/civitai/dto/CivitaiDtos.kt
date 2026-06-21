package com.neurotools.backend.integration.civitai.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CivitaiModelPageResponse(
    val items: List<CivitaiModel> = emptyList()
)

data class CivitaiModel(
    val id: Int,
    val name: String,
    val description: String? = null,
    val creator: CivitaiCreator? = null,
    val stats: CivitaiStats = CivitaiStats(),
    val tags: List<String> = emptyList(),
    val nsfw: Boolean = false,
    val type: String? = null,
    @JsonProperty("modelVersions")
    val modelVersions: List<CivitaiModelVersion> = emptyList()
)

data class CivitaiCreator(
    val username: String? = null
)

data class CivitaiStats(
    val downloadCount: Int = 0,
    val favoriteCount: Int = 0,
    val rating: Double = 0.0
)

data class CivitaiModelVersion(
    val images: List<CivitaiImage> = emptyList(),
    val baseModel: String? = null,
    val name: String? = null
)

data class CivitaiImage(
    val url: String? = null,
    val nsfw: Boolean = false,
    val width: Int? = null,
    val height: Int? = null
)
