package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

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
        primaryStage.setOnCloseRequest(event -> clean());
    }

    private void clean(){
        new File("Tokens.txt").delete();
        new File("Hashtable.txt").delete();
        new File("tree.bin").delete();
        new File("Tree.txt").delete();
        new File("Gramatical_Tree.txt").delete();
        new File("gramatical_tree.bin").delete();
        new File("hashtable.bin").delete();
        new File("code.RJI").delete();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
