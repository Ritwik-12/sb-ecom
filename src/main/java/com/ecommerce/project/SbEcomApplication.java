package com.ecommerce.project;

import com.ecommerce.project.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class   SbEcomApplication implements CommandLineRunner {



	@Value("${my.variable}")
	private String myVariable;


	private final DataService dataService;

	public static void main(String[] args) {
		SpringApplication.run(SbEcomApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("my variable :"+myVariable);
		System.out.println("This is "+dataService.getData());
	}
}
