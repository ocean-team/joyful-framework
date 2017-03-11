package org.joyfulframework.notification.mail;

import org.apache.commons.lang3.StringUtils;
import org.joyful4j.commons.lang.Assert;
import org.joyfulframework.notification.AbstractMessage;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by richey on 17-1-5.
 */
public class MailMessage extends AbstractMessage{

    private InternetAddress from;
    private List<InternetAddress> to;
    private List<InternetAddress> cc;
    private List<InternetAddress> bcc;
    private String template;
    private Map<String,String> params = new HashMap<String,String>();
    private Date sendAt;
    private boolean useNotification;

    public MailMessage() {
    }
    public MailMessage(List<InternetAddress> to,String subject,String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
    public MailMessage(List<InternetAddress> to,List<InternetAddress> cc,List<InternetAddress> bcc,String subject,String text) {
        this.to = to;
        this.cc = cc;
        this.cc = bcc;
        this.subject = subject;
        this.text = text;
    }

    public String getEncoding() {
        return StringUtils.substringAfter(this.getContentType(), "charset=");
    }

    @Override
    public List<String> getRecipients() {
       //TODO
        return null;
    }

    public InternetAddress getFrom() {
        return this.from;
    }

    public MailMessage from(String from) {
        List froms = MimeUtil.parseAddress(from, this.getEncoding());
        if(froms.size() > 0) {
            this.from = (InternetAddress)froms.get(0);
        }

        return this;
    }

    public List<InternetAddress> getTo() {
        return to;
    }

    public List<InternetAddress> getCc() {
        return cc;
    }

    public List<InternetAddress> getBcc() {
        return bcc;
    }

    public void addTo(String to){
        Assert.notNull(to);
        this.to.addAll(MimeUtil.parseAddress(to, this.getEncoding()));
    }

    public void addCc(String cc) {
        Assert.notNull(cc);
        this.cc.addAll(MimeUtil.parseAddress(cc, this.getEncoding()));
    }

    public void addBcc(String bcc) {
        Assert.notNull(bcc);
        this.bcc.addAll(MimeUtil.parseAddress(bcc, this.getEncoding()));
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Date getSendAt() {
        return sendAt;
    }

    public void setSendAt(Date sendAt) {
        this.sendAt = sendAt;
    }

    public boolean isUseNotification() {
        return useNotification;
    }

    public void setUseNotification(boolean useNotification) {
        this.useNotification = useNotification;
    }
}
