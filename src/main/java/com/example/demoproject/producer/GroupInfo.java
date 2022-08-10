package com.example.demoproject.producer;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author joyee
 */
@Data
public class GroupInfo {
    @Value("${spring.data.redis.groupname}")
    private String groupName;

    private String groupKey;

    public GroupInfo() throws UnknownHostException {
        this.groupKey = InetAddress.getLocalHost().getHostAddress();
    }
}
