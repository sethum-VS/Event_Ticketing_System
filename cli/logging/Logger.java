package logging;

import java.io.IOException;
import java.util.logging.*;

public class Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static final String LOG_FILE = "resources/logs.txt"; // File to write logs

    static {
        try {
            // Disable default console handler
            LogManager.getLogManager().reset();

            // Create a file handler for logging to a file
            FileHandler fileHandler = new FileHandler(LOG_FILE, true); // Append mode
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    return record.getMessage() + System.lineSeparator();
                }
            });
            logger.addHandler(fileHandler);

            // Create a console handler for logging to the console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    return record.getMessage() + System.lineSeparator();
                }
            });
            logger.addHandler(consoleHandler);

            // Set the logging level (INFO level to show general application logs)
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static void log(String message) {
        logger.info(message);
    }

    public static void error(String message) {
        logger.severe(message);
    }

    public static void debug(String message) {
        logger.fine(message); // Fine-grained debugging messages
    }
}
