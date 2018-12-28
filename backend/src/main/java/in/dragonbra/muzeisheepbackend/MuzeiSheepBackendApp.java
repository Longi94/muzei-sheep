package in.dragonbra.muzeisheepbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lngtr
 * @since 2018-12-23
 */
@SpringBootApplication
@EnableScheduling
public class MuzeiSheepBackendApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MuzeiSheepBackendApp.class, args);
    }
}
