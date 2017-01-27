package be.pxl.examen;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Created by Daan Vankerkom on 27/01/2017.
 */
public class LogReadThread extends Thread {

    private Collection<LogEntry> logEntryCollection;
    private Path filePath;

    public LogReadThread(Collection<LogEntry> logEntryCollection, Path filePath) {
        this.logEntryCollection = logEntryCollection;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        LogEntryFactory.parseLogFile(logEntryCollection, filePath);
    }
}
