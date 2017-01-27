package be.pxl.examen;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Daan Vankerkom on 27/01/2017.
 */
public class ErrorOutputThread extends Thread {

    private Path errorFilePath;
    private List<LogEntry> errorList;

    public ErrorOutputThread(Path errorFilePath, List<LogEntry> errorList) {
        this.errorFilePath = errorFilePath;
        this.errorList = errorList;
    }

    @Override
    public void run() {
        // Start writing the output file.
        System.out.println("Writing: " + errorFilePath.getFileName());

        try (FileWriter file = new FileWriter(errorFilePath.toFile(), false)) {

            for (LogEntry logEntry : errorList) {
                file.write(logEntry.toString());
                file.write("\n");
            }

            file.close();

            System.out.println("Finished writing: " + errorFilePath.getFileName());
        } catch (IOException e) {
            System.err.println("Failed to write file: " + errorFilePath.getFileName());
            System.err.println(e.getMessage());
        }

    }
}
