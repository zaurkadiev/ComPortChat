package sample.ChannnelLayer.Frames;

import sample.ChannnelLayer.FrameManager;

import java.io.Serializable;

public class ServiceFrame implements Serializable {
    private byte startByte = FrameManager.START_BYTE;
    private byte frameType;
    private byte endByte = FrameManager.END_BYTE;
    private byte FILE_DATA_FRAME_SIZE = 3;

    public ServiceFrame(byte frameType){
        this.frameType = frameType;
    }

    public int getFrameType(){
        return frameType;
    }

    public void setFrameType(byte frameType) {
        this.frameType = frameType;
    }

    public byte[] getPackedFrame(){
        byte[] ba = new byte[FILE_DATA_FRAME_SIZE];
        ba[0] = startByte;
        ba[1] = frameType;
        ba[2] = endByte;
        return ba;
    }
}