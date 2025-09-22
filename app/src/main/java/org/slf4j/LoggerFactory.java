package org.slf4j;

public class LoggerFactory {

    private static final Logger LOGGER = new Logger() {
        @Override public void info(String msg) {}
        @Override public void warn(String msg) {}
        @Override public void error(String msg) {}
        @Override public void debug(String format, Object... arguments) {}
        @Override public boolean isDebugEnabled() { return false; }
    };

    public static Logger getLogger(Class<?> clazz) {
        return LOGGER;
    }

    public static Logger getLogger(String name) {
        return LOGGER;
    }
}
