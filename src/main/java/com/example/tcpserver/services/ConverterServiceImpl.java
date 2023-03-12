package com.example.tcpserver.services;

import com.example.tcpserver.model.ClientData;
import com.example.tcpserver.model.CommandID;
import com.example.tcpserver.model.ServerData;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


@Service
public class ConverterServiceImpl implements ConverterService{
    private ByteBuffer modelTimeBuf = ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN);
    private ByteBuffer lenBuffer = ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN);
    private ByteBuffer byteBuffer = ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN);
    @Override
    public byte[] convertToBytes(ServerData serverData) {
        ByteBuffer buffer = ByteBuffer.allocate(12 + serverData.getData().length * 8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(serverData.getData().length);
        buffer.putDouble(serverData.getModelTime());
        buffer.put(
                DoubleArrToByteArr(serverData.getData())
        );
        return buffer.array();
    }

    @Override
    public ClientData convertFromBytes(byte[] bytes) {
        CommandID id = CommandID.commandById(bytes[0]);

        double time = modelTimeBuf.put(bytes, 4, 8)
                .rewind()
                .getDouble(0);
        modelTimeBuf.clear();

        int bufLen = lenBuffer.put(bytes, 12, 4)
                .rewind()
                .getInt(0);
        lenBuffer.clear();

        double[] data = new double[bufLen];
        for (int i = 0; i < bufLen; i++) {
            data[i] = byteBuffer.put(bytes, 16 + i * 8, 8).rewind().getDouble(0);
            byteBuffer.clear();
        }
        byteBuffer.clear();
        return new ClientData(id, time, bufLen, data);
    }

    private byte[] DoubleArrToByteArr(double[] dArr) {
        int offset = 0;
        ByteBuffer bytes = ByteBuffer.allocate(dArr.length * 8);
        for (double v: dArr) {
            byte[] dv = ByteBuffer.allocate(8)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putDouble(v)
                    .array();
            bytes.put(dv, offset, 8);
            offset += 8;
        }
        return bytes.array();
    }
}
