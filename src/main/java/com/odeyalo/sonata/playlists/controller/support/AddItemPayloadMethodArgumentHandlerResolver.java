package com.odeyalo.sonata.playlists.controller.support;

import com.odeyalo.sonata.playlists.service.tracks.AddItemPayload;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Resolves the {@link AddItemPayload} from the given {@link ServerWebExchange}
 */
@Component
public final class AddItemPayloadMethodArgumentHandlerResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(AddItemPayload.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull MethodParameter parameter,
                                        @NotNull BindingContext bindingContext,
                                        @NotNull ServerWebExchange exchange) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();

        String[] itemUris = queryParams.get("uris").toArray(new String[0]);

        return Mono.just(
                AddItemPayload.withItemUris(itemUris)
        );
    }
}
