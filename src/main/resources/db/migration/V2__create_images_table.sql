-- Create table with images
CREATE TABLE images (
    id SERIAL PRIMARY KEY,
    url varchar(3000) NOT NULL,
    width INTEGER,
    height INTEGER
)