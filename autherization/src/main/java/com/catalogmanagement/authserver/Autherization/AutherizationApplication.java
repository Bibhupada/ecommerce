package com.catalogmanagement.authserver.Autherization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class AutherizationApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AutherizationApplication.class, args);
	}

}
