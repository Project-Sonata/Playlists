package com.odeyalo.sonata.playlists.config.security.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Custom ServerAccessDeniedHandler that returns 401 HTTP status with ExceptionMessage as body
 *
 * @see ExceptionMessage
 * @see ServerAccessDeniedHandler
 */
@Component
public class SonataServerAccessDeniedHandler implements ServerAccessDeniedHandler {
    public static final String ERROR_DESCRIPTION = "No permission to access this resource!";
    private final ObjectMapper objectMapper;

    public SonataServerAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        DataBuffer buffer = getBody(exchange);
        prepareAccessDeniedResponse(exchange);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    @NotNull
    @SneakyThrows
    private DataBuffer getBody(ServerWebExchange exchange) {
        ExceptionMessage message = ExceptionMessage.of(ERROR_DESCRIPTION);
        String body = objectMapper.writeValueAsString(message);
        return exchange.getResponse().bufferFactory().wrap(body.getBytes());
    }

    private void prepareAccessDeniedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(403));
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
}
