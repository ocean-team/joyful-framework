package com.ihr360.excel.logs;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>ExcelLogs</code>
 * <p>
 *
 * </p>
 *
 * @author richey.liu
 * @version 1.0, Created at 2017-12-17
 */
public class ExcelLogs{


    private List<ExcelRowLog> rowLogList = new ArrayList<>();

    private List<ExcelLog> excelLogs = new ArrayList<>();


    /**
     * @return
     */
    public boolean hasRowLogList() {
        return CollectionUtils.isNotEmpty(this.rowLogList);
    }

    public boolean hasExcelLogs() {
        return CollectionUtils.isNotEmpty(excelLogs);
    }



    /**
     * @return the rowLogList
     */
    public List<ExcelRowLog> getRowLogList() {
        return rowLogList;
    }


    /**
     * @param rowLogList the rowLogList to set
     */
    public void setRowLogList(List<ExcelRowLog> rowLogList) {
        this.rowLogList = rowLogList;
    }

    public List<ExcelLog> getExcelLogs() {
        return excelLogs;
    }

    public void setExcelLogs(List<ExcelLog> excelLogs) {
        this.excelLogs = excelLogs;
    }
}
