package com.neurotools.backend.config

import com.neurotools.backend.admin.controller.AdminSyncController
import com.neurotools.backend.integration.SyncService
import com.neurotools.backend.integration.SyncStatus
import com.neurotools.backend.tool.controller.ToolController
import com.neurotools.backend.tool.service.ClickTokenService
import com.neurotools.backend.tool.service.ClickRateLimitService
import com.neurotools.backend.tool.service.ToolService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(controllers = [AdminSyncController::class, ToolController::class])
@Import(SecurityConfig::class, ClickTokenService::class)
@TestPropertySource(
    properties = [
        "neurotools.security.admin-username=admin",
        "neurotools.security.admin-password=secret",
    ],
)
class SecurityConfigTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var syncService: SyncService

    @MockBean
    private lateinit var toolService: ToolService

    @MockBean
    private lateinit var clickRateLimitService: ClickRateLimitService

    @Autowired
    private lateinit var clickTokenService: ClickTokenService

    @BeforeEach
    fun setUp() {
        Mockito.`when`(toolService.findAll(null, null)).thenReturn(emptyList())
        Mockito.doNothing().`when`(clickRateLimitService).assertAllowed(Mockito.anyString(), Mockito.anyString())
        Mockito.`when`(syncService.getStatus()).thenReturn(
            SyncStatus(
                lastSyncAt = Instant.parse("2024-01-01T00:00:00Z"),
                totalModels = 1,
                huggingFaceModels = 1,
                civitaiModels = 0,
                modelsWithoutSource = 0
            )
        )
    }

    @Test
    fun `admin endpoints reject anonymous requests`() {
        mockMvc.perform(get("/api/admin/sync/status"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `admin endpoints allow basic auth`() {
        mockMvc.perform(get("/api/admin/sync/status").with(httpBasic("admin", "secret")))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalModels").value(1))
    }

    @Test
    fun `public tool catalog remains accessible`() {
        mockMvc.perform(get("/api/tools"))
            .andExpect(status().isOk)
            .andExpect(content().json("[]"))
    }

    @Test
    fun `public click endpoint remains accessible`() {
        val token = clickTokenService.generate("chatgpt")

        mockMvc.perform(post("/api/tools/chatgpt/click").param("token", token))
            .andExpect(status().isNoContent)
    }
}
