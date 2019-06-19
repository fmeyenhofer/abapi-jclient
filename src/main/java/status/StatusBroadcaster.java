package status;

import java.io.File;
import java.util.Observable;

/**
 * @author Felix Meyenhofer
 */
public class StatusBroadcaster extends Observable {

    private String message = "";

    private int totalIterations = -1;

    private int currentIterations = -1;

    public void setStatus(String msg) {
        message = msg;
        setChanged();
        notifyObservers();
    }

    public void setStatus(String msg, int cIter, int tIter) {
        message = msg;
        currentIterations = cIter;
        totalIterations = tIter;
        setChanged();
        notifyObservers();
    }

    public void setFileSizeObserver(File file) {
        FileSizeMonitor observer = new FileSizeMonitor(file, this);
        Thread thread = new Thread(observer);
        thread.start();
//        thread.join();
    }

    public String getMessage() {
        return message;
    }

    public int getTotalIterations() {
        return totalIterations;
    }

    public int getCurrentIterations() {
        return currentIterations;
    }
}
