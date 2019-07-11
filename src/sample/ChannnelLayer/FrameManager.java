package sample.ChannnelLayer;

import javafx.application.Platform;
import jssc.SerialPortException;
import sample.ApplicationLayer.Controllers.MainWindowController;
import sample.ChannnelLayer.Frames.DataFrame;
import sample.ChannnelLayer.Frames.FileSystemFrame;
import sample.ChannnelLayer.Frames.ServiceFrame;
import sample.Main;
import sample.PhysicalLayer.Port;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrameManager {

    private static Logger LOGGER = Logger.getLogger("FRAME MANAGER");

    public static byte FRAGMENT_TYPE_POSITION = 1;
    public final static byte START_BYTE = 2;
    public final static byte END_BYTE = 3;

    // frames
    public final  static byte LINK = 4; // info
    public final static byte LINK_CONFIRMED = 5; // info
    public final static byte LINK_DECLINED = 6; // info

    public final static byte UPLINK = 7; // info
    public final static byte UPLINK_CONFIRMED = 8; // info

    public final static byte FILE_DOWNLOAD_REQUEST = 9; // info
    public final static byte FILE_DOWNLOAD_ACCEPTED = 10; // info
    public final static byte FILE_DOWNLOAD_DECLINED = 11; // info

    public final static byte FILE_SYSTEM_REQUEST = 12; // info
    public final static byte FILE_SYSTEM_REQUEST_SENDED = 13; // file system frame

    public final static byte FILE_DOWNLOAD_FILE_ADDRESS = 14; // data

    public final static byte FILE_DATA_FRAME = 15; // data
    public final static byte FILE_DATA_FRAME_CONFIRMED = 16; // info
    public final static byte FILE_DATA_FRAME_FAIL = 17; // info


    public final static byte FILE_NAME = 18;
    public final static byte FILE_NAME_CONFIRMED = 19;
    public final static byte FILE_UPLOAD_FINISHED = 20;

    public final static byte FILE_LENGHT_CONFIRMED = 21;

    public final static byte FILE_UPLOAD_BREAK = 22;

    public final static byte FILE_LENGHT = 23;
    // frames

    public static void connect(){
        ServiceFrame serviceFrame = new ServiceFrame(LINK);
        try {
            Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        ServiceFrame serviceFrame = new ServiceFrame(UPLINK);
        try {
            Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public static void frameRecognize(final ArrayList<Byte> frameData){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                frameRecognizeHandler(frameData);
            }
        });
        thread.run();
    }

    private static void frameRecognizeHandler(ArrayList<Byte> frameData){
        switch (frameData.get(FRAGMENT_TYPE_POSITION)){

            ////////////////////////// LOGICAL CONNECTION
            case LINK:
                handleLINK();
                break;
            case LINK_CONFIRMED:
                handleLINKC();
                break;
            case LINK_DECLINED:
                handleLINKD();
                break;
            case UPLINK:
                handleUPLINK();
                break;
            case UPLINK_CONFIRMED:
                handleUPLINKC();
                break;
            ////////////////////////// LOGICAL CONNECTION

            ////////////////////////// FILE DOWNLOAD REQUESTS
            case FILE_DOWNLOAD_REQUEST:
                handleFDR();
                break;
            case FILE_DOWNLOAD_ACCEPTED:
                handleFDA();
                break;
            case FILE_DOWNLOAD_DECLINED:
                handleFDD();
                break;
            ////////////////////////// FILE UPLOAD SECTION

            ////////////////////////// FILE SYSTEM REQUEST
            case FILE_SYSTEM_REQUEST:
                handleFSR();
                break;
            case FILE_SYSTEM_REQUEST_SENDED:
                handleFSRS(frameData);
                break;
            case FILE_DOWNLOAD_FILE_ADDRESS:
                handleFDFA(frameData);
                break;

            // FILE UPLOADING
            case FILE_NAME:
                handleFN(frameData);
                break;
            case FILE_NAME_CONFIRMED:
                handleFNC();
                break;
            case FILE_LENGHT:
                handleFL(frameData);
                break;
            case FILE_LENGHT_CONFIRMED:
                handleFLC();
                break;
            case FILE_DATA_FRAME:
                handleFDF(frameData);
                break;
            case FILE_DATA_FRAME_CONFIRMED:
                handleFDFC();
                break;
            case FILE_UPLOAD_FINISHED:
                handleFUF();
                break;
            case FILE_DATA_FRAME_FAIL:
                handleFDFF();
                break;
            case FILE_UPLOAD_BREAK:
                handleFUB();
                break;
            ////////////////////////// FILE SYSTEM REQUEST
        }
    }

    ////////////////////////// LOGICAL CONNECTION
    private static void handleLINK(){ // server

        if(JOptionPane.showConfirmDialog(null, "Разрешить логическое соединение?",
                "Соединение", JOptionPane.YES_NO_OPTION) == 0) // YES
        {
            connectionStatus = true;
            ServiceFrame serviceFrame = new ServiceFrame(LINK_CONFIRMED);
            try {
                Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }

            Platform.runLater(new Runnable() {
                public void run() {
                    Main.mMainWindowController.OnConnected();
                }
            });
        }else{
            connectionStatus = false;
            ServiceFrame serviceFrame = new ServiceFrame(LINK_DECLINED);
            try {
                Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }

            Platform.runLater(new Runnable() {
                public void run() {
                    Main.mMainWindowController.OnDisconnected();
                }
            });
        }
    }

    private static void handleLINKC(){ // client
        connectionStatus = true;
        Platform.runLater(new Runnable() {
            public void run() {
                Main.mMainWindowController.OnConnected();
            }
        });
        JOptionPane.showConfirmDialog(null, "Запрос был принят", "Соединение", JOptionPane.CANCEL_OPTION);
    }

    private static void handleLINKD(){
        connectionStatus = false;
        Platform.runLater(new Runnable() {
            public void run() {
                Main.mMainWindowController.OnDisconnected();
            }
        });
        JOptionPane.showConfirmDialog(null, "Запрос был отклонен", "Соединение", JOptionPane.CANCEL_OPTION);
    }

    private static void handleUPLINK(){ // server
        ServiceFrame serviceFrame = new ServiceFrame(UPLINK_CONFIRMED);
        try {
            Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            public void run() {
                Main.mMainWindowController.OnDisconnected();
            }
        });
    }

    private static void handleUPLINKC(){ // client
        connectionStatus = false;
        Platform.runLater(new Runnable() {
            public void run() {
                Main.mMainWindowController.OnDisconnected();
            }
        });
        JOptionPane.showConfirmDialog(null, "Соединение было разорвано", "Соединение", JOptionPane.CANCEL_OPTION);
    }
    ////////////////////////// LOGICAL CONNECTION



    ////////////////////////// FILE DOWNLOAD REQUESTS
    private static void handleFDR(){ // server
        if(JOptionPane.showConfirmDialog(null, "Разрешить загрузку файла?", "Запрос на загрузку файла", JOptionPane.YES_NO_OPTION) == 0) // YES = 0
        {
            ServiceFrame serviceFrame = new ServiceFrame(FrameManager.FILE_DOWNLOAD_ACCEPTED);
            try {
                Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Main.mMainWindowController.OnDowndloading();
                }
            });
        }
        else {
            ServiceFrame serviceFrame = new ServiceFrame(FrameManager.FILE_DOWNLOAD_DECLINED);
            try {
                Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Main.mMainWindowController.OnDowndloadingFinished();
                }
            });
        }
    }

    private static void handleFDA(){ // client
        ServiceFrame serviceFrame = new ServiceFrame(FILE_SYSTEM_REQUEST);
        try {
            Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Main.mMainWindowController.OnDowndloading();
            }
        });
    }

    private static void handleFDD(){ // client
        JOptionPane.showConfirmDialog(null, "Загрузка была отклонена", "Запрос на загрузку файла", JOptionPane.CANCEL_OPTION);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Main.mMainWindowController.OnDowndloadingFinished();
            }
        });
    }
    ////////////////////////// FILE DOWNLOAD REQUESTS




    ////////////////////////// FILE UPLOAD SECTION
    public static boolean connectionStatus;

    private static void handleFSR(){ // server
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите файл");
        try {
            FileSystemFrame frame = new FileSystemFrame(FrameManager.FILE_SYSTEM_REQUEST_SENDED);
            frame.setData(Tool.serialize(fileChooser));
            Port.mSerialPort.writeBytes(frame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private static void handleFSRS(ArrayList<Byte> frameData){ // client

        List<Byte> l = frameData.subList(2, frameData.size()-1);
        byte[] ba = new byte[l.size()];
        for (int i = 0; i < l.size(); i++) {
            ba[i] = l.get(i);
        }


        JFileChooser fileChooser = (JFileChooser) Tool.deSerialize(ba);
        File file = null;
        if(fileChooser.showOpenDialog(null) == 0) // YES
        {
            file = fileChooser.getSelectedFile();
        } else {
            JOptionPane.showConfirmDialog(null, "Загрузка прервана", "Запрос на загрузку файла", JOptionPane.CANCEL_OPTION);
            Main.mMainWindowController.OnDowndloadingFinished();
            ServiceFrame serviceFrame = new ServiceFrame(FrameManager.FILE_UPLOAD_BREAK);
            try {
                Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            return;
        }
        LOGGER.log(Level.WARNING,"Path file size before send: " + String.valueOf(file.length()));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Main.mMainWindowController.OnDowndloading();
                Main.mMainWindowController.getDownloadProgressIndicator().setProgress(0);
            }
        });


        DataFrame dataFrame = new DataFrame(FILE_DOWNLOAD_FILE_ADDRESS);
        try {
            dataFrame.setData(file.getCanonicalPath().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Port.mSerialPort.writeBytes(dataFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private static void handleFDFA(ArrayList<Byte> frameData){ // server
        String filePath;
        List<Byte> l = Tool.getDataFromDataFrameInList(frameData);
        byte[] ba = Tool.ListOfBytesToArray(l);
        LOGGER.log(Level.WARNING,"Path file size byte[]: " + String.valueOf(ba.length));
        try {
            filePath = new String(ba, "UTF-8");
            LOGGER.log(Level.WARNING,"Path file: " + filePath);


            File file = new File(filePath);

            //
            byte [] bytearray = new byte [(int)file.length()];
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedInputStream bin = new BufferedInputStream(fin);
            try {
                bin.read(bytearray,0,bytearray.length);
            } catch (IOException e) {
                e.printStackTrace();
            }




            List<Byte> serializedFileList = Tool.ByteArrayToList(bytearray);

            LOGGER.log(Level.WARNING,"Downloaded file size byte[]: " + String.valueOf(bytearray.length));

            LOGGER.log(Level.WARNING,"Downloaded file size byte[]: " + String.valueOf(bytearray.length));
            LOGGER.log(Level.WARNING,"Downloaded file size list: " + String.valueOf(serializedFileList.size()));


            // SET FRAME PACKAGE
            int frameDataSize = DataFrame.FILE_DATA_FRAME_DATA_FIELD_SIZE;
            int dataSize = serializedFileList.size();
            int frameCount;

            if( ( (float)dataSize / frameDataSize - (int) dataSize/frameDataSize ) == 0 ){
                frameCount = dataSize/frameDataSize;
                mFramesPacketServer = Tool.chopped(serializedFileList, frameCount);
            }
            else {
                frameCount = dataSize/frameDataSize + 1;
                mFramesPacketServer = Tool.chopped(serializedFileList, frameCount);
            }

            LOGGER.log(Level.WARNING,"Frame count: " + String.valueOf(frameCount));
            LOGGER.log(Level.WARNING,"FramePacket size: " + String.valueOf(mFramesPacketServer.size()));
            mFileLenghtServer = file.length();



            // SEND FILE NAME // start frame
            DataFrame startframe = new DataFrame(FrameManager.FILE_NAME);
            startframe.setData(file.getName().getBytes(StandardCharsets.UTF_8));
            try {
                Port.mSerialPort.writeBytes(startframe.getPackedFrame());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Main.mMainWindowController.OnDowndloading();
                    Main.mMainWindowController.getDownloadProgressIndicator().setProgress(0);
                }
            });



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    private static void handleFDF(ArrayList<Byte> frameData){ // client

        try {
            mDownloadedFileClient.addAll(Tool.ByteArrayToList(CRCCoder.decode(Tool.getDataFromDataFrameInByteArray(frameData))));
        } catch (Exception e) {
            e.printStackTrace();

            ServiceFrame serviceFrame = new ServiceFrame(FrameManager.FILE_DATA_FRAME_FAIL);

            try {
                Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
            } catch (SerialPortException e1) {
                e1.printStackTrace();
            }

            return;
        }


        ServiceFrame confirmFrame = new ServiceFrame(FrameManager.FILE_DATA_FRAME_CONFIRMED);
        try {
            Port.mSerialPort.writeBytes(confirmFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        updateProgressDownload();
    }

    private static void handleFDFC(){ // server
        mFrameNumberSendedServer++;

//        if (mFrameNumberSendedServer >= mFramesPacketServer.size()) return;

        updateProgressSend();
        if(mFrameNumberSendedServer < mFramesPacketServer.size()){
            DataFrame dataFrame = new DataFrame(FILE_DATA_FRAME);
            dataFrame.setData(CRCCoder.encode(Tool.ListOfBytesToArray(mFramesPacketServer.get(mFrameNumberSendedServer))));
            try {
                Port.mSerialPort.writeBytes(dataFrame.getPackedFrame());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
        if(mFrameNumberSendedServer == mFramesPacketServer.size()){
            ServiceFrame serviceFrame = new ServiceFrame(FILE_UPLOAD_FINISHED);
            try {
                Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            mFrameNumberSendedServer = 0;
            mFramesPacketServer.clear();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Main.mMainWindowController.OnDowndloadingFinished();
                }
            });
        }
    }

    private static void handleFUF() {  // client
        LOGGER.log(Level.WARNING,"Downloaded file size: " + String.valueOf(mDownloadedFileClient.size()));
        byte[] fileBytes = Tool.ListOfBytesToArray(mDownloadedFileClient);

        LOGGER.log(Level.WARNING,"Downloaded file name: "+ mFileNameClient);
        FileOutputStream fos = null;

        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save binary file as:");
        fileChooser.setName(mFileNameClient);
        fileChooser.setSelectedFile(new File(mFileNameClient));
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            try {
                fos = new FileOutputStream(fileToSave.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                fos.write(fileBytes);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mDownloadedFileClient.clear();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Main.mMainWindowController.OnDowndloadingFinished();
            }
        });
    }

    private static List<List<Byte>> mFramesPacketServer;
    private static int mFrameNumberSendedServer = 0;
    private static String mFileNameServer;
    private static long mFileLenghtServer = 0;


    private static List<Byte> mDownloadedFileClient = new ArrayList<>();
    private static String mFileNameClient;
    private static long mFileLenghtClient;

    private static void handleFN(ArrayList<Byte> frameData){ // client
        byte[] fileNameBytes = Tool.getDataFromDataFrameInByteArray(frameData);
        try {
            mFileNameClient = new String(fileNameBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Main.mMainWindowController.getDownloadProgressIndicator().setVisible(true);

        ServiceFrame serviceFrame = new ServiceFrame(FILE_NAME_CONFIRMED);
        try {
            Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private static void handleFL(ArrayList<Byte> frameData){  // client
        byte[] fileLenght = Tool.getDataFromDataFrameInByteArray(frameData);
        mFileLenghtClient = java.nio.ByteBuffer.wrap(fileLenght).getLong();
        System.out.println(mFileLenghtClient + " FILE LENGHT CLIENT");

        // SEND FILE LENGHT CONFIRMED
        ServiceFrame dataFrame = new ServiceFrame(FILE_LENGHT_CONFIRMED);
        try {
            Port.mSerialPort.writeBytes(dataFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }


    private static void handleFNC(){ // server

        // SEND FILE LENGHT
        DataFrame lenghtframe = new DataFrame(FILE_LENGHT);
        lenghtframe.setData(ByteBuffer.allocate(8).putLong(mFileLenghtServer).array());
        try {
            Port.mSerialPort.writeBytes(lenghtframe.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        Main.mMainWindowController.getDownloadProgressIndicator().setVisible(true);

    }
    private static void handleFLC(){ // server
        // SEND FIRST FILE DATA FRAME
        DataFrame dataFrame = new DataFrame(FILE_DATA_FRAME);
        dataFrame.setData(CRCCoder.encode(Tool.ListOfBytesToArray(mFramesPacketServer.get(0)))); // initial is 0;
        try {
            Port.mSerialPort.writeBytes(dataFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private static void handleFDFF(){ // server FAIL Frame
        LOGGER.log(Level.INFO, "FILE DATA FRAME FAIL");
        DataFrame dataFrame = new DataFrame(FILE_DATA_FRAME);
        dataFrame.setData(Tool.ListOfBytesToArray(mFramesPacketServer.get(mFrameNumberSendedServer)));
        try {
            Port.mSerialPort.writeBytes(dataFrame.getPackedFrame());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private static void handleFUB(){
        if (mFramesPacketServer != null)
            mFramesPacketServer.clear();
        mFrameNumberSendedServer = 0;
        mFileNameServer = null;
        mFileLenghtServer = 0;

        if (mDownloadedFileClient != null)
            mDownloadedFileClient.clear();
        mFileLenghtClient = 0;
        mFileNameClient = null;

        JOptionPane.showConfirmDialog(null, "Загрузка прервана", "Загрузка", JOptionPane.CANCEL_OPTION);
        Main.mMainWindowController.OnDowndloadingFinished();
    }
    ////////////////////////// FILE UPLOAD SECTION

    private static void updateProgressDownload() {
        final double percent = (double) mDownloadedFileClient.size() / mFileLenghtClient;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main.mMainWindowController.getDownloadProgressIndicator().setProgress(percent);
            }
        });
    }

    private static void updateProgressSend() {
        final double percent = (double) mFrameNumberSendedServer * DataFrame.FILE_DATA_FRAME_DATA_FIELD_SIZE / mFileLenghtServer;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main.mMainWindowController.getDownloadProgressIndicator().setProgress(percent);
            }
        });
    }
}
