package dicomprinter.configsaver;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by tripsin on 15.02.16.
 */
public class ConfigSaver {
    private static final String view = "ConfigSaver.fxml";
    private AnchorPane paneConfigSaver;
    private ConfigSaverController controller;

    public ConfigSaver() {
        super();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ConfigSaver.class.getResource(view));
        controller = loader.getController();
        try {
            paneConfigSaver = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(paneConfigSaver);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("ERROR: ConfigSaver.fxml not load.");
            System.exit(-1);
        }


    }

}

