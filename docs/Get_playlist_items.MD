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

<details>
<summary><strong>item</strong> <p>Information about item such name, album, performers, etc.</p></summary>

- id - string, NOT NULL. Unique identifier for the track.
- name - string, NOT NULL. The name of the track.
- duration_ms - integer, NOT NULL. The duration of the track in milliseconds.
- explicit - boolean, NOT NULL. Indicates whether the track contains explicit content.
- track_number - integer, NOT NULL. The track number of the track on the disk. Zero-based index is used and for each disk index started from 0.
- disc_number - integer, NOT NULL. The disc number of the track within its album.

 <details>
    <summary>
    <strong>artists</strong>

  Information about the artists associated with this track
  </summary>

- empty - boolean, NOT NULL. Indicates if the array of items is empty.

    <details>
    <summary>
        <strong>items</strong>
        <p>Array of ArtistObject</p>
    </summary>
  
  - id - string, NOT NULL. Unique identifier for the artist.
  - name - string, NOT NULL. The name of the artist.
</details>

<details> 
<summary>album <p>Information about album associated with track</p></summary>
    
- id - string, NOT NULL. Unique identifier for the album.
- name - string, NOT NULL. The name of the album.
- album_type - string, NOT NULL. The type of album (e.g., "EPISODE").

- <details> <summary> artists <p> object. Information about the artists associated with the album. </p></summary>
      <details>
      <summary>
        items <p> array of ArtistObject</p>
      </summary>
  <ul>
    <li> id - string, NOT NULL. Unique identifier for the artist.</li>
    <li> name - string, NOT NULL. The name of the artist. </li>
  </ul>
      </details>
  
  - empty - boolean, NOT NULL. Indicates if the array of items is empty.
  </details>

- total_tracks - integer, NOT NULL. The total number of tracks on the album.

<details>

<summary> release_date <p> object. Information about the release date of the album.</p></summary>

- day - integer. NULLABLE. The day of the release date.
- month - integer. NULLABLE. The month of the release date.
- year - integer. The year of the release date.
- precision - string. The precision of the release date ("DAY", "MONTH", "YEAR").
</details>

<details>

<summary> images <p> object. Contains information about the images for this album </p> </summary>
        <details>
        <summary>
         images - array of ImageObject.
        </summary>

</details>- url - string, NOT NULL. A URL to access the image.
- width - integer, NULLABLE. The image width in pixels.
- height - integer, NULLABLE. The image height in pixels.
  - empty - boolean, NOT NULL. Indicates if the array of images is empty.
  </details>

</details>
</details>
</details>

- **type** - string, NOT NULL. The type of the item (e.g., "TRACK").

- **added_at** Indicates a time when the item was added to this playlist
  string, NOT NULL. The timestamp when the item was added. <h6> Example: 2024-05-03T14:40:18.932993Z </h6>

<details>
<summary>
<strong> added_by </strong>

<p> Information about the user, who added this item to the playlist </p> 
</summary>

- id - string, NOT NULL. Unique identifier for the user who added the item.
- display_name - string, NOT NULL. The display name of the user who added the item.
- type - string, NOT NULL. The type of the user (e.g., "USER").
- uri - string, NOT NULL. A URI to access information about the user.

</details>

<details>
<summary>Example of response</summary>

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

</details>

See the [tests](/src/test/java/com/odeyalo/sonata/playlists/controller/FetchPlaylistTracksEndpointTest.java) for this
endpoint for further info
