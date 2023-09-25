CREATE TABLE playlist_owner
(
   id SERIAL PRIMARY KEY,
   public_id varchar(255) NOT NULL,
   display_name varchar(255),
   entity_type varchar(30) NOT NULL
);

ALTER TABLE playlist_owner ADD CONSTRAINT
    entity_type_check_constraint CHECK (entity_type IN ('USER'))