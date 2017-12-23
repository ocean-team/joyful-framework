/**
 * @author SargerasWang
 */
package org.joyful4j.modules.excel;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The <code>TestExportMap</code>	
 * 
 * @author richey.liu
 */
public class TestExportMap {

  public static final String EXCEL_TEST_NAME = "excel_test_name";
  public static final String EXCEL_TEST_YEAR = "excel_test_year";
  public static final String EXCEL_TEST_MONTH = "excel_test_month";
  public static final String EXCEL_TEST_SALARY = "excel_test_salary";
  public static final String EXCEL_TEST_TAX = "excel_test_tax";
  public static final String EXCEL_TEST_PAYTIME = "excel_test_payTime";



  @Test
  public void exportXls() throws IOException {
    List<Map<String,Object>> datas = new ArrayList<>();
    Map<String,Object> data1 =new LinkedHashMap<>();
    data1.put("name", "Foo");
    data1.put("year",2017);
    data1.put("month",12);
    data1.put("salary",1234);
    data1.put("tax",123);
    data1.put("excel_test_payTime",new Date());

    Map<String,Object> data2 =new LinkedHashMap<String, Object>();
    data2.put("name", "Hoo");
    data2.put("year",2017);
    data2.put("month",11);
    data2.put("salary",1563);
    data2.put("tax",125);
    data2.put("excel_test_payTime",new Date());


    datas.add(data1);
    datas.add(data2);

    Map<String,String> headerMap = new LinkedHashMap<>();
    headerMap.put("name", "姓名");
    headerMap.put("year", "年");
    headerMap.put("month", "月");
    headerMap.put("salary", "薪资");
    headerMap.put("tax", "税额");
    headerMap.put("excel_test_payTime", "支付时间");

    File f= new File("testExportMapData.xls");
    OutputStream out = new FileOutputStream(f);

    /**
     * 导出map类型的数据
     * 表头map要用有序的map，导出Excel表头为表头map的顺序
     * 表头key和数据key要对应
     */
    ExcelUtil.exportExcel(headerMap,datas, out );
    out.close();
  }
}
