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
        String devicesJsonFilepath = System.getenv("DEVICES_JSON_FILEPAT H");
        String usersFilepath = System.getenv("USERS_JSON_FILEPATH");


        if (jwtSecret == null || timeZone == null || jwtExpiration == null || frontUrl == null
        || devicesJsonFilepath == null || usersFilepath == null) {
            dotenv = Dotenv.configure().load();

            if (jwtSecret == null) System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
            if (timeZone == null) System.setProperty("TIME_ZONE", dotenv.get("TIME_ZONE"));
            if (jwtExpiration == null) System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
            if (frontUrl == null) System.setProperty("FRONT_URL", dotenv.get("FRONT_URL"));
            if (devicesJsonFilepath == null) System.setProperty("DEVICES_JSON_FILEPATH", dotenv.get("DEVICES_JSON_FILEPATH"));
            if (usersFilepath == null) System.setProperty("USERS_FILEPATH", dotenv.get("USERS_FILEPATH"));
        }

        System.setProperty("JWT_SECRET", jwtSecret);
        System.setProperty("TIME_ZONE", timeZone);
        System.setProperty("JWT_EXPIRATION", jwtExpiration);
        System.setProperty("FRONT_URL", frontUrl);

        SpringApplication.run(ServerApplication.class, args);
    }

}
