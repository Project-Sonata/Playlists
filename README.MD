## Sonata Playlist

Sonata playlist is used to create and store user's playlist.

Playlists are containers for tracks and other items that can be played. By creating a playlist,
a user can specify a subset of tracks

## Endpoints

Sonata Playlist provides REST API to create, delete and manage playlists.

### List of available endpoints:

- [Get playlist](#get-playlist)
- [Create playlist](#create-playlist)
- [Fetch playlist cover image](#Fetch-playlist-cover-image)
- [Change playlist details](#Change-playlist-details)
- [Add items to playlist](#add-items-to-playlist)

### Get Playlist

Get a spotify playlist by its ID

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

Example of response:

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

See the [tests](src/test/java/com/odeyalo/sonata/playlists/controller/FetchPlaylistEndpointTest.java) for this endpoint

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

Example of response:

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

See the [tests](src/test/java/com/odeyalo/sonata/playlists/controller/CreatePlaylistEndpointTest.java) for this endpoint

### Fetch-playlist-cover-image

Get the current image associated with a specific playlist.

#### Request

GET /playlist/{playlistId}/images
Request parameters:

- playlistId - ID of the playlist to fetch image associated with

#### Response

A set of images, in most cases single element is returned

Body:

Array of ImageObject.

ImageObject:

- url - string, NOT NULL, ALWAYS present in response. A url to access the playlist cover image
- width - integer, NULLABLE, the image width in pixels
- height - integer, NULLABLE, the image height in pixels

Example of response:

```json
{
  "images": [
    {
      "url": "https://i.pinimg.com/564x/9b/34/1a/9b341ae067ce9e8fc554538ff669b089.jpg",
      "width": null,
      "height": null
    }
  ],
  "empty": false
}
```

Note, that CDN URI can be different from listed above

See the [tests](src/test/java/com/odeyalo/sonata/playlists/controller/FetchPlaylistCoverImageEndpointTest.java) for this
endpoint for further info


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

See the [tests](src/test/java/com/odeyalo/sonata/playlists/controller/PartialPlaylistUpdateEndpointTest.java) for this
endpoint for further info


### Add items to playlist

Add one or more items to the playlist

#### Request

POST /playlist/{playlistId}/items

Request parameters:

- playlistId - ID of the playlist to fetch
- uris - a comma-separated Sonata context uris of the tracks. Example: sonata:track:4A2YFhVf1CosFA443ibQhH

Example request:

```http request
POST /playlist/S0uJTfunR67nKJ2DusSgUO/items?uris=sonata:track:4A2YFhVf1CosFA443ibQhH,sonata:track:37i9dQZF1E38YGQWW3GHOP
Authorization: Bearer access_token
```

#### Response

Returns a status indicating result

Status:

- 201 CREATED - items have been added to the playlist successfully
- 400 BAD REQUEST - a playlist with the given ID does not exist or item by context uri does not exist either
- 401 UNAUTHORIZED - request is unauthorized and cannot be performed, you need to obtain or refresh access token

See the [tests](src/test/java/com/odeyalo/sonata/playlists/controller/AddItemToPlaylistEndpointTest.java) for this endpoint
