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
@ComponentScan(basePackages = "org.server")
public class ServerApplication {

    public static void main(String[] args) {
        try {
            Map<String, String> env = EnvParser.parse("./.env");

            String dbUser = env.get("MYSQL_USER");
            String dbPassword = env.get("MYSQL_PASSWORD");
            String dbUrl = env.get("MYSQL_URL");
            String jwtSecret = env.get("JWT_SECRET");

            System.setProperty("MYSQL_USER", dbUser);
            System.setProperty("MYSQL_PASSWORD", dbPassword);
            System.setProperty("MYSQL_URL", dbUrl);
            System.setProperty("JWT_SECRET", jwtSecret);

            if (!isDatabaseConnected(dbUrl, dbUser, dbPassword)) {
                System.out.println(dbUrl);
                System.err.println("Nie wykryto połączenia z bazą danych!");
            } else {
                System.out.println("Poprawnie nawiązano połączenie z bazą danych.");
            }

        } catch (IOException e) {
            System.err.println("Błąd podczas ładowania pliku .env: " + e.getMessage());
        }

        SpringApplication.run(ServerApplication.class, args);
    }

    private static boolean isDatabaseConnected(String url, String username, String password) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            return connection != null;
        } catch (SQLException e) {
            System.err.println("Błąd połączenia z bazą danych: " + e.getMessage());
        }
        return false;
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
