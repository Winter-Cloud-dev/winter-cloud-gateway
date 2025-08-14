package com.wintercloud.wintercloudgateway.filter

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthenticationFilter {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun filter(): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

            if (!hasAuthHeader(authHeader)) {
                // 인증 실패
                logger.info {
                    "Authentication header not found -> " +
                            "ID: ${request.id}, URI: ${request.uri}, Remote: ${request.remoteAddress}"
                }
                return@GatewayFilter exchange.onError()
            }

            // 인증 성공
            chain.filter(exchange)
        }
    }

    private fun hasAuthHeader(header: String?): Boolean {
        return !(header.isNullOrEmpty() || !header.startsWith("Bearer "))
    }

    private fun ServerWebExchange.onError(): Mono<Void?> {
        val response = this.response
        response.statusCode = HttpStatus.UNAUTHORIZED

        return response.setComplete()
    }
}


