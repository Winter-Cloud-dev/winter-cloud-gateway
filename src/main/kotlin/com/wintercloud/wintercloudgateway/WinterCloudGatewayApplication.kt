package com.wintercloud.wintercloudgateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WinterCloudGatewayApplication

fun main(args: Array<String>) {
    runApplication<WinterCloudGatewayApplication>(*args)
}
