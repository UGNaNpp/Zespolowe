package org.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        String jwtSecret = dotenv.get("JWT_SECRET");
        System.setProperty("JWT_SECRET", jwtSecret);
        System.setProperty("TIME_ZONE", dotenv.get("TIME_ZONE"));
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
        System.setProperty("FRONT_URL", dotenv.get("FRONT_URL"));
        SpringApplication.run(ServerApplication.class, args);
    }

}
