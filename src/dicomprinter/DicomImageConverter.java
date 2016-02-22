package dicomprinter;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.display.SourceImage;
import propertyuser.PropertiesEnum;
import propertyuser.PropertyUser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** Конвертер изображений. DCM -> JPEG/PNG
 * @author Roman Orekhov, tripsin@yandex.ru
 * @since 16-10-01
 */
public class DicomImageConverter extends PropertyUser {
    /** Формат файла с изображением. Может быть JPEG и PNG */
    private String imageType;
    /** Параметр обрезка изображения. Отступ слева в пикселях */
    private int cropX;
    /** Параметр обрезка изображения. Отступ сверху в пикселях */
    private int cropY;
    /** Параметр обрезка изображения. Ширина в пикселях */
    private int cropWidth;
    /** Параметр обрезка изображения. Высота в пикселях */
    private int cropHeight;
    /** Главный класс приложения */
    private Main mainClass;

    private Boolean needsCropping;

    /** Конструктор
     *
     */
    public DicomImageConverter(Main main) {
        super();
        this.mainClass = main;
    }

    /** Преобразует переданный dcm-файл в соотвествии с настройками (формат JPEG,PNG и обрезка)
 * @param dicomFileName имя временного dcm-файла
 * @return Возвращает объект (File) файла изображения (для принтера)
 * @see dicomprinter.DicomImageReceiver.DicomReceivedObjectHandler#sendReceivedObjectIndication(String, String, String)
 * */
    File convert(String dicomFileName) throws DicomException, IOException {
        //SourceImage from PixelMed
        BufferedImage sourcePicture = new SourceImage(dicomFileName).getBufferedImage();
        if (needsCropping) sourcePicture = sourcePicture.getSubimage(cropX, cropY, cropWidth, cropHeight);
        File outputJPGfile = new File(dicomFileName + "." + imageType.toLowerCase());
        if (ImageIO.write(sourcePicture, imageType, outputJPGfile))
            System.err.println("Image file created - " + outputJPGfile.getName());
        if (new File(dicomFileName).delete())
            System.err.println("Temporary file deleted.");

        mainClass.addImageBox(outputJPGfile.getPath());

        return outputJPGfile;
        //TODO: Обработать DicomException, IOException
    }

    @Override
    protected Boolean load() {
        imageType = getProperty(PropertiesEnum.IMAGE_TYPE);
        String cropXstring = getProperty(PropertiesEnum.CROP_X);
        String cropYstring = getProperty(PropertiesEnum.CROP_Y);
        String cropWstring = getProperty(PropertiesEnum.CROP_WIDTH);
        String cropHstring = getProperty(PropertiesEnum.CROP_HEIGHT);
        if (imageType == null) return false;
        if ((cropXstring == null) || (cropYstring == null) ||
                (cropWstring == null) || (cropHstring == null)) needsCropping = false;
        else {
            cropX = Integer.parseInt(cropXstring);
            cropY = Integer.parseInt(cropYstring);
            cropWidth = Integer.parseInt(cropWstring);
            cropHeight = Integer.parseInt(cropHstring);
            needsCropping = true;
        }
        return true;
    }
}
