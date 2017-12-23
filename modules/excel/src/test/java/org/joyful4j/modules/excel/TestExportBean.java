package org.joyful4j.modules.excel;

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
        headerMap.put("name", "姓名");
        headerMap.put("year", "年");
        headerMap.put("month", "月");
        headerMap.put("salary", "薪资");
        headerMap.put("tax", "税额");
        headerMap.put("excel_test_payTime", "支付时间");


        Collection<Object> dataset=new ArrayList<Object>();
        dataset.add(new Payroll4ExcelVo("张三",2017,12,1234.0,10.34,new Date()));
        dataset.add(new Payroll4ExcelVo("李四",2017,10,1345.0,20.56,new Date()));
        dataset.add(new Payroll4ExcelVo("李四",2017,11,null,20.56,new Date()));
        File f=new File("exportBean.xls");
        OutputStream out =new FileOutputStream(f);
        
        ExcelUtil.exportExcel(headerMap, dataset, out);
        out.close();
    }
}
