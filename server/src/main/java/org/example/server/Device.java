package org.example.server;

public class Device {
    public String AssociatedIP;
    public String AssociatedMAC;

    public Device() {
    }
    public Device(String AssociatedIP)
    {
        this.AssociatedIP = AssociatedIP;
    }
    public Device(String AssociatedIP, String AssociatedMAC) {
        this.AssociatedIP = AssociatedIP;
        this.AssociatedMAC = AssociatedMAC;
    }
}