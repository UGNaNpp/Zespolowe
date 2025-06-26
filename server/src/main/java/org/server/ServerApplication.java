package org.server;

import org.server.devices.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ServerApplication {

    @Autowired
    private DeviceMapper deviceMapper;


    public static void main(String[] args) {

//        Dotenv dotenv = Dotenv.configure().load();
//        String jwtSecret = dotenv.get("JWT_SECRET");
//        System.setProperty("JWT_SECRET", jwtSecret);
//        System.setProperty("TIME_ZONE", dotenv.get("TIME_ZONE"));
//        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
//        System.setProperty("FRONT_URL", dotenv.get("FRONT_URL"));
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        deviceMapper.loadDevicesFromJson();
        deviceMapper.addMockedCameras();
        return args->{

        };
    }

}
