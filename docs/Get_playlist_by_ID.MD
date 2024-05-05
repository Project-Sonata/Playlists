### Get Playlist

Get a Sonata playlist by its ID

#### Request

GET /playlist/{playlistId}

Request parameters:

- playlistId - ID of the playlist to fetch

Example request:

```http request
GET /playlist/S0uJTfunR67nKJ2DusSgUO
Authorization: Bearer access_token
```

#### Response

Returns a found playlist

Status:

- 200 - OK, playlist has been found and returned.
- 204 - No Content, playlist with a given ID does not exist OR currently authorized user DOES NOT have permission to
  access this playlist.
- 401 - UNAUTHORIZED, request is unauthorized and cannot be performed, you need to obtain or refresh access token

Body:

- id - id of the playlist
- name - name of the playlist
- description - description of the playlist
- playlist_type - type of the playlist, one of PRIVATE or PUBLIC
- type - type of the entity, always PLAYLIST
- images - array of cover images for this playlist
- owner - information about user who owns the given playlist


<details>
<summary>Example of response</summary>

```json
{
  "id": "S0uJTfunR67nKJ2DusSgUO",
  "name": "Your 2023",
  "description": "Songs you loved the most in 2023!",
  "playlist_type": "PUBLIC",
  "type": "PLAYLIST",
  "images": {
    "images": [],
    "empty": true
  },
  "owner": {
    "id": "123",
    "display_name": null,
    "type": "USER"
  }
}
```
</details>

See the [tests](/src/test/java/com/odeyalo/sonata/playlists/controller/FetchPlaylistEndpointTest.java) for this endpoint
