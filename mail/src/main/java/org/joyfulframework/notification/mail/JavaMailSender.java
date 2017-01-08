package org.joyfulframework.notification.mail;

import org.apache.commons.lang3.StringUtils;
import org.joyfulframework.notification.NotificationException;
import org.joyfulframework.notification.NotificationSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by richey on 17-1-8.
 */
public class JavaMailSender implements MailSender{

    protected static final Logger logger = LoggerFactory.getLogger(JavaMailSender.class);
    private static final String HEADER_MESSAGE_ID = "Message-ID";
    private Properties javaMailProperties = new Properties();
    private Session session;
    private String protocol = "smtp";
    private String host;
    private int port = -1;
    private String username;
    private String password;
    private String defaultEncoding;
    private List<MimeMessageBuilder> mimeMessageBuilders = new ArrayList<MimeMessageBuilder>();

    protected synchronized Session getSession() {
        if(this.session == null) {
            this.session = Session.getInstance(this.javaMailProperties);
        }
        return this.session;
    }

    @Override
    public void send(MailMessage... messages) {
        //TODO
        List<MimeMessage> mimeMessages = new ArrayList<MimeMessage>();
        MailMessage[] mailMessages = messages;
        MailMessage mailMessage;
        for(int i=0 ;i<mailMessages.length;++i){
            mailMessage = mailMessages[i];
            try {
                mimeMessages.add(this.createMimeMessage(mailMessage));
            } catch (MessagingException e) {
                logger.error("Cannot mapping message" + mailMessage.getSubject(), e);
            }
        }
        this.doSend((MimeMessage[])mimeMessages.toArray(new MimeMessage[mimeMessages.size()]));
        //TODO
    }

    protected MimeMessage createMimeMessage(MailMessage mailMsg) throws MessagingException {
        MimeMessage mimeMsg = new MimeMessage(this.getSession());
        mimeMsg.setSentDate(null == mailMsg.getSendAt()?new Date():mailMsg.getSendAt());
        if(null != mailMsg.getFrom()) {
            mimeMsg.setFrom(mailMsg.getFrom());
        }

        this.addRecipient(mimeMsg, mailMsg);
        String encoding = StringUtils.substringAfter(mailMsg.getContentType(), "charset=");
        try {
            mimeMsg.setSubject(MimeUtility.encodeText(mailMsg.getSubject(), encoding, "B"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        for (MimeMessageBuilder builder : mimeMessageBuilders) {
            if (builder.isSupported(mailMsg)) {
                builder.build(mimeMsg,mailMsg,encoding);
            }
        }

        return mimeMsg;
    }

    private int addRecipient(MimeMessage mimeMsg, MailMessage mailMsg) throws MessagingException {
        int recipients = 0;
        for(InternetAddress internetAddress : mailMsg.getTo()){
            mimeMsg.addRecipient(Message.RecipientType.TO,internetAddress);
            recipients++;
        }
        for(InternetAddress internetAddress : mailMsg.getCc()){
            mimeMsg.addRecipient(Message.RecipientType.CC,internetAddress);
            recipients++;
        }
        for(InternetAddress internetAddress : mailMsg.getBcc()){
            mimeMsg.addRecipient(Message.RecipientType.BCC,internetAddress);
            recipients++;
        }
        return recipients;
    }

    protected void doSend(MimeMessage[] mimeMessages) {
        LinkedHashMap failedMessages = new LinkedHashMap();

        Transport transport;
        int i;
        try {
            transport = this.getTransport(this.getSession());
            transport.connect(this.getHost(), this.getPort(), this.getUsername(), this.getPassword());
        } catch (AuthenticationFailedException e) {
            throw new NotificationException(e);
        } catch (MessagingException e) {
            MessagingException ex = e;
            for(i = 0; i < mimeMessages.length; ++i) {
                MimeMessage original = mimeMessages[i];
                failedMessages.put(original, ex);
            }
            throw new NotificationException("Mail server connection failed", ex);
        }

        try {
            for (MimeMessage mimeMessage : mimeMessages) {
                try {
                    if(mimeMessage.getSentDate() == null) {
                        mimeMessage.setSentDate(new Date());
                    }
                    String msgId = mimeMessage.getMessageID();
                    mimeMessage.saveChanges();
                    if(msgId != null) {
                        mimeMessage.setHeader("Message-ID", msgId);
                    }
                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                } catch (MessagingException e) {
                    failedMessages.put(mimeMessage, e);
                }
            }
        } finally {
            try {
                transport.close();
            } catch (MessagingException e) {
                if(!failedMessages.isEmpty()) {
                    throw new NotificationSendException("Failed to close server connection after message failures", e, failedMessages);
                }
                throw new NotificationException("Failed to close server connection after message sending", e);
            }
        }

        if(!failedMessages.isEmpty()) {
            throw new NotificationSendException(failedMessages);
        }
    }



    protected Transport getTransport(Session session) throws NoSuchProviderException {
        String protocol = this.getProtocol();
        if(protocol == null) {
            protocol = session.getProperty("mail.transport.protocol");
        }

        return session.getTransport(protocol);
    }

    public Properties getJavaMailProperties() {
        return this.javaMailProperties;
    }

    public void setJavaMailProperties(Properties javaMailProperties) {
        this.javaMailProperties = javaMailProperties;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
