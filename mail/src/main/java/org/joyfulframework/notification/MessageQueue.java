package org.joyfulframework.notification;

import java.util.List;

/**
 * Created by richey on 17-1-5.
 */
public interface MessageQueue<T extends Message> {
    int size();

    T poll();

    List<T> getMessage();

    void addMessage(T msg);

    void addMessage(List<T> msgs);
}
