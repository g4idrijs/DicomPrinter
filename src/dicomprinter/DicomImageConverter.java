package dicomprinter;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.display.SourceImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** Конвертер изображений. DCM -> JPEG/PNG
 * @author Roman Orekhov, tripsin@yandex.ru
 * @since 16-10-01
 */
public class DicomImageConverter {
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
    /** Главный класс приложения */
    private Main mainClass;

    /** Конструктор
     * @param properties объект с настройками
     */
    public DicomImageConverter(Main main, DicomProperties properties) {
        this.mainClass = main;
        IMAGE_TYPE  = properties.getImageType();
        CROP_X      = properties.getCropX();
        CROP_Y      = properties.getCropY();
        CROP_WIDTH  = properties.getCropWidth();
        CROP_HEIGHT = properties.getCropHeight();
    }

    /** Преобразует переданный dcm-файл в соотвествии с настройками (формат JPEG,PNG и обрезка)
 * @param dicomFileName имя временного dcm-файла
 * @return Возвращает объект (File) файла изображения (для принтера)
 * @see dicomprinter.DicomImageReceiver.DicomReceivedObjectHandler#sendReceivedObjectIndication(String, String, String)
 * */
    File convert(String dicomFileName) throws DicomException, IOException {
        //SourceImage from PixelMed
        BufferedImage sourcePicture = new SourceImage(dicomFileName).getBufferedImage();
        BufferedImage croppedPicture = sourcePicture.getSubimage(CROP_X, CROP_Y, CROP_WIDTH, CROP_HEIGHT);
        File outputJPGfile = new File(dicomFileName + "." + IMAGE_TYPE.toLowerCase());
        if (ImageIO.write(croppedPicture, IMAGE_TYPE, outputJPGfile))
            System.err.println("Image file created - " + outputJPGfile.getName());
        if (new File(dicomFileName).delete())
            System.err.println("Temporary file deleted.");

        mainClass.addImageBox(outputJPGfile.getPath());

        return outputJPGfile;
        //TODO: Обработать DicomException, IOException
    }
}
