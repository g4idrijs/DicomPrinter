package dicomprinter;

import java.io.*;
import java.util.EnumMap;
import java.util.Properties;

/**
 * Настройки программы DicomPrinter
 */
enum PropertiesEnum {
    AE_TITLE,    // Имя поставщика DICOM-объектов (в настройках УЗИ-сканера)
    DICOM_PORT,  // Номер порта (установить также в настройках УЗИ-сканера
    IMAGE_TYPE,  // Формат файла с изображением. Может быть JPEG и PNG
    CROP_X,      // Параметр обрезка изображения. Отступ слева в пикселях
    CROP_Y,      // Параметр обрезка изображения. Отступ сверху в пикселях
    CROP_WIDTH,  // Параметр обрезка изображения. Ширина в пикселях
    CROP_HEIGHT, // Параметр обрезка изображения. Высота в пикселях
    TMP_DIR,     // Временная папка для полученных файлов и изображений. Убедись, что есть права на запись в нее
    PRINTER_NAME // Имя принтера
}

/**
 * Класс для работы с файлом настроек
 * Singleton pattern
 */
final class ConfigStore {
    public static final String CONFIG_FILE_NAME = "dicomprinter.config";
    private static ConfigStore instance;
    private static EnumMap<PropertiesEnum, String> map;

    private ConfigStore() {
        map = new EnumMap<>(PropertiesEnum.class);
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(CONFIG_FILE_NAME));
        } catch (IOException e) {
            System.err.println("Error loading configuration file.");
            e.printStackTrace();
        }
        for (PropertiesEnum key : PropertiesEnum.values()) {
            map.put(key, prop.getProperty(key.toString()));
        }
    }

    public static ConfigStore getInstance(){
        if (instance == null) instance = new ConfigStore();
        return instance;
    }

    /**
     * Сохраняет файл настроек в формате Property и в его кодироваке,
     * Параметры сохраняются в строгом порядке.
     * @param fileName Config file name
     */
    public void save(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "8859_1"));
            writer.write("# DicomPrinter configuration file");
            writer.newLine();
            for (PropertiesEnum pe : PropertiesEnum.values())
                if (map.get(pe) != null) {
                    writer.write(pe.toString() + "=" + saveConvert(map.get(pe)));
                    writer.newLine();
                }
            writer.flush();
            writer.close();
        } catch (UnsupportedEncodingException e) {
            System.err.println("Property encoding error.");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Failed loading property file.");
            e.printStackTrace();
        }
    }

    /**
     * Экранирует обратные слеши в строке. Это надо для записи в текстовый файл.
     * Copy-pasted from Properties.java and cropped
     * Escapes slashes with a preceding slash.
     * @param theString строка, которую надо изменить перед записью
     * @return измененная строка с экранированными слешами
     */
    private static String saveConvert(String theString) {
        int len = theString.length();
        StringBuilder outBuffer = new StringBuilder();
        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /**
     * Получает значение
     * @param property свойство
     * @return значение свойства
     */
    public String getProperty(PropertiesEnum property) {
        return map.get(property);
    }

    /**
     * Устанавливает свойство
     * @param property свойство
     * @param value значение свойства
     */
    public void setProperty(PropertiesEnum property, String value){
        map.put(property, value);
    }
}

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


