CREATE TABLE playlist_images (
    id SERIAL PRIMARY KEY,
    playlist_id BIGINT NOT NULL,
    image_id BIGINT NOT NULL
);

ALTER TABLE playlist_images ADD CONSTRAINT playlist_id_pk FOREIGN KEY (playlist_id)
    REFERENCES playlists(playlist_id) ON DELETE CASCADE;

ALTER TABLE playlist_images ADD CONSTRAINT playlist_image_pk FOREIGN KEY (image_id)
    REFERENCES images(id) ON DELETE CASCADE;