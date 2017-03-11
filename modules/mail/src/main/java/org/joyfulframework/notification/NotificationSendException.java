package org.joyfulframework.notification;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by richey on 17-1-8.
 */
public class NotificationSendException extends NotificationException{

    private static final long serialVersionUID = 8834417833481215177L;

    private final Map<Object, Exception> failedMessages;

    public NotificationSendException(String msg, Throwable cause, Map<Object, Exception> failedMessages) {
        super(msg, cause);
        this.failedMessages = new LinkedHashMap(failedMessages);
    }

    public NotificationSendException(Map<Object, Exception> failedMessages) {
        this((String)null, (Throwable)null, failedMessages);
    }

    public Map<Object, Exception> getFailedMessages() {
        return this.failedMessages;
    }
}
