package com.neurotools.backend.integration.huggingface.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class HuggingFaceModel(
    val id: String,
    val name: String? = null,
    val description: String? = null,
    val downloads: Int = 0,
    val likes: Int = 0,
    val tags: List<String> = emptyList(),
    @JsonProperty("pipeline_tag")
    val pipelineTag: String? = null,
    @JsonProperty("library_name")
    val libraryName: String? = null,
    val author: String? = null,
    @JsonProperty("created_at")
    val createdAt: String? = null
)
