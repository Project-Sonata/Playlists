package com.odeyalo.sonata.playlists.support.pagination;

import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.io.Serializable;

/**
 * Custom pageable with offset support
 */
@Value
public class OffsetBasedPageRequest implements Pageable, Serializable {

    @Serial
    private static final long serialVersionUID = -25822477129613575L;
    private static final int DEFAULT_LIMIT = 50;
    private static final int STARTING_OFFSET = 0;

    int limit;
    long offset;
    Sort sort;

    /**
     * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     * @param sort   can be {@literal null}.
     */
    public OffsetBasedPageRequest(long offset, int limit, Sort sort) {
        if ( offset < 0 ) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }

        if ( limit < 1 ) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    /**
     * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
     *
     * @param offset     zero-based offset.
     * @param limit      the size of the elements to be returned.
     * @param direction  the direction of the {@link Sort} to be specified, can be {@literal null}.
     * @param properties the properties to sort by, must not be {@literal null} or empty.
     */
    public OffsetBasedPageRequest(long offset, int limit, Sort.Direction direction, String... properties) {
        this(offset, limit, Sort.by(direction, properties));
    }

    /**
     * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     */
    public OffsetBasedPageRequest(int offset, int limit) {
        this(offset, limit, Sort.unsorted());
    }

    public static OffsetBasedPageRequest of(int offset, int limit) {
        return new OffsetBasedPageRequest(offset, limit);
    }

    public static Pageable withOffset(int offset) {
        return new OffsetBasedPageRequest(offset, DEFAULT_LIMIT);
    }

    public static Pageable withLimit(int limit) {
        return new OffsetBasedPageRequest(STARTING_OFFSET, limit);
    }

    @Override
    public int getPageNumber() {
        return Math.toIntExact(offset / limit);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    @NotNull
    public Sort getSort() {
        return sort;
    }

    @Override
    @NotNull
    public Pageable next() {
        return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    public OffsetBasedPageRequest previous() {
        return hasPrevious() ? new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
    }


    @Override
    @NotNull
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    @NotNull
    public Pageable first() {
        return new OffsetBasedPageRequest(0, getPageSize(), getSort());
    }

    @Override
    @NotNull
    public Pageable withPage(int pageNumber) {
        return new OffsetBasedPageRequest((long) pageNumber * getPageSize(), getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}