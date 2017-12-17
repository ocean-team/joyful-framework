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
        //用排序的Map且Map的键应与ExcelCell注解的index对应
        Map<String,String> map = new LinkedHashMap<>();
        map.put("a","姓名");
        map.put("b","年");
        map.put("c","月");
        map.put("d","工资");
        map.put("","税额");
        map.put("e","发放时间");

        Collection<Object> dataset=new ArrayList<Object>();
        dataset.add(new Payroll4Excel(null,2017,12,1234.0,10.34,new Date()));
        dataset.add(new Payroll4Excel("李四",2017,null,1345.0,20.56,new Date()));
        dataset.add(new Payroll4Excel("李四",2017,11,null,20.56,new Date()));
        File f=new File("test2.xls");
        OutputStream out =new FileOutputStream(f);
        
        ExcelUtil.exportExcel(map, dataset, out);
        out.close();
    }
}
