package dicomprinter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/** Класс настроек для пакета
 * @author Roman Orekhov, tripsin@yandex.ru
 * @since 16-10-01
 */
public class DicomProperties {
    /** Имя конфигурационного файла по умолчанию */
    static final String DEFAULT_CONFIG_FILE_NAME = "uzi_utilites.config";
    /** Имя конфигурационного файла */
    private String CONFIG_NAME = "";
    /** Имя поставщика DICOM-объектов (в настройках УЗИ-сканера) */
    private String AE_TITLE;
    /** Номер порта (установить также в настройках УЗИ-сканера */
    private int DICOM_PORT;
    /** Формат файла с изображением. Может быть JPEG и PNG */
    private String IMAGE_TYPE;
    /** Параметр обрезка изображения. Отступ слева в пикселях */
    private int CROP_X;
    /** Параметр обрезка изображения. Отступ сверху в пикселях */
    private int CROP_Y;
    /** Параметр обрезка изображения. Ширина в пикселях */
    private int CROP_WIDTH;
    /** Параметр обрезка изображения. Высота в пикселях */
    private int CROP_HEIGHT;
    /** Временная папка для полученных файлов и изображений. Убедись, что есть права на запись в нее */
    private String TMP_DIR;

    //Набор геттеров и сеттеров
    public String getConfigName() {
        return CONFIG_NAME;
    }

    public void setConfigName(String configName) {
        CONFIG_NAME = configName;
    }

    public String getAeTitle() {
        return AE_TITLE;
    }

    public void setAeTitle(String aeTitle) {
        AE_TITLE = aeTitle;
    }

    public int getDicomPort() {
        return DICOM_PORT;
    }

    public void setDicomPort(int dicomPort) {
        DICOM_PORT = dicomPort;
    }

    public String getImageType() {
        return IMAGE_TYPE;
    }

    public void setImageType(String imageType) {
        IMAGE_TYPE = imageType;
    }

    public int getCropX() {
        return CROP_X;
    }

    public void setCropX(int cropX) {
        CROP_X = cropX;
    }

    public int getCropY() {
        return CROP_Y;
    }

    public void setCropY(int cropY) {
        CROP_Y = cropY;
    }

    public int getCropWidth() {
        return CROP_WIDTH;
    }

    public void setCropWidth(int cropWidth) {
        CROP_WIDTH = cropWidth;
    }

    public int getCropHeight() {
        return CROP_HEIGHT;
    }

    public void setCropHeight(int cropHeight) {
        CROP_HEIGHT = cropHeight;
    }

    public String getTmpDir() {
        return TMP_DIR;
    }

    public void setTmpDir(String tmpDir) {
        TMP_DIR = tmpDir;
    }

    /** Приватный конструктор с параметром. Использует класс Properties.
     *  Если не смог открыть файл настроек, то завершает программу.
     * @param CONFIG_NAME имя файла с настройками
     */
    private DicomProperties(String CONFIG_NAME) {
        this.CONFIG_NAME = CONFIG_NAME;
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(CONFIG_NAME));
            //TODO: Контроль параметров
            setAeTitle(prop.getProperty("AE_TITLE"));
            setDicomPort(Integer.parseInt(prop.getProperty("DICOM_PORT")));
            setImageType(prop.getProperty("IMAGE_TYPE"));
            setCropX(Integer.parseInt(prop.getProperty("CROP_X")));
            setCropY(Integer.parseInt(prop.getProperty("CROP_Y")));
            setCropWidth(Integer.parseInt(prop.getProperty("CROP_WIDTH")));
            setCropHeight(Integer.parseInt(prop.getProperty("CROP_HEIGHT")));
            setTmpDir(prop.getProperty("TMP_DIR"));
        } catch (IOException e) {
            System.err.println("Wrong name of config file. Recreate this with parameter -n <файл>.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /** Приватный конструктуор без параметра. Загружает настройки по умолчанию. Файлы не использует. */
    private DicomProperties(){
        setAeTitle("a");
        setDicomPort(104);
        setImageType("JPEG");
        setCropX(260);
        setCropY(100);
        setCropWidth(800);
        setCropHeight(630);
        setTmpDir("D:\\tmp");
    }

    /** Статический метод. Загружает настройки из файла.
     * @param configFileName Имя файла с настройками
     * @return Возвращает готовый к работе объект DicomProperties
     */
    public static DicomProperties load(String configFileName) {
        return new DicomProperties(configFileName);
    }

    /** Статический метод. Использует дефолтные настройки.
     * @return Возвращает готовый к работе объект DicomProperties
     */
    public static DicomProperties loadDefaults() {
        return new DicomProperties();
    }

    /**
     * Статический метод. Создает файл настроек. Настройки берет из полей класса. Использует класс Properties.
     * TODO: Записывает параметры вразнобой и как попало. Использовать другой класс для записи в файл.
     * TODO: Загружает дефольтные настройки. Сделать диалог для ввода настроек из консоли.
     * @return Возвращает готовый к работе объект DicomProperties
     */
    public static DicomProperties create(String configFileName) {
        DicomProperties dicomProperties = DicomProperties.loadDefaults();
        dicomProperties.setConfigName(configFileName);
        try {
            Properties prop = new Properties();
            prop.setProperty("AE_TITLE", dicomProperties.getAeTitle());
            prop.setProperty("DICOM_PORT", Integer.toString(dicomProperties.getDicomPort()));
            prop.setProperty("IMAGE_TYPE", dicomProperties.getImageType());
            prop.setProperty("CROP_X", Integer.toString(dicomProperties.getCropX()));
            prop.setProperty("CROP_Y", Integer.toString(dicomProperties.getCropY()));
            prop.setProperty("CROP_WIDTH", Integer.toString(dicomProperties.getCropWidth()));
            prop.setProperty("CROP_HEIGHT", Integer.toString(dicomProperties.getCropHeight()));
            prop.setProperty("TMP_DIR", dicomProperties.getTmpDir());
            System.err.println("Trying to create new config file.");
            prop.store(new FileOutputStream(dicomProperties.getConfigName()), "DICOM Printer settings");

        } catch (IOException e) {
            System.err.println("Error. Config file not saved.");
            e.printStackTrace();
            System.exit(-1);
        }
        return dicomProperties;
    }


}
