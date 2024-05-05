## Sonata Playlist

Sonata playlist is used to create and store user's playlist.

Playlists are containers for tracks and other items that can be played. By creating a playlist,
a user can specify a subset of tracks

## Endpoints

Sonata Playlist provides REST API to create, delete and manage playlists.

### List of available endpoints:

- [Get playlist](docs/Get_playlist_by_ID.MD)
- [Create playlist](docs/Create_Playlist.MD)
- [Set playlist cover image](#Set-playlist-cover-image)
- [Fetch playlist cover image](docs/Get_playlist_cover_image.MD)
- [Change playlist details](docs/Change_playlist_details.MD)
- [Add items to playlist](docs/Add_item_to_playlist.MD)
- [Get items of the playlist](docs/Get_playlist_items.MD)

### Set playlist cover image

Set the cover image for specific playlist

#### Request
POST /playlist/{playlistId}/images
Request parameters:

- playlistId - ID of the playlist to set cover image for

Content type: multipart/form-data

Body:

image - image file that should be used as cover image. Supported formats are:
- png
- jpg
- jpeg
- gif


#### Response

Returns a status indicating result of the operation

Status:

- 202 - ACCEPTED, command has been received and image will be updated soon.
- 400 - BAD REQUEST, request contains invalid file(not supported file type, etc.), file has greater size than supported, malformed file is received, etc
- 401 - UNAUTHORIZED, request is unauthorized and cannot be performed, you need to obtain or refresh access token

See the [tests](src/test/java/com/odeyalo/sonata/playlists/controller/PlaylistImageUploadEndpointTest.java) for this endpoint
