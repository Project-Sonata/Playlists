package com.odeyalo.sonata.playlists.repository.support;

import com.odeyalo.sonata.playlists.entity.R2dbcPlaylistEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static testing.faker.PlaylistEntityFaker.createWithNoId;

@DataR2dbcTest
@ActiveProfiles("test")
class R2dbcPlaylistRepositoryDelegateTest {

    @Autowired
    R2dbcPlaylistRepositoryDelegate r2dbcPlaylistRepositoryDelegate;

    @Test
    void findByPublicId() {
        R2dbcPlaylistEntity saved = r2dbcPlaylistRepositoryDelegate
                .save(createWithNoId().asR2dbcEntity()).block();

        R2dbcPlaylistEntity found = r2dbcPlaylistRepositoryDelegate.findByPublicId(saved.getPublicId()).block();

        assertThat(found).isEqualTo(saved);
    }

    @Test
    void findByNotExistingPublicId_AndExpectNull() {
        R2dbcPlaylistEntity found = r2dbcPlaylistRepositoryDelegate.findByPublicId("not_exist").block();

        assertThat(found).isNull();;
    }
}