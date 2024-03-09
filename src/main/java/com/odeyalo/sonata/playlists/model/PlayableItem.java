package com.odeyalo.sonata.playlists.model;

import org.jetbrains.annotations.NotNull;

/**
 * Item that can be played(track, episode, etc.). Made it interface because there is no generic structure for playable items
 */
public interface PlayableItem {
    @NotNull
    String getContextUri();
}
