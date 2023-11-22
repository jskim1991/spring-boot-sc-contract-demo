package io.jay.consumer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestConfig.class)
@AutoConfigureStubRunner(ids = "io.jay:producer:+:stubs", stubsMode = StubsMode.LOCAL)
@Testcontainers(disabledWithoutDocker = true)
class ConsumerApplicationTests {

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    static {
        // why is this needed...?
        kafka.start();
    }

    @Autowired
    StubTrigger stubTrigger;

    @SpyBean
    ConsumerService service;

    @Test
    void consumes() {
        stubTrigger.trigger("user-created-event");


        Awaitility.await().untilAsserted(() -> {
            verify(service).handle();
        });
    }
}

@TestConfiguration
@RequiredArgsConstructor
class TestConfig {

    private final KafkaTemplate<?, ?> kafkaTemplate;

    @Bean
    MessageVerifierSender<Message<?>> messageVerifierSender() {
        return new MessageVerifierSender<>() {
            @Override
            public void send(Message<?> message, String destination, @Nullable YamlContract contract) {
                kafkaTemplate.send(message);
            }

            @Override
            public <T> void send(T payload, Map<String, Object> headers, String destination, @Nullable YamlContract contract) {
                kafkaTemplate.send(MessageBuilder.
                        createMessage(payload, new MessageHeaders(Map.of(TOPIC, destination))));
            }
        };
    }

    @Bean
    public NewTopic topic() {
        return new NewTopic("users", 1, (short) 1);
    }
}



