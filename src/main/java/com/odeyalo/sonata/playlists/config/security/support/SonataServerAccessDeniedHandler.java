package com.odeyalo.sonata.playlists.config.security.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Custom {@link ServerAccessDeniedHandler} that returns 403 HTTP status with {@link ExceptionMessage} as body
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
        exchange.getResponse().setStatusCode(FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(APPLICATION_JSON);

        DataBuffer buffer = getBody(exchange);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    @NotNull
    @SneakyThrows
    private DataBuffer getBody(ServerWebExchange exchange) {
        ExceptionMessage message = ExceptionMessage.of(ERROR_DESCRIPTION);
        String body = objectMapper.writeValueAsString(message);
        return exchange.getResponse().bufferFactory().wrap(body.getBytes());
    }
}
