package com.directa24.challenge;

import com.directa24.challenge.config.MovieProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
@EnableConfigurationProperties(MovieProperties.class)
public class Directa24ChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(Directa24ChallengeApplication.class, args);
	}

}
