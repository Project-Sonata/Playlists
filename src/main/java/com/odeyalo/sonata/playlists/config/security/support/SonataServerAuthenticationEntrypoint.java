package com.odeyalo.sonata.playlists.config.security.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Custom ServerAuthenticationEntryPoint that wrap the exception and return 401 HTTP status
 * with body into {@link ExceptionMessage}
 */
@Component
public class SonataServerAuthenticationEntrypoint implements ServerAuthenticationEntryPoint {
    public static final String EXCEPTION_DESCRIPTION = "Missing access token or token has been expired";
    private final ObjectMapper objectMapper;

    @Autowired
    public SonataServerAuthenticationEntrypoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        exchange.getResponse().setStatusCode(UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = getBody(exchange);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @NotNull
    @SneakyThrows
    private DataBuffer getBody(ServerWebExchange exchange) {
        ExceptionMessage message = ExceptionMessage.of(EXCEPTION_DESCRIPTION);
        String body = objectMapper.writeValueAsString(message);
        return exchange.getResponse().bufferFactory().wrap(body.getBytes());
    }
}
