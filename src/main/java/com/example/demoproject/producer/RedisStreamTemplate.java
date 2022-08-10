package com.example.demoproject.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class RedisStreamTemplate {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private GroupInfo groupInfo;

    /**
     * 创建消费组
     * @return
     */
    public String creartGroup(){
        return redisTemplate.opsForStream().createGroup(groupInfo.getGroupKey(), groupInfo.getGroupName());
    }

    /**
     * 消费组信息
     * @return
     */
    public StreamInfo.XInfoConsumers consumers(){
        return redisTemplate.opsForStream().consumers(groupInfo.getGroupKey(), groupInfo.getGroupName());
    }

    public StreamInfo.XInfoGroups groups(String key){
        return redisTemplate.opsForStream().groups(groupInfo.getGroupKey());
    }
    /**
     * 确认已消费
     * @param mapRecord
     * @return
     */
    public Long ack( ObjectRecord mapRecord){
        return redisTemplate.opsForStream().acknowledge(groupInfo.getGroupName(), mapRecord);
    }

    /**
     * 追加消息
     * @param field
     * @param value
     * @return
     */
    public String add(String field, String value){
        Map<String, String> content = new HashMap<>(1);
        content.put(field, value);
        return add(content);
    }

    public String add(Map<String, String> content){
        return redisTemplate.opsForStream().add(groupInfo.getGroupKey(), content).getValue();
    }

    /**
     * 删除消息，这里的删除仅仅是设置了标志位，不影响消息总长度
     * 消息存储在stream的节点下，删除时仅对消息做删除标记，当一个节点下的所有条目都被标记为删除时，销毁节点
     * @param key
     * @param recordIds
     * @return
     */
    public Long del(String key, String... recordIds){
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    /**
     * 消息长度
     * @param key
     * @return
     */
    public Long len(String key){
        return redisTemplate.opsForStream().size(key);
    }

    /**
     * 从开始读
     * @param key
     * @return
     */
    public List<MapRecord<String, Object, Object>> read(String key){
        return redisTemplate.opsForStream().read(StreamOffset.fromStart(key));
    }

    /**
     * 从指定的ID开始读
     * @param key
     * @param recordId
     * @return
     */
    public List<MapRecord<String, Object, Object>> read(String key, String recordId){
        return redisTemplate.opsForStream().read(StreamOffset.from(MapRecord.create(key, new HashMap<>(1)).withId(RecordId.of(recordId))));
    }

}
