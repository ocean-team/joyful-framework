package org.joyfulframework.util.reflect;


import org.joyfulframework.utils.reflect.ReflectionUtil;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by richey on 16-12-28.
 */
public class ReflectionUtilTest {

    private Foo foo;

    @org.junit.Test
    public void newInstance() throws Exception {
        ReflectionUtil.newInstance(Foo.class);
    }

    @Test
    public void invokeMethod() throws Exception {
        Foo foo = new Foo();
        Method setName = Foo.class.getMethod("setName",String.class);
        Method introdute = Foo.class.getMethod("introduce");
        ReflectionUtil.InvokeMethod(foo,setName,"richey");
        ReflectionUtil.InvokeMethod(foo,introdute,null);
    }

    @org.junit.Test
    public void setField() throws Exception {

    }

}