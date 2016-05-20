package dicomprinter;

import dicomprinter.imagebox.ImageBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by 1 on 19.05.2016.
 */
public class DicomPrinter implements Initializable {
    @FXML
    private FlowPane paneDicom;
    private ScrollPane scrollPane;

    public DicomPrinter() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("newmain.fxml"));
        loader.setController(this);
        try {
            scrollPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Parent form(){
        return scrollPane;
    }

    public void addImageBox(String imageFileName){
        ImageBox imageBox = new ImageBox(paneDicom);
        imageBox.setImage(imageFileName);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
