package com.odeyalo.sonata.playlists.service.tracks;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public final class MockClock implements Clock {
    private final Instant toReturn;

    public MockClock(Instant toReturn) {
        this.toReturn = toReturn;
    }

    @Override
    @NotNull
    public Instant now() {
        return toReturn;
    }
}
