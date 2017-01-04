package org.joyfulframework.util.reflect;

/**
 * Created by richey on 16-12-28.
 */
public class Foo {
    public Foo(String name){};
    public Foo(){};

    private String name;

    public void introduce(){
        System.out.println("hello,"+name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
