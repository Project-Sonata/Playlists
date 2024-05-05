
### Create Playlist

Create a playlist for the currently authenticated user. The playlist will be empty until track is added.

#### Request

POST /playlist

- Content Type: application/json

Body:

```
Supports free form of parameters:
- name - REQUIRED. the name of the playlist
- description - OPTIONAL, description of the playlist, default is NULL
- type - OPTIONAL, type of the playlist, one of PRIVATE or PUBLIC, default is PRIVATE 
```

Example of request:

```json
{
  "name": "Lofi Chill",
  "description": "LoFI songs to chill and think",
  "type": "PUBLIC"
}
```

#### Response

Returns a created playlist

Status:

- 201 - CREATED, playlist has been created and saved.
- 400 - BAD REQUEST, request body contains invalid data, required fields are not set, field has invalid value, etc.
- 401 - UNAUTHORIZED, request is unauthorized and cannot be performed, you need to obtain or refresh access token

Body:

- id - id of the created playlist, can be used to fetch playlist
- name - name of the playlist, equal to provided in request body
- description - description of the playlist, equal to provided in request body or NULL if was not provided
- playlist_type - type of the playlist, equal to provided in request body or default value
- type - type of the entity, always PLAYLIST
- images - array of images for this playlist, by default new playlist does not have any cover image, so empty array will
  be returned
- owner - information about user who owns the given playlist

<details> 

<summary> Example of response:</summary>


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

See the [tests](/src/test/java/com/odeyalo/sonata/playlists/controller/CreatePlaylistEndpointTest.java) for this endpoint
