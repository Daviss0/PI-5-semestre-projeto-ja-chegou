package com.ja.chegou.ja_chegou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JaChegouApplication {

	public static void main(String[] args) {
		SpringApplication.run(JaChegouApplication.class, args);
	}


}
