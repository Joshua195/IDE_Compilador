package sample;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import model.Archivo;
import model.Token;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    private Button buttonLexico;
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

    private Archivo fileActive;

    private CodeArea textAreaCode;
    @FXML
    private StackPane stackPane;

    @FXML
    private TableColumn tableColumnType;
    @FXML
    private TableColumn tableColumnLexema;
    @FXML
    private TableColumn tableColumnRow;
    @FXML
    private TableColumn tableColumnColumn;
    @FXML
    private TableView<Token> tokenTableView;
    private ObservableList<Token> tokenObservableList = FXCollections.observableArrayList();



    @FXML
    private void initialize() {
        fileActive = new Archivo("new","");
        initAreaCode();
        initEvents();
        initButtons();
        initList();
        initColumns();
    }

    private void initAreaCode(){
        CodeAreaControl codeAreaControl = new CodeAreaControl();
        textAreaCode = codeAreaControl.getCodeArea();
        textAreaCode.setDisable(true);
        stackPane.getChildren().add(new VirtualizedScrollPane<>(textAreaCode));
    }

    private void initColumns(){
        tableColumnType.setCellValueFactory(new PropertyValueFactory<Token,String>("type"));
        tableColumnLexema.setCellValueFactory(new PropertyValueFactory<Token, String>("lexema"));
        tableColumnRow.setCellValueFactory(new PropertyValueFactory<Token,String>("row"));
        tableColumnColumn.setCellValueFactory(new PropertyValueFactory<Token, String>("column"));
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
        menuItemSaveAs.setOnAction(event -> saveAsFile());
        menuItemClose.setOnAction(event -> Main.mainStage.close());
        buttonLexico.setOnAction(event -> initLexico());
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
        if (fileActive.getName().equals("new")) {
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
        textAreaCode.replaceText("");
        countChar();
        fileActive.setName("new");
    }

    private void saveFile(){
        if (fileActive.getName().equals("new")){
            saveAsFile();
        }else {
            Archivo archivo = observableListFileData.get(observableListFileData.indexOf(fileActive));
//            for (Archivo archivo : observableListFileData) {
//                if (archivo.getName().equals(fileActive)) {
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
//            }
//        }
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
            fileActive = archivo;
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
                textAreaCode.replaceText(code);
                textAreaCode.requestFocus();
                textAreaCode.setDisable(false);
                fileActive = archivo;
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
                textAreaCode.replaceText(code);
                textAreaCode.requestFocus();
                textAreaCode.setDisable(false);
                fileActive = archivo;
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

    private void initLexico(){
        tokenObservableList.clear();
        if (fileActive.getName().equals("new")){
            saveAsFile();
        }else {
            Process process = null;
            try {
                List<String> command = new ArrayList<>();
                command.add("python");
                command.add("D:\\PycharmProjects\\untitled\\Test.py");
                command.add("\"" + fileActive.getLocation() + "\"");
                ProcessBuilder pb = new ProcessBuilder(command);
                process = pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert process != null;
            List<Token> tokens = output(process.getInputStream());
            tokenObservableList.addAll(tokens);
            tokenTableView.setItems(tokenObservableList);
        }
    }

    private List<Token> output(InputStream inputStream){
        StringBuilder sb = new StringBuilder();
        List<Token> tokens = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.getProperty("line.separator"));
                String lineToken[] = line.split("#");
                Token token = new Token(lineToken[0],lineToken[1],lineToken[2],lineToken[3]);
                tokens.add(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    private void setEnableButtons(boolean status){
        buttonLexico.setDisable(status);
    }
}