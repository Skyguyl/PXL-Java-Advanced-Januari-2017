package be.pxl.examen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Daan Vankerkom on 27/01/2017.
 */
public class LogEntryFactory {

    public static final String DATEFORMAT_PATTERN = "yyyy-mm-dd HH:mm:sss";

    public static void parseLogFile(Collection<LogEntry> logEntries, Path filePath) {
        List<String> lines = readLogFile(filePath); // Read the file.
        LogEntry logEntry = null;

        for (String line : lines) {
            try {
                logEntry = parseLog(line);
            } catch (Exception e) {
                System.out.println("Exception at line: " + line);
                e.printStackTrace();
                logEntry = null;
                // Failed to parse the line for some reason. We skip it to avoid crashing the app.
                // This shouldn't happen but just in case if someone decided to edit the log file...
            }

            if (logEntry != null && logEntry.getLevel() != LogLevel.IGNORE) {
                logEntries.add(logEntry);
            }
        }
    }

    private static LogEntry parseLog(String line) throws Exception {
        String[] lineData = line.split(" ", 4);
        String message = lineData[3].trim();
        String[] messageData = message.split(" ", 2);

        String logMessage = "";
        if (messageData.length > 1) {
            logMessage = messageData[1].trim();
        }

        return new LogEntry(
                new SimpleDateFormat(DATEFORMAT_PATTERN).parse(lineData[0] + " " + lineData[1]), // Date
                LogLevel.getLogLevelType(lineData[2]), // Log Level
                messageData[0], // Package Class
                logMessage // Message.
        );
    }

    private static List<String> readLogFile(Path filePath) {
        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

}
