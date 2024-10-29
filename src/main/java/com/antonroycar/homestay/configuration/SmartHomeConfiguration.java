package com.antonroycar.homestay.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class SmartHomeConfiguration {

    public NewTopic smartHomeTopic() {
        return TopicBuilder.name("smart_home_topic").build();
    }
}
