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
        boolean tokens = new File("Tokens.txt").delete();
        boolean hashtable = new File("Hashtable.txt").delete();
        boolean treeBin = new File("tree.bin").delete();
        boolean tree = new File("Tree.txt").delete();
        boolean gramaticalTree = new File("Gramatical_Tree.txt").delete();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
