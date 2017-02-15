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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    ObservableList<Archivo> observableListFileData = FXCollections.observableArrayList();

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

    public void initEvents(){

        menuItemNew.setOnAction(event -> newFile());

        menuItemSave.setOnAction(event -> saveFile());

        buttonNew.setOnAction(event -> newFile());

        textAreaCode.setOnKeyTyped(event -> countChar());
    }

    public void initButtons(){
        Image imageNewFile = new Image(getClass().getResourceAsStream("new-file.png"));
        buttonNew.setGraphic(new ImageView(imageNewFile));

        Image imageOpenFile = new Image(getClass().getResourceAsStream("open-file.png"));
        buttonOpen.setGraphic(new ImageView(imageOpenFile));

        Image imageSaveFile = new Image(getClass().getResourceAsStream("save-file.png"));
        buttonSave.setGraphic(new ImageView(imageSaveFile));
    }

    public void newFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("New File...");
        File file = fileChooser.showSaveDialog(Main.mainStage);
        if (file != null){
            Archivo archivo = new Archivo(file.getName() + ".rj",file.getAbsolutePath());
            System.out.println("Name of file: " + archivo.getName());
            System.out.println("Route File: " + archivo.getLocation());
            labelContentStatus.setText("Archivo actual -> " + archivo.getName());
            fileActive = archivo.getName();
            observableListFileData.add(archivo);
            textAreaCode.setDisable(false);
            textAreaCode.requestFocus();
        }
    }

    public void saveFile(){
        ArrayList<Archivo> archivos = new ArrayList<>();

        for (int i = 0; i < observableListFileData.size(); i++){
            if (observableListFileData.get(i).getName().equals(fileActive)){
                Archivo archivo = observableListFileData.get(i);
                File file = new File(archivo.getLocation());
                if (file != null){
                    String code = textAreaCode.getText();
                    BufferedWriter bufferedWriter = null;
                    try {
                        bufferedWriter = new BufferedWriter(new FileWriter(file));
                        bufferedWriter.write(code);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            bufferedWriter.close();
                            labelContentProgress.setText("Archivo guardado");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void initList(){
        historyFiles.setItems(observableListFileData);
    }

    public void countChar(){
        String text = textAreaCode.getText();
        if (!text.isEmpty()){
            labelContentWord.setText(String.valueOf(text.length()));
        }else {
            labelContentWord.setText("0");
        }

    }
}
