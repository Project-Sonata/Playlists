-- Executes every time when row from the playlist_image has been deleted
-- To clear the image that unused
create or replace function delete_image() returns trigger as
$$BEGIN
    delete from images where id=OLD.image_id;
    RETURN OLD;
END;$$ language plpgsql;

CREATE TRIGGER cascade_delete_playlist_image AFTER DELETE ON playlist_images FOR EACH ROW
EXECUTE PROCEDURE delete_image()
