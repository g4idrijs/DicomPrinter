package propertyuser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Dialog for confirm action if project properties are failed.
 * Uses built-in controller for fxml-file.
 * Project: PropertyUser
 * Created by tripsin on 20.02.2016.
 */
class ConfigErrorDialog extends Stage implements Initializable {
    private static final String view = "./views/ConfigErrorDialog.fxml";

    @SuppressWarnings("unused")
    @FXML
    private Button btnStop;
    @SuppressWarnings("unused")
    @FXML
    private Button btnEdit;
    @SuppressWarnings("unused")
    @FXML
    private Label lblMessage;

    private final String message;

    private Boolean checkedEdit = false;

    public ConfigErrorDialog(String message) {
        this.message = message;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        loader.setController(this);
        try {
            Scene scene = new Scene(loader.load());
            setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setResizable(false);
        initStyle(StageStyle.UTILITY);
        initModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblMessage.setText(message);
        btnStop.setOnAction(event -> {
            checkedEdit = false;
            close();
        });
        btnEdit.setOnAction(event -> {
            checkedEdit = true;
            close();
        });
    }

    /**
     * @return true - if edit-button clicked. Check this then dialog is closed.
     */
    public Boolean checkedEdit() {
        return checkedEdit;
    }
}
