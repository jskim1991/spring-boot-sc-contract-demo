package io.jay.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@SpringBootApplication
public class ClientApplication {

	@Bean
	UserClient userClient(WebClient.Builder builder) {
		var wc = builder.baseUrl("http://localhost:8080").build();
		var wca = WebClientAdapter.forClient(wc);
		return HttpServiceProxyFactory.builder()
				.clientAdapter(wca)
				.build()
				.createClient(UserClient.class);
	}


	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}
}

@RestController
@RequiredArgsConstructor
class ClientController {

	private final UserClient userClient;

	@GetMapping("/all")
	public List<User> fetchAllUsers() {
		return userClient.fetchUsers();
	}
}
