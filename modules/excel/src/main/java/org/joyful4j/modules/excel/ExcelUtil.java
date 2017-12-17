package org.joyful4j.modules.excel;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The <code>ExcelUtil</code> 与 {@link ExcelCell}搭配使用
 *
 * @author richey.liu
 * @version 1.0, Created at 2017-12-17
 */
public class ExcelUtil {

    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    private static final String DEFAULT_OUTPUT_DATE_PATTERN = "yyyy-MM-dd"; //日期类型默认输出格式

    /**
     * 用来验证excel与Vo中的类型是否一致 <br>
     * Map<栏位类型,只能是哪些Cell类型>
     */
    private static Map<Class<?>, CellType[]> validateMap = new HashMap<>();

    static {
        validateMap.put(String[].class, new CellType[]{CellType.STRING});
        validateMap.put(Double[].class, new CellType[]{CellType.NUMERIC});
        validateMap.put(String.class, new CellType[]{CellType.STRING});
        validateMap.put(Double.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Date.class, new CellType[]{CellType.NUMERIC, CellType.STRING});
        validateMap.put(Integer.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Float.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Long.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Boolean.class, new CellType[]{CellType.BOOLEAN});
    }

    /**
     * 获取cell类型的文字描述
     *
     * @param cellType <pre>
     *                 CellType.BLANK
     *                 CellType.BOOLEAN
     *                 CellType.ERROR
     *                 CellType.FORMULA
     *                 CellType.NUMERIC
     *                 CellType.STRING
     *                 </pre>
     * @return
     */
    private static String getCellTypeByInt(CellType cellType) {
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
                return "Numeric type";
            case STRING:
                return "String type";
            default:
                return "Unknown type";
        }
    }

    /**
     * 获取单元格值
     *
     * @param cell
     * @return
     */
    private static Object getCellValue(Cell cell) {

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
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    Object inputValue = null;
                    double doubleVal = cell.getNumericCellValue();
                    long longVal = Math.round(cell.getNumericCellValue());
                    if (Double.parseDouble(longVal + ".0") == doubleVal) {
                        return longVal;
                    }
                    return doubleVal;
                }

            case STRING:
                return cell.getStringCellValue();
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
        return cell.getCellTypeEnum() == CellType.STRING
                && StringUtils.isBlank(cell.getStringCellValue());
    }

    /**
     * 利用JAVA的反射机制，将放置在JAVA集合中并且符合一定条件的数据以EXCEL 的形式输出到指定IO设备上<br>
     * 用于单个sheet
     *
     * @param <T>
     * @param headers 表格属性列名
     * @param dataset 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,String[],Double[]
     * @param out     与输出设备关联的流对象
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
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet();

        write2Sheet(sheet, headers, dataset, datePattern);
        try {
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
        try {
            // 声明一个工作薄
            HSSFWorkbook workbook = new HSSFWorkbook();
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
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (ExcelSheet<T> sheet : sheets) {
            // 生成一个表格
            HSSFSheet hssfSheet = workbook.createSheet(sheet.getSheetName());
            write2Sheet(hssfSheet, sheet.getHeaders(), sheet.getDataset(), datePattern);
        }
        try {
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
            datePattern = DEFAULT_OUTPUT_DATE_PATTERN;
        }
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        // 标题行转中文
        Set<String> headerKeys = headers.keySet();
        Iterator<String> it1 = headerKeys.iterator();
        String key = "";    //存放临时键变量
        int c = 0;   //标题列数
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
     * @param clazz       vo的Class支持Map类型
     * @param inputStream excel输入流
     * @param pattern     如果有时间数据，设定输入格式。默认为"yyy-MM-dd"
     * @param logs        错误log集合
     * @param arrayCount  如果vo中有数组类型,那就按照index顺序,把数组应该有几个值写上.
     * @return voList
     * @throws RuntimeException
     */
    public static <T> Collection<T> importExcel(Class<T> clazz, InputStream inputStream,
                                                String pattern, ExcelLogs logs, Integer... arrayCount) {
        Workbook workBook;
        try {
            //支持.xls和.xlsx
            workBook = WorkbookFactory.create(inputStream);
        } catch (Exception e) {
            logger.error("load excel file error", e);
            return null;
        }
        List<T> list = new ArrayList<>();
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        try {
            List<ExcelLog> logList = new ArrayList<>();
            // Map<title,index>
            Map<String, Integer> titleMap = new HashMap<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    // 解析map用的key,就是excel标题行
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String value = cell.getStringCellValue();
                        titleMap.put(value, cell.getColumnIndex());
                    }
                    continue;
                }
                // 整行都空，就跳过
                boolean allRowIsNull = true;
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Object cellValue = getCellValue(cellIterator.next());
                    if (cellValue != null) {
                        allRowIsNull = false;
                        break;
                    }
                }
                if (allRowIsNull) {
                    //todo 空行处理
                    logger.warn("Excel row " + row.getRowNum() + " all row value is null!");
                    continue;
                }
                StringBuilder log = new StringBuilder();
                if (clazz == Map.class) {
                    Map<String, Object> map = new HashMap<>();
                    for (String k : titleMap.keySet()) {
                        Integer index = titleMap.get(k);
                        Cell cell = row.getCell(index);
                        // 判空
                        if (cell == null) {
                            map.put(k, null);
                        } else {
                            cell.setCellType(CellType.STRING);
                            String value = cell.getStringCellValue();
                            map.put(k, value);
                        }
                    }
                    list.add((T) map);

                } else {
                    //fixme 表头导入要支持乱序
                    T t = clazz.newInstance();
                    int arrayIndex = 0;// 标识当前第几个数组了
                    //fixme 导入时不能通过index顺序
                    List<FieldForSortting> sorttingsFields = sortFieldByAnno(clazz);

                    int localeHeaderIndex = getLocaleIndex(titleMap, sorttingsFields);

                    for (FieldForSortting ffs : sorttingsFields) {
                        Field field = ffs.getField();
                        Integer cellIndex = getHeaderIndex(titleMap, field);

                        if (requiredColumnNotExist(logs, log, field, cellIndex)) {
                            continue;
                        }
                        String localeHeaderName = getLocaleHeaderName(localeHeaderIndex, field);

                        field.setAccessible(true);
                        if (field.getType().isArray()) {
                            Integer count = arrayCount[arrayIndex];
                            Object[] value;
                            if (field.getType().equals(String[].class)) {
                                value = new String[count];
                            } else {
                                // 目前只支持String[]和Double[]
                                value = new Double[count];
                            }
                            for (int i = 0; i < count; i++) {
                                Cell cell = row.getCell(cellIndex);
                                String errMsg = validateCell(cell, field, cellIndex, localeHeaderName);
                                if (StringUtils.isBlank(errMsg)) {
                                    value[i] = getCellValue(cell);
                                } else {
                                    addExcelLog(logs, log, errMsg);
                                }
                            }
                            field.set(t, value);
                            arrayIndex++;
                        } else {
                            Cell cell = row.getCell(cellIndex);
                            String errMsg = validateCell(cell, field, cellIndex, localeHeaderName);
                            if (StringUtils.isBlank(errMsg)) {
                                Object value = null;
                                // 处理特殊情况,Excel中的String,转换成Bean的Date
                                if (field.getType().equals(Date.class)
                                        && cell.getCellTypeEnum() == CellType.STRING) {
                                    Object strDate = getCellValue(cell);
                                    try {
                                        //fixme 时间格式处理
                                        value = new SimpleDateFormat(pattern).parse(strDate.toString());

                                    } catch (ParseException e) {
                                        errMsg =
                                                MessageFormat.format("the column [{0}] can not be converted to a date ", localeHeaderName);
                                    }
                                } else {
                                    value = getCellValue(cell);
                                    // 处理特殊情况,excel的value为String,且bean中为其他,且defaultValue不为空,那就=defaultValue
                                    ExcelCell annoCell = field.getAnnotation(ExcelCell.class);
                                    if (value instanceof String && !field.getType().equals(String.class)
                                            && StringUtils.isNotBlank(annoCell.defaultValue())) {
                                        value = annoCell.defaultValue();
                                    }
                                }
                                value = convertToFieldValue(field.getType(), value);
                                field.set(t, value);
                            }
                            if (StringUtils.isNotBlank(errMsg)) {
                                addExcelLog(logs, log, errMsg);
                            }
                        }
                    }
                    list.add(t);
                    logList.add(new ExcelLog(t, log.toString(), row.getRowNum() + 1));
                }
            }
            logs.setLogList(logList);
        } catch (InstantiationException e) {
            throw new RuntimeException(MessageFormat.format("can not instance class:{0}",
                    clazz.getSimpleName()), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(MessageFormat.format("can not instance class:{0}",
                    clazz.getSimpleName()), e);
        }
        return list;
    }

    //获取当前索引表头
    private static String getLocaleHeaderName(int localeHeaderIndex, Field field) {
        String[] headers = field.getAnnotation(ExcelCell.class).header();
        String localeHeaderName = headers[0];
        if (headers.length > localeHeaderIndex) {
            localeHeaderName = headers[localeHeaderIndex];
        }
        return localeHeaderName;
    }

    /**
     * 根据表头非空字段，判断多语言表头索引，若没有默认0
     *
     * @param titleMap        表头{列名，列索引}
     * @param sorttingsFields 所有字段
     * @return
     */
    private static int getLocaleIndex(Map<String, Integer> titleMap, List<FieldForSortting> sorttingsFields) {
        int localeHeaderIndex = 0;
        List<Field> notNullFields = sorttingsFields.stream()
                .map(FieldForSortting::getField)
                .filter(f -> !f.getAnnotation(ExcelCell.class).valid().allowNull())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(notNullFields)) {
            return localeHeaderIndex;
        }
        boolean continueFlag = true;
        for (Field field : notNullFields) {
            String[] headers = field.getAnnotation(ExcelCell.class).header();
            for (int i = 0; i < headers.length; i++) {
                if (titleMap.get(headers[i]) != null) {
                    localeHeaderIndex = i;
                    continueFlag = false;
                    break;
                }
            }
            if (!continueFlag) {
                break;
            }
        }
        return localeHeaderIndex;
    }

    //非空列不存在
    private static boolean requiredColumnNotExist(ExcelLogs logs, StringBuilder log, Field field, Integer cellIndex) {
        boolean allowNull = field.getAnnotation(ExcelCell.class).valid().allowNull();
        if (cellIndex != null || allowNull) {
            return false;
        }
        String errMsg = MessageFormat.format("No required [{0}] column found", field.getAnnotation(ExcelCell.class).header()[0]);
        addExcelLog(logs, log, errMsg);
        return true;

    }

    private static void addExcelLog(ExcelLogs logs, StringBuilder log, String errMsg) {
        log.append(errMsg);
        log.append(";");
        logs.setHasError(true);
    }

    /**
     * 根据Field的表头获取
     *
     * @param titleMap excel表头{表头名：列index}
     * @param field
     * @return
     */
    private static Integer getHeaderIndex(Map<String, Integer> titleMap, Field field) {
        ExcelCell ec = field.getAnnotation(ExcelCell.class);
        String[] headers = ec.header();
        Integer cellIndex = null;
        for (String header : headers) {
            cellIndex = titleMap.get(header);
            if (cellIndex != null) {
                break;
            }
        }
        return cellIndex;
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
        String columnName = CellReference.convertNumToColString(cellNum);
        String result = null;
        CellType[] cellTypeArr = validateMap.get(field.getType());
        if (cellTypeArr == null) {
            result = MessageFormat.format("Unsupported type [{0}]", field.getType().getSimpleName());
            return result;
        }
        ExcelCell annoCell = field.getAnnotation(ExcelCell.class);
        if (!cellNullValid(cell, annoCell.valid().allowNull())) {
            result = MessageFormat.format("the column [{0}] can not null", localeHeaderName);
        } else if (cell.getCellTypeEnum() == CellType.BLANK && annoCell.valid().allowNull()) {
            return result;
        } else {
            List<CellType> cellTypes = Arrays.asList(cellTypeArr);

            // 如果了类型不在指定范围内
            if (!cellTypes.contains(cell.getCellTypeEnum())) {
                StringBuilder strType = new StringBuilder();
                for (int i = 0; i < cellTypes.size(); i++) {
                    CellType cellType = cellTypes.get(i);
                    strType.append(getCellTypeByInt(cellType));
                    if (i != cellTypes.size() - 1) {
                        strType.append(",");
                    }
                }
                result =
                        MessageFormat.format("the column [{0}] type must [{1}]", localeHeaderName, strType.toString());
            } else {
                // 类型符合验证,但值不在要求范围内的
                // String in
                if (annoCell.valid().in().length != 0 && cell.getCellTypeEnum() == CellType.STRING) {
                    String[] in = annoCell.valid().in();
                    String cellValue = cell.getStringCellValue();
                    boolean isIn = false;
                    for (String str : in) {
                        if (str.equals(cellValue)) {
                            isIn = true;
                        }
                    }
                    if (!isIn) {
                        result = MessageFormat.format("the column [{0}] value must in {1}", localeHeaderName, in);
                    }
                }
                //todo 空值未判断
                // 数字型
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    double cellValue = cell.getNumericCellValue();
                    // 小于
                    if (!Double.isNaN(annoCell.valid().lt())) {
                        if (!(cellValue < annoCell.valid().lt())) {
                            result =
                                    MessageFormat.format("the column [{0}] value must less than [{1}]", localeHeaderName,
                                            annoCell.valid().lt());
                        }
                    }
                    // 大于
                    if (!Double.isNaN(annoCell.valid().gt())) {
                        if (!(cellValue > annoCell.valid().gt())) {
                            result =
                                    MessageFormat.format("the column [{0}] value must greater than [{1}]", localeHeaderName,
                                            annoCell.valid().gt());
                        }
                    }
                    // 小于等于
                    if (!Double.isNaN(annoCell.valid().le())) {
                        if (!(cellValue <= annoCell.valid().le())) {
                            result =
                                    MessageFormat.format("the column [{0}] value must less than or equal [{1}]",
                                            localeHeaderName, annoCell.valid().le());
                        }
                    }
                    // 大于等于
                    if (!Double.isNaN(annoCell.valid().ge())) {
                        if (!(cellValue >= annoCell.valid().ge())) {
                            result =
                                    MessageFormat.format("the column [{0}] value must greater than or equal [{1}]",
                                            localeHeaderName, annoCell.valid().ge());
                        }
                    }
                }
            }
        }
        return result;
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
        List<FieldForSortting> annoNullFields = new ArrayList<>();
        for (Field field : fieldsArr) {
            ExcelCell ec = field.getAnnotation(ExcelCell.class);
            if (ec == null) {
                // 没有ExcelCell Annotation 视为不导入
                continue;
            }
            int id = ec.index();
            fields.add(new FieldForSortting(field, id));
        }
        fields.addAll(annoNullFields);
        sortByProperties(fields, true, false, "index");
        return fields;
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
        return !isNullOrBlankStringCell(cell) || (isNullOrBlankStringCell(cell) && !allowNull);
    }

}
