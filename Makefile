.PHONY: server
server:
	cd server && ./mvnw clean install

.PHONY: client
client:
	cd client && ./mvnw clean install

.PHONY: producer
producer:
	cd producer && ./mvnw clean install

.PHONY: consumer
consumer:
	cd consumer && ./mvnw clean install