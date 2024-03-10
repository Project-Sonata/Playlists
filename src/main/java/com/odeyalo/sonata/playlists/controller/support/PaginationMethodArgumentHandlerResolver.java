package com.odeyalo.sonata.playlists.controller.support;

import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Resolve the {@link Pagination} from the {@link ServerWebExchange}
 */
@Component
public final class PaginationMethodArgumentHandlerResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Pagination.class);
    }

    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull MethodParameter parameter,
                                        @NotNull BindingContext bindingContext,
                                        @NotNull ServerWebExchange exchange) {
        String offset = exchange.getRequest().getQueryParams().getFirst("offset");
        String limit = exchange.getRequest().getQueryParams().getFirst("limit");

        Pagination.PaginationBuilder paginationBuilder = Pagination.builder();

        if ( NumberUtils.isParsable(offset) ) {
            paginationBuilder.offset(NumberUtils.toInt(offset));
        }

        if ( NumberUtils.isParsable(limit) ) {
            paginationBuilder.limit(NumberUtils.toInt(limit));
        }

        return Mono.just(
                paginationBuilder.build()
        );
    }
}
