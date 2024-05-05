### Fetch playlist cover image

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

- url - string, NOT NULL, ALWAYS present in response. Url to access the playlist cover image
- width - integer, NULLABLE, the image width in pixels
- height - integer, NULLABLE, the image height in pixels

<details>
<summary>Example of response</summary>


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
</details>

Note, that CDN URI can be different from listed above

See the [tests](/src/test/java/com/odeyalo/sonata/playlists/controller/FetchPlaylistCoverImageEndpointTest.java) for this
endpoint for further info
