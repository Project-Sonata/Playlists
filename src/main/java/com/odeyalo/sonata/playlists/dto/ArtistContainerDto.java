package com.odeyalo.sonata.playlists.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;


@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.PROPERTIES))
@Builder
public class ArtistContainerDto implements Iterable<ArtistDto> {
    @Getter(value = AccessLevel.PUBLIC)
    @Singular
    List<ArtistDto> items;

    public static ArtistContainerDto empty() {
        return new ArtistContainerDto(Collections.emptyList());
    }

    public static ArtistContainerDto single(ArtistDto artist) {
        Assert.notNull(artist, "ArtistDto cannot be null!");
        return new ArtistContainerDto(List.of(artist));
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public ArtistDto get(int index) {
        return items.get(index);
    }

    public Stream<ArtistDto> stream() {
        return items.stream();
    }

    public List<ArtistDto> asList() {
        return Collections.unmodifiableList(items);
    }

    @NotNull
    @Override
    public Iterator<ArtistDto> iterator() {
        return items.iterator();
    }
}
