package org.joyfulframework.notification.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by richey on 17-1-8.
 */
public interface MimeMessageBuilder {
    boolean isSupported(MailMessage mailMessage);

    void build(MimeMessage mimeMessage, MailMessage mailMessage, String encoding) throws MessagingException;

    void postSend(MailMessage mailMessage);
}
