package com.example.demoproject.config;

import com.example.demoproject.consumer.AbstractRedisStreamListener;
import com.example.demoproject.producer.GroupInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author joyee
 */
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Autowired
    private Map<String, AbstractRedisStreamListener> stringStreamListenerMapOri;

        @Bean
        @Primary
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
            RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
            template.setConnectionFactory(factory);
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            jackson2JsonRedisSerializer.setObjectMapper(om);
            StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
            // key采用String的序列化方式
            template.setKeySerializer(stringRedisSerializer);
            // hash的key也采用String的序列化方式
            template.setHashKeySerializer(stringRedisSerializer);
            // value序列化方式采用jackson
            template.setValueSerializer(jackson2JsonRedisSerializer);
            // hash的value序列化方式采用jackson
            template.setHashValueSerializer(jackson2JsonRedisSerializer);
            template.afterPropertiesSet();

            return template;
        }


    @Bean
    public StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String,String>> streamMessageListenerContainerOptions(){

            return StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                    .batchSize(10)
                    .targetType(String.class)
                    .build();
    }

    @Bean
    public StreamMessageListenerContainer streamMessageListenerContainer(RedisConnectionFactory factory,
                                                                         StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String,String>> streamMessageListenerContainerOptions){
        StreamMessageListenerContainer listenerContainer = StreamMessageListenerContainer.create(factory,
                streamMessageListenerContainerOptions);
        listenerContainer.start();
        return listenerContainer;
    }

    /**
     * 订阅者1，消费组group1，收到消息后自动确认，与订阅者2为竞争关系，消息仅被其中一个消费
     * @param streamMessageListenerContainer
     * @return
     */
    @Bean
    public Subscription subscription(StreamMessageListenerContainer streamMessageListenerContainer) throws UnknownHostException {
        Subscription subscription = streamMessageListenerContainer.receive(
                Consumer.from(stringStreamListenerMapOri.entrySet().iterator().next().getKey(),stringStreamListenerMapOri.entrySet().iterator().next().getKey()),
                StreamOffset.create(InetAddress.getLocalHost().getHostAddress(), ReadOffset.lastConsumed()),
                stringStreamListenerMapOri.entrySet().iterator().next().getValue()
        );
        return subscription;
    }
    @Bean
    public GroupInfo getGroupInfo() throws UnknownHostException {
        GroupInfo groupInfo = new GroupInfo();
        return groupInfo;
    }
}
