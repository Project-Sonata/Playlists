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
- [Add items to playlist](#add-items-to-playlist)
- [Get items of the playlist](#get-playlist-items)

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

See the [tests](src/test/java/com/odeyalo/sonata/playlists/controller/AddItemToPlaylistEndpointTest.java) for this
endpoint


### Get playlist items

Get the items(tracks or episodes) associated with a specific playlist.

#### Request

GET /playlist/{playlistId}/items
Request parameters:

- playlistId - ID of the playlist to fetch image associated with

#### Response

A set of playlist items

Body:

Array of PlaylistItemObject.
Note, that the example below is for TRACK object only.

PlaylistItemObject:

**item:**

Information about item such name, album, performers, etc.

- id - string, NOT NULL. Unique identifier for the item.
- name - string, NOT NULL. The name of the item.
- duration_ms - integer, NOT NULL. The duration of the item in milliseconds.
- explicit - boolean, NOT NULL. Indicates whether the item contains explicit content.
- track_number - integer, NOT NULL. The track number of the item within its album.
- disc_number - integer, NOT NULL. The disc number of the item within its album.
- artists:
  - items - array of objects. Information about the artists associated with the item.
    - id - string, NOT NULL. Unique identifier for the artist.
    - name - string, NOT NULL. The name of the artist.
  - empty - boolean, NOT NULL. Indicates if the array of items is empty.
- album:
  - id - string, NOT NULL. Unique identifier for the album.
  - name - string, NOT NULL. The name of the album.
  - album_type - string, NOT NULL. The type of album (e.g., "EPISODE").
  - artists - object. Information about the artists associated with the album.
    - items - array of objects. Information about the artists associated with the album.
      - id - string, NOT NULL. Unique identifier for the artist.
      - name - string, NOT NULL. The name of the artist.
    - empty - boolean, NOT NULL. Indicates if the array of items is empty.
  - total_tracks - integer, NOT NULL. The total number of tracks on the album.
  - release_date - object. Information about the release date of the album.
    - day - integer. NULLABLE. The day of the release date.
    - month - integer. NULLABLE. The month of the release date.
    - year - integer. The year of the release date.
    - precision - string. The precision of the release date ("DAY", "MONTH", "YEAR").
  - images:
    - images - array of objects. Information about the images associated with the album.
      - url - string, NOT NULL. A URL to access the image.
      - width - integer, NULLABLE. The image width in pixels.
      - height - integer, NULLABLE. The image height in pixels.
    - empty - boolean, NOT NULL. Indicates if the array of images is empty.
- type - string, NOT NULL. The type of the item (e.g., "TRACK").

**added_at:**

Indicates a time when the item was added to this playlist
- string, NOT NULL. The timestamp when the item was added.

**added_by:**

Information about the user, who added this item to the playlist
- id - string, NOT NULL. Unique identifier for the user who added the item.
- display_name - string, NOT NULL. The display name of the user who added the item.
- type - string, NOT NULL. The type of the user (e.g., "USER").
- uri - string, NOT NULL. A URI to access information about the user.
  Example of response:

```json
{
  "items": [
    {
      "item": {
        "id": "04nJixim5a0MAz3PGiVID1",
        "name": "Something",
        "duration_ms": 790024,
        "explicit": true,
        "track_number": 1,
        "disc_number": 1,
        "artists": {
          "items": [
            {
              "id": "fea3aFas3f",
              "name": "Alex G"
            }
          ],
          "empty": false
        },
        "album": {
          "id": "a3la23bu91m",
          "name": "Sarah",
          "album_type": "EPISODE",
          "artists": {
            "items": [
              {
                "id": "fea3aFas3f",
                "name": "Alex G"
              }
            ],
            "empty": false
          },
          "total_tracks": 2,
          "release_date": {
            "day": 21,
            "month": 2,
            "year": 2022,
            "precision": "DAY"
          },
          "images": {
            "images": [
              {
                "url": "https://i.pinimg.com/564x/02/27/b0/0227b0ff5ff93d6429d2c80d402cea43.jpg",
                "width": null,
                "height": null
              },
              {
                "url": "https://i.pinimg.com/564x/db/ff/9f/dbff9f74ef082687010dacc455eac7ac.jpg",
                "width": null,
                "height": null
              },
              {
                "url": "https://i.pinimg.com/564x/77/59/e6/7759e6183f1853857180149f74ea7777.jpg",
                "width": 300,
                "height": 300
              }
            ],
            "empty": false
          }
        },
        "type": "TRACK"
      },
      "added_at": "2024-05-03T14:40:18.932993Z",
      "added_by": {
        "id": "123",
        "display_name": "123",
        "type": "USER",
        "uri": "sonata:user:123"
      }
    }
  ]
}
```

See the [tests](src/test/java/com/odeyalo/sonata/playlists/controller/FetchPlaylistTracksEndpointTest.java) for this
endpoint for further info


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
