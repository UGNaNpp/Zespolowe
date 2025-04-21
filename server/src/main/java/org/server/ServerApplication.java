package org.server;

import io.github.cdimascio.dotenv.Dotenv;

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
        Dotenv dotenv = Dotenv.configure().load();
        String jwtSecret = dotenv.get("JWT_SECRET");
        System.setProperty("JWT_SECRET", jwtSecret);

        SpringApplication.run(ServerApplication.class, args);
    }

}
