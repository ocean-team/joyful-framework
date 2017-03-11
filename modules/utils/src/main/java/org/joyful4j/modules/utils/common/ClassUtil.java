package org.joyful4j.modules.utils.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * 类操作工具类
 * 提供获取类加载器、加载类、获取指定包下的所有类等方法
 * Created by richey on 16-9-15.
 * @author richey
 * @since 1.0.SNAPSHOT
 */
public class ClassUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     * @return
     */
    public static ClassLoader getClassLoader(){
        return  Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     * @param className
     * @param isInitialized 是否执行类的静态代码块
     * @return
     */
    public static Class<?> loadClass(String className,boolean isInitialized){
        Class<?> cls;
        try {
            cls = Class.forName(className,isInitialized,getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failure",e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 获取指定包下面的所有类
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClassSet(String packageName){

        Set<Class<?>> classSet = new HashSet<Class<?>>();
        URL url = getClassLoader().getResource(packageName.replace(".","/"));
        if(url !=null){
            String protocol = url.getProtocol();//协议名称
            if(protocol.equals("file")){
                String packagePath = url.getPath().replaceAll("%20"," ");//空格在URL中是用%20替换的(utf8编码)
                addClass(classSet,packagePath,packageName);

            }
        }
        return classSet;
    }

    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(new FileFilter(){
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
        for(File file:files){
            String fileName = file.getName();
            if(file.isFile()){
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if(StringUtils.isNotBlank(packageName)){
                    className = packageName + "." + className;
                }
                doAddClass(classSet,className);
            }else{
                String subPackagePath = fileName;
                if(StringUtils.isNotBlank(packageName)){
                    subPackagePath = packagePath + "/"+subPackagePath;
                }
                String subPackageName = fileName;
                if(StringUtils.isNotBlank(packageName)){
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet,subPackagePath,subPackageName);
            }
        }
    }

    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className,false);
        classSet.add(cls);
    }
}
