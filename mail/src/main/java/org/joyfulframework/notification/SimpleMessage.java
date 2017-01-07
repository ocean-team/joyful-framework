package org.joyfulframework.notification;

import org.joyfulframework.notification.AbstractMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richey on 17-1-5.
 */
public class SimpleMessage extends AbstractMessage {
    private List<String> recipients = new ArrayList<String>();

    public SimpleMessage(){}

    public SimpleMessage(String receipient,String subJect,String text){
        this.recipients.add(receipient);
        this.setSubject(subJect);
        this.setText(text);
    }

    @Override
    public List<String> getRecipients() {
        return this.recipients;
    }
}
