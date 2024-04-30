package com.odeyalo.sonata.playlists.repository.r2dbc.delegate;

import com.odeyalo.sonata.playlists.entity.ItemEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import testing.ItemEntityFaker;

import java.util.Arrays;

@DataR2dbcTest
@ActiveProfiles("test")
class R2dbcItemRepositoryDelegateTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    R2dbcItemRepositoryDelegate testable;

    @AfterEach
    void clear() {
        testable.deleteAll().block();
    }

    @Test
    void shouldFindExistingEntityByContextUri() {
        ItemEntity itemEntity = ItemEntityFaker.createWithoutId().get();

        insertItems(itemEntity);

        testable.findByContextUri(itemEntity.getContextUri())
                .as(StepVerifier::create)
                .expectNext(itemEntity)
                .verifyComplete();
    }

    @Test
    void shouldFindExistingEntityByPublicId() {
        ItemEntity itemEntity = ItemEntityFaker.createWithoutId().get();

        insertItems(itemEntity);

        testable.findByPublicId(itemEntity.getPublicId())
                .as(StepVerifier::create)
                .expectNext(itemEntity)
                .verifyComplete();
    }

    @Test
    void shouldRemoveExistingEntity() {
        ItemEntity itemEntity = ItemEntityFaker.createWithoutId().get();

        insertItems(itemEntity);

        testable.removeById(itemEntity.getId())
                .as(StepVerifier::create)
                .verifyComplete();

        testable.findById(itemEntity.getId())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    private void insertItems(ItemEntity... itemEntities) {
        testable.saveAll(Arrays.asList(itemEntities))
                .as(StepVerifier::create)
                .expectNextCount(itemEntities.length)
                .verifyComplete();
    }
}