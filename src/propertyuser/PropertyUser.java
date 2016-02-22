package propertyuser;

import java.util.EnumMap;

/**
 * Абстрактный класс, от которого наследуются классы, использующие настройки.
 * <p>
 * Example:
 * <pre>
 * class Sample extends PropertyUser {
 *     private String tmp;
 *
 *     \@Override
 *     protected Boolean load() {
 *         tmp = getProperty(PropertiesEnum.TMP_DIR);
 *         return tmp != null;
 *     }
 *
 *     public static void main(String[] args) {
 *         Sample sample = new Sample();
 *         System.out.println("TMP_DIR = " + sample.tmp);
 *         sample.setProperty(PropertiesEnum.PRINTER_NAME, "priPrinter");
 *         sample.savePropertiesFile();
 *     }
 * }</pre>
 */

public abstract class PropertyUser {
    private static ConfigStore configStore;

    public void startConfigSaver(){
        new ConfigSaver();
    }

    protected PropertyUser() {

        configStore = ConfigStore.getInstance();

        while (!load()){
            ConfigErrorDialog dialog = new ConfigErrorDialog("Ошибка при загрузке модуля " +
                    getClass().getSimpleName());
            dialog.showAndWait();
            if (dialog.checkedEdit()){
                new ConfigSaver();
            }
            else {
                System.exit(-1);
            }
        }
    }

    protected EnumMap<PropertiesEnum, String> configMap() {
        return configStore.map;
    }

    /**
     * Здесь проводить инициализация и валидацию параметров настроек.
     * @return если false, то выкинет RuntimeException
     */
    protected abstract Boolean load();

    protected String getProperty(PropertiesEnum property) {
        return configStore.getProperty(property);
    }

    protected void setProperty(PropertiesEnum property, String value) {
        configStore.setProperty(property, value);
    }

    protected void savePropertiesFile() {
        configStore.save(ConfigStore.CONFIG_FILE_NAME);
    }
}


