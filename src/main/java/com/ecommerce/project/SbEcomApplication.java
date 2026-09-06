package com.ecommerce.project;

import com.ecommerce.project.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication

public class   SbEcomApplication implements CommandLineRunner {



	@Value("${my.variable}")
	private String myVariable;





	public static void main(String[] args) {

		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		SpringApplication.run(SbEcomApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("my variable :"+myVariable);


	}
}
