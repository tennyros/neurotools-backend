package com.neurotools.backend.integration

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SyncBootstrapRunner(
    private val syncService: SyncService,
    @Value("\${neurotools.sync.startup-enabled:true}") private val startupEnabled: Boolean
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(SyncBootstrapRunner::class.java)

    override fun run(args: ApplicationArguments?) {
        if (!startupEnabled) {
            log.info("Startup sync is disabled")
            return
        }

        log.info("Running startup catalog sync")
        syncService.syncAll()
    }
}
