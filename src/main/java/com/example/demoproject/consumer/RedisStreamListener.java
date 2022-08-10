package com.example.demoproject.consumer;

import com.example.demoproject.producer.RedisStreamTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.stereotype.Component;


/**
 * @author joyee
 */
@Slf4j
@Component
public  class RedisStreamListener implements AbstractRedisStreamListener<String, ObjectRecord<String, String>> {

    @Autowired
    private RedisStreamTemplate redisStreamTemplate;

    @Override
    public void beforeMessage() {

    }

    @Override
    public void message(ObjectRecord message) {
        log.info((String) message.getValue());
        long a = redisStreamTemplate.ack(message);
        if(a>0){
            log.info("{}:手动消费{}",this.getClass().getName(),message.getValue());
        }else{
            //消费失败
            log.info("{}:消费失败{}",this.getClass().getName(),message);
        }
    }

    @Override
    public void afterMessage() {

    }

}