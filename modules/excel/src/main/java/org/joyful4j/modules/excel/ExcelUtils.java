package cn.irenshi.core.utils;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;


public class ExcelUtils {

    private static ThreadLocal<DataFormatter> formatterThreadLocal = new ThreadLocal<DataFormatter>();

    /**
     * 获取日期格式yyyy-MM
     *
     * @param cell
     * @return
     */
    public static Date getDateMonthStringCell(Cell cell) throws Exception {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                if (Strings.isNullOrEmpty(cell.getStringCellValue())) {
                    return null;
                }
                return DateUtil.parseDateMonth(cell.getStringCellValue().trim());
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getDateCellValue();
        }
        return null;
    }

    /**
     * 获取日期格式yyyy-MM
     *
     * @param cellIndex
     * @return
     */
    public static Date getDateMonthStringCell(int cellIndex, Object object) throws Exception {
        if (object instanceof Row) {
            Row row = (Row) object;
            return getDateMonthStringCell(row.getCell(cellIndex));
        } else if (object instanceof Map) {
            Map<Integer, Object> valueMap = (Map<Integer, Object>) object;
            Object valueObject = valueMap.get(cellIndex);
            if (valueObject != null) {
                if (valueObject instanceof Double) {
                    return org.apache.poi.ss.usermodel.DateUtil.getJavaDate((double) valueObject);
                } else if (valueObject instanceof String) {

                    Pattern temp = Pattern.compile("^([0-9]{6})$");//为了识别 199007类型日期
                    if (temp.matcher(((String) valueObject)).matches()) {
                        return DateUtil.parseDateMonth((String) valueObject);
                    }

                    Pattern pattern = Pattern.compile("^[0-9]+$");
                    if (pattern.matcher(((String) valueObject)).matches()) {
                        return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(Double.parseDouble((String) valueObject));
                    }
                    return DateUtil.parseDateMonth((String) valueObject);
                }
            }
        }
        return null;
    }

    /**
     * 获取日期格式 yyyy-MM-dd
     */
    public static Date getDateString(Object value) {
        if (value != null) {
            if (value instanceof Double) {
                return org.apache.poi.ss.usermodel.DateUtil.getJavaDate((double) value);
            } else if (value instanceof String) {

                Pattern temp = Pattern.compile("^[0-9]{8}$");//为了识别 19900707类型日期
                if (temp.matcher(((String) value)).matches()) {
                    return DateUtil.parseDate((String) value);
                }

                Pattern pattern = Pattern.compile("^[0-9]+[.]?[0-9]*$");
                if (pattern.matcher(((String) value)).matches()) {
                    return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(Double.parseDouble((String) value));
                }
                return DateUtil.parseDate((String) value);
            }
        }
        return null;
    }

    /**
     * 获取日期格式 yyyy-MM-dd
     *
     * @param cell
     * @return
     * @throws Exception
     */
    public static Date getDateStringCell(Cell cell) throws Exception {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                if (StringUtils.isBlank(cell.getStringCellValue())) {
                    return null;
                }
                return DateUtil.parseDate(cell.getStringCellValue().trim());
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getDateCellValue();
        }
        return null;
    }


    /**
     * 获取日期格式 yyyy-MM-dd HH:mm
     *
     * @param cell
     * @return
     * @throws Exception
     */
    public static Date getDateYYYYMMDDHHMMCell(Cell cell) throws Exception {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                if (StringUtils.isBlank(cell.getStringCellValue())) {
                    return null;
                }
                return DateUtil.parseDateMinute(cell.getStringCellValue().trim());
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getDateCellValue();
            default:
        }
        return null;
    }

    /**
     * 获取日期格式 yyyy-MM-dd
     *
     * @param cellIndex
     * @return
     * @throws Exception
     */
    public static Date getDateStringCell(int cellIndex, Object object) throws Exception {
        if (object instanceof Row) {
            Row row = (Row) object;
            return getDateStringCell(row.getCell(cellIndex));
        } else if (object instanceof Map) {
            Map<Integer, Object> valueMap = (Map<Integer, Object>) object;
            Object valueObject = valueMap.get(cellIndex);
            if (valueObject != null) {
                if (valueObject instanceof Double) {
                    return org.apache.poi.ss.usermodel.DateUtil.getJavaDate((double) valueObject);
                } else if (valueObject instanceof String) {

                    Pattern temp = Pattern.compile("^[0-9]{8}$");//为了识别 19900707类型日期
                    if (temp.matcher(((String) valueObject)).matches()) {
                        return DateUtil.parseDate((String) valueObject);
                    }

                    Pattern pattern = Pattern.compile("^[0-9]+[.]?[0-9]*$");
                    if (pattern.matcher(((String) valueObject)).matches()) {
                        return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(Double.parseDouble((String) valueObject));
                    }
                    return DateUtil.parseDate((String) valueObject);
                }
            }
        }
        return null;
    }

    /**
     * 新的导入方法：获取字符串类型的值
     *
     * @param map
     * @param headName
     * @return
     */
    public static String getStringValue(Map<String, Object> map, String headName) {
        if (map == null || StringUtils.isBlank(headName) || map.get(headName) == null) {
            return null;
        }
        Object value = map.get(headName);
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Double) {
            Double doubleValue = (Double) value;
            if (doubleValue != null && StringUtils.isNotBlank(doubleValue.toString())) {
                if ((String.valueOf(doubleValue)).indexOf("E") > 0) {
                    BigDecimal db = new BigDecimal(doubleValue);
                    return ExcelUtils.getExcelNumberString(db.toPlainString());

                } else if (String.valueOf(doubleValue).indexOf(".") > 0) {
                    return String.valueOf(doubleValue).substring(0, String.valueOf(doubleValue).lastIndexOf("."));
                }
                return String.valueOf(doubleValue);
            }
        }
        return null;
    }

    /**
     * 获取日期格式 yyyy-MM-dd
     *
     * @return
     * @throws Exception
     */
    public static Date getDateYYYYMMDD(Map<String, Object> map, String headName) throws Exception {
        if (map == null || StringUtils.isBlank(headName) || map.get(headName) == null) {
            return null;
        }
        Object valueObject = map.get(headName);
        if (valueObject != null) {
            if (valueObject instanceof Double) {
                return org.apache.poi.ss.usermodel.DateUtil.getJavaDate((double) valueObject);
            } else if (valueObject instanceof String) {

                Pattern temp = Pattern.compile("^[0-9]{8}$");//为了识别 19900707类型日期
                if (temp.matcher(((String) valueObject)).matches()) {
                    return DateUtil.parseDate((String) valueObject);
                }
                Pattern pattern = Pattern.compile("^[0-9]+[.]?[0-9]*$");
                if (pattern.matcher(((String) valueObject)).matches()) {
                    return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(Double.parseDouble((String) valueObject));
                }
                return DateUtil.parseDate((String) valueObject);
            }
        }
        return null;
    }

    /**
     * 获取日期格式 yyyy-MM-dd HH:mm
     *
     * @param map
     * @return
     * @throws Exception
     */
    public static Date getDateYYYYMMDDHHMM(Map<String, Object> map, String headName) throws Exception {
        if (map == null || StringUtils.isBlank(headName) || map.get(headName) == null) {
            return null;
        }
        Object valueObject = map.get(headName);
        if (valueObject != null) {
            if (valueObject instanceof Double) {
                return org.apache.poi.ss.usermodel.DateUtil.getJavaDate((double) valueObject);
            } else if (valueObject instanceof String) {
                Pattern pattern = Pattern.compile("^[0-9]+[.]?[0-9]*$");
                String value = ((String) valueObject).trim();
                if (pattern.matcher(value).matches()) {
                    return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(Double.parseDouble(value));
                }
                if (StringUtils.isNotBlank(value)) {
                    return DateUtil.parseDateMinute(value);
                }
            }
        }
        return null;
    }


    /**
     * 获取日期格式 yyyy-MM-dd HH:mm
     *
     * @param cellIndex
     * @return
     */
    public static Date getDateYYYYMMDDHHMM(int cellIndex, Object object) throws Exception {
        if (object instanceof Row) {
            Row row = (Row) object;
            return getDateYYYYMMDDHHMMCell(row.getCell(cellIndex));
        } else if (object instanceof Map) {
            Map<Integer, Object> valueMap = (Map<Integer, Object>) object;
            Object valueObject = valueMap.get(cellIndex);
            if (valueObject != null) {
                if (valueObject instanceof Double) {
                    return org.apache.poi.ss.usermodel.DateUtil.getJavaDate((double) valueObject);
                } else if (valueObject instanceof String) {
                    Pattern pattern = Pattern.compile("^[0-9]+[.]?[0-9]*$");
                    if (pattern.matcher(((String) valueObject)).matches()) {
                        return DateUtil.parseDateMinute((String) valueObject);
                    }
                }
            }
        }
        return null;
    }


    /**
     * 获取日期格式 yyyy-MM
     *
     * @return
     * @throws Exception
     */
    public static Date getDateYYYYMM(Map<String, Object> map, String headName) throws Exception {
        if (map == null || StringUtils.isBlank(headName) || map.get(headName) == null) {
            return null;
        }
        Object valueObject = map.get(headName);
        if (valueObject != null) {
            if (valueObject instanceof Double) {
                return org.apache.poi.ss.usermodel.DateUtil.getJavaDate((double) valueObject);
            } else if (valueObject instanceof String) {

                Pattern temp = Pattern.compile("^([0-9]{6})$");//为了识别 199007类型日期
                if (temp.matcher(((String) valueObject)).matches()) {
                    return DateUtil.parseDateMonth((String) valueObject);
                }

                Pattern pattern = Pattern.compile("^[0-9]+$");
                if (pattern.matcher(((String) valueObject)).matches()) {
                    return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(Double.parseDouble((String) valueObject));
                }
                return DateUtil.parseDateMonth((String) valueObject);
            }
        }
        return null;
    }

    public static Double getDoubleCellData(Map<String, Object> map, String headName) {
        if (map == null || StringUtils.isBlank(headName) || map.get(headName) == null) {
            return null;
        }
        Object object = map.get(headName);
        if (object instanceof String) {
            return Double.parseDouble((String) object);
        } else if (object instanceof Double) {
            return (Double) object;
        }
        return null;
    }


    public static String getStringCellData(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                if (checkCellStringEmpty(cell)) {
                    return null;
                }
                // \r回车（Carriage Return） \n新行（New Line）
                return cell.getStringCellValue().replaceAll("\r|\n", "").trim();
            case Cell.CELL_TYPE_NUMERIC:
               /* Double cellValue = cell.getNumericCellValue();
                if (cellValue != null && StringUtils.isNotBlank(cellValue.toString())) {
                    if ((String.valueOf(cellValue)).indexOf("E") > 0) {
                        BigDecimal db = new BigDecimal(cellValue);
                        return getExcelNumberString(db.toPlainString());

                    }
                    return String.valueOf(cellValue);
                }*/
                DataFormatter dataFormatter = getCurrentDataFormat();
                return dataFormatter.formatCellValue(cell);
            default:
        }
        return null;
    }

    private static DataFormatter getCurrentDataFormat() {
        DataFormatter dataFormatter = formatterThreadLocal.get();
        if (dataFormatter == null) {
            dataFormatter = new DataFormatter();
            formatterThreadLocal.set(dataFormatter);
        }
        return dataFormatter;
    }

    private static boolean checkCellStringEmpty(Cell cell) {
        return StringUtils.isBlank(cell.toString()) || StringUtils.isBlank(cell.getStringCellValue());
    }

    public static String getStringCellData(int cellIndex, Object object) {
        if (object instanceof Row) {
            Row row = (Row) object;
            return getStringCellData(row.getCell(cellIndex));
        } else if (object instanceof Map) {
            Map<Integer, Object> valueMap = (Map<Integer, Object>) object;
            Object valueObject = valueMap.get(cellIndex);
            return valueObject == null ? null : String.valueOf(valueObject);
        }
        return null;
    }


    /**
     * excel表格，超过15位的数字，将15位和16位的数字四舍五入，后面的位数补0（excel本身的限制造成的，excel只能支持到15位数字）
     *
     * @param value
     * @return
     */
    public static String getExcelNumberString(String value) {
        int length = value.length();
        if (length < 16) {
            return value;
        }
        long longValue = Math.round(Long.parseLong(value.substring(0, 16)) / 10.0);
        int zeroLength = length - 15;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < longValue; i++) {
            buffer.append("0");
        }
        return String.valueOf(zeroLength) + buffer.toString();

    }

    public static Integer getIntegerCellData(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                if (StringUtils.isBlank(cell.getStringCellValue())) {
                    return null;
                }
                return Integer.parseInt(cell.getStringCellValue());
            case Cell.CELL_TYPE_NUMERIC:
                Double cellValue = cell.getNumericCellValue();
                if (cellValue != null) {
                    return cellValue.intValue();
                }

        }
        return null;
    }

    public static Integer getIntegerCellData(int cellIndex, Object object) {
        if (object instanceof Row) {
            Row row = (Row) object;
            return getIntegerCellData(row.getCell(cellIndex));
        } else if (object instanceof Map) {
            Map<Integer, Object> valueMap = (Map<Integer, Object>) object;
            Object valueObject = valueMap.get(cellIndex);
            if (valueObject == null) {
                return null;
            }
            return StringUtils.isNotBlank(String.valueOf(valueObject)) ? Integer.parseInt(String.valueOf(valueObject)) : null;
        }
        return null;
    }

    public static Integer getIntegerCellData(Map<String, Object> map, String headName) {
        if (map == null || StringUtils.isBlank(headName) || map.get(headName) == null) {
            return null;
        }
        Object object = map.get(headName);
        if (object instanceof String) {
            return Integer.parseInt((String) object);
        } else if (object instanceof Double) {
            return (Integer) object;
        } else if (object instanceof Integer) {
            return (Integer) object;
        }
        return null;
    }

    public static Double getDoubleCellData(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                if (StringUtils.isBlank(cell.getStringCellValue())) {
                    return null;
                }
                return Double.parseDouble(cell.getStringCellValue());
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
        }
        return null;
    }

    public static Double getDoubleCellData(int cellIndex, Object object) {
        if (object instanceof Row) {
            Row row = (Row) object;
            return getDoubleCellData(row.getCell(cellIndex));
        } else if (object instanceof Map) {
            Map<Integer, Object> valueMap = (Map<Integer, Object>) object;
            Object valueObject = valueMap.get(cellIndex);
            if (valueObject == null) {
                return null;
            }
            return StringUtils.isNotBlank(String.valueOf(valueObject)) ? Double.parseDouble(String.valueOf(valueObject)) : null;
        }
        return null;
    }

}
