package com.neurotools.backend.storage

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
@EnableConfigurationProperties(S3Properties::class)
class S3Config {
    @Bean
    @ConditionalOnProperty(prefix = "app.s3", name = ["enabled"], havingValue = "true")
    fun s3Client(properties: S3Properties): S3Client {
        val builder = S3Client.builder()
            .region(Region.of(properties.region))

        if (!properties.endpoint.isNullOrBlank()) {
            builder.endpointOverride(URI.create(properties.endpoint))
                .forcePathStyle(true)
        }

        if (!properties.accessKey.isNullOrBlank() && !properties.secretKey.isNullOrBlank()) {
            builder.credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.accessKey, properties.secretKey)
                )
            )
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create())
        }

        return builder.build()
    }
}
