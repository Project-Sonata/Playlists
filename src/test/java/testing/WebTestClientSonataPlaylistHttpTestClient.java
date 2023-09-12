package testing;

import com.odeyalo.sonata.playlists.dto.CreatePlaylistRequest;
import com.odeyalo.sonata.playlists.dto.PlaylistDto;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * SonataPlaylistHttpTestClient that uses WebTestClient
 */
public class WebTestClientSonataPlaylistHttpTestClient implements SonataPlaylistHttpTestClient {

    private final WebTestClient webTestClient;

    public WebTestClientSonataPlaylistHttpTestClient(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Override
    public PlaylistDto fetchPlaylist(String authorizationHeader, String playlistId) {
        return webTestClient.get()
                .uri("/playlist/{playlistId}", playlistId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .exchange()
                .expectBody(PlaylistDto.class).returnResult().getResponseBody();
    }

    @Override
    public PlaylistDto createPlaylist(String authorizationHeader, String userId, CreatePlaylistRequest body) {
        return webTestClient.post()
                .uri("/playlist", userId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .bodyValue(body)
                .exchange().expectBody(PlaylistDto.class).returnResult().getResponseBody();
    }
}
