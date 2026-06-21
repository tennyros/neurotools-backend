package com.neurotools.backend.integration.huggingface.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration
import com.neurotools.backend.integration.huggingface.dto.HuggingFaceModel

@Component
class HuggingFaceClient(
    restTemplateBuilder: RestTemplateBuilder,
    @Value("\${neurotools.integration.huggingface.api-url}") private val baseUrl: String,
    @Value("\${neurotools.integration.huggingface.timeout}") timeoutMs: Long
) {
    private val log = LoggerFactory.getLogger(HuggingFaceClient::class.java)
    private val restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofMillis(timeoutMs))
        .setReadTimeout(Duration.ofMillis(timeoutMs))
        .build()

    fun searchModels(pipelineTag: String, limit: Int): List<HuggingFaceModel> {
        val url = UriComponentsBuilder.fromHttpUrl("$baseUrl/models")
            .queryParam("pipeline_tag", pipelineTag)
            .queryParam("sort", "downloads")
            .queryParam("limit", limit)
            .build()
            .toUriString()

        return try {
            restTemplate.getForObject(url, Array<HuggingFaceModel>::class.java)?.toList().orEmpty()
        } catch (exception: Exception) {
            log.warn("Hugging Face request failed for tag {}: {}", pipelineTag, exception.message)
            emptyList()
        }
    }

    fun getModel(modelId: String): HuggingFaceModel? =
        try {
            restTemplate.getForObject("$baseUrl/models/$modelId", HuggingFaceModel::class.java)
        } catch (exception: Exception) {
            log.warn("Hugging Face request failed for model {}: {}", modelId, exception.message)
            null
        }
}
