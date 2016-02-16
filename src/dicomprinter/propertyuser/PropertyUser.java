package dicomprinter.propertyuser;

import java.io.*;
import java.util.EnumMap;
import java.util.Properties;

/**
 * Абстрактный класс, от которого наследуются классы, использующие настройки.
 *
 * Example:
 * <pre>
 * class Sample extends PropertyUser {
 *     private String tmp;
 *
 *     @Override
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

    public PropertyUser() {
        configStore = ConfigStore.getInstance();
        if (!load()) throw new RuntimeException("Failed property loading");
    }

    protected EnumMap<PropertiesEnum, String> configMap(){
        return configStore.map;
    }

    /**
     * Только переопределить и больше не использовать
     * @return если false, то выкинет RuntimeException
     */
    protected abstract Boolean load();

    protected String getProperty(PropertiesEnum property) {
        return configStore.getProperty(property);
    }

    protected void setProperty(PropertiesEnum property, String value){
        configStore.setProperty(property, value);
    }

    protected void savePropertiesFile(){
        configStore.save(ConfigStore.CONFIG_FILE_NAME);
    }
}


