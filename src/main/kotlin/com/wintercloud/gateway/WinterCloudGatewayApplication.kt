package com.wintercloud.gateway

import com.wintercloud.gateway.config.TokenProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(TokenProperties::class)
@SpringBootApplication
class WinterCloudGatewayApplication

fun main(args: Array<String>) {
    runApplication<WinterCloudGatewayApplication>(*args)
}
