package be.pxl.examen;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by Daan Vankerkom on 27/01/2017.
 */
public class LogOutputThread extends Thread {

    private Path filePath;
    private Map<LogLevel, List<LogEntry>> logLevelMap;
    private long successfullLogins;
    private long failedLogins;
    private int timeStamp;

    public LogOutputThread(Path filePath, Map<LogLevel, List<LogEntry>> logLevelMap, long successfullLogins, long failedLogins, int timeStamp) {
        this.filePath = filePath;
        this.logLevelMap = logLevelMap;
        this.successfullLogins = successfullLogins;
        this.failedLogins = failedLogins;
        this.timeStamp = timeStamp;
    }

    @Override
    public void run() {
        // Debug output for the maps.
        for (Map.Entry<LogLevel, List<LogEntry>> logLevelListEntry : logLevelMap.entrySet()) {
            System.out.println(logLevelListEntry.getKey() + " -> " + logLevelListEntry.getValue().size());
        }

        System.out.println("Valid logins: " + successfullLogins);
        System.out.println("Failed logins: " + failedLogins);

        // Start writing the output file.
        System.out.println("Writing: " + filePath.getFileName());

        String lineToWrite = "";
        try (FileWriter file = new FileWriter(filePath.toFile(), false)) {

            for (Map.Entry<LogLevel, List<LogEntry>> logLevelListEntry : logLevelMap.entrySet()) {

                lineToWrite = String.format(
                        "Aantal %s regels: %d%n%n",
                        logLevelListEntry.getKey(),
                        logLevelListEntry.getValue().size()
                );

                file.write(lineToWrite);

            }

            file.write(String.format("Sucessvolle logins: %d%n%n", successfullLogins));
            file.write(String.format("Foute login pogingen: %d%n%n", failedLogins));
            file.write(String.format("Meeste belasting tussen %du en %du%n%n", timeStamp, (timeStamp + 1) % 24));

            file.close();

            System.out.println("Finished writing: " + filePath.getFileName());
        } catch (IOException e) {
            System.err.println("Failed to write file: " + filePath.getFileName());
            System.err.println(e.getMessage());
        }

    }

}
