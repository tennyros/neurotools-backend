package com.neurotools.backend.integration

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SyncScheduler(
    private val syncService: SyncService,
    @Value("\${neurotools.sync.enabled:true}") private val enabled: Boolean
) {
    private val log = LoggerFactory.getLogger(SyncScheduler::class.java)

    @Scheduled(cron = "\${neurotools.sync.schedule:0 0 4 * * *}")
    fun scheduledSync() {
        if (!enabled) {
            log.info("Scheduled sync is disabled")
            return
        }

        log.info("Starting scheduled catalog sync")
        syncService.syncAll()
    }
}
