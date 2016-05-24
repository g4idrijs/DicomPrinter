package dicomprinter;

import javafx.concurrent.Task;
import java.util.function.Supplier;

public class FXWait {

    private FXWait() {}

    public static void delay(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {}
    }

    public static void delayExecution(long delay, Supplier<Void> function) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {}
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> function.get());
        new Thread(sleeper).start();
    }
}
