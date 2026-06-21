package com.neurotools.backend.integration.civitai.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration
import com.neurotools.backend.integration.civitai.dto.CivitaiModel
import com.neurotools.backend.integration.civitai.dto.CivitaiModelPageResponse

@Component
class CivitaiClient(
    restTemplateBuilder: RestTemplateBuilder,
    @Value("\${neurotools.integration.civitai.api-url:https://civitai.com/api/v1}") private val baseUrl: String,
    @Value("\${neurotools.integration.civitai.api-key:}") private val apiKey: String,
    @Value("\${neurotools.integration.civitai.timeout:10000}") timeoutMs: Long
) {
    private val log = LoggerFactory.getLogger(CivitaiClient::class.java)
    private val restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofMillis(timeoutMs))
        .setReadTimeout(Duration.ofMillis(timeoutMs))
        .build()

    fun listModels(types: List<String>, limit: Int): List<CivitaiModel> {
        val url = UriComponentsBuilder.fromHttpUrl("$baseUrl/models")
            .queryParam("types", types.joinToString(","))
            .queryParam("sort", "MostDownloaded")
            .queryParam("limit", limit)
            .build()
            .toUriString()

        return try {
            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity<Any>(createHeaders()),
                CivitaiModelPageResponse::class.java
            )
            response.body?.items.orEmpty()
        } catch (exception: Exception) {
            log.warn("Civitai request failed: {}", exception.message)
            emptyList()
        }
    }

    private fun createHeaders(): HttpHeaders =
        HttpHeaders().apply {
            accept = listOf(org.springframework.http.MediaType.APPLICATION_JSON)
            if (apiKey.isNotBlank()) {
                setBearerAuth(apiKey)
            }
        }
}
