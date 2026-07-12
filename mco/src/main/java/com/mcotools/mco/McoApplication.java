package com.mcotools.mco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.mcotools.models")
@EnableJpaRepositories(basePackages = "com.mcotools.repository")
public class McoApplication {

	public static void main(String[] args) {
		SpringApplication.run(McoApplication.class, args);
	}

}
