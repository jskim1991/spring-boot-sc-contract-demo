package io.jay.client;

import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface UserClient {

    @GetExchange("/users")
    List<User> fetchUsers();
}
