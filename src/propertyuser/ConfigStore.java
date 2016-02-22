package propertyuser;

import java.io.*;
import java.util.EnumMap;
import java.util.Properties;

/**
 * Класс для работы с файлом настроек
 * Singleton pattern
 */
final class ConfigStore {
    public static final String CONFIG_FILE_NAME = "dicomprinter.config";
    private static ConfigStore instance;
    public final EnumMap<PropertiesEnum, String> map;

    private ConfigStore() {

        checkConfigFile();

        map = new EnumMap<>(PropertiesEnum.class);
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(CONFIG_FILE_NAME));
        } catch (IOException e) {
            System.err.println("Error loading configuration file.");
            e.printStackTrace();
            System.exit(-1);
        }
        for (PropertiesEnum key : PropertiesEnum.values()) {
            map.put(key, prop.getProperty(key.toString()));
        }
    }

    private void checkConfigFile() {
        File propFile = new File(CONFIG_FILE_NAME);
        try {
            //noinspection ResultOfMethodCallIgnored
            propFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigStore getInstance() {
        if (instance == null) instance = new ConfigStore();
        return instance;
    }

    /**
     * Экранирует обратные слеши в строке. Это надо для записи в текстовый файл.
     * Copy-pasted from Properties.java and cropped
     * Escapes slashes with a preceding slash.
     *
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
     * Сохраняет файл настроек в формате Property и в его кодироваке,
     * Параметры сохраняются в строгом порядке.
     *
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
     * Получает значение
     *
     * @param property свойство
     * @return значение свойства
     */
    public String getProperty(PropertiesEnum property) {
        return map.get(property);
    }

    /**
     * Устанавливает свойство
     *
     * @param property свойство
     * @param value    значение свойства
     */
    public void setProperty(PropertiesEnum property, String value) {
        map.put(property, value);
    }
}
