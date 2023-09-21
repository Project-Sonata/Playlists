-- Multiple images can't have the same URL even if they have different width/height
ALTER TABLE images ADD CONSTRAINT unique_image_url UNIQUE (url)