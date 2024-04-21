package com.odeyalo.sonata.playlists.support;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public final class JavaClock implements Clock {

    @Override
    @NotNull
    public Instant now() {
        return Instant.now();
    }
}
