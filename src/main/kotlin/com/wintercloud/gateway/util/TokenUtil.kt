package com.wintercloud.gateway.util

import com.wintercloud.gateway.config.TokenProperties
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component

@Component
class TokenUtil(
    private val tokenProperties: TokenProperties,
) {

    private val secretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenProperties.secret))
    }

    fun getAuthentication(token: String): String? {
        val claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
        val userId = claims.subject
        val role = claims["role"] as String
        // val authorities = listOf(SimpleGrantedAuthority(role))

        // Principal에는 userId를, authorities에는 권한 정보를 담습니다.
        return userId
    }
}