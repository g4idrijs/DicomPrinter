package dicomprinter.imagebox;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Объект панели с компонентами для коногократного добвления в GridPane
 * Все ImageBox добаляют себя в общий список (ArrayList). Доступ к каждому ImageBox надо делать через этот список.
 * Created by 1 on 25.01.2016.
 */
public class ImageBox {
    private static final String view = "ImageBox.fxml";

    private dicomprinter.imagebox.ImageBoxController controller;
    private GridPane parentGrid;
    private ArrayList<ImageBox> parentList;
    private String imageFileName;
    private Pane imageBox;

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
            controller.imageView.setImage(new Image(new FileInputStream(imageFileName)));
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Image not load form " + imageFileName);
        }
    }

    public ImageBox(GridPane parentGrid, ArrayList<ImageBox> parentList) {
        this.parentList = parentList;
        this.parentGrid = parentGrid;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ImageBox.class.getResource(view));
        try {
            imageBox = loader.load();
        } catch (IOException e) {
            System.err.println("ERROR: ImageBox.fxml not load.");
            System.exit(-1);
        }
        controller = loader.getController();
        parentList.add(this); // бокс сам себя добавляет в список
    }

    public String caption(){
        return controller.textArea.getText();
    }

    public Boolean checked(){
        return controller.checkBox.isSelected();
    }

    public void show(double containerWidth){
        int columnsNumber = (int)(containerWidth/imageBox.getPrefWidth());
        int column = parentList.indexOf(this)%columnsNumber;
        int row    = parentList.indexOf(this)/columnsNumber;
        Platform.runLater(() -> parentGrid.add(imageBox, column, row));
    }

    public void showOnResize(double containerWidth){
        int columnsNumber = (int)(containerWidth/imageBox.getPrefWidth());
        int column = parentList.indexOf(this)%columnsNumber;
        int row    = parentList.indexOf(this)/columnsNumber;
        parentGrid.add(imageBox, column, row);
    }

    public void setSelected(){
        controller.checkBox.setSelected(true);
    }

    /** @deprecated */
    public void show(){
        int columnsNumber = (int)(parentGrid.getWidth()/imageBox.getPrefWidth());
        int column = parentList.indexOf(this)%columnsNumber;
        int row    = parentList.indexOf(this)/columnsNumber;
        Platform.runLater(() -> parentGrid.add(imageBox, column, row));
    }
}
