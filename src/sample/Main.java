package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage mainStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Main.mainStage = primaryStage;
        primaryStage.setTitle("IDE: J-Developer");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
        Scene mainScene = new Scene(root);
        mainScene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
