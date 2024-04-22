package com.odeyalo.sonata.playlists.support;

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
