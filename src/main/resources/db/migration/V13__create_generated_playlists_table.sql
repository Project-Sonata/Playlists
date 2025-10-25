CREATE TABLE generated_playlists
(
    id            SERIAL PRIMARY KEY,
--     A public User ID for which this playlist was generated for
    generated_for VARCHAR(30)  NOT NULL,
--     We know that public playlist ID is always unique and can't be changed.
--     Use public ID instead of internal primary key for performance reasons,
--     to skip non-mandatory JOIN and SELECT queries, and additional INSERT as well
    playlist_id   VARCHAR(255) NOT NULL REFERENCES playlists(public_id),
    generated_at timestamp NOT NULL,
    playlist_type VARCHAR(100)
);
