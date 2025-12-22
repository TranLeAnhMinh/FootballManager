package com.example.footballmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FootballmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballmanagementApplication.class, args);
	}

}
