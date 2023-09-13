package com.odeyalo.sonata.playlists.service.upload;

import com.odeyalo.sonata.playlists.model.Image;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * ImageUploader impl that always returns the same value
 */
@Service
public class MockImageUploader implements ImageUploader {

    @Override
    public Mono<Image> uploadImage(Mono<FilePart> filePart) {
        return Mono.just(Image.urlOnly("https:/cdn.sonata.com/mikunakano"));
    }
}
