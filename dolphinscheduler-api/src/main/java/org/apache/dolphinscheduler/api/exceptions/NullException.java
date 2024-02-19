package org.apache.dolphinscheduler.api.exceptions;

/**
 * @author jxz
 * @version 1.0
 * @date 2021/3/16 9:46
 */
public class NullException extends RuntimeException {
    public NullException(String message) {
        super(message);
    }

    public NullException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
