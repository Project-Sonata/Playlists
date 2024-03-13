package com.odeyalo.sonata.playlists.service.tracks;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public final class JavaClock implements Clock {

    @Override
    @NotNull
    public Instant now() {
        return Instant.now();
    }
}
