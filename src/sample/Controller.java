package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.File;

import java.util.Optional;

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

    @FXML
    TabPane tabPaneCode;

    @FXML
    ListView<File> historyFiles;
    ObservableList<File> observableListFileData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        initEvents();
        initButtons();
        initList();
    }

    public void initEvents(){
        menuItemNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                System.out.println("Menu Item New");
                newFile();
            }
        });

        menuItemOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                System.out.println("Menu Item Open");
            }
        });
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println("Name of file: " + result.get());
            String nameFile = result.get() + ".rj";
            Tab auxTab = new Tab(nameFile);
            TextArea textArea = new TextArea();
            textArea.setWrapText(true);
            auxTab.setContent(textArea);
            tabPaneCode.getTabs().add(auxTab);
//            observableListFileData.add(new File(nameFile,"test"));
        }
    }

    public void saveFile(){

    }

    public void initList(){
        historyFiles.setItems(observableListFileData);
    }
}
