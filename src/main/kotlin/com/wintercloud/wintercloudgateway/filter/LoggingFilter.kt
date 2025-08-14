package com.wintercloud.wintercloudgateway.filter

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class LoggingFilter : GlobalFilter, Ordered {

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val START_TIME_KEY = "startTime"
    }

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        exchange.attributes[START_TIME_KEY] = System.currentTimeMillis()

        // 요청 로깅 (Pre-Filter)
        logger.info {
            "Request Starting -> " +
                    "ID: ${request.id}, " +
                    "URI: ${request.uri}, " +
                    "Method: ${request.method}, " +
                    "Remote: ${request.remoteAddress}"
        }

        // 응답 로깅 (Post-Filter)
        return chain.filter(exchange).then(
            Mono.fromRunnable {
                val response = exchange.response
                val startTime = exchange.attributes.getOrDefault(START_TIME_KEY, 0L) as Long
                val duration = System.currentTimeMillis() - startTime

                logger.info {
                    "Response Finished -> " +
                            "ID: ${request.id}, " +
                            "Status: ${response.statusCode}, " +
                            "Duration: ${duration}ms"
                }
            }
        )
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }
}