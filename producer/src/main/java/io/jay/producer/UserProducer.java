package io.jay.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProducer {

    private final KafkaTemplate<?, ?> kafkaTemplate;
    private final ObjectMapper mapper;

    @SneakyThrows
    public void publish(String newUser) {
        UserCreatedEvent payload = UserCreatedEvent.user(newUser);
        var message = MessageBuilder
                .withPayload(mapper.writeValueAsString(payload))
                .setHeader(KafkaHeaders.TOPIC, "users")
                .build();

        kafkaTemplate.send(message);
//        kafkaTemplate.send("users", message);
    }

}
