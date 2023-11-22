.PHONY: server
server:
	cd server && ./mvnw clean install

.PHONY: client
client:
	cd client && ./mvnw clean install