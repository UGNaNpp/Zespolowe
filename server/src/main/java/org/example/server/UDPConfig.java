package org.example.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;

@Configuration
public class UDPConfig {
    public static final int PORT = 9876;

    @Bean
    public IntegrationFlow processUniCastUdpMessage() {
        return IntegrationFlow
                .from(new UnicastReceivingChannelAdapter(PORT))
                .handle("UDPServer", "handleMessage")
                .get();
    }

}
