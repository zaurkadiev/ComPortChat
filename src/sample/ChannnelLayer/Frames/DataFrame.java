package sample.ChannnelLayer.Frames;

import sample.ChannnelLayer.FrameManager;

import java.io.Serializable;


public class DataFrame implements Serializable {
    private byte startByte = FrameManager.START_BYTE;
    private byte[] data;
    private byte frameType;
    private byte endByte = FrameManager.END_BYTE;
    public static short FILE_DATA_FRAME_SIZE = 1 + 1 + 2045 + 1; // start-0 type-1 data-2..129 end - 130
    public static short FILE_DATA_FRAME_DATA_FIELD_SIZE = 2045;

    public DataFrame(byte type){
        this.frameType = type;
    }

    public DataFrame(byte[] frame){
        if(frame[0] == startByte && frame[1] == frameType && frame[130] == endByte){
            for (int i = 2; i <= 129; i++) {
                data[i-2] = frame[i-2];
            }
        }
    }

    public void setData (byte[] data){
        this.data = data;
    }
    public byte[] getData() {
        return data;
    }
    public byte getFrameType() {
        return frameType;
    }

    public byte[] getPackedFrame(){
        byte[] ba = new byte[1+data.length+1+1];

        ba[0] = startByte;
        ba[1] = frameType;
        for (int i = 0; i < data.length; i++) {
            ba[i+2] = data[i];
        }
        ba[ba.length-1] = endByte;
        return ba;
    }
}