package de.bp2019.zentraldatei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

/**
 * Class containing the main method bootstraping the spring application
 * 
 * @author Leon Chemitz
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class ZentraldateiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZentraldateiApplication.class, args);
	}

}
