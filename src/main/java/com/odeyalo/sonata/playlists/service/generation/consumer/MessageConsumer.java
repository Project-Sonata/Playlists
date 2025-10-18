package com.odeyalo.sonata.playlists.service.generation.consumer;

import com.odeyalo.sonata.playlists.service.generation.PlaylistGenerationManager;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.PlaylistImagesGeneratedEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.ReceiverRecord;

@Component
@Log4j2
public final class MessageConsumer {
    private final ReactiveKafkaConsumerTemplate<String, PlaylistImagesGeneratedEvent> reactiveKafkaConsumerTemplate;
    private final PlaylistGenerationManager playlistGenerationManager;

    // TODO: save the received playlist to database
    public MessageConsumer(final ReactiveKafkaConsumerTemplate<String, PlaylistImagesGeneratedEvent> reactiveKafkaConsumerTemplate,
                           final PlaylistGenerationManager playlistGenerationManager) {
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
        this.playlistGenerationManager = playlistGenerationManager;
    }

    @PostConstruct
    public Disposable consumeRecord() {
        return reactiveKafkaConsumerTemplate.receive()
                .map(ReceiverRecord::value)
                .flatMap(playlistGenerationManager::handle)
                .doOnError(error -> log.error("Consumer error: {}", error.getMessage()))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
