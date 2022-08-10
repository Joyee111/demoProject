package com.example.demoproject.consumer;

import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;

public interface AbstractRedisStreamListener<String, O extends ObjectRecord<String, String>> extends StreamListener<java.lang.String, ObjectRecord<java.lang.String, java.lang.String>>   {
    void beforeMessage();
    void message(ObjectRecord value);
    void afterMessage();

    @Override
    default void onMessage(ObjectRecord<java.lang.String, java.lang.String> message) {
        beforeMessage();
        message(message);
        afterMessage();
    }
}
