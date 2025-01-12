package org.server;

import org.server.devices.Camera;
import org.server.devices.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
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

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    protected AutowireCapableBeanFactory autowireCapableBeanFactory;
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {

        Camera camera = new Camera();
        deviceMapper.addDeviceByIP("192.168.1.16",camera);

        autowireCapableBeanFactory.autowireBean(camera);

        return args -> {
            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
        };
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
}

