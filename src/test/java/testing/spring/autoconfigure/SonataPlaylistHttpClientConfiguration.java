package testing.spring.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import testing.SonataPlaylistHttpTestClient;
import testing.WebTestClientSonataPlaylistHttpTestClient;

public class SonataPlaylistHttpClientConfiguration {

    @Bean
    public SonataPlaylistHttpTestClient sonataPlaylistHttpClient(WebTestClient webTestClient) {
        return new WebTestClientSonataPlaylistHttpTestClient(webTestClient);
    }
}