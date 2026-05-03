package com.dresscode.api_dresscode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApiDresscodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDresscodeApplication.class, args);
	}

}
