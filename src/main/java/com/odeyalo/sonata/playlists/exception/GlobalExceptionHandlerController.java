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

    @ExceptionHandler(InvalidPlaylistItemPositionException.class)
    public ResponseEntity<ExceptionMessage> handlePlaylistNotFoundException(InvalidPlaylistItemPositionException ex) {
        ExceptionMessage exceptionMessage = ExceptionMessage.withDescription(
                ex.getMessage()
        );

        return HttpStatuses.defaultBadRequestStatus(exceptionMessage);
    }

    @ExceptionHandler(PlaylistOperationNotAllowedException.class)
    public ResponseEntity<ExceptionMessage> handlePlaylistOperationNotAllowedException(final PlaylistOperationNotAllowedException ignored) {
        final var exceptionMessage = ExceptionMessage.withDescription("You don't have permission to read or change the playlist");

        return HttpStatuses.defaultForbiddenStatus(exceptionMessage);
    }
}
