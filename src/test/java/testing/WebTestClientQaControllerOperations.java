package testing;

import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * QaControllerOperations impl that uses WebTestClient
 */
public class WebTestClientQaControllerOperations implements QaControllerOperations {
    private final WebTestClient webTestClient;

    public WebTestClientQaControllerOperations(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Override
    public void clearPlaylists() {
        webTestClient.delete().uri("/qa/playlist/all").exchange();
    }
}
