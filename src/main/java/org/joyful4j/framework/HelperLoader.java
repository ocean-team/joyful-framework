package org.joyful4j.framework;

import org.joyful4j.framework.helper.BeanHelper;
import org.joyful4j.framework.helper.ClassHelper;
import org.joyful4j.framework.helper.ConfigHelper;
import org.joyful4j.framework.helper.IocHelper;
import org.joyful4j.framework.utils.ClassUtil;

/**
 * 集中加载相应的Helper类
 * Created by richey on 16-9-15.
 * @author richey
 * @since 1.0.SNAPSHOT
 */
public final class HelperLoader {
    public static void init(){
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ConfigHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName(),true);
        }
    }
}
