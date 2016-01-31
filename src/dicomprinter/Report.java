package dicomprinter;

import dicomprinter.imagebox.ImageBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.PrintQuality;
//import javax.print.attribute.standard.PrinterResolution;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Create report for DicomPrinter, save it to disk and ptint on printer
 */
public class Report {
    public static final String DEFAULT_REPORT_NAME = "report.pdf";

    private static final int TOP_FONT_SIZE = 12;
    private static final int BOTTOM_FONT_SIZE = 8;
    private static final int CAPTION_FONT_SIZE = 14;

    //private static final String FONT_PATH = "C:\\Windows\\Fonts\\Arial.ttf"; // Windows system font
    //private static final String FONT_PATH = "Arial.ttf"; //Font file in project folder
    private static final String FONT_PATH = "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"; //embedded (pdfbox2)
    private static final float IMAGES_IN_ROW = 2f;
    private static final float IMAGES_IN_COLUMN = 3f;
    private static final int   IMAGES_ON_PAGE = (int)(IMAGES_IN_ROW * IMAGES_IN_COLUMN);
    private static final float LEFT_BORDER_WIDTH = 50f;
    private static final float BORDER_WIDTH = 20f;
    private static final float IMAGE_DISTANCE = 10f;
    private static final float TOP_HEIGHT = 20f;
    private static final float CAPTION_HEIGHT = 12f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth() - LEFT_BORDER_WIDTH - BORDER_WIDTH;
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight() - BORDER_WIDTH - BORDER_WIDTH;
    private static final float IMAGE_WIDTH = PAGE_WIDTH / IMAGES_IN_ROW - IMAGE_DISTANCE;
    private static final float IMAGE_HEIGHT = PAGE_HEIGHT / IMAGES_IN_COLUMN;

    private PDDocument document;
    private PDFont font;
    private PDPage page;
    private PDPageContentStream contentStream;
    private String topText;
    private String bottomText;

    /**
     * Constructor
     */
    public Report() {
        document = new PDDocument();
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try {
            font = PDType0Font.load(document, PDFont.class.getResourceAsStream(FONT_PATH));
        } catch (IOException e) {
            System.err.println("ERROR!: Can not load font from " + FONT_PATH);
            e.printStackTrace();
        }

        try {
            contentStream = new PDPageContentStream(document, page);
        } catch (IOException e) {
            System.err.println("ERROR!: Failed creating content stream.");
            e.printStackTrace();
        }
    }

    /**
     * Create standard report
     * @param list Array of ImageBoxes from imageGrid
     */
    public void create(ArrayList<ImageBox> list){
        top();
        bottom();
        int imageCounter = 0;
        for (ImageBox box:list){
            if (box.checked()){
                if (imageCounter == imagesOnPage()){
                    newpage();
                    imageCounter = 0;
                }
                int column = imageCounter % columnsNumber();
                int row    = imageCounter / columnsNumber();
                image(box.imageFileName(), row, column, box.caption());
                imageCounter++;
            }
        }
    }

    /**
     *
     * @return Number of columns on report page
     */
    public int columnsNumber(){
        return (int) IMAGES_IN_ROW;
    }

    /**
     *
     * @return Number of images on report page
     */
    public int imagesOnPage(){
        return IMAGES_ON_PAGE;
    }

    /**
     * Type text on absolute position. Private low level function
     * @param text The text string
     * @param x X position
     * @param y Y position
     * @param fontSize Size of font
     */
    private void insertText(String text, float x, float y, int fontSize){
        try {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            Color color = Color.black;
            contentStream.setNonStrokingColor(color);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(text);
            contentStream.endText();
        } catch (IOException e) {
            System.err.println("ERROR!: Failed text inserting");
            e.printStackTrace();
        }
    }

    /**
     * Create the top string on report page
     */
    public void top (){
        float x = LEFT_BORDER_WIDTH;
        float y = PDRectangle.A4.getHeight() - BORDER_WIDTH - TOP_FONT_SIZE;
        insertText(topText, x, y, TOP_FONT_SIZE);
    }

    /**
     * Create the bottom string on report page
     */
    public void bottom (){
        //noinspection SuspiciousNameCombination
        insertText(bottomText, LEFT_BORDER_WIDTH, BORDER_WIDTH, BOTTOM_FONT_SIZE);
    }

    /**
     * Insert image in report table
     * @param imageFileName Name of image file (with full path or relation path)
     * @param row Row in report table
     * @param column Column in report table
     * @param caption Text description
     */
    public void image(String imageFileName, int row, int column, String caption){
        PDImageXObject image;
        try {
            image = PDImageXObject.createFromFile(imageFileName, document);

            float scale = IMAGE_WIDTH/image.getWidth();
            float scaleY = IMAGE_HEIGHT/image.getHeight();
            if (scale > scaleY) scale = scaleY;

            float x = column * (PAGE_WIDTH/ IMAGES_IN_ROW) + LEFT_BORDER_WIDTH ;
            float y = PDRectangle.A4.getHeight() - BORDER_WIDTH - (row * IMAGE_HEIGHT) - (image.getHeight() * scale) - TOP_HEIGHT;

            contentStream.drawImage(image, x, y, image.getWidth() * scale, image.getHeight() * scale);

            //TODO: Перенос длинного текста на новую строку
            if (caption != null) insertText(caption, x , y - CAPTION_HEIGHT, CAPTION_FONT_SIZE);

        } catch (IOException e) {
            System.err.println("ERROR!: Failed openin image from " + imageFileName);
            e.printStackTrace();
        }
    }

    /**
     * Add new page to report
     */
    public void newpage(){
        try {
            contentStream.close();
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
        } catch (IOException e) {
            System.err.println("ERROR!: Failed new page adding.");
            e.printStackTrace();
        }
        top();
        bottom();
    }

    /**
     *
     * @param text Text for top colontitul
     * TODO: Настройка колонтитулов (из properties)
     */
    public void setTopText(String text){
        topText = text;
    }

    /**
     *
     * @param text Text for bottom colontitul
     * TODO: Настройка колонтитулов (из properties)
     */
    public void setBottomText(String text){
        bottomText = text;
    }



    /**
     * Save report to PDF-file
     * @param filename Name for PDF-file (extension not added)
     */
    public void save(String filename){
        try {
            contentStream.close();
            document.save(filename);
        } catch (IOException e) {
            System.err.println("ERROR!: Failed saving report to " + filename);
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                System.err.println("ERROR!: Failed documet closing after error.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Print report on default printer
     * TODO: Выбор принтера
     */
    public void print(){
        try {
            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
        //set.add(new PrinterResolution(600,600, PrinterResolution.DPI));
        set.add(PrintQuality.HIGH);
        try {
            job.print(set);
        } catch (PrinterException e) {
            e.printStackTrace();
        }
        try {
            contentStream.restoreGraphicsState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print PDF-file from disk on default printer. Static function.
     * @param fileName Path to PDF-file
     */
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

    public static void main(String[] args) {
        Report report = new Report();
        report.image("pict1.jpeg", 0,0, "описание 1");
        report.newpage();
        report.image("pict1.jpeg", 2,1, "описание 2");
        report.print();
        report.save(Report.DEFAULT_REPORT_NAME);
    }
}
