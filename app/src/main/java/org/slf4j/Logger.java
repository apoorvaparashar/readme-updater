package org.slf4j;

public interface Logger {
    void info(String msg);
    void warn(String msg);
    void error(String msg);
    void debug(String format, Object... arguments);
    boolean isDebugEnabled();
}
