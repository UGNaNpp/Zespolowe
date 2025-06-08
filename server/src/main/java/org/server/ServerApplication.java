package org.server;

import io.github.cdimascio.dotenv.Dotenv;
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
        Dotenv dotenv = null;

        String jwtSecret = System.getenv("JWT_SECRET");
        String timeZone = System.getenv("TIME_ZONE");
        String jwtExpiration = System.getenv("JWT_EXPIRATION");
        String frontUrl = System.getenv("FRONT_URL");
        String devicesJsonFilepath = System.getenv("DEVICES_JSON_FILEPATH");
        String usersJsonFilepath = System.getenv("USERS_FILEPATH");
        String mediaFolderFilepath = System.getenv("MEDIA_FOLDER");


        if (jwtSecret == null || timeZone == null || jwtExpiration == null || frontUrl == null
        || devicesJsonFilepath == null || usersJsonFilepath == null) {
            System.out.println("system env doesn't exist, loading from file");
            dotenv = Dotenv.configure().load();

            if (jwtSecret == null) System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
            if (timeZone == null) System.setProperty("TIME_ZONE", dotenv.get("TIME_ZONE"));
            if (jwtExpiration == null) System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
            if (frontUrl == null) System.setProperty("FRONT_URL", dotenv.get("FRONT_URL"));
            if (devicesJsonFilepath == null) System.setProperty("DEVICES_JSON_FILEPATH", dotenv.get("DEVICES_JSON_FILEPATH"));
            if (usersJsonFilepath == null) System.setProperty("USERS_FILEPATH", dotenv.get("USERS_FILEPATH"));
            if (mediaFolderFilepath == null) System.setProperty("MEDIA_FOLDER", dotenv.get("MEDIA_FOLDER"));
        } else {
            System.setProperty("JWT_SECRET", jwtSecret);
            System.setProperty("TIME_ZONE", timeZone);
            System.setProperty("JWT_EXPIRATION", jwtExpiration);
            System.setProperty("FRONT_URL", frontUrl);
            System.setProperty("DEVICES_JSON_FILEPATH", devicesJsonFilepath);
            System.setProperty("USERS_FILEPATH", usersJsonFilepath);
            System.setProperty("MEDIA_FOLDER", mediaFolderFilepath);
        }

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
