-- Store the items that can be saved to playlist
CREATE TABLE items (
  id SERIAL PRIMARY KEY,
  public_id varchar(300) NOT NULL UNIQUE,
  context_uri varchar(300) NOT NULL UNIQUE
);