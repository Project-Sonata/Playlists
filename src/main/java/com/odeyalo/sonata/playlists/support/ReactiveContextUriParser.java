package com.odeyalo.sonata.playlists.support;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.common.context.MalformedContextUriException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Adapter for {@link ContextUri#fromString(String)} that can be used for Reactive Streams
 */
@Component
public final class ReactiveContextUriParser {

    @NotNull
    public Mono<ContextUri> parse(@NotNull final String contextUriStr) {
        try {
            return Mono.just(ContextUri.fromString(contextUriStr));
        } catch (MalformedContextUriException e) {
            return Mono.error(e);
        }
    }
}
