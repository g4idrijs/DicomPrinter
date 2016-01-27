package dicomprinter;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.network.StorageSOPClassSCPDispatcher;

import java.io.File;
import java.io.IOException;
//import java.net.BindException;


/**
 * Получает DICOM-объект(изображение) и сохраняет его во временную папку. Передает имя полученного файла
 * в конвертер изображений. Используется с УЗИ-сканером Mindray DC-7, с другими аппратами не испытывался.
 * Сервер, слушающий DICOM-порт, запускается в отдельном потоке.
 * TODO: Если не хватает прав для биндинга к порту, серверный поток выкидывает BindException, но не завершается.
 * <p>
 * <b>Зависимости</b>: pixelmed, PixelMed™ Java DICOM Toolkit, http://www.pixelmed.com/
 * @author Roman Orekhov, tripsin@yandex.ru
 * @since 2016-04-01
 */
public class DicomImageReceiver {

    /** Статический метод. Запускает диспетчер DICOM-объектов.
     * @param dicomProperties Объект с настройками.
     * @param converter Объект конвертера изображений.
     * @return объект DicomImageReceiver с запущеным диспетчером. Возвращаемое значение пока не используется.
     */
    public static DicomImageReceiver Go(DicomProperties dicomProperties, DicomImageConverter converter) {
        return new DicomImageReceiver(dicomProperties, converter);
    }

    /** Приватный конструктор
     * @param dicomProperties Объект с настройками.
     * @param converter Объект конвертера изображений.
     * @see StorageSOPClassSCPDispatcher
     * @see DicomReceivedObjectHandler
     */
    private DicomImageReceiver(DicomProperties dicomProperties, DicomImageConverter converter){
        try {
            DicomReceivedObjectHandler handler = new DicomReceivedObjectHandler();
            handler.setConverter(converter);
            Thread DicomListener = new Thread(new StorageSOPClassSCPDispatcher(dicomProperties.getDicomPort(),
                    dicomProperties.getAeTitle(), new File(dicomProperties.getTmpDir()),
                    handler, 0));
            /* Этот код не работает. Исключения перехватывается раньше. Печать стека идет из StorageSOPClassSCP.
            DicomListener.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    System.err.println("!!! Exception from Thread !!!");
                    System.err.println(e.getLocalizedMessage());
                }
            });*/
            DicomListener.start();
        } catch (IOException e) {
            System.err.println("Fail - Server thread not started.");
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    /**
     * Вспомогательный внутренний класс. Обрабатывает DICOM-объект, принятый диспетчером, служебный обработчик.
     * Сохраняет во временной папке файл в формате *.dcm (без этого расширения) и передает имя временного файла
     * на дальнейшую обработку в конвертер. Вызывается из диспетчера StorageSOPClassSCPDispatcher.
     * @see ReceivedObjectHandler
     */
    private class DicomReceivedObjectHandler extends ReceivedObjectHandler {

        /** Конвертер изображений
         * @see DicomImageConverter
         * */
        private DicomImageConverter converter;

        /** Получает конвертер изображений из внешнего класса
         * @see DicomImageReceiver
         * @see DicomImageConverter
         */
        void setConverter(DicomImageConverter converter) {
            this.converter = converter;
        }

        /** Метод обработчика. Вызывается из потока сервера. Передает имя dcm-файла в конвертер изображений.
         * @param dicomFileName имя временного dcm-файла.
         * @param callingAETitle Символьное имя УЗИ-аппарата. Задается в настройках аппарата.
         * @param transferSyntax Идентификатор синтаксиса.
         * @see DicomImageConverter#convert(String)
         */
        public void sendReceivedObjectIndication(String dicomFileName, String transferSyntax, String callingAETitle)
                throws DicomNetworkException, DicomException, IOException {
            if (dicomFileName != null) {
                System.err.println("Received: " + dicomFileName + " from " + callingAETitle + " in " + transferSyntax);
                converter.convert(dicomFileName);
            }
        }
    }

}