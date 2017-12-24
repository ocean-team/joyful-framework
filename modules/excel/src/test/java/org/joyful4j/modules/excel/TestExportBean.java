package org.joyful4j.modules.excel;

import org.apache.commons.collections.map.HashedMap;
import org.joyful4j.modules.excel.cellstyle.ExcelCellStyle;
import org.joyful4j.modules.excel.cellstyle.ExcelCellStyleFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestExportBean {

    @Test
    public void exportXls() throws IOException {
        /**
         * 用排序的Map且key的顺序应与ExcelCell注解的index对应
         * 导出的数据顺序
         */
        Map<String,String> headerMap = new LinkedHashMap<>();
        headerMap.put(Payroll4ExcelVo.EXCEL_TEST_NAME, "姓名");
        headerMap.put(Payroll4ExcelVo.EXCEL_TEST_YEAR, "年");
        headerMap.put(Payroll4ExcelVo.EXCEL_TEST_MONTH, "月");
        headerMap.put(Payroll4ExcelVo.EXCEL_TEST_SALARY, "薪资");
        headerMap.put(Payroll4ExcelVo.EXCEL_TEST_TAX, "税额");
        headerMap.put(Payroll4ExcelVo.EXCEL_TEST_PAYTIME, "支付时间");

        ExcelCellStyle requiredStyle = ExcelCellStyleFactory.createRequiredHeaderCellStyle();

        Map<String, ExcelCellStyle> headerStyleMap = new HashedMap();
        headerStyleMap.put(Payroll4ExcelVo.EXCEL_TEST_NAME, requiredStyle);

        Collection<Object> dataset=new ArrayList<Object>();
        dataset.add(new Payroll4ExcelVo("张三",2017L,12,1234.0,10.34,new Date()));
        dataset.add(new Payroll4ExcelVo("李四",2017L,10,1345.0,20.56,new Date()));
        dataset.add(new Payroll4ExcelVo("李四",2017L,11,null,20.56,new Date()));
        File f=new File("exportBean.xls");
        OutputStream out =new FileOutputStream(f);


        ExcelUtil.exportExcel(headerMap, dataset,headerStyleMap ,out);
        out.close();
    }
}
