package com.wintercloud.gateway.route

/**
 * Spring Cloud Gateway의 라우트 정보를 타입 안전(type-safe)하게 관리하기 위한 열거형 클래스.
 * <p>
 * 각 enum 상수는 하나의 라우트 설정을 나타내며,
 * 확장 함수와 함께 사용되어 동적으로 라우트를 등록하는 데 쓰입니다.
 *
 * @property path 요청 경로가 일치할 때 이 라우트를 활성화시키는 경로 패턴. ("/users/&#42;&#42;")
 * @property uri 라우팅될 최종 목적지 URI. 서비스 디스커버리의 경우 'lb://서비스이름' 또는 'https://url:port' 형식을 사용합니다.
 * @property stripPrefix 요청을 전달하기 전에 경로에서 제거할 접두사(prefix)의 개수.
 */
enum class RouteInfo(
    val uri: String,
    val path: String,
    val stripPrefix: Int = 0,
) {
    // EXAMPLE
    USER_SERVICE(
        uri = "lb://USER-SERVICE",
        path = "/users/**",
        stripPrefix = 1,
    );
}