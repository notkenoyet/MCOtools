package com.mcotools.mco;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.mcotools")
@EntityScan(basePackages = "com.mcotools.models")
@EnableJpaRepositories(basePackages = "com.mcotools.repository")
public class McoApplication {

	private static final Logger log = LoggerFactory.getLogger(McoApplication.class);

	public static void main(String[] args) {
		log.info("Demarrage du service mco...");
		SpringApplication.run(McoApplication.class, args);
		log.info("Service mco demarre avec succes");
	}

}
