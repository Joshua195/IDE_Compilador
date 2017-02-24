package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Archivo;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

public class Controller {

    @FXML
    MenuItem menuItemNew;
    @FXML
    private MenuItem menuItemClose;
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
    @FXML
    TextArea textAreaCode;
    @FXML
    TextArea textAreaCountLines;

    @FXML
    Label labelContentStatus;
    @FXML
    Label labelContentWord;
    @FXML
    Label labelContentProgress;

    @FXML
    ListView<Archivo> historyFiles;
    private ObservableList<Archivo> observableListFileData = FXCollections.observableArrayList();

    @FXML
    private Label labelcolumRow;

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
        fileActive = "";
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
        textAreaCode.setOnKeyReleased(event -> counter());
        textAreaCode.setOnMouseClicked(event -> counter());
        historyFiles.setOnMouseClicked(event -> openListViewItem(historyFiles.getSelectionModel().getSelectedItem()));
        textAreaCode.scrollTopProperty().bindBidirectional(textAreaCountLines.scrollTopProperty());
        menuItemSaveAs.setOnAction(event -> saveAsFile());
        menuItemClose.setOnAction(event -> Main.mainStage.close());
    }

    private void initButtons(){
        Image imageNewFile = new Image(getClass().getResourceAsStream("new-file.png"));
        buttonNew.setGraphic(new ImageView(imageNewFile));

        Image imageOpenFile = new Image(getClass().getResourceAsStream("open-file.png"));
        buttonOpen.setGraphic(new ImageView(imageOpenFile));

        Image imageSaveFile = new Image(getClass().getResourceAsStream("save-file.png"));
        buttonSave.setGraphic(new ImageView(imageSaveFile));
    }

    private void newFile() {
        if (fileActive.equals("new")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Archivo...");
            alert.setHeaderText(null);
            alert.setContentText("Archivo no guardado ¿Deseas Guardarlo?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                saveAsFile();
            } else {
                activateTextCode();
            }
        }else {
            activateTextCode();
        }
    }

    private void activateTextCode(){
        textAreaCode.setDisable(false);
        textAreaCode.requestFocus();
        textAreaCode.setText("");
        countChar();
        fileActive = "new";
    }

    private void saveFile(){
        if (fileActive.equals("new")){
            saveAsFile();
        }else {
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
    }

    private void saveAsFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("RJ", "*.rj")
        );
        File file = fileChooser.showSaveDialog(Main.mainStage);
        String code = textAreaCode.getText();
        BufferedWriter bufferedWriter;
        try {
            Archivo archivo = new Archivo(file.getName(), file.getAbsolutePath());
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(code);
            bufferedWriter.close();
            labelContentProgress.setText("Archivo Guardado...");
            observableListFileData.add(archivo);
            fileActive = archivo.getName();
            labelContentStatus.setText("Archivo actual -> " + archivo.getName());
            buttonSave.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
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
                countChar();    //Adición
                historyFiles.getSelectionModel().select(observableListFileData.size()-1); //Adición para focus
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
        String lines = "";
        for (int i = 1; i < countLines() + 1; i++){
            lines += i + "\n";
        }
        textAreaCountLines.setText(lines);
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
                countChar();    //Adición
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            error();
        }
    }

    private int countLines(){
        String text = textAreaCode.getText();
        int count = 0;
        for (int  i = 0; i < text.length(); i++){
            if (text.charAt(i) == '\n'){
                count++;
            }
        }
        return count + 1;
    }

    /* Cuenta el numero de renglones y los guarda en un Arraylist<String> */
    private void counter(){
        ArrayList<String> arrayList = new ArrayList<>();
        String text = textAreaCode.getText();
        String linea = "";
        for (int  i = 0; i < text.length(); i++){
            if (text.charAt(i) != '\n'){
                linea += text.charAt(i);
            }else{
                arrayList.add(linea);
                linea = "";
            }
        }
        arrayList.add(linea);
        coordinates(arrayList);
    }

    private void check(ArrayList<String> array){
        System.out.println("Numero de renglones: " + array.size());
        for (int i = 0; i < array.size(); i++){
            System.out.println((i+1) + ": " + array.get(i));
        }
    }

    private void coordinates(ArrayList<String> renglones){
        int x = 0;
        int filas = 1;
        int caret = textAreaCode.getCaretPosition();
//        System.out.println("Caret: " + caret);

        int i = 0; // Para recorrer el arrayList
        while(caret >= 0){  // Mientras la posicion actual aún no se alcance (-1 es condición de paro)
            String renglon = renglones.get(i);  // Trabajamos con renglon.length actúal
            if(renglon.length() < caret){   //Si el renglon es menor al caret, la el cursor no está aquí aún
                caret -= (renglon.length()+1);
                filas++;
            }else if(renglon.length()-caret == 0){ //El cursor está aqui, y está en x = 0
                x = caret;  // x = 0
                caret = -1; // Detenemos while
            }else{ // Si el renglon es mayor al caret, entonces el caret está en este renglon, y está en lo que resta del caret
                x = caret;
                caret = -1; //Detenemos while
            }
            i++;
        }
        labelcolumRow.setText(filas + ":" + x);
    }
}
