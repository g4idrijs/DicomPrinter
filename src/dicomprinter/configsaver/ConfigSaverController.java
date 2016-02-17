package dicomprinter.configsaver;

import dicomprinter.propertyuser.PropertiesEnum;
import dicomprinter.propertyuser.PropertyUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
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
        tableProperties.setEditable(true);

        //class PropertyItem{PropertiesEnum key; String value;}

        class PropertyItem{
            private final SimpleStringProperty key;
            private final SimpleStringProperty value;

            private PropertyItem(PropertiesEnum key, String value) {
                this.key = new SimpleStringProperty(key.toString());
                this.value = new SimpleStringProperty(value);
            }

            public void setValue(String fName) {
                value.set(fName);
            }
        }

        ObservableList<PropertyItem> propertyItems = FXCollections.observableArrayList();

        propertyItems.setAll(new ArrayList(configMap().entrySet())); // TODO THIS ///

        colPropertyName.setCellValueFactory(new PropertyValueFactory<>("key"));
        colPropertyName.setCellFactory(TextFieldTableCell.<PropertyItem>forTableColumn());
        /*firstNameCol.setOnEditCommit(
            (CellEditEvent<Person, String> t) -> {
                ((Person) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setFirstName(t.getNewValue());
        });*/

        colPropertyValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colPropertyValue.setCellFactory(TextFieldTableCell.<PropertyItem>forTableColumn());

        colPropertyValue.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent t) {
                        ((PropertyItem) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setValue((String) t.getNewValue());
                    }
                }
        );

        tableProperties.setItems(propertyItems);

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
