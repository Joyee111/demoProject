package com.example.demoproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class DemoProjectApplication {


	public static void main(String[] args) {
		SpringApplication.run(DemoProjectApplication.class, args);

	}
}
