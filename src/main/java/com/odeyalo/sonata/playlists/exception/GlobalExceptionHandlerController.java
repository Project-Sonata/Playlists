package com.odeyalo.sonata.playlists.exception;

import com.odeyalo.sonata.playlists.dto.ExceptionMessage;
import com.odeyalo.sonata.playlists.support.web.HttpStatuses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandlerController {

    @ExceptionHandler(InvalidPaginationLimitException.class)
    public ResponseEntity<Void> handleInvalidPaginationLimitException(InvalidPaginationLimitException ignored) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidPaginationOffsetException.class)
    public ResponseEntity<Void> handleInvalidPaginationOffsetException(InvalidPaginationOffsetException ignored) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(PlaylistNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handlePlaylistNotFoundException(PlaylistNotFoundException ex) {
        ExceptionMessage exceptionMessage = ExceptionMessage.withDescription(
                String.format("Playlist with ID: %s does not exist", ex.getPlaylistId())
        );

        return HttpStatuses.defaultBadRequestStatus(exceptionMessage);
    }
}
