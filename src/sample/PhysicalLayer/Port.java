package sample.PhysicalLayer;

import jssc.*;
import sample.ChannnelLayer.FrameManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Port {


    public static SerialPort mSerialPort;

    public static String mPortName;

    public static void init (String comPortId){
        mSerialPort = new SerialPort(comPortId);
        mPortName  = comPortId;
    }

    private static Logger LOGGER = Logger.getLogger("PORT");


    public static void setParams(int baudrate, int databits, int stopbits, int parity){

        if(!mSerialPort.isOpened()){

            try {
                mSerialPort.openPort();
                setListener();
            } catch (SerialPortException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            mSerialPort.setParams(baudrate, databits, stopbits, parity);
            mSerialPort.setEventsMask(
                    SerialPort.MASK_DSR +
                    SerialPort.MASK_CTS +
                    SerialPort.MASK_BREAK +
                    SerialPort.MASK_RLSD +
                    SerialPort.MASK_RXCHAR
            );
            LOGGER.log(Level.INFO, "Setting port properties");
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

//    public static final int RXCHAR = 1;
//    public static final int RXFLAG = 2;
//    public static final int TXEMPTY = 4;
//    public static final int CTS = 8;
//    public static final int DSR = 16;
//    public static final int RLSD = 32;
//    public static final int BREAK = 64;
//    public static final int ERR = 128;
//    public static final int RING = 256;

    public static void setListener(){
        try {
            mSerialPort.addEventListener(new EventListener());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public boolean checkConnection(){
        try {
            mSerialPort.isRLSD();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static class EventListener implements SerialPortEventListener{
        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            handlePortEventListener(serialPortEvent);
        }
    }

    private static void handlePortEventListener(SerialPortEvent serialPortEvent){
        switch (serialPortEvent.getEventType()){
            case SerialPortEvent.BREAK:
                LOGGER.log(Level.INFO, "break interrupt");
                break;
            case SerialPortEvent.CTS:
                LOGGER.log(Level.INFO, "CTS true");
                break;
            case SerialPortEvent.DSR:
                LOGGER.log(Level.INFO, "DSR true");
                break;
            case SerialPortEvent.RLSD:
                LOGGER.log(Level.INFO, "RLSD true");
                break;
            case SerialPortEvent.RXCHAR:
                try {
                    byte[] data = mSerialPort.readBytes();
                    handlePortData(data);
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public static String[] getAvailablePorts(){
       return SerialPortList.getPortNames();
    }

    private static void handlePortData(byte[] data){
        ArrayList<Byte> frameData = new ArrayList<>();
        if (data == null) return;
        try {
            if (data[0] == FrameManager.START_BYTE){
                for (byte b : data) {
                    frameData.add(b);
                }
                LOGGER.log(Level.INFO,"FRAME TYPE "+ frameData.get(1).toString());
                LOGGER.log(Level.INFO,"LENGHT "+ frameData.size());
                FrameManager.frameRecognize(frameData);
            }

        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    public static void connect(){
        if(mSerialPort != null)
            disconnect();

        try {
            mSerialPort.setDTR(true);
            mSerialPort.setRTS(true);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

    }

    public static void disconnect() {
        try {
            mSerialPort.setRTS(false);
            mSerialPort.setDTR(false);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected(){
        try {
            return mSerialPort.isDSR() & mSerialPort.isCTS();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return false;
    }

}
