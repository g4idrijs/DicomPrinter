package dicomprinter;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.display.SourceImage;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.network.StorageSOPClassSCPDispatcher;
import dicomprinter.imagebox.ImageBox;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;


/**
 * Created by 1 on 19.05.2016.
 * TODO extends DataUser for preferences
 */
public class DicomPrinter implements Initializable {
    @FXML
    private FlowPane paneDicom;

    public Parent form;

    //preferences
    private String aeTitle;
    private int dicomPort;
    private String tmpDir;
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
    private Boolean needsCropping;

    private StorageSOPClassSCPDispatcher dispatcher;

    public DicomPrinter() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("dicomprinter.fxml"));
        loader.setController(this);
        try {
            form = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
        getPreferences();
        startReceiver();
    }

    public void addImageBox(String imageFileName){
        ImageBox imageBox = new ImageBox(paneDicom);
        imageBox.setImage(imageFileName);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /** Преобразует переданный dcm-файл в соотвествии с настройками (формат JPEG,PNG и обрезка)
     * @param dicomFileName имя временного dcm-файла
     * @return Возвращает объект (File) файла изображения (для принтера)
     * */
    private File convert(String dicomFileName) {
        BufferedImage sourcePicture = null;
        try {
            sourcePicture = new SourceImage(dicomFileName).getBufferedImage(); //SourceImage from PixelMed
        } catch (IOException | DicomException e) {
            e.printStackTrace();
        }
        if (needsCropping) sourcePicture = sourcePicture.getSubimage(cropX, cropY, cropWidth, cropHeight);
        File outputJPGfile = new File(dicomFileName + "." + imageType.toLowerCase());
        try {
            if (ImageIO.write(sourcePicture, imageType, outputJPGfile))
                System.err.println("Image file created - " + outputJPGfile.getName()); //DEBUG PRINT
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (new File(dicomFileName).delete())
            System.err.println("Temporary file deleted."); //DEBUG PRINT

        addImageBox(outputJPGfile.getPath());

        return outputJPGfile;
    }

    /**
     * Вспомогательный внутренний класс. Обрабатывает DICOM-объект, принятый диспетчером, служебный обработчик.
     * Сохраняет во временной папке файл в формате *.dcm (без этого расширения) и передает имя временного файла
     * на дальнейшую обработку в конвертер. Вызывается из диспетчера StorageSOPClassSCPDispatcher.
     * @see ReceivedObjectHandler
     */
    private class DicomReceivedObjectHandler extends ReceivedObjectHandler {
        public void sendReceivedObjectIndication(String dicomFileName, String transferSyntax, String callingAETitle)
                throws DicomNetworkException, DicomException, IOException {
            if (dicomFileName != null) {
                //DEBUG PRINT
                System.err.println("Received: " + dicomFileName + " from " + callingAETitle + " in " + transferSyntax);
                convert(dicomFileName);
            }
        }
    }

    public void stop(){
        dispatcher.shutdown();
    }

    private void startReceiver(){
        try {
            dispatcher = new StorageSOPClassSCPDispatcher(
                    dicomPort,
                    aeTitle,
                    new File(tmpDir),
                    new DicomReceivedObjectHandler(), 0);
            new Thread(dispatcher).start();
            FXWait.delayExecution(1000, this::checkDispatcherAlive);
        } catch (IOException e) {
            System.err.println("ERROR - Server thread not started.");
            Platform.exit();
        }
    }

    private Void checkDispatcherAlive(){
        if (!dispatcher.isReady()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Критическая неисправность");
            alert.setHeaderText("DICOM-сервер остановлен");
            alert.setContentText("Вероятная причина: не хватило прав для открытия порта. Остановите для запущенные приложения.");
            alert.showAndWait();
            Platform.exit();
        }
        return null;
    }

    //ЗАГЛУШКА С ДЕФОЛТНЫМИ ЗНАЧЕНИЯМИ
    private void getPreferences(){
        aeTitle = "a";
        dicomPort = 104;
        tmpDir = "D:\\tmp";
        imageType = "JPG";
        cropX = 260;
        cropY = 100;
        cropWidth = 800;
        cropHeight = 630;
        needsCropping = true;
    }

    public void createReport(){
        Collection<ImageBox> boxes = new ArrayList<>(paneDicom.getChildren().size());
        paneDicom.getChildren().forEach(node -> {
            ImageBox imageBox = (ImageBox) node.getUserData();
            if (imageBox.checked()) boxes.add(imageBox);
        });

        if (boxes.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Важное сообщение");
            alert.setHeaderText("Не выделены изображения для печати");
            alert.setContentText("Выделите необходимые изображения и нажмите Print");
            alert.showAndWait();
            return;
        }

        Date currentDate = new Date();
        Locale local = new Locale("ru","RU");
        DateFormat df = DateFormat.getDateTimeInstance (DateFormat.DEFAULT,DateFormat.DEFAULT,local);

        Report report = new Report();
        report.setTopText(topText);

        if (bottomText == null)
            report.setBottomText(df.format(currentDate));
        else
            report.setBottomText(bottomText);

        report.create(boxes);
        report.print(); //TODO: Выбор принтера
        report.save(Report.DEFAULT_REPORT_NAME); //TODO: Выбор имени для сохранения
    }

    private String topText;
    private String bottomText;

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }
}
