package org.joyful4j.framework.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by richey on 16-9-14.
 * 类型操作工具类
 */
public class CastUtil {

    /**
     * 转为String类型,如果obj是空则返回默认值
     * @param obj
     * @param defaultValue
     * @return
     */
    public static String castString(Object obj,String defaultValue){
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    /**
     * 转为String类型,如果obj是空则返回""
     * @param obj
     * @return
     */
    public static String castString(Object obj){
        return castString(obj,"");
    }

    /**
     * 转为long类型(提供默认值)
     * @param obj
     * @param defaultLong  默认值,如果obj为空,或obj不能转为long类型,则返回默认值
     * @return
     */
    public static long castLong(Object obj,long defaultLong){
        long value = defaultLong;
        if(obj != null){
            String strValue = castString(obj);
            if(StringUtils.isNotBlank(strValue)){
                try {
                    value = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    value = defaultLong;
                }
            }
        }
        return value;
    }

    /**
     * 转为long类型,如果obj为空或不能转为long类型则返回0
     * @param obj
     * @return
     */
    public static long castLong(Object obj){
        return castLong(obj,0);
    }

    /**
     * 转为double类型(提供默认值)
     * @param obj
     * @param defaultvalue  默认值,如果obj为空,或obj不能转为double类型,则返回默认值
     * @return
     */
    public static double castDouble(Object obj,double defaultvalue){
        double value = defaultvalue;
        if(obj != null){
            String strValue = castString(obj);
            if(StringUtils.isNotBlank(strValue)){
                try {
                    value = Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    value = defaultvalue;
                }
            }
        }
        return value;
    }

    /**
     * 转为double类型,如果obj为空或不能转为double类型则返回0
     * @param obj
     * @return
     */
    public static double castDouble(Object obj){
        return castDouble(obj,0);
    }

    /**
     * 转为int类型(提供默认值)
     * @param obj
     * @param defaultvalue  默认值,如果obj为空,或obj不能转为int类型,则返回默认值
     * @return
     */
    public static int castInt(Object obj,int defaultvalue){
        int value = defaultvalue;
        if(obj != null){
            String strValue = castString(obj);
            if(StringUtils.isNotBlank(strValue)){
                try {
                    value = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    value = defaultvalue;
                }
            }
        }
        return value;
    }

    /**
     * 转为int类型,如果obj为空或不能转为int类型则返回0
     * @param obj
     * @return
     */
    public static int castInt(Object obj){
        return castInt(obj,0);
    }

    /**
     * 转为boolean类型,提供默认值
     * @param obj
     * @param defaultValue  如果obj是空的时候返回默认值
     * @return
     */
    public static boolean castBoolean(Object obj,boolean defaultValue){
        boolean value = defaultValue;
        if(obj != null){
            value = Boolean.parseBoolean(castString(obj));
        }
        return value;
    }

    /**
     * 转为boolean类型,当obj为空的时候,返回false;
     * @param obj
     * @return
     */
    public static boolean castBoolean(Object obj){
        return castBoolean(obj,false);
    }

}
