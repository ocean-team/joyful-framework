package org.joyfulframework.notification;

/**
 * Created by richey on 17-1-8.
 */
public class NotificationException extends RuntimeException {
    public NotificationException() {
        super();
    }
    public NotificationException(Throwable cause) {
        super(cause);
    }
    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }

}
