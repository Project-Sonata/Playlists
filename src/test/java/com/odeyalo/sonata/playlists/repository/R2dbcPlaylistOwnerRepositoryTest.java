package com.odeyalo.sonata.playlists.repository;

import com.odeyalo.sonata.playlists.entity.PlaylistEntity;
import com.odeyalo.sonata.playlists.entity.PlaylistOwnerEntity;
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
        PlaylistEntity saved = saveR2dbcPlaylistEntity();

        assertThat(saved).isNotNull();

        PlaylistEntity found = r2dbcPlaylistRepositoryDelegate.findByPublicId(saved.getPublicId()).block();

        assertThat(found).isNotNull();
        assertThat(found.getPublicId()).isEqualTo(saved.getPublicId());
    }

    @Test
    void findByNotExistingPublicId_AndExpectNull() {
        PlaylistEntity found = r2dbcPlaylistRepositoryDelegate.findByPublicId("not_exist").block();

        assertThat(found).isNull();;
    }

    @NotNull
    private PlaylistEntity saveR2dbcPlaylistEntity() {
        PlaylistEntity toSave = getR2dbcPlaylistEntity();

        return requireNonNull(r2dbcPlaylistRepositoryDelegate.save(toSave).block(), "Expected not null value not be saved!");
    }

    @NotNull
    private PlaylistEntity getR2dbcPlaylistEntity() {
        PlaylistEntity toSave = createWithNoId().asR2dbcEntity();

        PlaylistOwnerEntity savedOwner = savePlaylistOwner(toSave);

        toSave.setPlaylistOwnerId(savedOwner.getId());

        return toSave;
    }

    @NotNull
    private PlaylistOwnerEntity savePlaylistOwner(PlaylistEntity toSave) {
        PlaylistOwnerEntity owner = PlaylistOwnerEntity.builder()
                .displayName(toSave.getPlaylistOwner().getDisplayName())
                .publicId(toSave.getPlaylistOwner().getPublicId())
                .build();

        return requireNonNull(r2DbcPlaylistOwnerRepository.save(owner).block(), "Expected not null value to be saved");
    }
}