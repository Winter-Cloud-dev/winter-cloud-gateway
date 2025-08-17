package com.wintercloud.gateway.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    // Auth Error
    // ✅ 토큰 만료
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "로그인 정보가 만료되었습니다."),
    // ✅ 그 외 모든 JWT 관련 예외
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 로그인 정보입니다."),
    // ✅ 토큰 서명 오류
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "로그인 정보의 서명이 유효하지 않습니다."),
    // ✅ 토큰 형식이 잘못되었을 때 (세 부분으로 나뉘지 않음 등)
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "로그인 정보의 형식이 유효하지 않습니다."),
    // ✅ 지원되지 않는 형식의 토큰
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "지원되지 않는 로그인 정보입니다."),
    // ✅ 토큰이 존재하지 않음
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "로그인 정보가 존재하지 않습니다."),

    // System Error
    INVALID_ERROR_CODE(HttpStatus.BAD_REQUEST, "SYSTEM.001"),

}