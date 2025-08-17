package com.wintercloud.gateway.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.wintercloud.gateway.dto.ErrorResponse
import com.wintercloud.gateway.exception.ErrorCode
import com.wintercloud.gateway.util.TokenUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthenticationFilter(
    private val tokenUtil: TokenUtil,
    private val objectMapper: ObjectMapper
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        private val whitelistEndpoint = listOf(
            "/api/users/signup"
        )
    }

    fun filter(): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

            logger.info { "request.uri.path: ${request.uri.path}, isEqual?: ${isWhitelistEndpoint(request.uri.path)}" }
            // ==================================== Whitelist ====================================
            if (isWhitelistEndpoint(request.uri.path)) return@GatewayFilter chain.filter(exchange)
            // ==================================== Whitelist ====================================

            if (!hasAuthHeader(authHeader)) {
                // 인증 실패
                logger.info {
                    "Authentication header not found -> " +
                            "ID: ${request.id}, URI: ${request.uri}, Remote: ${request.remoteAddress}"
                }
                return@GatewayFilter exchange.onError(objectMapper, ErrorCode.TOKEN_NOT_FOUND)
            }

            val userId = try {
                getUserIdByToken(authHeader!!.removePrefix("Bearer "))
            } catch (je: JwtException) {
                val errorCode = when (je) {
                    // ✅ 토큰 만료
                    is ExpiredJwtException -> ErrorCode.TOKEN_EXPIRED

                    // ✅ 토큰 서명 오류
                    is SignatureException -> ErrorCode.TOKEN_SIGNATURE_INVALID

                    // ✅ 토큰 형식이 잘못되었을 때 (세 부분으로 나뉘지 않음 등)
                    is MalformedJwtException -> ErrorCode.TOKEN_MALFORMED

                    // ✅ 지원되지 않는 형식의 토큰
                    is UnsupportedJwtException -> ErrorCode.TOKEN_UNSUPPORTED

                    // ✅ 그 외 모든 JWT 관련 예외
                    else -> ErrorCode.TOKEN_INVALID
                }

                return@GatewayFilter exchange.onError(objectMapper, errorCode)
            }


            // 인증 성공
            chain.filter(
                exchange.mutate()
                    .request(
                        request.mutate()
                            .header("X-User-Id", userId)
                            .build()
                    ).build()
            )
        }
    }

    private fun isWhitelistEndpoint(path: String): Boolean {
        return path in whitelistEndpoint
    }

    private fun getUserIdByToken(token: String): String? {
        return tokenUtil.getAuthentication(token)
    }

    private fun hasAuthHeader(header: String?): Boolean {
        return !(header.isNullOrEmpty() || !header.startsWith("Bearer "))
    }

    private fun ServerWebExchange.onError(
        objectMapper: ObjectMapper,
        errorCode: ErrorCode,
    ): Mono<Void?> {
        val response = this.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON

        val errorResponse = ErrorResponse(
            status = errorCode.status.name,
            message = errorCode.message,
        )

        val bytes = objectMapper.writeValueAsBytes(errorResponse)
        val buffer: DataBuffer = response.bufferFactory().wrap(bytes)

        return response.writeWith(Mono.just(buffer))
    }

    private fun ServerWebExchange.onError(): Mono<Void?> {
        val response = this.response
        response.statusCode = HttpStatus.UNAUTHORIZED

        return response.setComplete()
    }
}


