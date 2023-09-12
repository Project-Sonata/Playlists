package testing.spring.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to autoconfigure the SonataPlaylistHttpClient
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import(SonataPlaylistHttpClientConfiguration.class)
public @interface AutoConfigureSonataPlaylistHttpClient {
}