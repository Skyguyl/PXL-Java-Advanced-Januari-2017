package be.pxl.examen;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    // Made in Intellij.
    public static void main(String[] args) {

        List<LogEntry> logEntries;
        String inputFolder;
        String outputFolder;
        String errorFolder;
        String[] visibleLogLevels;
        String resultsFileName;

        try (FileInputStream in = new FileInputStream("application.properties")) {
            Properties atts = new Properties();
            atts.load(in);
            atts.list(System.out);

            inputFolder = atts.getProperty("input.folder");
            outputFolder = atts.getProperty("output.folder");
            errorFolder = atts.getProperty("error.folder");

            visibleLogLevels = atts.getProperty("log.levels").split(",");
            resultsFileName = atts.getProperty("result.filename");

        } catch (Exception e) {
            e.printStackTrace();
            return; // Terminate program.
        }

        Path inputPath = Paths.get(inputFolder);
        Path outputPath = Paths.get(outputFolder);
        Path errorPath = Paths.get(errorFolder);

        // Create directories if they do not exist.
        try {
            makeDirectory(inputPath);
            makeDirectory(outputPath);
            makeDirectory(errorPath);
        } catch (IOException e) {
            System.err.println("Failed to create a directory.");
            return;
        }

        // Start parsing
        logEntries = parseLogData(inputFolder);

        // Processing.
        Map<LogLevel, List<LogEntry>> logLevelMap = new LinkedHashMap<>();

        for (String visibleLogLevel : visibleLogLevels) {
            LogLevel logLevel = LogLevel.getLogLevelType(visibleLogLevel);

            // Filter per log level.
            List<LogEntry> collected = logEntries.stream()
                    .filter(log -> log.getLevel() == logLevel)
                    .collect(Collectors.toList());

            logLevelMap.put(logLevel, collected);
        }

        List<LogEntry> errorList;
        if (logLevelMap.containsKey(LogLevel.ERROR)) {
            errorList = logLevelMap.get(LogLevel.ERROR); // Found errors in the map, take these to save performance.
        }else{
            // Search for the errors.
            errorList = logEntries.stream()
                    .filter(log -> log.getLevel() == LogLevel.ERROR)
                    .collect(Collectors.toList());
        }

        long successfullLogins = logEntries.stream()
                .filter(log -> log.getPackageClass().endsWith("AuthenticationSuccessListener"))
                .count();

        long failedLogins = logEntries.stream()
                .filter(log -> log.getPackageClass().endsWith("AuthenticationFailureListener"))
                .count();

        // Calculate peak hours.
        int peakHour = 0;
        long peakValue = 0, currentPeak = 0;

        for (int i = 0; i < 24; i++) {

            final int searchHour = i;

            currentPeak = logEntries.stream()
                    .filter(log -> log.getDate().getHours() == searchHour)
                    .count();

            if (currentPeak > peakValue) {
                peakValue = currentPeak;
                peakHour = i;
            }

        }

        System.out.println("Peak hour -> " + peakHour);

        // Output thread
        Path resultFilePath = Paths.get(outputFolder, resultsFileName);
        Path errorFilePath = Paths.get(errorFolder, "errors.txt"); // ?? -> Geen Specifieke naam opgegeven?

        // Geen Lambdas hier omdat ik van clean code hou.
        Thread logOutputThread = new LogOutputThread(resultFilePath, logLevelMap,successfullLogins, failedLogins, peakHour);
        Thread errorOutputThread = new ErrorOutputThread(errorFilePath, errorList);

        logOutputThread.start();
        errorOutputThread.start();
    }

    private static List<LogEntry> parseLogData(String inputFolder) {
        List<LogEntry> logFile1Entries = new ArrayList<>();
        List<LogEntry> logFile2Entries = new ArrayList<>();
        List<LogEntry> logFile3Entries = new ArrayList<>();

        // Parse logs.
        Thread logFile1Thread = new LogReadThread(logFile1Entries, Paths.get(inputFolder, "system1.log"));
        Thread logFile2Thread = new LogReadThread(logFile2Entries, Paths.get(inputFolder, "system2.log"));
        Thread logFile3Thread = new LogReadThread(logFile3Entries, Paths.get(inputFolder, "system3.log"));

        logFile1Thread.start();
        logFile2Thread.start();
        logFile3Thread.start();

        // Wacht op de log files.
        try {
            logFile1Thread.join();
            logFile2Thread.join();
            logFile3Thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("File1: " + logFile1Entries.size());
        System.out.println("File2: " + logFile2Entries.size());
        System.out.println("File3: " + logFile3Entries.size());

        List<LogEntry> allLogs = new ArrayList<>();

        // Merge lists.
        logFile1Entries.forEach(allLogs::add);
        logFile2Entries.forEach(allLogs::add);
        logFile3Entries.forEach(allLogs::add);

        return allLogs;
    }

    private static void makeDirectory(Path directoryPath) throws IOException {
        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
        } catch (FileAlreadyExistsException e) {
            // Do nothing, something went horribly wrong here? Created between exist & create? Impossible!
        }
    }

}
