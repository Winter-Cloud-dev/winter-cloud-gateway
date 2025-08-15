package com.wintercloud.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WinterCloudGatewayApplication

fun main(args: Array<String>) {
    runApplication<WinterCloudGatewayApplication>(*args)
}
