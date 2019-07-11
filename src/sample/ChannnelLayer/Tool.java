package sample.ChannnelLayer;

import sample.ChannnelLayer.Frames.DataFrame;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


public class Tool {

    private static Logger LOGGER = Logger.getLogger("TOOL");

    public static byte[] serialize(Object yourObject){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(yourObject);
            byte[] yourBytes = bos.toByteArray();
            return yourBytes;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                // ignore close exception
            }
        }
        return null;
    }

    public static Object deSerialize(byte[] yourBytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return o;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // ignore close exception
            }
        }
        return null;
    }

    public static List<List<Byte>> chopped(List<Byte> list, int frameCount) {
        List<List<Byte>> partitions = new LinkedList<List<Byte>>();
        int s = DataFrame.FILE_DATA_FRAME_DATA_FIELD_SIZE;

        for (int i = 0; i < list.size(); i += s) {
            partitions.add(list.subList(i, Math.min(i + s, list.size())));
        }
        return partitions;
    }

    public static byte[] getDataFromDataFrameInByteArray(byte[] frameData){
        List<Byte> l = new ArrayList<>();
        for (byte b : frameData) {
            l.add(b);
        }

        List<Byte> la = l.subList(2, l.size()-1);

        byte[] ba = new byte[la.size()];

        for (int i = 0; i < la.size(); i++) {
            ba[i] = la.get(i);
        }

        return ba;
    }
    public static List<Byte> getDataFromDataFrameInList(byte[] frameData){
        List<Byte> l = new ArrayList<>();
        for (byte b : frameData) {
            l.add(b);
        }

        List<Byte> la = l.subList(2, l.size()-1);

        return la;
    }

    public static List<Byte> getDataFromDataFrameInList(List<Byte> frameData){

        return frameData.subList(2, frameData.size()-1);
    }

    public static byte[] getDataFromDataFrameInByteArray(List<Byte> frameData){
        List<Byte> data;
        data = frameData.subList(2, frameData.size()-1);
        byte[] b = new byte[data.size()];
        for (int i = 0; i < data.size(); i++) {
            b[i] = data.get(i);
        }

        return b;
    }

    public static byte[] ListOfBytesToArray(List<Byte> frameList){
        byte[] ba = new byte[frameList.size()];
        for (int i = 0; i < frameList.size(); i++) {
            ba[i] = frameList.get(i);
        }
        return ba;
    }

    public static List<Byte> ByteArrayToList(byte[] data){
        List<Byte> list = new ArrayList<>();
        for (byte b : data) {
            list.add(b);
        }
        return list;
    }


}
