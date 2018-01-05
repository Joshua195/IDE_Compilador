package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import model.Archivo;
import model.LexicoToken;
import model.SemanticoToken;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {


    public static final String PATH = "C:\\Users\\Richa\\PycharmProjects\\Compiler\\";
//    public static final String PATH = "E:\\Usuarios\\Joshua\\IdeaProjects\\Compiler_v3\\";

    /*
    * Vistas Globales
    * */
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
    private Archivo fileActive;
    private CodeArea textAreaCode;
    @FXML
    private StackPane stackPane;
    @FXML
    private TabPane outputBottom;
    @FXML
    private TabPane outputRight;
    /*
    * End Vistas Globales
    * */

    /*
    * Lexico
    * */
    @FXML
    private Button buttonLexico;
    @FXML
    private TextArea textAreaErroresLexico;
    @FXML
    private TableColumn tableColumnType;
    @FXML
    private TableColumn tableColumnLexema;
    @FXML
    private TableColumn tableColumnRow;
    @FXML
    private TableColumn tableColumnColumn;
    @FXML
    private TableView<LexicoToken> tokenTableView;
    private ObservableList<LexicoToken> tokenObservableList = FXCollections.observableArrayList();
    /*
    * End Lexico
    * */

    /*
    * Sintactico
    * */
    @FXML
    private Button buttonSintactico;
    @FXML
    private TextArea textAreaErroresSintactico;
    @FXML
    private TreeView sintactico_treeView;
    /*
    * End Sintactico
    * */

    /*
    * Semantica
    * */
    @FXML
    private Button semantica_buttonSemantic;
    @FXML
    private TableColumn sematica_nombreVariable;
    @FXML
    private TableColumn sematica_localidad;
    @FXML
    private TableColumn sematica_noLinea;
    @FXML
    private TableColumn sematica_valor;
    @FXML
    private TableColumn sematica_tipo;
    @FXML
    private TextArea semantica_textAreaErrores;
    @FXML
    private TableView<SemanticoToken> semantica_tokenTableView;
    private ObservableList<SemanticoToken> semantica_tokenObservableList = FXCollections.observableArrayList();
    @FXML
    private TreeView semantica_treeView;

    /*
    * End Semantica
    * */


    /*
    Salidas
     */
    @FXML
    private TextArea textAreaCodigoIntermedio;
    @FXML
    private TextArea textAreaTablaSimbolos;
    @FXML
    private Tab shellTabOutput;
    @FXML
    Button button_compile;
    @FXML
    private TextArea textAreaSalida;
    @FXML
    private Button codgen_initButton;
    @FXML
    private Button execute_program;

    @FXML
    public TextArea salida;

    /******************/




    /*****************/
    private ProcessBuilder pb;
    private Process process;
    private String input = "";
    private BufferedReader stdInput, stdError;
    private OutputStream out;
    /*****************/



    @FXML
    private void initialize() {
        fileActive = new Archivo("","");
        initAreaCode();
        initEvents();
        initButtons();
        initList();
        initColumnsLexico();
        iniColumnsSemantic();
    }

    private void initExecutionProgram() {
        salida.setOnKeyTyped(event -> {
            input = input.concat(event.getCharacter());
            System.out.println(input);
        });

        final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        salida.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    String command = input.replaceAll("\r", "") + "\n";
//                    command = input.replaceAll("\b", "");
                    System.out.println(command);
                    out.write(command.getBytes());
                    out.flush();
                    input = "";
                    System.out.println("Command Sent");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else if (keyComb1.match(event)) {
                System.out.println("Control+C pressed");
            }else if (event.getCode()== KeyCode.BACK_SPACE){
                //    input=input.substring(0, input.length()-1);
                //  System.out.println(input);
            }

        });

        File resultFile = new File("code.RJI");
        pb = new ProcessBuilder("python", PATH+"TM.py", resultFile.getAbsolutePath());
//        pb = new ProcessBuilder("java", "-jar", "C:\\Users\\Joshua\\Documents\\work\\fosscam\\untitled\\out\\artifacts\\untitled_jar\\untitled.jar");
        new Thread(() -> {
            try {
                process = pb.start();
                stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                //OutputStream out;
                out = process.getOutputStream();
                StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
                // any output?
                StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
                // start gobblers
                outputGobbler.start();
                errorGobbler.start();
                process.waitFor();
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
        outputBottom.getSelectionModel().select(4);
    }

    private void initAreaCode(){
        CodeAreaControl codeAreaControl = new CodeAreaControl();
        textAreaCode = codeAreaControl.getCodeArea();
        textAreaCode.setDisable(true);
        stackPane.getChildren().add(new VirtualizedScrollPane<>(textAreaCode));
        textAreaCode.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                clean();
            }
        });
    }

    private void initColumnsLexico(){
        tableColumnType.setCellValueFactory(new PropertyValueFactory<LexicoToken,String>("type"));
        tableColumnLexema.setCellValueFactory(new PropertyValueFactory<LexicoToken, String>("lexema"));
        tableColumnRow.setCellValueFactory(new PropertyValueFactory<LexicoToken,String>("row"));
        tableColumnColumn.setCellValueFactory(new PropertyValueFactory<LexicoToken, String>("column"));
    }

    private void iniColumnsSemantic(){
        sematica_nombreVariable.setCellValueFactory(new PropertyValueFactory<SemanticoToken, String>("nombreVariable"));
        sematica_localidad.setCellValueFactory(new PropertyValueFactory<SemanticoToken, String>("localidad"));
        sematica_noLinea.setCellValueFactory(new PropertyValueFactory<SemanticoToken, String>("noLinea"));
        sematica_valor.setCellValueFactory(new PropertyValueFactory<SemanticoToken, String>("valor"));
        sematica_tipo.setCellValueFactory(new PropertyValueFactory<SemanticoToken, String>("tipo"));
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
        buttonLexico.setOnAction(event -> initLexicon());
        buttonSintactico.setOnAction(event -> initSintactico());
        semantica_buttonSemantic.setOnAction(event -> initSematic());
        codgen_initButton.setOnAction(event -> initGenCode());
        execute_program.setOnAction(event -> initExecutionProgram());
        button_compile.setOnAction(event -> execute());
    }

    private void execute(){
        initLexicon();
        initSintactico();
        initSematic();
        initGenCode();
        initExecutionProgram();
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
        clean();
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

    private void saveFile() {
        if (fileActive.getName().equals("new")) {
            saveAsFile();
        } else {
            clean();
            Archivo archivo = observableListFileData.get(observableListFileData.indexOf(fileActive));
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
        clean();
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

    private void fileEmpty(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Archivo...");
        alert.setHeaderText(null);
        alert.setContentText("Abre o crea un nuevo archivo para continuar");
        alert.showAndWait();
    }

    private void openListViewItem(Archivo archivo){
        clean();
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

    private void initLexicon(){
        clean();
        if(fileActive.getName().equals("new")){
            saveAsFile();
        }else if (fileActive.getName().equals("")){
            fileEmpty();
        }else {
            String file = "\"" + fileActive.getLocation() + "\"";
            List<String> list = executeLexico(file);
            ArrayList<LexicoToken> tokensValidos = new ArrayList<>();
            ArrayList<LexicoToken> tokensError = new ArrayList<>();
            for (String line : list) {
                if (!line.contains("TKN_ERROR")) {
                    String[] desc = line.split(" ");
                    tokensValidos.add(new LexicoToken(desc[0], desc[1], desc[2], desc[3]));
                } else {
                    String[] desc = line.split(" ");
                    tokensError.add(new LexicoToken(desc[0], desc[1], desc[2], desc[3]));
                }
            }
            for (LexicoToken token : tokensError){
                textAreaErroresLexico.appendText(token.toString() + "\n");
            }
            tokenObservableList.addAll(tokensValidos);
            tokenTableView.setItems(tokenObservableList);
            buttonSintactico.setDisable(false);
            outputBottom.getSelectionModel().select(0);
            outputRight.getSelectionModel().select(0);
        }
    }

    private List<String> executeLexico (String pathFile){
        try {
            String script = PATH + "Lexico.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", script, pathFile);
            Process process  = processBuilder.start();
//            process.waitFor();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                return bufferedReader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void clean(){
        tokenObservableList.clear();
        semantica_tokenObservableList.clear();
        textAreaErroresLexico.setText("");
        textAreaErroresSintactico.setText("");
        semantica_textAreaErrores.setText("");
        sintactico_treeView.setRoot(new TreeItem("No Elements..."));
        semantica_treeView.setRoot(new TreeItem("No Elements..."));
        it = 0;
        buttonSintactico.setDisable(true);
        semantica_buttonSemantic.setDisable(true);
        outputBottom.getSelectionModel().select(0);
        outputRight.getSelectionModel().select(0);
        execute_program.setDisable(true);
        codgen_initButton.setDisable(true);
        textAreaCodigoIntermedio.setText("");
        salida.setText("");
    }

    private void initSintactico(){
        File file = new File("Tokens.txt");
        List<String> result = executeSintactico(file.getAbsolutePath());
        ArrayList<String> resultNoErrors = new ArrayList<>();
        for (String line : result){
            if (!line.contains("Syntax error")){
                resultNoErrors.add(line);
            }else {
                textAreaErroresSintactico.appendText("\n" + line);
            }
        }
        sintactico_treeView.setRoot(save_tree(resultNoErrors));
        it = 0;
        semantica_buttonSemantic.setDisable(false);
        outputBottom.getSelectionModel().select(1);
        outputRight.getSelectionModel().select(1);
    }


    private List<String> executeSintactico(String pathFile){
        try{
            String script = PATH + "Sintactico.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", script, pathFile);
            Process process = processBuilder.start();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                return bufferedReader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initSematic(){
        semantica_textAreaErrores.setText("");
        semantica_tokenObservableList.clear();
        File file = new File("tree.bin");
        List<String> result = executeSemantic(file.getAbsolutePath());
        ArrayList<SemanticoToken> semanticoTokens = new ArrayList<>();
        List<String> treeSemantic = new ArrayList<>();
        int i = 0;
        for (String line : result){
            if (!line.contains("Gramatical error") && !line.contains("main") && !line.contains("   ")){
                String[] split_line = line.split("-");
                semanticoTokens.add(new SemanticoToken(split_line[0], String.valueOf(i),split_line[2],split_line[3],split_line[4]));
                i++;
            }else if(line.contains("   ") || line.contains("main")){
                treeSemantic.add(line);
            }else {
                semantica_textAreaErrores.appendText(line + "\n");
            }
        }
        semantica_treeView.setRoot(save_tree(treeSemantic));
        semantica_tokenObservableList.addAll(semanticoTokens);
        semantica_tokenTableView.setItems(semantica_tokenObservableList);
        outputBottom.getSelectionModel().select(2);
        outputRight.getSelectionModel().select(2);
        codgen_initButton.setDisable(false);
        it = 0;
    }

    private List<String> executeSemantic(String pathFile){
        try{
            String script = PATH + "Gramatical.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", script, pathFile);
            Process process = processBuilder.start();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                return bufferedReader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initGenCode(){
        File gramatical_tree = new File("gramatical_tree.bin");
        File hashtable = new File("hashtable.bin");
        executeGenCode(gramatical_tree.getAbsolutePath(), hashtable.getAbsolutePath());
        File resultFile = new File("code.RJI");
        try(Stream<String> stringStream = Files.lines(Paths.get(resultFile.getAbsolutePath()))) {
            stringStream.forEach(line -> textAreaCodigoIntermedio.appendText(line + "\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputRight.getSelectionModel().select(3);
        execute_program.setDisable(false);
    }

    private List<String> executeGenCode(String gramatical_tree, String hashtable){
        try {
            String script = PATH + "GenCodigo.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", script, gramatical_tree, hashtable);
            Process process = processBuilder.start();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                return bufferedReader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int it = 0;
    private TreeItem<String> save_tree(List<String> line){
        TreeItem<String> root = new TreeItem<String>(line.get(it));
        root.setExpanded(true);
        this.it++;
        return get_Tree(0, root, line);
    }

    private TreeItem<String> get_Tree(int prevTab, TreeItem<String> root, List<String> line){
        // Contamos el numero de Tabulaciones que tiene la linea
        int i = root.getChildren().size();
        while (it < line.size()){
            int tabs = count_tabs(line.get(it));
            if (tabs > prevTab){  // Si es hijo
                TreeItem<String> children = new TreeItem<>(line.get(it).replace("   ",""));
                children.setExpanded(true);
                root.getChildren().add(children);
                it++;
                get_Tree(tabs, root.getChildren().get(root.getChildren().size()-1), line);  // El ultimo agregado
            }else{
                break;
            }
        }
        return root;
    }

    private int count_tabs(String linea){
        int contador = 0;
        for (int i = 0; i < linea.length(); i++) {
            if (linea.charAt(i) == ' '){
                contador++;
            }else{
                break;
            }
        }
        // Al no poder hacer split('\t'), contamos los (espaciosTotales+1)/4
        if (contador > 0){
            contador++;
            contador /= 4;
        }
        return contador;
    }

    private class StreamGobbler extends Thread {
        InputStream is;
        String type;

        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(type + "> " + line);
                    String str =line;
                    Platform.runLater(() -> {
                        if (!str.equals("\n")){
                            salida.appendText(str + "\n");
                        }
                    });
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}