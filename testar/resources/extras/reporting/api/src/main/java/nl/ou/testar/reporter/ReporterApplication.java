package nl.ou.testar.reporter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import nl.ou.testar.reporter.service.ScreenshotService;
import nl.ou.testar.reporter.storage.StorageProperties;

//@Configuration
//@EnableJpaRepositories(basePackages="nl.ou.testar.reporter", entityManagerFactoryRef="emf")
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ReporterApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ReporterApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ScreenshotService screenshotService) {
        return (args) -> {
            screenshotService.deleteAll();
            screenshotService.init();
        };
    }

    // @Bean
    // PasswordEncoder passwordEncoder() {
    // return new BCryptPasswordEncoder();
    // }
}
