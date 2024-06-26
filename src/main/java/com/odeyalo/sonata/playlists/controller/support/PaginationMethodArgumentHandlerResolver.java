package com.odeyalo.sonata.playlists.controller.support;

import com.odeyalo.sonata.playlists.exception.InvalidPaginationLimitException;
import com.odeyalo.sonata.playlists.exception.InvalidPaginationOffsetException;
import com.odeyalo.sonata.playlists.support.pagination.Pagination;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

        Mono<Object> offsetError = maybeBuildOffset(offset, paginationBuilder);

        if ( offsetError != null ) return offsetError;

        Mono<Object> limitError = maybeBuildLimit(limit, paginationBuilder);

        if ( limitError != null ) return limitError;

        return Mono.just(
                paginationBuilder.build()
        );
    }

    @Nullable
    private static Mono<Object> maybeBuildOffset(String offset, Pagination.PaginationBuilder paginationBuilder) {
        if (offset == null) {
            // Do nothing, we should use default values
            return null;
        }

        if ( !NumberUtils.isParsable(offset) ) {
            return Mono.error(new InvalidPaginationOffsetException("Offset parameter is not parsable!"));
        }

        int offsetValue = NumberUtils.toInt(offset);

        if (offsetValue < 0) {
            return Mono.error(new InvalidPaginationOffsetException("Offset parameter must be greater or equal to 0!"));
        }


        paginationBuilder.offset(offsetValue);
        return null;
    }

    @Nullable
    private static Mono<Object> maybeBuildLimit(String limit, Pagination.PaginationBuilder paginationBuilder) {
        if ( limit == null ) {
            // Do nothing, we should use default values
            return null;
        }

        if ( !NumberUtils.isParsable(limit) ) {
            return Mono.error(
                    InvalidPaginationLimitException.withCustomMessage("'limit' isn't number. Must be greater than 0")
            );
        }

        int limitValue = NumberUtils.toInt(limit);

        if ( limitValue <= 0 ) {
            return Mono.error(InvalidPaginationLimitException.withCustomMessage("'limit' must be greater than 0"));
        }

        paginationBuilder.limit(limitValue);

        return null;
    }
}
