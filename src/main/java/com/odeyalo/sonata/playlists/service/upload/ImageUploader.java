package com.odeyalo.sonata.playlists.service.upload;

import com.odeyalo.sonata.playlists.model.Image;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * Base interface for image uploading
 */
public interface ImageUploader {
    /**
     * Upload the given image
     * @param filePart - image to upload
     * @return - image info wrapped in Image object
     */
    Mono<Image> uploadImage(Mono<FilePart> filePart);

}
