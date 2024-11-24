# Verve Assignment Project

This project is a Spring Boot application designed to handle unique request IDs, send them to Kafka, and log their counts periodically. It also integrates Redis for caching and high performance.

## Architecture Diagram

![Verve Group Design](https://github.com/user-attachments/assets/936f462b-fa5f-4ffc-a4f6-5f1849649841)

---

## **Features**
- REST API to handle incoming requests with unique IDs.
- Redis caching for fast access and de-duplication.
- Kafka integration to publish unique IDs.
- Logging of unique request counts every minute.
- Scalable to handle at least 10K RPS.

---

## **Requirements & Pre-requisites**
- Java 17 or higher
- Redis server running on port 6379
- Kafka server running on localhost 9092
- Docker (optional for containerized deployment)
