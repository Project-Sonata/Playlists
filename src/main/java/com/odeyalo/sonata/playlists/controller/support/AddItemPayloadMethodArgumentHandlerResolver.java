package com.odeyalo.sonata.playlists.controller.support;

import com.odeyalo.sonata.playlists.exception.InvalidPlaylistItemPositionException;
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

        String position = queryParams.getFirst("position");

        if ( position == null ) {
            return Mono.just(
                    AddItemPayload.withItemUris(itemUris)
            );
        }

        if ( !NumberUtils.isParsable(position) ) {
            return Mono.error(new InvalidPlaylistItemPositionException("Position can be Integer type only!"));
        }

        final int pos = NumberUtils.createInteger(position);

        return Mono.just(
                AddItemPayload.atPosition(PlaylistItemPosition.at(pos), itemUris)
        );

    }
}
