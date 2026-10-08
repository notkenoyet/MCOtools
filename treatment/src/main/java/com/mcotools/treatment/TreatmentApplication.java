package com.mcotools.treatment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TreatmentApplication {

	private static final Logger log = LoggerFactory.getLogger(TreatmentApplication.class);

	public static void main(String[] args) {
		log.info("Demarrage du service treatment...");
		SpringApplication.run(TreatmentApplication.class, args);
		log.info("Service treatment demarre avec succes");
	}

}
