package com.wintercloud.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class TokenProperties(
    val secret: String,
)