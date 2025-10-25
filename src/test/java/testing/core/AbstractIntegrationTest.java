package testing.core;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import testing.spring.AutoConfigureSonataStubs;
import testing.spring.autoconfigure.AutoConfigureSonataPlaylistHttpClient;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureSonataPlaylistHttpClient
@AutoConfigureSonataStubs
public abstract class AbstractIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.2"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://"
                + postgres.getHost() + ":" + postgres.getFirstMappedPort()
                + "/" + postgres.getDatabaseName());
        registry.add("spring.r2dbc.username", () -> postgres.getUsername());
        registry.add("spring.r2dbc.password", () -> postgres.getPassword());
        // flyway
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.user", () -> postgres.getUsername());
        registry.add("spring.flyway.password", () -> postgres.getPassword());
        registry.add("spring.flyway.url", () -> postgres.getJdbcUrl());
    }
}