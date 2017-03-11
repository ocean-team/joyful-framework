package org.joyfulframework.notification;

import java.util.List;
import java.util.Properties;

/**
 * Created by richey on 17-1-5.
 */
public interface Message {
    String TEXT = "text/plain; charset=UTF-8";
    String HTML = "text/html; charset=UTF-8";

    String getSubject();

    void setSubject(String var1);

    String getText();

    void setText(String var1);

    Properties getProperties();

    List<String> getRecipients();

    String getContentType();

    void setContentType(String var1);
}
