package com.odeyalo.sonata.playlists.repository.r2dbc;

import com.odeyalo.sonata.playlists.entity.ItemEntity;
import com.odeyalo.sonata.playlists.repository.r2dbc.delegate.R2dbcItemRepositoryDelegate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import testing.ItemEntityFaker;

@DataR2dbcTest
@ActiveProfiles("test")
@Import(R2dbcItemRepositoryTest.Configuration.class)
class R2dbcItemRepositoryTest {

    @Autowired
    R2dbcItemRepository testable;

    @AfterEach
    void clear() {
        testable.deleteAll().block();
    }

    @Test
    void shouldSaveItemToDatabase() {
        ItemEntity itemEntity = ItemEntityFaker.createWithoutId().get();

        insertItems(itemEntity);

        testable.findById(itemEntity.getId())
                .as(StepVerifier::create)
                .expectNext(itemEntity)
                .verifyComplete();
    }

    @Test
    void shouldFindItemByPublicId() {
        ItemEntity itemEntity = ItemEntityFaker.createWithoutId().get();

        insertItems(itemEntity);

        testable.findByPublicId(itemEntity.getPublicId())
                .as(StepVerifier::create)
                .expectNext(itemEntity)
                .verifyComplete();
    }

    @Test
    void shouldFindItemByContextUri() {
        ItemEntity itemEntity = ItemEntityFaker.createWithoutId().get();

        insertItems(itemEntity);

        testable.findByContextUri(itemEntity.getContextUri())
                .as(StepVerifier::create)
                .expectNext(itemEntity)
                .verifyComplete();
    }

    @Test
    void shouldRemoveItemByIdIfExist() {
        // then
        ItemEntity itemEntity = ItemEntityFaker.createWithoutId().get();

        insertItems(itemEntity);
        // when
        testable.removeById(itemEntity.getId())
                .as(StepVerifier::create)
                .verifyComplete();
        // then
        testable.findByContextUri(itemEntity.getContextUri())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void shouldRemoveNothingIfItemDoesNotExist() {
        // then
        ItemEntity itemEntity = ItemEntityFaker.createWithoutId().get();

        insertItems(itemEntity);
        // when
        testable.removeById(-10L)
                .as(StepVerifier::create)
                .verifyComplete();
        // then
        testable.findByContextUri(itemEntity.getContextUri())
                .as(StepVerifier::create)
                .expectNext(itemEntity)
                .verifyComplete();
    }

    private void insertItems(ItemEntity... itemEntities) {
        testable.saveAll(itemEntities)
                .as(StepVerifier::create)
                .expectNextCount(itemEntities.length)
                .verifyComplete();
    }

    @TestConfiguration
    static class Configuration {

        @Bean
        public R2dbcItemRepository r2dbcItemRepository(R2dbcItemRepositoryDelegate delegate) {
            return new R2dbcItemRepository(delegate);
        }
    }
}