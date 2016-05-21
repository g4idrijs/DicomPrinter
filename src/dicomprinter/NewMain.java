package dicomprinter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NewMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Dicom Printer Sample");
        DicomPrinter dicomPrinter = new DicomPrinter();
        primaryStage.setScene(new Scene(dicomPrinter.form, 370, 500));
        primaryStage.setOnHiding(event -> dicomPrinter.stop());
        primaryStage.show();
/*
        dicomPrinter.addImageBox("pict1.jpeg");
        dicomPrinter.addImageBox("pict1.jpeg");
        dicomPrinter.addImageBox("pict1.jpeg");
        dicomPrinter.addImageBox("pict1.jpeg");
        dicomPrinter.addImageBox("pict1.jpeg");
        dicomPrinter.addImageBox("pict1.jpeg");
        dicomPrinter.addImageBox("pict1.jpeg");
        dicomPrinter.addImageBox("pict1.jpeg");
        dicomPrinter.addImageBox("pict1.jpeg");
        dicomPrinter.addImageBox("pict1.jpeg");
        */
    }

    public static void main(String[] args) {
        launch(args);
    }
}
