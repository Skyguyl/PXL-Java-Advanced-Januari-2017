package be.pxl.examen;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Daan Vankerkom on 27/01/2017.
 */
public class LogEntry {

    private Date date;
    private LogLevel level;
    private String packageClass;
    private String message;

    public LogEntry(Date date, LogLevel level, String packageClass, String message) {
        this.date = date;
        this.level = level;
        this.packageClass = packageClass;
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getPackageClass() {
        return packageClass;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s %s %s %s",
                getDateAsString(getDate()),
                getTimeAsString(getDate()),
                getLevel().name(),
                getPackageClass(),
                getMessage()
        );
    }

    private static String getDateAsString(Date date) {
        try {
            return new SimpleDateFormat(LogEntryFactory.DATEFORMAT_PATTERN).format(date).split(" ")[0];
        } catch (Exception e) {
            System.err.println("Error formatting date " + e.getMessage());
            return "";
        }
    }

    private static String getTimeAsString(Date date) {
        try {
            return new SimpleDateFormat(LogEntryFactory.DATEFORMAT_PATTERN).format(date).split(" ")[1];
        } catch (Exception e) {
            System.err.println("Error formatting time " + e.getMessage());
            return "";
        }

    }
}
