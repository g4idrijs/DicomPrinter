package dicomprinter.configsaver;

import dicomprinter.propertyuser.PropertiesEnum;
import dicomprinter.propertyuser.PropertyUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * Created by tripsin on 15.02.16.
 */
public class ConfigSaverController  extends PropertyUser implements Initializable {
    public AnchorPane paneConfigSaver;
    public Button btnLoad;
    public Button btnSave;
    public Button btnCancel;
    public TableView tableProperties;
    public TableColumn colPropertyName;
    public TableColumn colPropertyValue;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        class PropertyItem{PropertiesEnum key; String value;}
        ArrayList<PropertyItem> list = new ArrayList(configMap().entrySet());
        ObservableList<PropertyItem> propertyItems = FXCollections.observableArrayList();
        propertyItems.setAll(list);

        colPropertyName.setCellValueFactory(new PropertyValueFactory<PropertyItem,PropertiesEnum>("key"));
        colPropertyValue.setCellValueFactory(new PropertyValueFactory<PropertyItem,String>("value"));

        tableProperties.setItems(propertyItems);
        tableProperties.setEditable(true);

    }

    /**
     * Только переопределить и больше не использовать
     *
     * @return если false, то выкинет RuntimeException
     */
    @Override
    protected Boolean load() {
        return true;
    }
}
