package io.jay.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@SpringBootApplication
@RequiredArgsConstructor
public class ConsumerApplication {

	private final ConsumerService service;

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

//	@Bean
//	public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
//			ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
//			ConsumerFactory<Object, Object> kafkaConsumerFactory,
//			KafkaTemplate<Object, Object> kafkaTemplate) {
//		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//		configurer.configure(factory, kafkaConsumerFactory);
//		return factory;
//	}

	@KafkaListener(topics = "users", groupId = "consumer-service")
	public void listen(String payload) {
		System.out.println("** " + payload);
		service.handle();
	}
}