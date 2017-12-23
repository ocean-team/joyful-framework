package com.ihr360.excel.logs;

/**
 * The <code>ExcelRowLog</code>
 * <p>
 *     Excel行日志
 * </p>
 * @author richey.liu
 * @version 1.0, Created at 2017-12-17
 */
public class ExcelRowLog extends ExcelBasicLog {

    /**
     * 行号
     */
    private Integer rowNum;
    /**
     * 操作的对象
     */
    private Object object;



    /**
     * @return the rowNum
     */
    public Integer getRowNum() {
        return rowNum;
    }

    /**
     * @param rowNum the rowNum to set
     */
    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    /**
     * @return the object
     */
    public Object getObject() {
        return object;
    }

    /**
     * @param object the object to set
     */
    public void setObject(Object object) {
        this.object = object;
    }



    /**
     * @param object
     * @param log
     */
    public ExcelRowLog(Object object, String log) {
        this.object = object;
        this.log = log;
    }

    /**
     * @param rowNum
     * @param object
     * @param log
     */
    public ExcelRowLog(Object object, String log, Integer rowNum) {
        this.object = object;
        this.rowNum = rowNum;
        this.log = log;
    }

    public ExcelRowLog(String log, Integer rowNum) {
        this.log = log;
        this.rowNum = rowNum;
    }


}
