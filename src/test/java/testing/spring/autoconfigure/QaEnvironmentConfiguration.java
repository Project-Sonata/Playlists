package testing.spring.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import testing.QaControllerOperations;
import testing.WebTestClientQaControllerOperations;

/**
 * Configure beans for QA environment
 */
public class QaEnvironmentConfiguration {

    @Bean
    public QaControllerOperations qaControllerOperations(WebTestClient webTestClient) {
        return new WebTestClientQaControllerOperations(webTestClient);
    }
}
