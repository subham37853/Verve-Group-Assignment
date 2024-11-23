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

## **Requirements**
- Java 17 or higher
- Redis server
- Kafka server
- Docker (optional for containerized deployment)

---

## **Setup Instructions**

### **1. Clone the Repository**
```bash
git clone <repository-url>
cd <project-directory>
```

---

### **2. Install Dependencies**
Ensure you have **Gradle** installed. Then run:
```bash
./gradlew build
```

---

### **3. Setup Redis**
Start a Redis instance:
```bash
docker run --name redis-instance -p 6379:6379 -d redis
```
Or, install Redis locally by following [Redis Installation Guide](https://redis.io/docs/getting-started/installation/).

---

### **4. Setup Kafka**
1. Download and extract Kafka:
   ```bash
   wget https://downloads.apache.org/kafka/3.4.0/kafka_2.13-3.4.0.tgz
   tar -xvf kafka_2.13-3.4.0.tgz
   cd kafka_2.13-3.4.0
   ```
2. Start Kafka Broker:
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```
4. Create the Kafka topic:
   ```bash
   bin/kafka-topics.sh --create --topic unique-ids --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
   ```

---

### **5. Configure Application**
Update the `application.yml` file with the correct configuration:
```yaml
spring:
  redis:
    host: localhost
    port: 6379

kafka:
  bootstrap-servers: localhost:9092
  topic: unique-ids
```

---

### **6. Monitor Logs**
The application logs unique request counts every minute. Check the logs in the console or the generated log file.

---

## **Building Docker Image**

1. Build the Docker image:
   ```bash
   docker build -t verve-assignment .
   ```

2. Run the Docker container:
   ```bash
   docker run -p 8080:8080 --name verve-assignment-container verve-assignment
   ```

---

### **7. Test the API**
Send a POST request to the `/api/v1/unique` endpoint:
```bash
curl -X POST http://localhost:8080/api/v1/unique?id=<unique-id>
```

---

## **Tech Stack**
- **Spring Boot**: Backend framework
- **Redis**: In-memory data store for caching and de-duplication
- **Kafka**: Messaging queue for publishing unique IDs
- **Gradle**: Build tool
- **Docker**: Containerization

---

## **Future Improvements**
- Add support for distributed tracing (e.g., OpenTelemetry).
- Add integration tests for better coverage.
- Support batch publishing to Kafka for higher throughput.

---

## **Contributors**
Shubham Prasad
