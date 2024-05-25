package com.huseyincan.financeportfolio.config;

import com.mongodb.ConnectionString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

@Configuration
public class MongoConfig {
    @Value("${mongodb.connection-string}")
    String connectionString;

    /**
     * Returns mongodb instance via factory method.
     */
    public @Bean MongoClientFactoryBean mongoClient() {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
        mongo.setConnectionString(new ConnectionString(connectionString));
        return mongo;
    }
}
