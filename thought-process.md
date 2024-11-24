# Thought Process

This document outlines the thought process behind building the Verve application, addressing scalability, unique request counting, deduplication using Redis, and periodic reporting via Kafka.

---

## 1. Understand the Requirements

### Key Requirements
- Build a REST API that accepts requests and ensures the uniqueness of requests based on an `id` parameter.
- Use Redis for deduplication to handle distributed environments.
- Log the count of unique requests received every minute.
- Send the unique count to a Kafka topic.
- Ensure the system can handle at least **10,000 RPS**.

---

## 2. System Design

### High-Level Architecture
1. **REST Controller**:
    - Handles incoming requests (`/accept`) with an `id` and optional `endpoint`.
    - Validates uniqueness using Redis.
    - Logs the results and processes the requests.

2. **Redis for Deduplication**:
    - Redis Set is used to store unique IDs temporarily, ensuring fast operations (`O(1)` for add/check).
    - The data in Redis is cleared every minute after processing to ensure fresh counts.

3. **Kafka for Reporting**:
    - Kafka is used to send the count of unique requests every minute to a specified topic.
    - This enables decoupling of request processing from downstream analytics.

4. **Periodic Scheduler**:
    - A scheduled task runs every minute to:
        - Retrieve the count of unique IDs from Redis.
        - Send the count to Kafka.
        - Clear Redis data to prepare for the next minute.

> Below attached Architecture Diagram shows that the Periodic Scheduler can be separated in a different service.
![Verve Group Design](https://github.com/user-attachments/assets/bca63fa1-092b-4f27-be3f-10134d761ab1)
---

## 3. Challenges and Solutions

### Challenge 1: Deduplication
- **Problem**: How to ensure that the `id` is unique across multiple instances of the application?
- **Solution**: Use a distributed store like Redis.
    - Redis’ `SET` data type ensures uniqueness and allows fast `add` and `check` operations.
    - Store all incoming `id`s in a Redis Set (e.g., `uniqueIds`).
    - Use `opsForSet().size()` to calculate the count of unique `id`s.

### Challenge 2: Scalability to Handle 10,000 RPS
- **Problem**: How to ensure the application can handle high RPS without slowing down?
- **Solution**:
    - Optimize Redis operations by batching requests when feasible.
    - Use `LettuceConnectionFactory` for efficient Redis connections.
    - Use Kafka to offload reporting and asynchronous tasks.
    - Add proper Docker configuration to support resource allocation and JVM tuning:
      ```bash
      java -Xms1g -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=100
      ```

### Challenge 3: Reporting Unique Count
- **Problem**: How to periodically send the unique count of requests to Kafka?
- **Solution**:
    - Use Spring's `@Scheduled` annotation to trigger a task every minute.
    - Retrieve the unique count from Redis using `size()`.
    - Publish the count to Kafka using a producer template.

---

## 4. Implementation Steps

### Step 1: REST API
- Define a `VerveController` class to handle `/accept` requests.
- Accept two parameters: `id` and `endpoint`.
- Use the `StringRedisTemplate` to store `id`s in a Redis Set.

### Step 2: Redis Configuration
- Create a `RedisConfig` class to configure `StringRedisTemplate`.
- Use Spring Boot's auto-configuration to handle `RedisConnectionFactory`.

### Step 3: Kafka Integration
- Create a Kafka producer configuration (`KafkaProducerConfig`) with `KafkaTemplate`.
- Define a Kafka topic (`unique-counts`) for reporting unique counts.

### Step 4: Periodic Scheduler
- Add a scheduled task in `VerveController` to:
    - Retrieve the unique count from Redis.
    - Send the count to the Kafka topic.
    - Reset Redis for the next minute's operations.

### Step 5: Dockerization
- Optimize the Docker file for Java application and Redis setup.
- Configure resource limits to support high throughput.

---

## 5. Code Walkthrough

### Controller
- Handles `/accept` requests and validates uniqueness via Redis.
- Logs requests for debugging.

### Redis Integration
- Uses a `RedisTemplate` for adding IDs to a Redis Set.
- Performs `O(1)` operations for `add` and `size`.

### Kafka Integration
- Configures a producer to publish unique counts.
- Utilizes a dedicated Kafka topic for reporting.

### Scheduled Task
- Triggers every minute to:
    - Retrieve the unique count from Redis.
    - Publish the count to Kafka.
    - Reset Redis for the next minute's operations.

---

## 6. Scalability Measures

### Optimizing Redis Usage
- Redis is chosen for its low latency and ability to handle concurrent requests.
- Set operations (`add`, `size`) are fast and scalable.

### Kafka for Decoupling
- Kafka decouples the reporting process from the main application flow.
- Allows asynchronous processing of logs and analytics.

### JVM Optimization
- Use G1GC (`-XX:+UseG1GC`) to reduce garbage collection pauses.
- Limit heap size (`-Xms1g -Xmx1g`) for predictable memory usage.

### Horizontal Scaling
- The application can be scaled horizontally by deploying multiple instances.
- Redis ensures consistency across instances.

---

## 7. Testing and Verification

### Load Testing
- Use tools like **Apache JMeter** or **k6** to simulate 10,000 RPS.
- Verify Redis performance under load.

### Functional Testing
- Test `/accept` with various `id` inputs to ensure deduplication.
- Validate logs for correct unique counts.

### Integration Testing
- Verify Kafka messages are published correctly.
- Test Redis behavior during failovers.

---

## 8. Conclusion

This thought process ensures that the application:
1. Is scalable to handle high RPS using Redis and Kafka.
2. Ensures uniqueness of requests across distributed instances.
3. Periodically logs and reports the unique count for downstream analysis.

This approach leverages Spring Boot's strengths (e.g., auto-configuration, dependency injection) and robust middleware like Redis and Kafka to meet the functional and non-functional requirements effectively.
