package com.sample.ordermonitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.extensions.mongo.DefaultMongoTemplate;
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SpringBootApplication
public class OrderMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderMonitorApplication.class, args);
	}

	@Bean(destroyMethod = "")
	public DeadlineManager deadlineManager(TransactionManager transactionManager,
										   Configuration config) {
		return SimpleDeadlineManager.builder()
				.transactionManager(transactionManager)
				.scopeAwareProvider(new ConfigurationScopeAwareProvider(config))
				.build();
	}

	@Bean(destroyMethod = "shutdown")
	public ScheduledExecutorService workerExecutorService() {
		return Executors.newScheduledThreadPool(4);
	}

	@Autowired
	public void configureSerializers(ObjectMapper objectMapper) {
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
				ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);
	}

	@Bean
	public TokenStore getTokenStore(MongoTemplate template, Serializer serializer) {

		var axonTemplate = DefaultMongoTemplate.builder()
				.mongoDatabase(template.getMongoDatabaseFactory().getMongoDatabase())
				.build();

		return MongoTokenStore.builder()
				.mongoTemplate(axonTemplate)
				.serializer(serializer)
				.build();
	}
}
