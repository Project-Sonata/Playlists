CREATE TABLE playlist_collaborators
(
    id           SERIAL PRIMARY KEY,
    public_id    varchar(300) UNIQUE NOT NULL,
    display_name varchar(300) NOT NULL,
    entity_type  varchar(15)  NOT NULL DEFAULT 'USER',
    context_uri  varchar(50)  UNIQUE NOT NULL
);


ALTER TABLE playlist_collaborators ADD CONSTRAINT entity_type_assert
        CHECK ( entity_type IN ('USER') )
