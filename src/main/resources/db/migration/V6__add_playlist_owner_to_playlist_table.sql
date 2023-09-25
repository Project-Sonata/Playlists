-- Every playlist should have owner, so ignore the warning
ALTER TABLE playlists ADD COLUMN owner_id BIGINT NOT NULL;


ALTER TABLE playlists ADD CONSTRAINT owner_id_fk FOREIGN KEY (owner_id)
    REFERENCES playlist_owner(id);
