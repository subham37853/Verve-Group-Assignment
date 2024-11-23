# Verve Group Assignment

## 1. Understanding the Requirements

### Key Requirements
- Build a REST API that accepts requests and ensures the uniqueness of requests based on an `id` parameter.
- Use Redis for deduplication to handle distributed environments.
- Log the count of unique requests received every minute.
- Send the unique count to a Kafka topic.
- Ensure the system can handle at least **10,000 RPS**.

## Architecture Diagram

![Verve Group Design](https://github.com/user-attachments/assets/936f462b-fa5f-4ffc-a4f6-5f1849649841)
