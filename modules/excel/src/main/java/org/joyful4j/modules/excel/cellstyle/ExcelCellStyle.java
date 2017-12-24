package org.joyful4j.modules.excel.cellstyle;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class ExcelCellStyle {

    /**
     * 前景色
     */
    private short foregroundColor;

    /**
     * 背景色
     */
    private short backgroundColor;

    /**
     * 单元格的填充信息模式和纯色填充单元。
     */
    private FillPatternType fillPattern;


    /**
     * 下边框
     */
    private BorderStyle borderBottom;

    /**
     * 左边框
     */
    private BorderStyle borderLeft;

    /**
     * 上边框
     */
    private BorderStyle borderTop;

    /**
     * 右边框
     */
    private BorderStyle borderRight;

    /**
     * 单元格为水平对齐的类型
     */
    private HorizontalAlignment horizontalAlignment;

    private ExcelFont excelFont;


    public short getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(short foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public short getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(short backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public FillPatternType getFillPattern() {
        return fillPattern;
    }

    public void setFillPattern(FillPatternType fillPattern) {
        this.fillPattern = fillPattern;
    }

    public BorderStyle getBorderBottom() {
        return borderBottom;
    }

    public void setBorderBottom(BorderStyle borderBottom) {
        this.borderBottom = borderBottom;
    }

    public BorderStyle getBorderLeft() {
        return borderLeft;
    }

    public void setBorderLeft(BorderStyle borderLeft) {
        this.borderLeft = borderLeft;
    }

    public BorderStyle getBorderTop() {
        return borderTop;
    }

    public void setBorderTop(BorderStyle borderTop) {
        this.borderTop = borderTop;
    }

    public BorderStyle getBorderRight() {
        return borderRight;
    }

    public void setBorderRight(BorderStyle borderRight) {
        this.borderRight = borderRight;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public ExcelFont getExcelFont() {
        return excelFont;
    }

    public void setExcelFont(ExcelFont excelFont) {
        this.excelFont = excelFont;
    }
}
