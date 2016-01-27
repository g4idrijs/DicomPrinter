package dicomprinter;

import java.io.*;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;


/**
 * Create multi-image PDF-report for DICOM-printer
 * Created by 1 on 14.01.2016.
 */

public class Report {

    public static final String DEFAULT_REPORT_NAME = "report.pdf";

    // Windows system font
    //private static final String FONT_PATH = "C:\\Windows\\Fonts\\Arial.ttf";
    // Font in Project Folder
    private static final String FONT_PATH = "Arial.ttf";
    private static final float IMAGES_IN_ROW = 2f;
    private static final float IMAGES_IN_COLUMN = 3f;
    private static final int IMAGES_ON_PAGE = (int)(IMAGES_IN_ROW * IMAGES_IN_COLUMN);
    private static final float leftBorderWidth = 50f;
    private static final float borderWidth = 20f;
    private static final float imageDistance = 10f;
    private static final float topHeight = 20f;
    private static final float captionHeight = 12f;
    private static final float pageWidth = PageSize.A4.getWidth() - leftBorderWidth - borderWidth;
    private static final float pageHeight = PageSize.A4.getHeight() - borderWidth - borderWidth;
    private static final float imageWidth = pageWidth/ IMAGES_IN_ROW - imageDistance;
    private static final float imageHeight = pageHeight / IMAGES_IN_COLUMN;

    private BaseFont bf;
    private final Document document;
    private PdfWriter writer;

    public Report(String filename) {
        try {
            bf = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
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

    private void insertText(String text, float x, float y, int fontSize){
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
        over.setLineWidth(0.1f);
        over.setFontAndSize(bf, fontSize);
        over.moveText(x, y);
        over.showText(text);
        over.endText();
        over.restoreState();
    }

    public void top (String top){
        float x = leftBorderWidth;
        float y = PageSize.A4.getHeight() - borderWidth - 12;
        insertText(top, x, y, 12);
    }

    public void bottom (String bottom){
        //noinspection SuspiciousNameCombination
        insertText(bottom, leftBorderWidth, borderWidth, 10);
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
            image.scaleToFit(imageWidth, imageHeight);
        } catch (BadElementException e) {
            System.err.println("ERROR: Bad image in file " + imageFileName);
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("ERROR: Can not open file " + imageFileName);
            e.printStackTrace();
            System.exit(-1);
        }

        float x = column * (pageWidth/ IMAGES_IN_ROW) + leftBorderWidth ;
        float y = PageSize.A4.getHeight() - borderWidth - row * imageHeight - image.getScaledHeight() - topHeight;
        image.setAbsolutePosition(x, y);

        try {
            document.add(image);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        //TODO: Перенос длинного текста на новую строку
        if (caption != null) insertText(caption, x , y - captionHeight, 14);
    }

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
