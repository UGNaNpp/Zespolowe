package org.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;

@Configuration
public class UDPConfig {
    public static final int UDP_PORT = 9876;
    public static final int UDP_PREHEADER_INFO_SIZE = 64;

    @Bean
    public IntegrationFlow processUniCastUdpMessage() {
        return IntegrationFlow
                .from(new UnicastReceivingChannelAdapter(UDP_PORT))
                .handle("UDPServer", "handleMessage")
                .get();
    }

}
