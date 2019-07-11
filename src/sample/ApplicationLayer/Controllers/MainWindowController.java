package sample.ApplicationLayer.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jssc.SerialPortException;
import sample.ChannnelLayer.FrameManager;
import sample.ChannnelLayer.Frames.FileSystemFrame;
import sample.ChannnelLayer.Frames.ServiceFrame;
import sample.ChannnelLayer.Tool;
import sample.Main;
import sample.PhysicalLayer.Port;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable, SettingsWindowController.SettingsInterfcae{


    private static MainWindowController mContext;
    public MenuItem mainMenuBarApplicationMenuItemSettings;
    public MenuItem mainMenuBarApplicationMenuItemAbout;
    public MenuItem mainMenuBarApplicationMenuItemExit;
    public MenuItem mainMenuBarApplicationMenuItemDevelopers;

    public Button downloadButton;
    public Button connectButton;
    public Button disconnectButton;
    public Button exitButton;
    public Label currentComPortNumber;
    public Label connectionStatus;
    public ProgressBar downloadProgressIndicator;

    public ProgressIndicator getDownloadProgressIndicator() {
        return downloadProgressIndicator;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert downloadButton != null : "downloadButton is not exists, check your fxml file";
        assert mainMenuBarApplicationMenuItemSettings != null : "mainMenuBarApplicationMenuItemSettings is not exist in fxml file";
        assert mainMenuBarApplicationMenuItemAbout != null : "mainMenuBarApplicationMenuItemAbout is not exist in fxml file";
        assert mainMenuBarApplicationMenuItemDevelopers != null : "mainMenuBarApplicationMenuItemDevelopers is not exist in fxml file";
        assert mainMenuBarApplicationMenuItemExit != null : "mainMenuBarApplicationMenuItemExit is not exist in fxml file";
        assert downloadProgressIndicator != null : "downloadProgressIndicator is not exists in fxml file";
        assert connectionStatus != null : "connectionStatus is not exists in fxml file";
        assert currentComPortNumber != null : "currentComPortNumber is not exists in fxml file";
        assert exitButton != null : "exitButton is not exists in fxml file";
        mContext = this;
        setUpMenuItems();
        setUpButtons();
        downloadProgressIndicator.setVisible(false);
    }

    @Override
    public void onClosedSettingsWindow() {
        currentComPortNumber.setText(Port.mSerialPort.getPortName());
        if(Port.mPortName != null) connectButton.setDisable(false);
    }

    public void OnConnected() {
        connectionStatus.setText("Подключено");
        downloadButton.setDisable(false);
        connectButton.setDisable(true);
        disconnectButton.setDisable(false);
    }

    public void OnDowndloading() {
        downloadButton.setDisable(true);
        connectButton.setDisable(true);
        disconnectButton.setDisable(true);
    }

    public void OnDowndloadingFinished() {
        if(FrameManager.connectionStatus){
            OnConnected();
        }else{
            OnDisconnected();
        }
    }

    public void OnDisconnected() {
        connectionStatus.setText("Отключено");
        downloadButton.setDisable(true);
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
    }

    public void setUpButtons(){
        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                try {
                    ServiceFrame serviceFrame = new ServiceFrame(FrameManager.FILE_DOWNLOAD_REQUEST);
                    Port.mSerialPort.writeBytes(serviceFrame.getPackedFrame());
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }

            }
        });
        downloadButton.setVisible(true);
        downloadButton.setDisable(true);
        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FrameManager.connect();
            }
        });
        connectButton.setDisable(true);
        disconnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FrameManager.disconnect();
            }
        });
        disconnectButton.setDisable(true);

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
            }
        });
    }

    private void setUpMenuItems(){

        mainMenuBarApplicationMenuItemExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
            }
        });

        mainMenuBarApplicationMenuItemSettings.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showSettings();
            }
        });

        mainMenuBarApplicationMenuItemAbout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    Stage stage = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("../Layouts/about_window.fxml"));
                    stage.setTitle("О приложении");
                    stage.setScene(new Scene(root, 300, 250));
                    stage.setResizable(false);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mainMenuBarApplicationMenuItemDevelopers.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Stage stage = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("../Layouts/developers_window.fxml"));
                    stage.setTitle("Разработчики");
                    stage.setScene(new Scene(root, 250, 200));
                    stage.setResizable(false);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSettings(){
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Layouts/settings_window.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SettingsWindowController controller = loader.getController();
        controller.registerInterfaceListener(mContext);
        controller.setStage(stage);
        stage.setTitle("Настройки");
        stage.setScene(new Scene(root, 400, 300));
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.mPrimaryStage);
        stage.show();
    }
}
