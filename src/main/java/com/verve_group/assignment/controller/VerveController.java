package com.verve_group.assignment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verve")
public class VerveController {

    private static final Logger logger = LoggerFactory.getLogger(VerveController.class);

    // Redis key for storing unique IDs
    private static final String UNIQUE_IDS_KEY = "uniqueIds";

    private KafkaTemplate<String, String> kafkaTemplate;

    private StringRedisTemplate redisTemplate;

    // Kafka topic for unique counts
    private static final String KAFKA_TOPIC = "unique-counts";

    @Autowired
    public VerveController(KafkaTemplate<String, String> kafkaTemplate, StringRedisTemplate redisTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
    }

    // Accept request and deduplicate based on ID using Redis
    @PostMapping("/accept")
    public String acceptRequest(@RequestParam int id, @RequestParam(required = false) String endpoint) {
        try {
            // Deduplicate using Redis Set (set ensures uniqueness)
            redisTemplate.opsForSet().add(UNIQUE_IDS_KEY, String.valueOf(id));

            // Optionally process the endpoint
            if (endpoint != null && !endpoint.isEmpty()) {
                logger.info("Processing endpoint: {}", endpoint);
            }

            return "ok";
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            return "failed";
        }
    }

    /**
     * Scheduled task to send the count of unique IDs to Kafka and log file every minute.
     */
    @Scheduled(fixedRate = 60000) // Every 1 minute
    public void sendUniqueCount() {
        try {
            // Get the unique count of IDs from Redis Set
            Long uniqueCount = redisTemplate.opsForSet().size(UNIQUE_IDS_KEY);

            // Send the unique count to Kafka
            String message = String.format("Unique count in the last minute: %d", uniqueCount);
            kafkaTemplate.send(KAFKA_TOPIC, message);

            // Log the message
            logger.info("Sent unique count to Kafka topic '{}': {}", KAFKA_TOPIC, message);

            // Clear the Redis Set for the next minute
            redisTemplate.delete(UNIQUE_IDS_KEY);

        } catch (Exception e) {
            logger.error("Error sending unique count to Kafka: {}", e.getMessage(), e);
        }
    }
}



