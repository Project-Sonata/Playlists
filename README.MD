## Sonata Playlist

Sonata playlist is used to create and store user's playlist.

Playlists are containers for tracks and other items that can be played. By creating a playlist,
a user can specify a subset of tracks

## Endpoints

Sonata Playlist provides REST API to create, delete and manage playlists.

### List of available endpoints:

- [Get playlist](docs/Get_playlist_by_ID.MD)
- [Create playlist](docs/Create_Playlist.MD)
- [Set playlist cover image](docs/Set_playlist_cover_image.MD)
- [Fetch playlist cover image](docs/Get_playlist_cover_image.MD)
- [Change playlist details](docs/Change_playlist_details.MD)
- [Add items to playlist](docs/Add_item_to_playlist.MD)
- [Get items of the playlist](docs/Get_playlist_items.MD)

## Build locally


Set required env variables
```
export GH_USERNAME=username
export GH_ACCESS_TOKEN=access_token
```

Build image
```
docker build --secret id=github.username,env=GH_USERNAME --secret id=github.token,env=GH_ACCESS_TOKEN -t "playlists" .
```

Run container
```
docker run -e DATABASE_CONNECTION_URL=r2dbc:postgresql://playlists-postgres:5432/postgres -e DATABASE_MIGRATION_CONNECTION_URL=jdbc:postgresql://playlists-postgres:5432/postgres -e DATABASE_PASSWORD=root -e DATABASE_USERNAME=postgres --name "playlists.local" --network playlists  -d playlists:latest
```
