package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.ApplicationLayer.Controllers.MainWindowController;

import java.net.URL;

public class Main extends Application {

    public static Stage mPrimaryStage;
    public static MainWindowController mMainWindowController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mPrimaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ApplicationLayer/Layouts/main_window.fxml"));
        loader.load();
        mMainWindowController = loader.getController();
        primaryStage.setTitle("Курсовая работа");
        primaryStage.setScene(new Scene(loader.getRoot(), 540, 430));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
