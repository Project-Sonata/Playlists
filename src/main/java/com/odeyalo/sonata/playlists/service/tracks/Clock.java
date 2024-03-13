package com.odeyalo.sonata.playlists.service.tracks;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * A wrapper around the system clock to allow custom implementations to be used in unit tests where we want to fake or control the clock behavior.
 */
public interface Clock {

    @NotNull
    Instant now();

}
