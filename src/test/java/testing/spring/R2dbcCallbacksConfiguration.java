package testing.spring;

import com.odeyalo.sonata.playlists.repository.r2dbc.callback.read.PlaylistImagesAssociationAfterConvertCallback;
import com.odeyalo.sonata.playlists.repository.r2dbc.callback.read.PlaylistOwnerAssociationAfterConvertCallback;
import com.odeyalo.sonata.playlists.repository.r2dbc.callback.write.AssociateItemWithPlaylistItemOnMissingBeforeConvertCallback;
import com.odeyalo.sonata.playlists.repository.r2dbc.callback.write.SavePlaylistCollaboratorOnMissingBeforeConvertCallback;
import com.odeyalo.sonata.playlists.repository.r2dbc.callback.write.SavePlaylistImageOnMissingAfterSaveCallback;
import com.odeyalo.sonata.playlists.repository.r2dbc.callback.write.SavePlaylistOwnerOnMissingBeforeConvertCallback;
import org.springframework.context.annotation.Import;

@Import({
        SavePlaylistOwnerOnMissingBeforeConvertCallback.class,
        PlaylistOwnerAssociationAfterConvertCallback.class,
        SavePlaylistImageOnMissingAfterSaveCallback.class,
        PlaylistImagesAssociationAfterConvertCallback.class,
        SavePlaylistCollaboratorOnMissingBeforeConvertCallback.class,
        AssociateItemWithPlaylistItemOnMissingBeforeConvertCallback.class
})
public class R2dbcCallbacksConfiguration {
}
