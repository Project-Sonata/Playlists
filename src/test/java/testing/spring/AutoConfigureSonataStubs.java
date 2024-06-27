package testing.spring;

import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.REMOTE;

@Retention(RUNTIME)
@Target(TYPE)
@AutoConfigureStubRunner(stubsMode = REMOTE,
        repositoryRoot = "${spring.contracts.repository.root}",
        ids = "com.odeyalo.sonata:authorization:+")
public @interface AutoConfigureSonataStubs {}
