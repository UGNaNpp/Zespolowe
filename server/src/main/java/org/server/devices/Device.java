package org.server.devices;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Transient;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.HashMap.newHashMap;

public abstract class Device {


    public long id = -1;
    public String AssociatedIP;
    public String AssociatedMAC;

    public String getAssociatedIP() {
        return AssociatedIP;
    }

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
    public int transmissionID = -1;
    public int PacketSequenceNumber = -1;
    public int expectedPacketNumber = -1;

    public CompletableFuture missingPacketsFuture;
    public long transmissionPacketNumber = -1;

    public Map<Long, Byte[]> PacketMap;     /// do skladania pakietow out of order

    public PacketAccumulator(){}
    public PacketAccumulator(int PacketSequenceNumber){
        this.PacketSequenceNumber = PacketSequenceNumber;
    }

    public PacketAccumulator(int transmissionID, int transmissionSize)
    {
        this.transmissionID = transmissionID;
        this.transmissionPacketNumber = transmissionSize;
        PacketMap = newHashMap(transmissionSize);
    }

    public void addPacketWithSequenceNumber(long PacketSequenceNumber,  Byte[] packet){
        PacketMap.put(PacketSequenceNumber, packet);
    }
    public void addPacket(Byte[] packet)
    {
        AccumulatedBytes = ArrayUtils.addAll(AccumulatedBytes, packet);
    }

    public void clear(){
        AccumulatedBytes = new Byte[0];
    }

    public Byte[] getZippedAccumulatedBytes(){
        Byte[] zippedBytes = new Byte[0];

        int placeholder_length = PacketMap.get(0L).length;
        Byte[] placeholder = new Byte[placeholder_length];
        for (int j = 0; j < placeholder_length; j++){
            placeholder[j] = 66;
        }

        for(long i = 0L; i < transmissionPacketNumber; i++){
            Byte[] gotPacket = PacketMap.getOrDefault(i, placeholder);
            zippedBytes = ArrayUtils.addAll(zippedBytes, gotPacket);
        }

        return zippedBytes;
    }

    public byte[] getAccumulatedBytesPrimitive(){
        return ArrayUtils.toPrimitive(AccumulatedBytes);
    }
    public Byte[] getAccumulatedBytes(){
        return AccumulatedBytes;
    }


}

