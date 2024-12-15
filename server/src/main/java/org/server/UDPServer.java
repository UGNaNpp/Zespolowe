package org.server;

import org.server.devices.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import static org.server.UDPConfig.UDP_PREHEADER_INFO_SIZE;

@Service
public class UDPServer
{
    @Autowired
    org.server.devices.DeviceMapper DeviceMapper;

    public void handleMessage(Message message)
    {
        try{
            Object clas = message.getPayload().getClass();
            byte[] payload_bytes = (byte[]) message.getPayload();

            int payload_length = payload_bytes.length;

            if(payload_length < UDP_PREHEADER_INFO_SIZE)
            {
                // malformed packet - too short to contain info header
                return;
            }

            String sender_ipv4 = message.getHeaders().get("ip_address").toString();
            String sender_port = message.getHeaders().get("ip_port").toString();
            String sender_ip_hostname = message.getHeaders().get("ip_hostname").toString();


            Device sender_device = DeviceMapper.getDevice(sender_ipv4);
            sender_device.newPacket(payload_bytes);

        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}