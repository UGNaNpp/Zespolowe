package org.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        Dotenv dotenv = null;

        String jwtSecret = System.getenv("JWT_SECRET");
        String timeZone = System.getenv("TIME_ZONE");
        String jwtExpiration = System.getenv("JWT_EXPIRATION");
        String frontUrl = System.getenv("FRONT_URL");

        if (jwtSecret == null || timeZone == null || jwtExpiration == null || frontUrl == null) {
            dotenv = Dotenv.configure().load();

            if (jwtSecret == null) jwtSecret = dotenv.get("JWT_SECRET");
            if (timeZone == null) timeZone = dotenv.get("TIME_ZONE");
            if (jwtExpiration == null) jwtExpiration = dotenv.get("JWT_EXPIRATION");
            if (frontUrl == null) frontUrl = dotenv.get("FRONT_URL");
        }

        System.setProperty("JWT_SECRET", jwtSecret);
        System.setProperty("TIME_ZONE", timeZone);
        System.setProperty("JWT_EXPIRATION", jwtExpiration);
        System.setProperty("FRONT_URL", frontUrl);

        SpringApplication.run(ServerApplication.class, args);
    }

}
