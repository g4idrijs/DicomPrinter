package dicomprinter;

/**
 * Выводит
 * Created by 1 on 23.12.2015.
 *
 */

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.io.*;

public class SimplePrinter {

    private String printerName = "priPrinter";
    private PrintService printService = null;
    private PrintRequestAttributeSet pras = null;
    //private DocFlavor flavor = DocFlavor.INPUT_STREAM.JPEG;
    private DocFlavor flavor = DocFlavor.INPUT_STREAM.PDF;
    private FileInputStream fis = null;
    private Doc doc = null;

    public SimplePrinter(String name){
        printerName = name;
    }

    public void print(String filename){
        try {
            fis = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            System.err.println("Can not load file for Printer : " + filename);
            e.printStackTrace();
        }
        GetPrinterService();
        CreatePrinterAttributes();
        CreateDocumentAndDocumentAttributes();
        DocPrintJob jobJPEG = printService.createPrintJob();
        try {
            jobJPEG.print(doc, pras);
        } catch (PrintException e) {
            System.err.println("ERROR during printing.");
            e.printStackTrace();
        }
    }

    private void GetPrinterService(){
        if (printerName.isEmpty()){
            printService = PrintServiceLookup.lookupDefaultPrintService();
        } else
            for (PrintService p:PrintServiceLookup.lookupPrintServices(null,null))
                if (p.getName().equals(printerName)) printService = p;
    }

    private void CreatePrinterAttributes(){
        pras = new HashPrintRequestAttributeSet();
        //pras.add(new Copies(1));
        //pras.add(new PrinterResolution(600,600,PrinterResolution.DPI));
        //pras.add(PrintQuality.HIGH);
        //pras.add(new MediaPrintableArea(0, 0, 100, 100, MediaPrintableArea.MM));
        //pras.add(MediaTray.TOP);
        //pras.add(new PrintQuality());
        //pras.add(new MediaSize(210, 297, Size2DSyntax.MM, MediaSizeName.ISO_A5));
    }

    private void CreateDocumentAndDocumentAttributes(){
        DocAttributeSet das = new HashDocAttributeSet();
        //doc = new SimpleDoc(fis, flavor, das);
        doc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE,null);
    }

    public static void main(String[] args){
        //SimplePrinter p = new SimplePrinter("priPrinter");
        SimplePrinter p = new SimplePrinter("");
        p.print("D:\\tmp\\pict.jpg");
    }
}
