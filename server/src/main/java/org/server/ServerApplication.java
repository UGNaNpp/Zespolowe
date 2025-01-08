package org.server;

import org.server.devices.Camera;
import org.server.devices.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
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
}
