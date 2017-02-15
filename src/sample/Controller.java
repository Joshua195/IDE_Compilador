package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import model.Archivo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class Controller {

    @FXML
    MenuItem menuItemNew;

    @FXML
    MenuItem menuItemOpen;

    @FXML
    MenuItem menuItemSave;

    @FXML
    MenuItem menuItemSaveAs;

    @FXML
    Button buttonNew;

    @FXML
    Button buttonSave;

    @FXML
    Button buttonOpen;

//    @FXML
//    TabPane tabPaneCode;
    @FXML
    TextArea textAreaCode;
    @FXML
    Label labelContentStatus;
    @FXML
    Label labelContentWord;
    @FXML
    Label labelContentProgress;

    @FXML
    ListView<Archivo> historyFiles;
    private ObservableList<Archivo> observableListFileData = FXCollections.observableArrayList();

/*
Salidas
 */
    @FXML
    private TextArea textAreaLexico;
    @FXML
    private TextArea textAreaSintactico;
    @FXML
    private TextArea textAreaSemantico;
    @FXML
    private TextArea textAreaCodigoIntermedio;
    @FXML
    private TextArea textAreaErrores;
    @FXML
    private TextArea textAreaSalida;
    @FXML
    private TextArea textAreaTablaSimbolos;

/******************/

    private String fileActive;

    @FXML
    private void initialize() {
        initEvents();
        initButtons();
        initList();
    }

    private void initEvents(){
        menuItemNew.setOnAction(event -> newFile());
        menuItemSave.setOnAction(event -> saveFile());
        menuItemOpen.setOnAction(event -> openFile());
        buttonNew.setOnAction(event -> newFile());
        buttonSave.setOnAction(event -> saveFile());
        buttonOpen.setOnAction(event -> openFile());
        textAreaCode.setOnKeyTyped(event -> countChar());

        historyFiles.setOnMouseClicked(event -> openListViewItem(historyFiles.getSelectionModel().getSelectedItem()));
    }

    private void initButtons(){
        Image imageNewFile = new Image(getClass().getResourceAsStream("new-file.png"));
        buttonNew.setGraphic(new ImageView(imageNewFile));

        Image imageOpenFile = new Image(getClass().getResourceAsStream("open-file.png"));
        buttonOpen.setGraphic(new ImageView(imageOpenFile));

        Image imageSaveFile = new Image(getClass().getResourceAsStream("save-file.png"));
        buttonSave.setGraphic(new ImageView(imageSaveFile));
    }

    private void newFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("New File...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("RJ", "*.rj")
        );
        File file = fileChooser.showSaveDialog(Main.mainStage);
        if (file != null){
            Archivo archivo = new Archivo(file.getName(),file.getAbsolutePath());
            System.out.println("Name of file: " + archivo.getName());
            System.out.println("Route File: " + archivo.getLocation());
            labelContentStatus.setText("Archivo actual -> " + archivo.getName());
            fileActive = archivo.getName();
            observableListFileData.add(archivo);
            textAreaCode.setDisable(false);
            textAreaCode.requestFocus();
        }
    }

    private void saveFile(){
        for (Archivo archivo : observableListFileData) {
            if (archivo.getName().equals(fileActive)) {
                File file = new File(archivo.getLocation());
                String code = textAreaCode.getText();
                BufferedWriter bufferedWriter;
                try {
                    bufferedWriter = new BufferedWriter(new FileWriter(file));
                    bufferedWriter.write(code);
                    bufferedWriter.close();
                    labelContentProgress.setText("Archivo guardado");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("RJ", "*.rj")
        );
        File file = fileChooser.showOpenDialog(Main.mainStage);
        if (file != null) {
            Archivo archivo = new Archivo(file.getName(), file.getAbsolutePath());
            try {
                String code = new String(Files.readAllBytes(Paths.get(archivo.getLocation())));
                textAreaCode.setText(code);
                textAreaCode.requestFocus();
                textAreaCode.setDisable(false);
                fileActive = archivo.getName();
                labelContentStatus.setText("Archivo actual -> " + archivo.getName());
                if (observableListFileData.size() == 0) {
                    observableListFileData.add(archivo);
                }
                for (Archivo aux : observableListFileData) {
                    if (!aux.getName().equals(archivo.getName()) && !aux.getLocation().equals(archivo.getLocation())) {
                        observableListFileData.add(archivo);
                        break;
                    }
                }
                labelContentWord.setText(String.valueOf(code.length()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initList(){
        historyFiles.setItems(observableListFileData);
    }

    private void countChar(){
        String text = textAreaCode.getText();
        if (!text.isEmpty()){
            labelContentWord.setText(String.valueOf(text.length()));
        }else {
            labelContentWord.setText("0");
        }
    }

    private void error(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Archivo...");
        alert.setHeaderText(null);
        alert.setContentText("Error al abrir el archivo");
        alert.showAndWait();
    }

    private void openListViewItem(Archivo archivo){
        File file = new File(archivo.getLocation());
        if (file.exists()){
            try {
                String code = new String(Files.readAllBytes(Paths.get(archivo.getLocation())));
                textAreaCode.setText(code);
                textAreaCode.requestFocus();
                textAreaCode.setDisable(false);
                fileActive = archivo.getName();
                labelContentStatus.setText("Archivo actual -> " + archivo.getName());
                labelContentWord.setText(String.valueOf(code.length()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            error();
        }
    }
}
