package io.jay.producer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String eventId;
    private String name;
    private String role;

    public static UserCreatedEvent user(String name) {
        return new UserCreatedEvent(UUID.randomUUID().toString(), name, "user");
    }
}
