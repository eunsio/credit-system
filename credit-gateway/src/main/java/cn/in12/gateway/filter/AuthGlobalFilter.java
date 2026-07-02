package cn.in12.gateway.filter;

import cn.in12.common.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    // 白名单路径
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/auth/login",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/*/v3/api-docs/**",
            "/uploads/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 判断是否是白名单路径
        for (String excludePath : EXCLUDE_PATHS) {
            if (pathMatcher.match(excludePath, path)) {
                return chain.filter(exchange);
            }
        }

        // 2. 获取Token
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            return unauthorizedResponse(exchange, "未携带Token");
        }

        // 去掉 "Bearer " 前缀（如果前端传了）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 3. 校验Token
        try {
            Claims claims = JwtUtils.parseToken(token);
            // 校验通过，可以将用户信息放入Header传递给后续微服务
            ServerHttpRequest mutableRequest = request.mutate()
                    .header("userId", String.valueOf(claims.get("userId")))
                    .header("username", (String) claims.get("username"))
                    .header("role", (String) claims.get("role"))
                    .build();
            return chain.filter(exchange.mutate().request(mutableRequest).build());
        } catch (Exception e) {
            log.error("Token校验失败: {}", e.getMessage());
            return unauthorizedResponse(exchange, "Token无效或已过期");
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = "{\"code\": 401, \"message\": \"" + message + "\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        return -1; // 优先级最高
    }
}
