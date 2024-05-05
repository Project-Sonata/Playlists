### Change Playlist Details

Change a playlist's name, description and public/private state. (The user must own the playlist.)

#### Request

PATCH /playlist/{playlistId}

Request parameters:

- playlistId - ID of the playlist to fetch

- Content Type: application/json

Body:

```
- name - OPTIONAL, a new name of the playlist
- description - OPTIONAL, a new description of the playlist
- playlistType - OPTIONAL, a new playlist type, PUBLIC or PRIVATE values are supported
```

Example of request:

```http request
PATCH /playlist/S0uJTfunR67nKJ2DusSgUO
```

```json
{
  "description": "Here is my new and cool description!"
}
```

#### Response

Status:

- 204 NO CONTENT, playlist details have been updated
- 422 UNPROCESSABLE ENTITY, a playlist with a given ID does not exist or current user does not own this playlist
- 401 UNAUTHORIZED, missed or expired access token

See the [tests](/src/test/java/com/odeyalo/sonata/playlists/controller/PartialPlaylistUpdateEndpointTest.java) for this
endpoint for further info
