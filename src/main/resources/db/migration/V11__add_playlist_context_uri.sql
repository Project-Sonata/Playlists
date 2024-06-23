ALTER TABLE playlists ADD COLUMN context_uri varchar(50) UNIQUE;

UPDATE playlists SET context_uri=CONCAT('sonata:playlist:', public_id);

ALTER TABLE playlists ALTER COLUMN context_uri SET NOT NULL