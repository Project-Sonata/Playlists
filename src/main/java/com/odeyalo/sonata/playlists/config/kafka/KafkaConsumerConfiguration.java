package com.odeyalo.sonata.playlists.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.PlaylistImagesGeneratedEvent;
import com.odeyalo.sonata.suite.brokers.events.playlist.gen.payload.GenerativePlaylistEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.modules(
                new JavaTimeModule(),
                new ParameterNamesModule()
        );
    }

    @Bean
    public ReceiverOptions<String, PlaylistImagesGeneratedEvent> generatedPlaylistReceiverOptions(ObjectMapper objectMapper) {
        final Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "generated-playlists-consumers");
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        final JsonDeserializer<PlaylistImagesGeneratedEvent> deserializer = new JsonDeserializer<>(GenerativePlaylistEvent.class, objectMapper);

        deserializer.setUseTypeHeaders(false);

        return ReceiverOptions.<String, PlaylistImagesGeneratedEvent>create(consumerProps)
                .withValueDeserializer(deserializer)
                .subscription(Collections.singleton("playlists.gen.images"));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, PlaylistImagesGeneratedEvent> reactiveKafkaConsumerTemplate(ReceiverOptions<String, PlaylistImagesGeneratedEvent> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
    }
}
