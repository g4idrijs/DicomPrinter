package dicomprinter;

import dicomprinter.imagebox.ImageBox;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Main extends Application {

    private Stage primaryStage;
    public MainWindowController mainWindowController;

    ArrayList<ImageBox> listOfImageBoxes = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Dicom Printer v0.0");
        initMainWindow();

        DicomProperties properties = DicomProperties.loadDefaults();

        DicomImageConverter converter = new DicomImageConverter(this, properties);

        // Disable then run from Idea on linux (need root)
        DicomImageReceiver.Go(properties, converter);

        //code for debugging
        /*
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        */
    }

    public void initMainWindow(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("MainWindow.fxml"));
            AnchorPane mainWindow = loader.load();

            mainWindowController = loader.getController();
            mainWindowController.setMainClass(this);
            mainWindowController.setChangeWidthListener();

            Scene scene = new Scene(mainWindow);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addImageBox(String imageFileName){
        ImageBox imageBox = new ImageBox(mainWindowController.imageGrid, listOfImageBoxes);
        imageBox.setImage(imageFileName);
        imageBox.show(mainWindowController.scrollPane.getWidth());
    }

    /**
     *     Run from MainWindowController on press Print
     */
    public void createReport(){
        Boolean listEmpty = true;
        for (ImageBox box:listOfImageBoxes) if (box.checked()){ listEmpty = false; break; }
        if (listEmpty) {
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
        report.setTopText(mainWindowController.txtTopColontitul.getText());
        report.setBottomText(df.format(currentDate));
        report.create(listOfImageBoxes);
        report.print(); //TODO: Выбор принтера
        report.save(Report.DEFAULT_REPORT_NAME); //TODO: Выбор имени для сохранения
    }

    public static void main(String[] args) {
        launch(args);
    }
}
