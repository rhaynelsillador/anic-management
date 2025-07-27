package com.sillador.strecs;

import com.sillador.strecs.entity.Student;
import com.sillador.strecs.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StrecsApplication {


	public static void main(String[] args) {
		SpringApplication.run(StrecsApplication.class, args);


	}

}
