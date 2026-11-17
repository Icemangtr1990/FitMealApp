package es.dsw.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan(basePackages="es.dsw")
@EntityScan(basePackages="es.dsw.models")
@EnableJpaRepositories(basePackages="es.dsw.repositories")
public class FitMealAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitMealAppApplication.class, args);
	}

}
