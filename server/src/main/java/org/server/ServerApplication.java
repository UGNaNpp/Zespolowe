package org.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        try {
            Map<String, String> env = EnvParser.parse("./.env");
            String jwtSecret = env.get("JWT_SECRET");

            System.setProperty("JWT_SECRET", jwtSecret);

        } catch (IOException e) {
            System.err.println("Błąd podczas ładowania pliku .env: " + e.getMessage());
        }

        SpringApplication.run(ServerApplication.class, args);
    }

}

class EnvParser {
    public static Map<String, String> parse(String filePath) throws IOException {
        Map<String, String> env = new HashMap<>();
        Files.lines(Paths.get(filePath)).forEach(line -> {
            if (!line.trim().startsWith("#") && line.contains("=")) {
                String[] parts = line.split("=", 2);
                env.put(parts[0].trim(), parts[1].trim());
            }
        });
        return env;
    }
}
