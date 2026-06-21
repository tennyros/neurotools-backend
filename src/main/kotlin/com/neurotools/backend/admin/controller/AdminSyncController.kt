package com.neurotools.backend.admin.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.neurotools.backend.integration.SyncResult
import com.neurotools.backend.integration.SyncService
import com.neurotools.backend.integration.SyncStatus

@RestController
@RequestMapping("/api/admin/sync")
class AdminSyncController(
    private val syncService: SyncService
) {
    @PostMapping
    fun syncAll(): ResponseEntity<List<SyncResult>> = ResponseEntity.ok(syncService.syncAll())

    @PostMapping("/{category}")
    fun syncCategory(@PathVariable category: String): ResponseEntity<SyncResult> =
        ResponseEntity.ok(syncService.syncByCategory(category))

    @GetMapping("/status")
    fun status(): ResponseEntity<SyncStatus> = ResponseEntity.ok(syncService.getStatus())
}
