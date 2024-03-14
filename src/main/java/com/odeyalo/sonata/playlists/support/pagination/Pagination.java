package com.odeyalo.sonata.playlists.support.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
@Builder
public class Pagination {
    @Builder.Default
    int offset = 0;
    @Builder.Default
    int limit = 50;

    public static Pagination defaultPagination() {
        return builder().build();
    }

    public static Pagination withOffset(int offset) {
        return builder().offset(offset).build();
    }

    public static Pagination withLimit(int limit) {
        return builder().limit(limit).build();
    }

    public static Pagination withOffsetAndLimit(int offset, int limit) {
        return builder().offset(offset).limit(limit).build();
    }
}
