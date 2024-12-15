package org.server.devices;
import org.apache.commons.lang3.ArrayUtils;
public abstract class Device {
    public String AssociatedIP;
    public String AssociatedMAC;

    public Device() {
    }
    public Device(String AssociatedIP){
        this.AssociatedIP = AssociatedIP;
    }
    public Device(String AssociatedIP, String AssociatedMAC) {
        this.AssociatedIP = AssociatedIP;
        this.AssociatedMAC = AssociatedMAC;
    }
    abstract public void newPacket(byte[] packet);
}

class PacketAccumulator{
    public Byte[] AccumulatedBytes = new Byte[0];

    public PacketAccumulator(){}

    public void addPacket(Byte[] packet)
    {
        AccumulatedBytes = ArrayUtils.addAll(AccumulatedBytes, packet);
    }

    public void clear(){
        AccumulatedBytes = new Byte[0];
    }

    public byte[] getAccumulatedBytesPrimitive(){
        return ArrayUtils.toPrimitive(AccumulatedBytes);
    }
    public Byte[] getAccumulatedBytes(){
        return AccumulatedBytes;
    }
}

