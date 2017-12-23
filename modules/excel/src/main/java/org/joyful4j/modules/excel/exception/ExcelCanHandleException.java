package org.joyful4j.modules.excel.exception;

/**
 * @author richey
 */
public class ExcelCanHandleException extends RuntimeException {
    private static final long serialVersionUID = -3755477657584978882L;

    public ExcelCanHandleException(ExcellExceptionType excellExceptionType) {
        super(excellExceptionType.key,new ExcelException(excellExceptionType.name));
    }

}
