package sample.ApplicationLayer.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPort;
import jssc.SerialPortList;
import sample.PhysicalLayer.Port;

import java.net.URL;
import java.util.ResourceBundle;

//import javax.
public class SettingsWindowController implements Initializable {
    interface SettingsInterfcae{
        public void onClosedSettingsWindow();
    }

    private Stage mStage;

    private SettingsInterfcae mListener;

    @FXML
    private ChoiceBox settingsComPort;

    @FXML
    private ChoiceBox settingsSpeed;

    @FXML
    private ChoiceBox settingsParity;

    @FXML
    private ChoiceBox settingsStopBits;

    @FXML
    private ChoiceBox settingsDataBits;

    @FXML
    private Button settingsOkButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert settingsComPort != null : "settingsComPort is not existed in fxml file";
        assert settingsSpeed != null : "settingsSpeed is not existed in fxml file";
        assert settingsParity != null : "settingsParity is not existed in fxml file";
        assert settingsStopBits != null : "settingsStopBits is not existed in fxml file";
        assert settingsDataBits != null : "settingsDataBits is not existed in fxml file";
        assert settingsOkButton != null : "settingsOkButton is not existed in fxml file";


        setUpChooseBoxes();
        settingsOkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if ( Port.mSerialPort == null )
                    Port.init(settingsComPort.getValue().toString());

                Port.setParams(
                        Integer.parseInt(settingsSpeed.getValue().toString()),
                        Integer.parseInt(settingsDataBits.getValue().toString()),
                        Integer.parseInt(settingsStopBits.getValue().toString()),
                        Integer.parseInt(settingsParity.getValue().toString())
                );
                settingsOkButton.getScene().getWindow().hide();
                mListener.onClosedSettingsWindow();
            }
        });

    }

    public void setStage(Stage stage){
        mStage = stage;
    }

    public void registerInterfaceListener(MainWindowController obj){
        mListener = (SettingsInterfcae) obj;
    }

    private void setUpChooseBoxes(){

        String[] portList = Port.getAvailablePorts();
        settingsComPort.setItems(FXCollections.observableArrayList(
                portList
        ));
        settingsComPort.setTooltip(new Tooltip("Выбирите COM-порт"));
        settingsComPort.getSelectionModel().selectFirst();

        settingsSpeed.setItems(FXCollections.observableArrayList(
                SerialPort.BAUDRATE_110,
                SerialPort.BAUDRATE_300,
                SerialPort.BAUDRATE_600,
                SerialPort.BAUDRATE_1200,
                SerialPort.BAUDRATE_4800,
                SerialPort.BAUDRATE_9600,
                SerialPort.BAUDRATE_14400,
                SerialPort.BAUDRATE_19200,
                SerialPort.BAUDRATE_38400,
                SerialPort.BAUDRATE_57600,
                SerialPort.BAUDRATE_115200,
                SerialPort.BAUDRATE_128000,
                SerialPort.BAUDRATE_256000
        ));
        settingsSpeed.setTooltip(new Tooltip("Выбирите скорость"));
        settingsSpeed.getSelectionModel().selectFirst();

        settingsParity.setItems(FXCollections.observableArrayList(
                SerialPort.PARITY_EVEN,
                SerialPort.PARITY_MARK,
                SerialPort.PARITY_SPACE,
                SerialPort.PARITY_ODD,
                SerialPort.PARITY_NONE
        ));
        settingsParity.setTooltip(new Tooltip("Выбирите четность"));
        settingsParity.getSelectionModel().selectLast();

        settingsStopBits.setItems(FXCollections.observableArrayList(
                SerialPort.STOPBITS_1,
                SerialPort.STOPBITS_1_5,
                SerialPort.STOPBITS_2
        ));
        settingsStopBits.setTooltip(new Tooltip("Вибирите стоп-биты"));
        settingsStopBits.getSelectionModel().selectFirst();

        settingsDataBits.setItems(FXCollections.observableArrayList(
                SerialPort.DATABITS_5,
                SerialPort.DATABITS_6,
                SerialPort.DATABITS_7,
                SerialPort.DATABITS_8
        ));
        settingsDataBits.setTooltip(new Tooltip("Вибирите биты данных"));
        settingsDataBits.getSelectionModel().selectFirst();
    }
}
