package org.joyful4j.modules.excel.constants;

import org.apache.poi.ss.usermodel.CellType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExcelDefaultConfig {

    //日期类型默认输出格式
    public static final String DEFAULT_OUTPUT_DATE_PATTERN = "yyyy-MM-dd";

    //注解的排序字段
    public static final String SORT_ANNO_PROPS = "index";


    /**
     * 用来验证excel与Vo中的类型是否一致 <br>
     * Map<栏位类型,只能是哪些Cell类型>
     * 松散模式
     */
    public static Map<Class<?>, CellType[]> looseValidateMap = new HashMap<>();

    /**
     * 严格模式
     */
    public static Map<Class<?>, CellType[]> strickValidateMap = new HashMap<>();

    static {
        //暂不支持数组类型
//        looseValidateMap.put(String[].class, new CellType[]{CellType.STRING});
//        looseValidateMap.put(Double[].class, new CellType[]{CellType.NUMERIC});
        looseValidateMap.put(String.class, new CellType[]{CellType.STRING, CellType.BLANK,CellType.NUMERIC});
        looseValidateMap.put(Double.class, new CellType[]{CellType.NUMERIC,CellType.STRING});
        looseValidateMap.put(Date.class, new CellType[]{CellType.NUMERIC, CellType.STRING});
        looseValidateMap.put(Integer.class, new CellType[]{CellType.NUMERIC,CellType.STRING});
        looseValidateMap.put(Float.class, new CellType[]{CellType.NUMERIC,CellType.STRING});
        looseValidateMap.put(Long.class, new CellType[]{CellType.NUMERIC,CellType.STRING});
        looseValidateMap.put(Boolean.class, new CellType[]{CellType.BOOLEAN});

      /*  strickValidateMap.put(String.class, new CellType[]{CellType.STRING, CellType.BLANK});
        strickValidateMap.put(Double.class, new CellType[]{CellType.NUMERIC});
        strickValidateMap.put(Date.class, new CellType[]{CellType.NUMERIC});
        strickValidateMap.put(Integer.class, new CellType[]{CellType.NUMERIC});
        strickValidateMap.put(Float.class, new CellType[]{CellType.NUMERIC});
        strickValidateMap.put(Long.class, new CellType[]{CellType.NUMERIC});
        strickValidateMap.put(Boolean.class, new CellType[]{CellType.BOOLEAN});*/
    }





}
