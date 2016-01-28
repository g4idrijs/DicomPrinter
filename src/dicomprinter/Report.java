package dicomprinter;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;


/**
 * Create multi-image PDF-report for DICOM-printer
 * Created by 1 on 14.01.2016.
 */

public class Report {

    public static final String DEFAULT_REPORT_NAME = "report.pdf";

    private static final int TOP_FONT_SIZE = 12;
    private static final int BOTTOM_FONT_SIZE = 8;
    private static final int CAPTION_FONT_SIZE = 14;
    // Windows system font
    //private static final String FONT_PATH = "C:\\Windows\\Fonts\\Arial.ttf";
    // Font in Project Folder
    private static final String FONT_PATH = "Arial.ttf";
    private static final float IMAGES_IN_ROW = 2f;
    private static final float IMAGES_IN_COLUMN = 3f;
    private static final int IMAGES_ON_PAGE = (int)(IMAGES_IN_ROW * IMAGES_IN_COLUMN);
    private static final float LEFT_BORDER_WIDTH = 50f;
    private static final float BORDER_WIDTH = 20f;
    private static final float IMAGE_DISTANCE = 10f;
    private static final float TOP_HEIGHT = 20f;
    private static final float CAPTION_HEIGHT = 12f;
    private static final float PAGE_WIDTH = PageSize.A4.getWidth() - LEFT_BORDER_WIDTH - BORDER_WIDTH;
    private static final float PAGE_HEIGHT = PageSize.A4.getHeight() - BORDER_WIDTH - BORDER_WIDTH;
    private static final float IMAGE_WIDTH = PAGE_WIDTH / IMAGES_IN_ROW - IMAGE_DISTANCE;
    private static final float IMAGE_HEIGHT = PAGE_HEIGHT / IMAGES_IN_COLUMN;

    private BaseFont baseFont;
    private final Document document;
    private PdfWriter writer;

    public Report(String filename) {
        try {
            //IMPORTANT!! - cyrillic font creating
            baseFont = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            System.err.println("ERROR: Font creation failed.");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("ERROR: Can not open font from file.");
            e.printStackTrace();
            System.exit(-1);
        }

        document = new Document(PageSize.A4);

        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        } catch (DocumentException e) {
            System.err.println("ERROR: PDF-writer creation failed.");
            e.printStackTrace();
            System.exit(-1);
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: Can not open file " + filename);
            e.printStackTrace();
            System.exit(-1);
        }

        document.open();
    }

    public void save(){
        document.close();
    }

    //test print function - PDFBOX
    public static void print(String fileName){
        PDDocument docPDF = null;
        try {
            docPDF = PDDocument.load (new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(docPDF));
        try {
            job.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    // low level text function
    private void insertText(String text, float x, float y, int fontSize){
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
        over.setLineWidth(0.1f);
        over.setFontAndSize(baseFont, fontSize);
        over.moveText(x, y);
        over.showText(text);
        over.endText();
        over.restoreState();
    }

    public void top (String top){
        float x = LEFT_BORDER_WIDTH;
        float y = PageSize.A4.getHeight() - BORDER_WIDTH - TOP_FONT_SIZE;
        insertText(top, x, y, TOP_FONT_SIZE);
    }

    public void bottom (String bottom){
        //noinspection SuspiciousNameCombination
        insertText(bottom, LEFT_BORDER_WIDTH, BORDER_WIDTH, BOTTOM_FONT_SIZE);
    }

    public void newpage(){
        document.newPage();
    }

    public int columnsNumber(){
        return (int) IMAGES_IN_ROW;
    }

    public int imagesOnPage(){
        return IMAGES_ON_PAGE;
    }

    public void image(String imageFileName, int row, int column, String caption){
        Image image = null;
        try {
            image = Image.getInstance(imageFileName);
            image.scaleToFit(IMAGE_WIDTH, IMAGE_HEIGHT);
        } catch (BadElementException e) {
            System.err.println("ERROR: Bad image in file " + imageFileName);
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("ERROR: Can not open file " + imageFileName);
            e.printStackTrace();
            System.exit(-1);
        }

        float x = column * (PAGE_WIDTH / IMAGES_IN_ROW) + LEFT_BORDER_WIDTH;
        float y = PageSize.A4.getHeight() - BORDER_WIDTH - row * IMAGE_HEIGHT - image.getScaledHeight() - TOP_HEIGHT;
        image.setAbsolutePosition(x, y);

        try {
            document.add(image);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        //TODO: Перенос длинного текста на новую строку
        if (caption != null) insertText(caption, x , y - CAPTION_HEIGHT, CAPTION_FONT_SIZE);
    }

    //TODO: just for debug
    public void sixJpegImagesFromFolder(String folderName){
        File[] files = new File(folderName).listFiles((dir, name) -> {return name.toLowerCase().endsWith(".jpeg");});
        for (int i = 0; (i < 6) && (i < files.length) ; i++)
        {
            System.err.println(files[i].getPath());
            switch (i){
                case 0: {image(files[i].getPath(), 0, 0, "Картинка №" + i); break;}
                case 1: {image(files[i].getPath(), 0, 1, "Картинка №" + i); break;}
                case 2: {image(files[i].getPath(), 1, 0, "Картинка №" + i); break;}
                case 3: {image(files[i].getPath(), 1, 1, "Картинка №" + i); break;}
                case 4: {image(files[i].getPath(), 2, 0, "Картинка №" + i); break;}
                case 5: {image(files[i].getPath(), 2, 1, "Картинка №" + i); break;}
                default:
            }
        }
    }

    public static void main(String[] args) {
        Report report = new Report("D:\\tmp\\sample.pdf");
        report.sixJpegImagesFromFolder("D:\\tmp");
        report.newpage();
        report.sixJpegImagesFromFolder("D:\\tmp");
        report.save();
    }
}
