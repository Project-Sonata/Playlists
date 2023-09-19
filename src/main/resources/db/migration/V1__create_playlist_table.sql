--  SQL script to create playlist table with postgres dialect
CREATE TABLE playlists (
    playlist_id SERIAL PRIMARY KEY,
    public_id VARCHAR(22) UNIQUE NOT NULL,
    playlist_name VARCHAR(150) NOT NULL,
    playlist_description VARCHAR (400),
    playlist_type VARCHAR(25) NOT NULL DEFAULT 'PRIVATE'
);

ALTER TABLE playlists ADD CONSTRAINT playlists
    CHECK ( playlist_type IN ('PRIVATE', 'PUBLIC', 'COLLABORATIVE') )