package com.ssafyhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.ssafyhome")
public class SsafyHomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsafyHomeApplication.class, args);
	}

}
