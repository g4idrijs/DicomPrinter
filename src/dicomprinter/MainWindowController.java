package dicomprinter;

import dicomprinter.imagebox.ImageBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class MainWindowController {

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

    private Main mainClass;

    @FXML
    public void goPrint(){
        System.err.println("go printer");
        mainClass.createReport();
        //TODO: Печатать на принтере
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

    public void setMainClass(Main mainClass){
        this.mainClass = mainClass;
    }

    public void setChangeWidthListener(){
        scrollPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
                    imageGrid.getChildren().clear();
                    for (ImageBox box:mainClass.listOfImageBoxes) box.show(scrollPane.getWidth());
                }
        );
    }

    @FXML
    public void initialize(){}
}
