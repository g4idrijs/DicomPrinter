package propertyuser;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

class ConfigSaver extends Stage implements Initializable {

    @SuppressWarnings("unused")
    @FXML
    public AnchorPane paneConfigSaver;
    @SuppressWarnings("unused")
    @FXML
    public Button btnLoad;
    @SuppressWarnings("unused")
    @FXML
    public Button btnSave;
    @SuppressWarnings("unused")
    @FXML
    public Button btnExit;
    @SuppressWarnings("unused")
    @FXML
    public Label lblMessage;
    @SuppressWarnings("unused")
    @FXML
    public TableView<PropertyItem> tableProperties;
    @SuppressWarnings("unused")
    @FXML
    public TableColumn<PropertyItem, String> colPropertyName;
    @SuppressWarnings("unused")
    @FXML
    public TableColumn<PropertyItem, String> colPropertyValue;

    private final ObservableList<PropertyItem> propertyItems = FXCollections.observableArrayList();
    private static final String view = "./views/ConfigSaver.fxml";
    private final PropertyHolder propertyHolder;

    public ConfigSaver() {
        propertyHolder = new PropertyHolder();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        loader.setController(this);

        try {
            Scene scene = new Scene(loader.load());
            setScene(scene);
        } catch (IOException e) {
            System.err.println("ERROR: ConfigSaver.fxml not load.");
            System.exit(-1);
        }
        initStyle(StageStyle.UTILITY);
        initModality(Modality.APPLICATION_MODAL);
        showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableProperties.setEditable(true);
        colPropertyName.setCellValueFactory(new PropertyValueFactory<>("key"));
        colPropertyValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colPropertyValue.setCellFactory(tableColumn -> new EditingCell());
        colPropertyValue.setOnEditCommit(t ->
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue(t.getNewValue()));
        doReload();
    }

    @SuppressWarnings("unused")
    @FXML
    public void doExit() {
        ((Stage) paneConfigSaver.getScene().getWindow()).close();
    }

    @SuppressWarnings("WeakerAccess")
    @FXML
    public void doReload() {
        propertyItems.clear();
        //noinspection Convert2streamapi
        for (Map.Entry<PropertiesEnum, String> entry : propertyHolder.configMap().entrySet())
            propertyItems.add(new PropertyItem(entry.getKey(), entry.getValue()));
        tableProperties.setItems(propertyItems);
        lblMessage.setText("Загружен файл настроек: " + ConfigStore.CONFIG_FILE_NAME);
    }

    @SuppressWarnings("unused")
    @FXML
    public void doSave() {
        for(PropertyItem item:propertyItems){
            propertyHolder.setProperty(PropertiesEnum.valueOf(item.getKey()), item.getValue());
        }
        propertyHolder.savePropertiesFile();
        lblMessage.setText("Настройки сохранены в файл " + ConfigStore.CONFIG_FILE_NAME);
    }

    private class PropertyHolder extends PropertyUser{
        @Override
        protected Boolean load() {return true;}
    }

    public static class PropertyItem {
        private final SimpleStringProperty key;
        private final SimpleStringProperty value;

        private PropertyItem(PropertiesEnum key, String value) {
            this.key = new SimpleStringProperty(key.toString());
            this.value = new SimpleStringProperty(value);
        }

        public String getKey() {
            return key.get();
        }

        @SuppressWarnings("unused")
        public void setKey(String fName) {
            key.set(fName);
        }

        public String getValue() {
            return value.get();
        }

        public void setValue(String fName) {
            value.set(fName);
        }
    }

    class EditingCell extends TableCell<PropertyItem, String> {

        private TextField textField;

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.requestFocus();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.focusedProperty()
                    .addListener(
                            (ObservableValue<? extends Boolean> arg0, Boolean arg1,
                             Boolean arg2) -> {
                                if (!arg2) cancelEdit();
                            });

            textField.setOnKeyPressed((KeyEvent e) -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    commitEdit(textField.getText());
                    lblMessage.setText("Обновлен параметр " + colPropertyName.getCellData(getTableRow().getIndex()));
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }
}
