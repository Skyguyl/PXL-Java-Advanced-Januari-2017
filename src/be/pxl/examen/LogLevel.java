package be.pxl.examen;

/**
 * Created by Daan Vankerkom on 27/01/2017.
 */
public enum LogLevel {
    INFO,
    DEBUG,
    WARN,
    ERROR,
    IGNORE; // Used as a flag to ignore non-existing log levels.

    public static LogLevel getLogLevelType(String value) {
        for (LogLevel logLevel : values()) {
            if (logLevel.name().equalsIgnoreCase(value)) {
                return logLevel;
            }
        }

        return LogLevel.IGNORE;
    }
}
