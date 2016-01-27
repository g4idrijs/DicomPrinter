package dicomprinter;

import dicomprinter.imagebox.ImageBox;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

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

        // Disabled then run from Idea on linux (need root)
        DicomImageReceiver.Go(properties, converter);

        //debug
        /*
        addImageBox("pict1.jpeg");
        addImageBox("pict.jpg");
        addImageBox("pict1.jpeg");
        addImageBox("pict.jpg");
        addImageBox("pict1.jpeg");
        addImageBox("pict1.jpeg");
        addImageBox("pict.jpg");
        addImageBox("pict1.jpeg");
        addImageBox("pict.jpg");
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

    //Run from MainWindowController on press Print
    public void createReport(){
        Report report = new Report(Report.DEFAULT_REPORT_NAME);
        report.top("Dicom printer. ==== Верхний колонтитул ====");
        report.bottom("Dicom printer. ==== Нижний колонтитул ====");
        int imageCounter = 0;
        for (ImageBox box:listOfImageBoxes){
            if (box.checked()){
                if (imageCounter == report.imagesOnPage()){
                    report.newpage();
                    imageCounter = 0;
                }
                int column = imageCounter % report.columnsNumber();
                int row    = imageCounter / report.columnsNumber();
                report.image(box.imageFileName(), row, column, box.caption());
                imageCounter++;
            }

        }
        report.save();

        //задержка чтобы успел записаться pdf на диск
        try {
            Thread.sleep(3000);
        } catch(InterruptedException e) {
            System.err.println("Waiting ..... ");
            Thread.currentThread().interrupt();
        }

        SimplePrinter printer = new SimplePrinter("priPrinter");
        printer.print(Report.DEFAULT_REPORT_NAME);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
