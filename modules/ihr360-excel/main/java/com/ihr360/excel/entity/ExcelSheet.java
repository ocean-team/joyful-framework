package com.ihr360.excel.entity;

import java.util.Collection;
import java.util.Map;

/**
 * 用于导出多个sheet的Vo The <code>ExcelSheet</code>
 *
 * @author richeye.liu
 * @version 1.0, Created at 2017-12-17
 */
public class ExcelSheet<T> {
    private String sheetName;
    private Map<String, String> headers;
    private Collection<T> dataset;

    /**
     * @return the sheetName
     */
    public String getSheetName() {
        return sheetName;
    }

    /**
     * Excel页签名称
     *
     * @param sheetName the sheetName to set
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    /**
     * Excel表头
     *
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Excel数据集合
     *
     * @return the dataset
     */
    public Collection<T> getDataset() {
        return dataset;
    }

    /**
     * @param dataset the dataset to set
     */
    public void setDataset(Collection<T> dataset) {
        this.dataset = dataset;
    }

}
