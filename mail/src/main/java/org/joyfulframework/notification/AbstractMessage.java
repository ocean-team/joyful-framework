package org.joyfulframework.notification;

import java.util.Properties;

/**
 * Created by richey on 17-1-5.
 */
public abstract  class AbstractMessage implements Message{
    protected String subject; //主题
    protected String text;  //内容
    protected Properties properties = new Properties();//消息属性
    protected String contentType = TEXT;//内容类型

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
