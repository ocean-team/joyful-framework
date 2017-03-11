package org.joyfulframework.notification.mail;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by richey on 17-1-8.
 */
public class DefaultMimeMessageContentBuilder implements MimeMessageBuilder {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public DefaultMimeMessageContentBuilder() {
    }

    @Override
    public boolean isSupported(MailMessage mailMessage) {
        return true;
    }

    @Override
    public void build(MimeMessage message, MailMessage mailMessage, String encoding) throws MessagingException {
        boolean html = StringUtils.contains(mailMessage.getContentType(), "html");
        if(html) {
            if(StringUtils.isEmpty(encoding)) {
                message.setContent(mailMessage.getText(), "text/html");
            } else {
                message.setContent(mailMessage.getText(), "text/html;charset=" + encoding);
            }
        } else if(StringUtils.isEmpty(encoding)) {
            message.setText(mailMessage.getText());
        } else {
            message.setText(mailMessage.getText(), encoding);
        }
    }

    @Override
    public void postSend(MailMessage mailMessage) {

    }
}
