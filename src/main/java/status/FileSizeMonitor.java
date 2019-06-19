package status;

import java.io.File;

/**
 * @author Felix Meyenhofer
 */
public class FileSizeMonitor implements Runnable {

    private File file;
    private StatusBroadcaster broadcaster;
    private long previousSize = 0L;


    FileSizeMonitor(File file, StatusBroadcaster broadcaster) {
        this.file = file;
        this.broadcaster = broadcaster;
    }


    @Override
    public void run() {
        broadcaster.setStatus("Observing file " + file.getAbsolutePath());
        long currentSize;

        try {
            while (!file.exists()) {
                broadcaster.setStatus("Wait for file to be created");
                Thread.sleep(500);
            }

            long start = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted()) {
                currentSize = file.length();
                if (currentSize <= previousSize) {
                    long stop = System.currentTimeMillis();
                    broadcaster.setStatus("Observation time: " + ((stop - start) / 1E3) + " sec.");
                    broadcaster.setStatus("Final file size: " + currentSize/1E6 + " MB");
                    break;
                }
                broadcaster.setStatus((currentSize-previousSize) / 1E6 + " MB/sec");
                previousSize = currentSize;
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
