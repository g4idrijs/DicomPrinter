package dicomprinter.imagebox;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Объект панели с компонентами для коногократного добвления в GridPane
 * Все ImageBox добаляют себя в общий список (ArrayList). Доступ к каждому ImageBox надо делать через этот список.
 * Created by 1 on 25.01.2016.
 */
public class ImageBox implements Initializable {
    private static final String view = "ImageBox.fxml";
    private GridPane parentGrid;
    private ArrayList<ImageBox> parentList;
    private String imageFileName;
    private Pane imageBox;

    @FXML
    ImageView imageView;
    @FXML
    TextArea textArea;
    @FXML
    CheckBox checkBox;

    /**
     *
     * @return Имя временного файла с картинкой. Надо для принтера.
     */
    public String imageFileName() {
        return imageFileName;
    }

    public void setImage(String imageFileName) {
        this.imageFileName = imageFileName;
        try {
            imageView.setImage(new Image(new FileInputStream(imageFileName)));
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Image not load form " + imageFileName);
        }
    }

    public ImageBox(GridPane parentGrid, ArrayList<ImageBox> parentList) {
        this.parentList = parentList;
        this.parentGrid = parentGrid;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ImageBox.class.getResource(view));
        loader.setController(this);
        try {
            imageBox = loader.load();
        } catch (IOException e) {
            System.err.println("ERROR: ImageBox.fxml not load.");
            System.exit(-1);
        }
        parentList.add(this); // бокс сам себя добавляет в список
    }

    public ImageBox(FlowPane panel) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ImageBox.class.getResource(view));
        loader.setController(this);
        try {
            imageBox = loader.load();
        } catch (IOException e) {
            System.err.println("ERROR: ImageBox.fxml not load.");
            System.exit(-1);
        }
        // бокс сам себя добавляет на панель
        Platform.runLater(() -> panel.getChildren().add(this.imageBox));
        //ссылка на ImageBox (владелец компонента) зранится в самом компоненте. Надо в DicomPrinter.createReport
        this.imageBox.setUserData(this);
    }

    public String caption(){
        return textArea.getText();
    }

    public Boolean checked(){
        return checkBox.isSelected();
    }

    /** @deprecated */
    public void show(double containerWidth){
        int columnsNumber = (int)(containerWidth/imageBox.getPrefWidth());
        int column = parentList.indexOf(this)%columnsNumber;
        int row    = parentList.indexOf(this)/columnsNumber;
        Platform.runLater(() -> parentGrid.add(imageBox, column, row));
    }

    /** @deprecated */
    public void showOnResize(double containerWidth){
        int columnsNumber = (int)(containerWidth/imageBox.getPrefWidth());
        int column = parentList.indexOf(this)%columnsNumber;
        int row    = parentList.indexOf(this)/columnsNumber;
        parentGrid.add(imageBox, column, row);
    }

    public void setSelected(){
        checkBox.setSelected(true);
    }

    /** @deprecated */
    public void show(){
        int columnsNumber = (int)(parentGrid.getWidth()/imageBox.getPrefWidth());
        int column = parentList.indexOf(this)%columnsNumber;
        int row    = parentList.indexOf(this)/columnsNumber;
        Platform.runLater(() -> parentGrid.add(imageBox, column, row));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textArea.setOnKeyTyped(event -> checkBox.setSelected(true));
    }
}
