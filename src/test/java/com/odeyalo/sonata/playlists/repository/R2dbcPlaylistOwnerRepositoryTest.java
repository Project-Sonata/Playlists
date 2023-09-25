package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.R2dbcPlaylistEntity;
import com.odeyalo.sonata.playlists.entity.R2dbcPlaylistOwnerEntity;
import com.odeyalo.sonata.playlists.repository.R2dbcPlaylistOwnerRepository;
import com.odeyalo.sonata.playlists.repository.support.R2dbcPlaylistRepositoryDelegate;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static testing.faker.PlaylistEntityFaker.createWithNoId;

@DataR2dbcTest
@ActiveProfiles("test")
class R2dbcPlaylistOwnerRepositoryTest {

    @Autowired
    R2dbcPlaylistRepositoryDelegate r2dbcPlaylistRepositoryDelegate;

    @Autowired
    R2dbcPlaylistOwnerRepository r2DbcPlaylistOwnerRepository;

    @Test
    void findByPublicId() {
        R2dbcPlaylistEntity saved = saveR2dbcPlaylistEntity();

        assertThat(saved).isNotNull();

        R2dbcPlaylistEntity found = r2dbcPlaylistRepositoryDelegate.findByPublicId(saved.getPublicId()).block();

        assertThat(found).isNotNull();
        assertThat(found.getPublicId()).isEqualTo(saved.getPublicId());
    }

    @Test
    void findByNotExistingPublicId_AndExpectNull() {
        R2dbcPlaylistEntity found = r2dbcPlaylistRepositoryDelegate.findByPublicId("not_exist").block();

        assertThat(found).isNull();;
    }

    @NotNull
    private R2dbcPlaylistEntity saveR2dbcPlaylistEntity() {
        R2dbcPlaylistEntity toSave = getR2dbcPlaylistEntity();

        return requireNonNull(r2dbcPlaylistRepositoryDelegate.save(toSave).block(), "Expected not null value not be saved!");
    }

    @NotNull
    private R2dbcPlaylistEntity getR2dbcPlaylistEntity() {
        R2dbcPlaylistEntity toSave = createWithNoId().asR2dbcEntity();

        R2dbcPlaylistOwnerEntity savedOwner = savePlaylistOwner(toSave);

        toSave.setPlaylistOwnerId(savedOwner.getId());

        return toSave;
    }

    @NotNull
    private R2dbcPlaylistOwnerEntity savePlaylistOwner(R2dbcPlaylistEntity toSave) {
        R2dbcPlaylistOwnerEntity owner = R2dbcPlaylistOwnerEntity.builder()
                .displayName(toSave.getPlaylistOwner().getDisplayName())
                .publicId(toSave.getPlaylistOwner().getPublicId())
                .build();

        return requireNonNull(r2DbcPlaylistOwnerRepository.save(owner).block(), "Expected not null value to be saved");
    }
}