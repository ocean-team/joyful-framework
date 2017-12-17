/**
 * @author SargerasWang
 */
package org.joyful4j.modules.excel;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * 测试导入
 */
public class TestImportExcel {

  @Test
  public void importXls() throws FileNotFoundException {
    File f=new File("test2.xls");
    InputStream inputStream= new FileInputStream(f);
    
    ExcelLogs logs =new ExcelLogs();
    Collection<Payroll4Excel> importExcel = ExcelUtil.importExcel(Payroll4Excel.class, inputStream, "yyyy/MM/dd HH:mm:ss", logs , 0);
    
    for(Payroll4Excel m : importExcel){
      System.out.println(m);
    }
  }

  @Test
  public void importXlsx() throws FileNotFoundException {
    File f=new File("src/test/resources/test.xlsx");
    InputStream inputStream= new FileInputStream(f);

    ExcelLogs logs =new ExcelLogs();
    Collection<Map> importExcel = ExcelUtil.importExcel(Map.class, inputStream, "yyyy/MM/dd HH:mm:ss", logs , 0);

    for(Map m : importExcel){
      System.out.println(m);
    }
  }

}
