package com.sample.ordermonitor.config;

import com.sample.orderfulfillment.coreapi.events.OrderFulfillmentStatusChangedEvent;
import com.sample.orderintake.coreapi.events.OrderIntakeStatusChangedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, OrderIntakeStatusChangedEvent> orderIntakeConsumerFactory()
    {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-monitor");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(OrderIntakeStatusChangedEvent.class));
    }

    // Creating a Listener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderIntakeStatusChangedEvent> orderIntakeListenerContainerFactory()
    {
        ConcurrentKafkaListenerContainerFactory<String, OrderIntakeStatusChangedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderIntakeConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderFulfillmentStatusChangedEvent> orderFulfillmentConsumerFactory()
    {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-monitor");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(OrderFulfillmentStatusChangedEvent.class));
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderFulfillmentStatusChangedEvent> orderFulfillmentListenerContainerFactory()
    {
        ConcurrentKafkaListenerContainerFactory<String, OrderFulfillmentStatusChangedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderFulfillmentConsumerFactory());

        return factory;
    }
}
