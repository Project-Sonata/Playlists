CREATE TABLE playlist_items
(
    id          SERIAL PRIMARY KEY,
    added_at    timestamp                                          NOT NULL,
    added_by    BIGINT REFERENCES playlist_collaborators (id) NOT NULL,
    item        BIGINT REFERENCES items (id)                  NOT NULL,
--     A zero-based index  that indicate the position of this item in playlist
    index       integer                                       not null,
--     We know that public playlist ID is always unique and can't be changed.
--     Use public ID instead of internal primary key for performance reasons,
--     to skip non-mandatory JOIN and SELECT queries, and additional INSERT as well
    playlist_id VARCHAR(255)                                  NOT NULL
);
