package io.jay.consumer;

import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    void handle() {
        System.out.println("handling...");
    }
}

