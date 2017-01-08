package org.joyful4j.commons.lang;

import java.util.Iterator;

/**
 * Created by richey on 17-1-8.
 */
public class Assert {
    private static final String NotEmptyCharSeqMsg = "The validated character sequence is empty";
    private static final String IsNullMsg = "The validated object is null";
    private static final String IsTrueMsg = "The validated expression is false";

    public Assert() {
    }

    public static void isTrue(boolean expression) {
        if(!expression) {
            throw new IllegalArgumentException("The validated expression is false");
        }
    }

    public static void isTrue(boolean expression, String message, Object... values) {
        if(!expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T> T notNull(T object) {
        return notNull(object, "The validated object is null", new Object[0]);
    }

    public static <T> T notNull(T object, String message, Object... values) {
        if(object == null) {
            throw new NullPointerException(String.format(message, values));
        } else {
            return object;
        }
    }

    public static <T extends CharSequence> T notEmpty(T chars) {
        if(chars == null) {
            throw new NullPointerException("The validated character sequence is empty");
        } else if(chars.length() == 0) {
            throw new IllegalArgumentException("The validated character sequence is empty");
        } else {
            return chars;
        }
    }

    public static <T extends CharSequence> T notEmpty(T chars, String message, Object... values) {
        if(chars == null) {
            throw new NullPointerException(String.format(message, values));
        } else if(chars.length() == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        } else {
            return chars;
        }
    }

    public static <T extends Iterable<?>> T noNullElements(T iterable, String message, Object... values) {
        notNull(iterable);
        int i = 0;

        for(Iterator it = iterable.iterator(); it.hasNext(); ++i) {
            if(it.next() == null) {
                throw new IllegalArgumentException(String.format(message, new Object[]{Integer.valueOf(i)}));
            }
        }

        return iterable;
    }
}