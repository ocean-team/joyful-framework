package org.joyful4j.modules.excel;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.joyful4j.modules.excel.annotation.ExcelCell;
import org.joyful4j.modules.excel.annotation.ExcelConfig;
import org.joyful4j.modules.excel.constants.ExcelDefaultConfig;
import org.joyful4j.modules.excel.entity.CellTypeMode;
import org.joyful4j.modules.excel.entity.ExcelI18nStrategyType;
import org.joyful4j.modules.excel.entity.ExcelSheet;
import org.joyful4j.modules.excel.entity.FieldForSortting;
import org.joyful4j.modules.excel.exception.ExcelCanHandleException;
import org.joyful4j.modules.excel.exception.ExcelException;
import org.joyful4j.modules.excel.exception.ExcellExceptionType;
import org.joyful4j.modules.excel.logs.ExcelLog;
import org.joyful4j.modules.excel.logs.ExcelLogs;
import org.joyful4j.modules.excel.logs.ExcelRowLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The <code>ExcelUtil</code> 与 {@link ExcelCell}搭配使用
 *
 * @author richey.liu
 * @version 1.0, Created at 2017-12-17
 */
public class ExcelUtil {

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    private static final Pattern NUMBER_PATTERN = Pattern.compile("[1-9]+[0-9]*(\\.[0-9]+)*");


    public static void main(String[] args) {

        String num1 = "20.1";

        Matcher integerMatcher = NUMBER_PATTERN.matcher(num1);
        System.out.println(integerMatcher.matches());

        Double.parseDouble(num1);


    }


    /**
     * 获取cell类型的文字描述
     *
     * @param cellType <pre>
     *                                                                                                                                                                                                 CellType.BLANK
     *                                 @return
     */
    private static String getCellTypeByInt(CellType cellType, CellTypeMode cellTypeMode) {
        switch (cellType) {
            case BLANK:
                return "Null type";
            case BOOLEAN:
                return "Boolean type";
            case ERROR:
                return "Error type";
            case FORMULA:
                return "Formula type";
            case NUMERIC:
                switch (cellTypeMode) {
                    /*case STRICT:
                        return "Numeric type ";*/
                    case LOOSE:
                    default:
                        return "Numeric type or String type or Date type";
                }
            case STRING:
                switch (cellTypeMode) {
                    /*case STRICT:
                        return "String type or Null type";*/
                    case LOOSE:
                    default:
                        return "String type or Numeric type or String type or Date type";
                }

            default:
                return "Unknown type";
        }
    }

    /**
     * 获取单元格值
     *
     * @param cell
     * @param excelConfig
     * @return
     */
    private static Object getCellValue(Cell cell, ExcelConfig excelConfig) {

        if (isNullOrBlankStringCell(cell)) {
            return null;
        }

        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case BLANK:
                return null;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return cell.getErrorCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {// 判断是日期类型
                    return cell.getDateCellValue();
                } else {
                    // 解决问题：
                    // 1、科学计数法(如2.6E+10)，
                    // 2、超长小数小数位不一致（如1091.19649281798读取出1091.1964928179796），
                    // 3、整型变小数（如0读取出0.0）

                    return NumberToTextConverter.toText(cell.getNumericCellValue());

                    /*double doubleVal = cell.getNumericCellValue();
                    long longVal = Math.round(cell.getNumericCellValue());
                    if (Double.parseDouble(longVal + ".0") == doubleVal) {
                        return longVal;
                    }
                    return doubleVal;*/
                }

            case STRING:
                String cellValue = cell.getStringCellValue();
                if (StringUtils.isNotBlank(cellValue) && excelConfig != null && StringUtils.isNotEmpty(excelConfig.globalRemovePattern())) {
                    cellValue.trim();
                    cellValue.replaceAll(excelConfig.globalRemovePattern(), "");
                }
                return cellValue;
            default:
                return null;
        }
    }


    /**
     * Cell是null 或是CellType.STRING类型的，但是值是blank的
     *
     * @param cell
     * @return
     */
    private static boolean isNullOrBlankStringCell(Cell cell) {
        return cell == null
                || cell.getCellTypeEnum() == CellType.BLANK
                || (cell.getCellTypeEnum() == CellType.STRING && StringUtils.isBlank(cell.getStringCellValue()));
    }

    /**
     * 单个sheet导出
     * 导出数据可以是：
     * 1.javabean类型的对象集合
     * 2.Map类型的对象集合
     * 3.表头顺序由Map的key顺序决定
     * 4.如果导出javabean类型对象，数据顺序由@ExcellCell注解的index属性决定
     *
     * @param <T>
     * @param headers 表头列名 map<key,columnName> 如果导出数据是map类型则该key要与map对象的key对应
     * @param dataset 需要显示的数据集合,集合中可以市javabean风格的类的对象或者Map类型的对象。
     *                支持的javabean属性的数据类型有基本数据类型及String,Date,String[],Double[]
     * @param out
     */
    public static <T> void exportExcel(Map<String, String> headers, Collection<T> dataset, OutputStream out) {
        exportExcel(headers, dataset, out, null);
    }

    /**
     * 利用JAVA的反射机制，将放置在JAVA集合中并且符合一定条件的数据以EXCEL 的形式输出到指定IO设备上<br>
     * 用于单个sheet
     *
     * @param <T>
     * @param headers     表格属性列名
     * @param dataset     需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                    javabean属性的数据类型有基本数据类型及String,Date,String[],Double[]
     * @param out         与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param datePattern 如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */
    public static <T> void exportExcel(Map<String, String> headers, Collection<T> dataset, OutputStream out,
                                       String datePattern) {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            // 生成一个表格
            HSSFSheet sheet = workbook.createSheet();
            write2Sheet(sheet, headers, dataset, datePattern);
            workbook.write(out);
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * 将一个二维数组导出，一维是行，二维是行的cell值
     *
     * @param datalist
     * @param out
     */
    public static void exportExcel(String[][] datalist, OutputStream out) {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            // 生成一个表格
            HSSFSheet sheet = workbook.createSheet();

            for (int i = 0; i < datalist.length; i++) {
                String[] r = datalist[i];
                HSSFRow row = sheet.createRow(i);
                for (int j = 0; j < r.length; j++) {
                    HSSFCell cell = row.createCell(j);
                    //cell max length 32767
                    if (r[j].length() > 32767) {
                        r[j] = "--此字段过长(超过32767),已被截断--" + r[j];
                        r[j] = r[j].substring(0, 32766);
                    }
                    cell.setCellValue(r[j]);
                }
            }
            //自动列宽
            if (datalist.length > 0) {
                int colcount = datalist[0].length;
                for (int i = 0; i < colcount; i++) {
                    sheet.autoSizeColumn(i);
                }
            }
            workbook.write(out);
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * 利用JAVA的反射机制，将放置在JAVA集合中并且符合一定条件的数据以EXCEL 的形式输出到指定IO设备上<br>
     * 用于多个sheet
     *
     * @param <T>
     * @param sheets {@link ExcelSheet}的集合
     * @param out    与输出设备关联的流对象
     */
    public static <T> void exportExcel(List<ExcelSheet<T>> sheets, OutputStream out) {
        exportExcel(sheets, out, null);
    }

    /**
     * 利用JAVA的反射机制，将放置在JAVA集合中并且符合一定条件的数据以EXCEL 的形式输出到指定IO设备上<br>
     * 用于多个sheet
     *
     * @param <T>
     * @param sheets      {@link ExcelSheet}的集合
     * @param out         与输出设备关联的流对象
     * @param datePattern 如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
     */
    public static <T> void exportExcel(List<ExcelSheet<T>> sheets, OutputStream out, String datePattern) {
        if (CollectionUtils.isEmpty(sheets)) {
            return;
        }
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {

            for (ExcelSheet<T> sheet : sheets) {
                // 生成一个表格
                HSSFSheet hssfSheet = workbook.createSheet(sheet.getSheetName());
                write2Sheet(hssfSheet, sheet.getHeaders(), sheet.getDataset(), datePattern);
            }
            workbook.write(out);
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * 每个sheet的写入
     *
     * @param sheet       页签
     * @param headers     表头
     * @param dataset     数据集合
     * @param datePattern 日期格式
     */
    private static <T> void write2Sheet(HSSFSheet sheet, Map<String, String> headers, Collection<T> dataset,
                                        String datePattern) {
        //时间格式默认"yyyy-MM-dd"
        if (StringUtils.isEmpty(datePattern)) {
            datePattern = ExcelDefaultConfig.DEFAULT_OUTPUT_DATE_PATTERN;
        }
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        // 标题行转中文
        Set<String> headerKeys = headers.keySet();
        Iterator<String> it1 = headerKeys.iterator();
        //存放临时键变量
        String key = "";
        //标题列数
        int c = 0;
        while (it1.hasNext()) {
            key = it1.next();
            HSSFCell cell = row.createCell(c);
            HSSFRichTextString text = new HSSFRichTextString(headers.get(key));
            cell.setCellValue(text);
            c++;
        }

        // 遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = it.next();
            try {
                if (t instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) t;
                    int cellNum = 0;
                    //遍历列名
                    Iterator<String> it2 = headerKeys.iterator();
                    while (it2.hasNext()) {
                        key = it2.next();
                        Object value = map.get(key);
                        HSSFCell cell = row.createCell(cellNum);
                        cellNum = setCellValue(cell, value, datePattern, cellNum, null, row);
                        cellNum++;
                    }
                } else {
                    List<FieldForSortting> fields = sortFieldByAnno(t.getClass());
                    int cellNum = 0;
                    for (int i = 0; i < fields.size(); i++) {
                        HSSFCell cell = row.createCell(cellNum);
                        Field field = fields.get(i).getField();
                        field.setAccessible(true);
                        Object value = field.get(t);
                        cellNum = setCellValue(cell, value, datePattern, cellNum, field, row);
                        cellNum++;
                    }
                }
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }
        }
        // 设定自动宽度
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static int setCellValue(HSSFCell cell, Object value, String pattern, int cellNum, Field field, HSSFRow row) {
        String textValue = null;
        if (value instanceof Integer) {
            int intValue = (Integer) value;
            cell.setCellValue(intValue);
        } else if (value instanceof Float) {
            float fValue = (Float) value;
            cell.setCellValue(fValue);
        } else if (value instanceof Double) {
            double dValue = (Double) value;
            cell.setCellValue(dValue);
        } else if (value instanceof Long) {
            long longValue = (Long) value;
            cell.setCellValue(longValue);
        } else if (value instanceof Boolean) {
            boolean bValue = (Boolean) value;
            cell.setCellValue(bValue);
        } else if (value instanceof Date) {
            Date date = (Date) value;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            textValue = sdf.format(date);
        } else if (value instanceof String[]) {
            String[] strArr = (String[]) value;
            for (int j = 0; j < strArr.length; j++) {
                String str = strArr[j];
                cell.setCellValue(str);
                if (j != strArr.length - 1) {
                    cellNum++;
                    cell = row.createCell(cellNum);
                }
            }
        } else if (value instanceof Double[]) {
            Double[] douArr = (Double[]) value;
            for (int j = 0; j < douArr.length; j++) {
                Double val = douArr[j];
                // 值不为空则set Value
                if (val != null) {
                    cell.setCellValue(val);
                }

                if (j != douArr.length - 1) {
                    cellNum++;
                    cell = row.createCell(cellNum);
                }
            }
        } else {
            // 其它数据类型都当作字符串简单处理
            String defaultStr = StringUtils.EMPTY;
            if (field != null) {
                ExcelCell anno = field.getAnnotation(ExcelCell.class);
                if (anno != null) {
                    defaultStr = anno.defaultValue();
                }
            }
            textValue = value == null ? defaultStr : value.toString();
        }
        if (textValue != null) {
            HSSFRichTextString richString = new HSSFRichTextString(textValue);
            cell.setCellValue(richString);
        }
        return cellNum;
    }

    /**
     * 把Excel的数据封装成voList
     *
     * @param clazz        输出数据类型,支持 javabean.class 或 Map.class
     * @param importHeader Map<String,List<String>>
     *                     输出类型为javaBean时，且多语言策略为ExcelI18nStrategyType.EXCEL_I18N_STRATEGY_NONE时，
     *                     必须提供importHeader，key用于同javabean的字段对应
     * @param inputStream  excel输入流
     * @param pattern      如果有时间数据，设定输入格式。默认为"yyy-MM-dd"
     * @param logs         错误log集合
     * @return voList
     * @throws RuntimeException
     */
    public static <T> Collection<T> importExcel(Class<T> clazz, Map<String, List<String>> importHeader, InputStream inputStream,
                                                String pattern, ExcelLogs logs) {

        Sheet sheet = getSheetFromWorkbook(inputStream, logs);
        if (sheet == null) {
            return new ArrayList<>();
        }

        ExcelConfig excelConfig = clazz.getAnnotation(ExcelConfig.class);
        List<T> resultList = new ArrayList<>();
        List<ExcelRowLog> rowLogList = new ArrayList<>();


        Iterator<Row> rowIterator = sheet.rowIterator();
        // 从excel读取的表头 Map<title,index>
        Map<String, Integer> fileHeaderIndexMap = new HashMap<>();
        while (rowIterator.hasNext()) {
            StringBuilder log = new StringBuilder();
            Row row = rowIterator.next();
            if (row.getRowNum() == 0) {
                fileHeaderIndexMap = convertRowToHeaderMap(row);
                continue;
            }
            // 跳过空行,并记录日志
            boolean isBlankRow = checkBlankRow(excelConfig, row);
            if (isBlankRow) {
                rowLogList.add(new ExcelRowLog("The row is blank", row.getRowNum() + 1));
                continue;
            }

            //输出数据类型是Map时，简单将数据封装为Map<headerName,value>
            if (clazz == Map.class) {
                Map<String, Object> map = handleExcelRowToMap(fileHeaderIndexMap, row);
                resultList.add((T) map);
            } else {

                T excelEntityVo = handleExcelRowToJavabean(clazz, importHeader, pattern, excelConfig, fileHeaderIndexMap, log, row);
                if (StringUtils.isBlank(log.toString())) {
                    resultList.add(excelEntityVo);
                } else {
                    rowLogList.add(new ExcelRowLog(excelEntityVo, log.toString(), row.getRowNum() + 1));
                }

            }
        }
        logs.setRowLogList(rowLogList);
        return resultList;
    }

    private static <T> T handleExcelRowToJavabean(Class<T> clazz, Map<String, List<String>> importHeader, String pattern, ExcelConfig excelConfig, Map<String, Integer> fileHeaderIndexMap, StringBuilder log, Row row) {

        T excelEntityVo = null;
        try {
            excelEntityVo = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ExcelException(MessageFormat.format("can not instance class:{0}", clazz.getSimpleName()), e);
        }

        List<Field> excelCellFields = getExcelCellField(clazz);
        //从传入的表头或者根据i18n策略获得的表头在Excel中对应的列的集合
        List<Integer> configHeaderIndex = new ArrayList<>();

        for (Field field : excelCellFields) {
            String importHeaderKey = field.getAnnotation(ExcelCell.class).importHeaderKey();
            if (StringUtils.isBlank(importHeaderKey)) {
                //未指定表头key，无法确定对应表头
                continue;
            }
            //通过国际化策略得到或者传入的表头
            List<String> configHeaders = getI18nHeadersByStrategy(clazz.getAnnotation(ExcelConfig.class), importHeaderKey);
            if (CollectionUtils.isEmpty(configHeaders) && MapUtils.isNotEmpty(importHeader)) {
                configHeaders = importHeader.get(importHeaderKey);
                if (CollectionUtils.isEmpty(configHeaders)) {
                    //输入表头和国际化策略取的表头均无此表头时忽略不导入
                    continue;
                }
            }

            String currentHeaderName = "";
            Integer cellIndex = null;
            for (String header : configHeaders) {
                cellIndex = fileHeaderIndexMap.get(header);
                if (cellIndex != null) {
                    currentHeaderName = header;
                    break;
                }
            }

            if (requiredColumnNotExist(log, field, cellIndex)) {
                continue;
            }

            configHeaderIndex.add(cellIndex);
            Cell cell = row.getCell(cellIndex);
            String errMsg = validateCell(cell, field, cellIndex, currentHeaderName);
            if (StringUtils.isNotBlank(errMsg)) {
                appendToExcelRowLog(log, errMsg);
                continue;
            }

            Object excelValueObj = null;
            try {
                excelValueObj = getExcelValueObj(field, pattern, cell, excelConfig);
            } catch (ParseException e) {
                errMsg = MessageFormat.format("the column [{0}] can not be converted to a date ", currentHeaderName);
                appendToExcelRowLog(log, errMsg);
                continue;
            }

            if (StringUtils.isNotBlank(errMsg)) {
                appendToExcelRowLog(log, errMsg);
                continue;
            }
            if (excelValueObj == null) {
                continue;
            }

            try {
                excelValueObj = convertToFieldValue(field.getType(), excelValueObj);
            } catch (NumberFormatException e) {
                errMsg = MessageFormat.format("The column {0} has the wrong data type ", currentHeaderName);
                appendToExcelRowLog(log, errMsg);
                continue;
            }
            setEntityFieldValue(clazz,excelEntityVo,field,excelValueObj);
        }

        //处理弹性字段
        handleFlexfieldData(clazz, fileHeaderIndexMap, row, excelEntityVo, excelCellFields, configHeaderIndex);

        return excelEntityVo;
    }

    private static <T> void handleFlexfieldData(Class<T> clazz, Map<String, Integer> fileHeaderIndexMap, Row row, T excelEntityVo, List<Field> excelCellFields, List<Integer> configHeaderIndex) {
        Field flexField = getFlexbleField(excelCellFields);
        if (flexField != null) {
            Map<Integer, String> flexFieldHeaderMap = new LinkedHashMap<>();

            fileHeaderIndexMap.forEach((header, index) -> {
                if (!configHeaderIndex.contains(index)) {
                    String flexHeader = flexFieldHeaderMap.get(index);
                    //存在重复表头
                    if (flexHeader != null) {
                        throw new ExcelCanHandleException(ExcellExceptionType.REPEATED_HEADER);
                    }
                    flexFieldHeaderMap.put(index, header);
                }
            });
            //有序Map用来存放弹性字段
            Map<String, Object> flexFieldDataMap = new LinkedHashMap<>();

            if (MapUtils.isNotEmpty(flexFieldHeaderMap)) {
                flexFieldHeaderMap.forEach((index, header) -> {
                    Cell cell = row.getCell(index);
                    // 判空
                    addCellDataToMap(flexFieldDataMap, header, cell);
                });
                setEntityFieldValue(clazz, excelEntityVo, flexField, flexFieldDataMap);
            }

        }
    }

    private static <T> void setEntityFieldValue(Class<T> clazz, T excelEntityVo, Field flexField, Object fieldValue) {
        flexField.setAccessible(true);
        try {
            flexField.set(excelEntityVo, fieldValue);
        } catch (IllegalAccessException e) {
            throw new ExcelException(MessageFormat.format("Can not set a value {0} for Object: {1} - field: {2}", fieldValue, clazz.getSimpleName(), flexField.getName()), e);
        }
    }

    private static void addCellDataToMap(Map<String, Object> flexFieldDataMap, String header, Cell cell) {
        if (cell == null) {
            flexFieldDataMap.put(header, null);
        } else {
            //fixme 读取各种类型数据
            cell.setCellType(CellType.STRING);
            String value = cell.getStringCellValue();
            flexFieldDataMap.put(header, value);
        }
    }

    private static Field getFlexbleField(List<Field> excelCellFields) {
        Field flexField = null;
        List<Field> flexbleFields = excelCellFields.stream().filter(f -> f.getAnnotation(ExcelCell.class).flexibleField()).collect(Collectors.toList());
        if (flexbleFields.size() > 1) {
            throw new ExcelException("Each object can only have one flexible field，the current object has " + flexbleFields.size());
        } else if (flexbleFields.size() == 1) {
            flexField = flexbleFields.get(0);
        }
        return flexField;
    }


    private static Object getExcelValueObj(Field field, String pattern, Cell cell, ExcelConfig excelConfig) throws ParseException {
        Object excelValueObj = null;

        // String类型的日期转换
        if (field.getType().equals(Date.class) && cell.getCellTypeEnum() == CellType.STRING) {
            Object strDate = getCellValue(cell, excelConfig);


            //fixme 时间格式处理
            excelValueObj = new SimpleDateFormat(pattern).parse(strDate.toString());




        } else {
            excelValueObj = getCellValue(cell, excelConfig);
            // 处理特殊情况,excel的value为String,且bean中为其他,且defaultValue不为空,那就=defaultValue
            ExcelCell annoCell = field.getAnnotation(ExcelCell.class);
            if (excelValueObj instanceof String && !field.getType().equals(String.class) && StringUtils.isNotBlank(annoCell.defaultValue())) {
                excelValueObj = annoCell.defaultValue();
            }
        }
        return excelValueObj;
    }

    private static Map<String, Object> handleExcelRowToMap(Map<String, Integer> fileHeaderIndexMap, Row row) {
        Map<String, Object> map = new HashMap<>();
        for (String fileHeaderName : fileHeaderIndexMap.keySet()) {
            Integer headerIndex = fileHeaderIndexMap.get(fileHeaderName);
            Cell cell = row.getCell(headerIndex);
            // 判空
            addCellDataToMap(map, fileHeaderName, cell);
        }
        return map;
    }

    private static boolean checkBlankRow(ExcelConfig excelConfig, Row row) {
        boolean allRowIsNull = true;
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Object cellValue = getCellValue(cellIterator.next(), excelConfig);
            if (cellValue != null && StringUtils.isNotBlank(String.valueOf(cellValue))) {
                allRowIsNull = false;
                break;
            }
        }
        return allRowIsNull;
    }

    private static Sheet getSheetFromWorkbook(InputStream inputStream, ExcelLogs logs) {
        Sheet sheet = null;
        try (Workbook workBook = WorkbookFactory.create(inputStream)) {
            //支持.xls和.xlsx
            sheet = workBook.getSheetAt(0);
        } catch (EncryptedDocumentException e) {
            logger.info(e.getMessage());
            addToExcelLogs(logs, "Excel不能存在密码;");
        } catch (InvalidFormatException e) {
            logger.info(e.getMessage());
            addToExcelLogs(logs, "Excel格式错误或存在密码;");
        } catch (Exception e) {
            logger.error("load excel file error", e);
            addToExcelLogs(logs, "Excel格式错误;");
        }
        return sheet;
    }

    private static boolean addToExcelLogs(ExcelLogs logs, String log) {
        return logs.getExcelLogs().add(new ExcelLog(log));
    }

    private static List<String> getI18nHeadersByStrategy(ExcelConfig excelConfig, String importHeaderKey) {
        List<String> i18nHeaders = new ArrayList<>();
        if (excelConfig != null) {
            ExcelI18nStrategyType excelI18nStrategyType = excelConfig.i18nStrategy();
            switch (excelI18nStrategyType) {
                case EXCEL_I18N_STRATEGY_PROPS:
                    String propsFileName = excelConfig.propsFileName();
                    if (StringUtils.isNotBlank(propsFileName)) {
                        try {
                            i18nHeaders = CatcheExcelI18nProps.getI18SortedHeaders(importHeaderKey, propsFileName);
                        } catch (ExecutionException e) {
                            logger.error("根据词条key从配置文件中获取表头时失败，importHeaderKey：" + importHeaderKey + ",propsFileName:" + propsFileName + e.getStackTrace());
                            throw new ExcelException("Error reading Excel configuration file");
                        }
                    }
                    break;
                default:
            }
        }
        return i18nHeaders;
    }

    /**
     * row转Map<列名，列index>
     *
     * @param row
     * @return
     */
    private static Map<String, Integer> convertRowToHeaderMap(Row row) {
        Map<String, Integer> fileMap = new HashMap<>();
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String value = cell.getStringCellValue();
            fileMap.put(value, cell.getColumnIndex());
        }
        return fileMap;
    }


    //非空列不存在
    private static boolean requiredColumnNotExist(StringBuilder log, Field field, Integer cellIndex) {
        boolean nextField = false;
        boolean columnNotExist = (cellIndex == null || cellIndex < 0);
        if (columnNotExist) {
            nextField = true;
            ExcelCell.Valid excelValid = field.getAnnotation(ExcelCell.Valid.class);
            if (excelValid != null && !excelValid.allowNull()) {
                ExcelCell excelCell = field.getAnnotation(ExcelCell.class);
                String defaultHeaderName = "";
                if (excelCell != null) {
                    defaultHeaderName = excelCell.defaultHeaderName();
                }
                String errMsg = MessageFormat.format("No required [{0}] column found", defaultHeaderName);
                appendToExcelRowLog(log, errMsg);
            }
        }
        return nextField;
    }

    private static void appendToExcelRowLog(StringBuilder log, String errMsg) {
        log.append(errMsg);
        log.append(";");
    }


    /**
     * 根据类型处理value值
     * 读取Excel时数值类型被处理为long或double类型，所以映射成实体属性的时候要进行相应的处理
     *
     * @param type
     * @param value
     */
    private static Object convertToFieldValue(Class<?> type, Object value) {
        if (Integer.class == type) {
            return Integer.parseInt(value.toString());
        } else if (Double.class == type) {
            return Double.parseDouble(value.toString());
        } else if(Long.class == type){
            return Long.parseLong(value.toString());
        }else if (String.class == type) {
            return value.toString();
        }
        return value;
    }

    /**
     * 校验Cell类型是否正确
     *
     * @param cell             cell单元格
     * @param field            字段
     * @param cellNum          第几列,用於errMsg
     * @param localeHeaderName 当前列的表头
     * @return
     */
    private static String validateCell(Cell cell, Field field, int cellNum, String localeHeaderName) {
        String result = null;
        ExcelCell annoCell = field.getAnnotation(ExcelCell.class);

        CellTypeMode cellTypeMode = annoCell.cellTypeMode();
        CellType[] cellTypeArr = null;

        switch (cellTypeMode) {
           /* case STRICT:
                cellTypeArr = ExcelDefaultConfig.strickValidateMap.get(field.getType());
                break;*/
            case LOOSE:
            default:
                cellTypeArr = ExcelDefaultConfig.looseValidateMap.get(field.getType());
                break;

        }

        if (cellTypeArr == null) {
            result = MessageFormat.format("Unsupported type [{0}]", field.getType().getSimpleName());
            return result;
        }
        if (annoCell == null) {
            return result;
        }
        ExcelCell.Valid excelValid = field.getAnnotation(ExcelCell.Valid.class);
        if (excelValid == null) {
            return result;
        }
        if (!cellNullValid(cell, excelValid.allowNull())) {
            result = MessageFormat.format("the column [{0}] can not be null", localeHeaderName);
        } else if (allowBlankCell(cell, excelValid)) {
            return result;
        } else {
            List<CellType> cellTypes = Arrays.asList(cellTypeArr);

            // 如果了类型不在指定范围内
            if (!cellTypes.contains(cell.getCellTypeEnum())) {
                StringBuilder strType = new StringBuilder();
                for (int i = 0; i < cellTypes.size(); i++) {
                    CellType cellType = cellTypes.get(i);
                    strType.append(getCellTypeByInt(cellType, annoCell.cellTypeMode()));
                    if (i != cellTypes.size() - 1) {
                        strType.append(",");
                    }
                }
                result = MessageFormat.format("the column [{0}] type must [{1}]", localeHeaderName, strType.toString());
            } else {
                // 类型符合验证,但值不在要求范围内的
                // String in
                if (excelValid.in().length != 0 && cell.getCellTypeEnum() == CellType.STRING) {
                    String[] in = excelValid.in();
                    String cellValue = cell.getStringCellValue();
                    boolean isIn = false;
                    for (String str : in) {
                        if (str.equals(cellValue)) {
                            isIn = true;
                        }
                    }
                    if (!isIn) {
                        result = MessageFormat.format("the column [{0}] must in {1}", localeHeaderName, in);
                    }
                }
                // 数值型 或 可以转为数值的String
                if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.STRING) {
                    double cellValue = 0D;
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        String cellValueStr = cell.getStringCellValue();
                        if (matchNumber(cellValueStr)) {
                            cellValue = Double.parseDouble(cellValueStr);
                        } else {
                            return result;
                        }

                    } else {
                        cellValue = cell.getNumericCellValue();
                    }


                    // 小于
                    if (!Double.isNaN(excelValid.lt())) {
                        if (!(cellValue < excelValid.lt())) {
                            result =
                                    MessageFormat.format("the column [{0}] must less than [{1}]", localeHeaderName,
                                            excelValid.lt());
                        }
                    }
                    // 大于
                    if (!Double.isNaN(excelValid.gt())) {
                        if (!(cellValue > excelValid.gt())) {
                            result =
                                    MessageFormat.format("the column [{0}] must greater than [{1}]", localeHeaderName,
                                            excelValid.gt());
                        }
                    }
                    // 小于等于
                    if (!Double.isNaN(excelValid.le())) {
                        if (!(cellValue <= excelValid.le())) {
                            result =
                                    MessageFormat.format("the column [{0}] must less than or equal [{1}]",
                                            localeHeaderName, excelValid.le());
                        }
                    }
                    // 大于等于
                    if (!Double.isNaN(excelValid.ge())) {
                        if (!(cellValue >= excelValid.ge())) {
                            result =
                                    MessageFormat.format("the column [{0}] must greater than or equal [{1}]",
                                            localeHeaderName, excelValid.ge());
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean matchNumber(String cellValueStr) {
        Matcher nummMatcher = NUMBER_PATTERN.matcher(cellValueStr);
        return nummMatcher.matches();
    }

    private static boolean allowBlankCell(Cell cell, ExcelCell.Valid excelValid) {
        return (cell == null || cell.getCellTypeEnum() == CellType.BLANK) && excelValid.allowNull();
    }


    /**
     * 根据annotation的seq排序后的栏位
     *
     * @param clazz
     * @return
     */
    private static List<FieldForSortting> sortFieldByAnno(Class<?> clazz) {
        Field[] fieldsArr = clazz.getDeclaredFields();
        List<FieldForSortting> fields = new ArrayList<>();
        for (Field field : fieldsArr) {
            ExcelCell ec = field.getAnnotation(ExcelCell.class);
            if (ec == null) {
                // 没有ExcelCell Annotation 视为不导入
                continue;
            }
            int id = ec.exportIndex();
            fields.add(new FieldForSortting(field, id));
        }
        sortByProperties(fields, true, false, ExcelDefaultConfig.SORT_ANNO_PROPS);
        return fields;
    }

    private static List<Field> getExcelCellField(Class<?> clazz) {
        Field[] fieldsArr = clazz.getDeclaredFields();

        Class superClazz =  clazz.getSuperclass();
        if (superClazz != null) {
            Field[] superFieldsArr = superClazz.getDeclaredFields();
            fieldsArr = ArrayUtils.addAll(fieldsArr, superFieldsArr);
        }
        if (ArrayUtils.isEmpty(fieldsArr)) {
            return new ArrayList<>();
        }
        return Arrays.stream(fieldsArr).filter(f -> f.getAnnotation(ExcelCell.class) != null).collect(Collectors.toList());
    }


    private static void sortByProperties(List<? extends Object> list, boolean isNullHigh,
                                         boolean isReversed, String... props) {
        if (CollectionUtils.isNotEmpty(list)) {
            Comparator<?> typeComp = ComparableComparator.getInstance();
            if (isNullHigh == true) {
                typeComp = ComparatorUtils.nullHighComparator(typeComp);
            } else {
                typeComp = ComparatorUtils.nullLowComparator(typeComp);
            }
            if (isReversed) {
                typeComp = ComparatorUtils.reversedComparator(typeComp);
            }

            List<Object> sortCols = new ArrayList<Object>();

            if (props != null) {
                for (String prop : props) {
                    sortCols.add(new BeanComparator(prop, typeComp));
                }
            }
            if (sortCols.size() > 0) {
                Comparator<Object> sortChain = new ComparatorChain(sortCols);
                Collections.sort(list, sortChain);
            }
        }
    }

    //校验cell为空或""的合法性
    private static boolean cellNullValid(Cell cell, boolean allowNull) {
        return !isNullOrBlankStringCell(cell) || (isNullOrBlankStringCell(cell) && allowNull);
    }
}
