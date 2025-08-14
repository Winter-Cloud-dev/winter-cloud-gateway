package com.wintercloud.wintercloudgateway.route

import com.wintercloud.wintercloudgateway.filter.AuthenticationFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayRoutes(
    private val authenticationFilter: AuthenticationFilter,
) {

    @Bean
    fun gatewayRoutes(builder: RouteLocatorBuilder): RouteLocator =
        builder.routes()
            .route(
                routeInfo = RouteInfo.USER_SERVICE,
                requiresAuth = true
            )
            .build()

    fun RouteLocatorBuilder.Builder.route(
        routeInfo: RouteInfo,
        requiresAuth: Boolean,
    ): RouteLocatorBuilder.Builder =
        this.route(routeInfo.name) { r ->
            r.path(routeInfo.path)
                .filters { f ->
                    if (requiresAuth) {
                        f.filter(authenticationFilter.filter())
                    }
                    f.stripPrefix(routeInfo.stripPrefix)
                }
                .uri(routeInfo.uri)
        }

}