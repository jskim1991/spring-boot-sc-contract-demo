package io.jay.producer.contracts.setup;

import io.jay.producer.UserCreatedEvent;
import io.jay.producer.UserProducer;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(BaseTest.TestConfig.class)
@AutoConfigureMessageVerifier
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseTest {

    @Autowired
    UserProducer producer;

//    @Autowired
//    KafkaTemplate kafkaTemplate;

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    void createUser() {
//        kafkaTemplate.send(MessageBuilder)
        producer.publish("Jojo");
    }

    @EnableKafka
    @Configuration
    static class TestConfig {
        @Bean
        KafkaEventVerifier verifier() {
            return new KafkaEventVerifier();
        }
    }

    static class KafkaEventVerifier implements MessageVerifierReceiver<Message<?>> {

        Map<String, BlockingQueue<Message<?>>> broker = new ConcurrentHashMap<>();

        @KafkaListener(topics = "users", groupId = "test")
        void consume(Message<?> event) {
            String topic = event.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class);
            broker.putIfAbsent(topic, new ArrayBlockingQueue<>(1));
            var queue = broker.get(topic);
            queue.add(event);
        }

        @SneakyThrows
        @Override
        public Message<?> receive(String destination, long timeout, TimeUnit timeUnit, @Nullable YamlContract contract) {
            broker.putIfAbsent(destination, new ArrayBlockingQueue<>(1));
            var queue = broker.get(destination);
            return queue.poll(timeout, timeUnit);
        }

        @Override
        public Message<?> receive(String destination, YamlContract contract) {
            return receive(destination, 5, TimeUnit.SECONDS, contract);
        }
    }
}
