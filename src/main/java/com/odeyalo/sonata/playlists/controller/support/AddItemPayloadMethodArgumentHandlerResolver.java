package com.odeyalo.sonata.playlists.controller.support;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.playlists.exception.InvalidPlaylistItemPositionException;
import com.odeyalo.sonata.playlists.exception.MissingRequestParameterException;
import com.odeyalo.sonata.playlists.model.PlaylistItemPosition;
import com.odeyalo.sonata.playlists.service.tracks.AddItemPayload;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

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

        String rawContextUris = queryParams.getFirst("uris");

        if ( rawContextUris == null ) {
            return Mono.error(new MissingRequestParameterException("Missing required request parameter: 'uris'"));
        }

        String[] itemUris = rawContextUris.split(",");

        String position = queryParams.getFirst("position");

        ContextUri[] contextUris = Arrays.stream(itemUris)
                .map(ContextUri::fromString)
                .toArray(ContextUri[]::new);

        if ( position == null ) {

            return Mono.just(
                    AddItemPayload.withItemUris(contextUris)
            );
        }

        if ( !NumberUtils.isParsable(position) ) {
            return Mono.error(new InvalidPlaylistItemPositionException("Position can be Integer type only!"));
        }

        final int pos = NumberUtils.createInteger(position);

        return Mono.just(
                AddItemPayload.atPosition(PlaylistItemPosition.at(pos), contextUris)
        );
    }
}
