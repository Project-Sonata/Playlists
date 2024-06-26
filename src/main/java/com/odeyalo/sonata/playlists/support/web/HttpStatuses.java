package com.odeyalo.sonata.playlists.support.web;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.*;

/**
 * Utility methods for http statuses to make code cleaner
 */
public final class HttpStatuses {

    @NotNull
    public static <T> ResponseEntity<T> defaultUnprocessableEntityStatus() {
        return unprocessableEntity().build();
    }

    @NotNull
    public static <T> ResponseEntity<T> defaultCreatedStatus(T responseBody) {
        return status(CREATED).body(responseBody);
    }

    @NotNull
    public static <T> ResponseEntity<T> defaultCreatedStatus() {
        return status(CREATED).build();
    }

    @NotNull
    public static <T> ResponseEntity<T> defaultOkStatus(T body) {
        return ok().body(body);
    }

    @NotNull
    public static <T> ResponseEntity<T> defaultAcceptedStatus() {
        return accepted().build();
    }
    @NotNull
    public static <T> ResponseEntity<T> default204Response() {
        return noContent().build();
    }

    public static <T> ResponseEntity<T> defaultBadRequestStatus() {
        return badRequest().build();
    }

    public static <T> ResponseEntity<T> defaultBadRequestStatus(T body) {
        return badRequest().body(body);
    }

    public static <T> ResponseEntity<T> defaultForbiddenStatus() {
        return status(HttpStatus.FORBIDDEN).build();
    }

    public static <T> ResponseEntity<T> defaultForbiddenStatus(final T body) {
        return status(HttpStatus.FORBIDDEN).body(body);
    }
}
