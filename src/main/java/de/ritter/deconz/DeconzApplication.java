package de.ritter.deconz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeconzApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeconzApplication.class, args);
	}

}
