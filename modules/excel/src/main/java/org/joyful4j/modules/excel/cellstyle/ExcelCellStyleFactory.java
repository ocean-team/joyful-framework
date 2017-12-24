package org.joyful4j.modules.excel.cellstyle;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

public class ExcelCellStyleFactory {

    public static ExcelCellStyle createDefaultHeaderCellStyle() {
        ExcelCellStyle excelCellStyle = createDefaultExcelCellStyle();
        excelCellStyle.setExcelFont(createDefaultHeaderFont());
        return excelCellStyle;
    }


    public static ExcelCellStyle createRequiredHeaderCellStyle() {
        ExcelCellStyle excelCellStyle = createDefaultExcelCellStyle();
        ExcelFont excelFont = createDefaultHeaderFont();
        excelFont.setColor(Font.COLOR_RED);
        excelCellStyle.setExcelFont(excelFont);
        return excelCellStyle;
    }


    public static ExcelFont createDefaultHeaderFont() {
        ExcelFont excelFont = ExcelFont.createExcelFont();
        excelFont.setItalic(false);
        excelFont.setColor(Font.COLOR_NORMAL);
        excelFont.setFontHeightInPoints((short) 12);
        excelFont.setBold(true);
        return excelFont;
    }

    private static ExcelCellStyle createDefaultExcelCellStyle() {
        ExcelCellStyle excelCellStyle = new ExcelCellStyle();
        //单元格内容水平居中
        excelCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //浅绿背景色
        excelCellStyle.setBackgroundColor(IndexedColors.AQUA.getIndex());
        excelCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //边框
        excelCellStyle.setBorderBottom(BorderStyle.THIN);
        excelCellStyle.setBorderLeft(BorderStyle.THIN);
        excelCellStyle.setBorderTop(BorderStyle.THIN);
        excelCellStyle.setBorderRight(BorderStyle.THIN);
        return excelCellStyle;
    }


}
