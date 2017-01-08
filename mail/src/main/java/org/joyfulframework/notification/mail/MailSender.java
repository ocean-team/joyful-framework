package org.joyfulframework.notification.mail;

/**
 * Created by richey on 17-1-8.
 */
public interface MailSender {
    void send(MailMessage... messages);
}
