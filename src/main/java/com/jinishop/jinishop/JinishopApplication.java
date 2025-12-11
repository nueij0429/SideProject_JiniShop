package com.jinishop.jinishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JinishopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JinishopApplication.class, args);
	}

}
