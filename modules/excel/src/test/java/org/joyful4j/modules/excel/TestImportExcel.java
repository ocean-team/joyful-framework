/**
 * @author SargerasWang
 */
package org.joyful4j.modules.excel;

import org.joyful4j.modules.excel.logs.ExcelLog;
import org.joyful4j.modules.excel.logs.ExcelLogs;
import org.joyful4j.modules.excel.logs.ExcelRowLog;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试导入
 */
public class TestImportExcel {

    /**
     * 导入输出javaBean类型数据
     * @throws FileNotFoundException
     */
    @Test
    public void importBeanXls() throws FileNotFoundException {
        File f = new File("testImportExcel.xls");
        InputStream inputStream = new FileInputStream(f);
        ExcelLogs logs = new ExcelLogs();

        //国际化策略为EXCEL_I18N_STRATEGY_NONE时表头必填
        Map<String, List<String>> importHeaderMap = new HashMap<>();

        Collection<Payroll4ExcelVo> excelDatas = ExcelUtil.importExcel(Payroll4ExcelVo.class, null, inputStream, "yyyy-MM-dd", logs);

        if (logs.hasExcelLogs()) {
            System.out.println("Excel 基本日志：");
            for (ExcelLog excelLog : logs.getExcelLogs()) {
                System.out.println(excelLog.getLog());
            }
        }

        if (logs.hasRowLogList()) {
            List<ExcelRowLog> errorLogList = logs.getRowLogList();
            System.out.println("Excel 数据行日志");
            for (ExcelRowLog errorLog : errorLogList) {
                System.out.println(errorLog.getRowNum()+" "+errorLog.getLog());
            }
        }

        for (Payroll4ExcelVo m : excelDatas) {
            System.out.println(m);
        }
    }

    /**
     * 输出数据为Map<表头，数据>
     * @throws FileNotFoundException
     */
    @Test
    public void importMapXls() throws FileNotFoundException {
        File f = new File("testImportExcel.xls");
        InputStream inputStream = new FileInputStream(f);
        ExcelLogs logs = new ExcelLogs();

        Collection<Map> excelDatas = ExcelUtil.importExcel(Map.class, null, inputStream, "yyyy-MM-dd", logs);

        if (logs.hasExcelLogs()) {
            System.out.println("Excel 基本日志：");
            for (ExcelLog excelLog : logs.getExcelLogs()) {
                System.out.println(excelLog.getLog());
            }
        }
        if (logs.hasRowLogList()) {
            List<ExcelRowLog> errorLogList = logs.getRowLogList();
            System.out.println("Excel 数据行日志");
            for (ExcelRowLog errorLog : errorLogList) {
                System.out.println(errorLog.getRowNum()+" "+errorLog.getLog());
            }
        }
        for (Map rowMap : excelDatas) {
            StringBuilder sb = new StringBuilder();
            rowMap.forEach((key,value) -> {
                sb.append(key).append("：").append(value).append(";");
            });
            System.out.println(sb.toString());
        }
    }


    @Test
    public void importXlsx() throws FileNotFoundException {
        File f = new File("src/test/resources/test.xlsx");
        InputStream inputStream = new FileInputStream(f);

        ExcelLogs logs = new ExcelLogs();
        Collection<Map> importExcel = ExcelUtil.importExcel(Map.class, null, inputStream, "yyyy/MM/dd HH:mm:ss", logs);

        for (Map m : importExcel) {
            System.out.println(m);
        }
    }

}
