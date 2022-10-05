package nl.ou.testar.reporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@Configuration
//@EnableJpaRepositories(basePackages="nl.ou.testar.reporter", entityManagerFactoryRef="emf")
@SpringBootApplication
public class ReporterApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(ReporterApplication.class, args);
	}

//	@Bean
//	PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
}
