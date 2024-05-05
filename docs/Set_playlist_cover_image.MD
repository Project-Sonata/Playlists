
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

See the [tests](/src/test/java/com/odeyalo/sonata/playlists/controller/PlaylistImageUploadEndpointTest.java) for this endpoint
