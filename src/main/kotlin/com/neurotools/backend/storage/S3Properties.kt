package com.neurotools.backend.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.s3")
data class S3Properties(
    val enabled: Boolean = false,
    val bucket: String = "",
    val region: String = "us-east-1",
    val endpoint: String? = null,
    val accessKey: String? = null,
    val secretKey: String? = null
)
