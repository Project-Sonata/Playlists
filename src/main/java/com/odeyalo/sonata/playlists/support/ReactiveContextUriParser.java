package com.odeyalo.sonata.playlists.support;

import com.odeyalo.sonata.common.context.ContextUri;
import com.odeyalo.sonata.common.context.ContextUriParser;
import com.odeyalo.sonata.common.context.MalformedContextUriException;
import reactor.core.publisher.Mono;

/**
 * Adapter for {@link ContextUriParser} that can be used for Reactive Streams
 */
public final class ReactiveContextUriParser {
    private final ContextUriParser contextUriParser;

    public ReactiveContextUriParser(ContextUriParser contextUriParser) {
        this.contextUriParser = contextUriParser;
    }

    public Mono<ContextUri> parse(String contextUriStr) {
        try {
            ContextUri contextUri = contextUriParser.parse(contextUriStr);
            return Mono.just(contextUri);
        } catch (MalformedContextUriException e) {
            return Mono.error(e);
        }
    }
}
