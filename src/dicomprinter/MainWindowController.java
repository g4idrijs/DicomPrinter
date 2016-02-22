package dicomprinter;

import dicomprinter.imagebox.ImageBox;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import propertyuser.PropertyUser;

public class MainWindowController {

    public MenuItem mnuClose;
    public MenuItem mnuConfig;
    @FXML
    Button printButton;
    @FXML
    Button clearButton;
    @FXML
    Button exitButton;
    @FXML
    public GridPane imageGrid;
    @FXML
    ScrollPane scrollPane;
    @FXML
    AnchorPane mainPane;
    @FXML
    Button btnSelectAll;
    @FXML
    TextField txtTopColontitul;

    private Main mainClass;

    @FXML
    public void goSelectAll(){
        mainClass.listOfImageBoxes.forEach(ImageBox::setSelected);
    }

    @FXML
    public void goPrint(){
        mainClass.createReport();
    }

    @FXML
    public void goExit(){
        System.exit(0);
        //TODO: остановить серверный поток
    }

    @FXML
    public void goClear(){
        imageGrid.getChildren().clear();
        mainClass.listOfImageBoxes.clear();
        //TODO: Удалить временные файлы из папки
    }

    @FXML
    public void goConfigSaver(){
        (new PropertyUser(){
            @Override
            protected Boolean load() {
                return true;
            }
        }).startConfigSaver();
    }

    public void setMainClass(Main mainClass){
        this.mainClass = mainClass;
    }

    public void setChangeWidthListener(){
        scrollPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
                    imageGrid.getChildren().clear();
                    for (ImageBox box:mainClass.listOfImageBoxes) box.showOnResize(scrollPane.getWidth());
                }
        );
    }
}
