
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

See the [tests](/src/test/java/com/odeyalo/sonata/playlists/controller/AddItemToPlaylistEndpointTest.java) for this
endpoint

