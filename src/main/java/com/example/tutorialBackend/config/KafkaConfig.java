package com.example.tutorialBackend.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.buffer.memory:33554432}")
    private long bufferMemory;
    @Value("${kafka.retries:2147483647}")
    /** {@link ProducerConfig} default value: Integer.MAX_VALUE **/
    private int retries;
    @Value("${kafka.retry.backoff.ms:100}")
    /** {@link ProducerConfig} default value: 100 **/
    private int retryBackoffMs;
    @Value("${kafka.batch.size:16384}")
    private int batchSize;
    @Value("${kafka.linger.ms:50}")
    /** {@link ProducerConfig} default value: 0 **/
    private long lingerMs;
    @Value("${kafka.request.timeout.ms:30000}")
    private int requestTimeoutMs;
    @Value("${kafka.max.block.ms:60000}")
    private long maxBlockMs;
    @Value("${kafka.acks:all}")
    /** {@link ProducerConfig} default value: "1" **/
    private String acks;
    @Value("${kafka.delivery.timeout.ms:120000}")
    private int deliveryTimeout;
    @Value("${kafka.metadata.max.idle.ms:300000}")
    private int metadataMaxIdle;
    @Value("${kafka.metadata.max.age.ms:300000}")
    private int metadataMaxAge;
    @Bean
    public Map<String, Object> jsonProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffMs);

        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, metadataMaxAge);
        props.put(ProducerConfig.METADATA_MAX_IDLE_CONFIG, metadataMaxIdle);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);

        return props;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        ProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(jsonProducerConfigs());
        return new KafkaTemplate<>(factory);
    }
    @Autowired
    private ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory;

    @PostConstruct
    public void init() {
        kafkaListenerContainerFactory.getContainerProperties().setConsumerTaskExecutor(new SimpleAsyncTaskExecutor("consumer-"));
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new SeekToCurrentErrorHandler(new FixedBackOff(0, 2));
    }
}
