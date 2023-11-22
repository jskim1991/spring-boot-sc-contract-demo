package io.jay.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
        ids = "io.jay:server:+:stubs",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class ClientApplicationTests {

    @Autowired
    WebTestClient wtc;

    @StubRunnerPort("io.jay:server")
    int port;

    @Test
    void contractMatches() {
        var actual = wtc.get()
                .uri(String.format("http://localhost:%d/users", port))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(User.class)
                .returnResult()
                .getResponseBody();


        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).id()).isEqualTo(1);
        assertThat(actual.get(0).name()).isEqualTo("Jojo");
    }
}
