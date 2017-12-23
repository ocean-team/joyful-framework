package org.joyful4j.modules.excel.logs;

public abstract class ExcelBasicLog {

    protected String log;

    public ExcelBasicLog() {
    }

    public ExcelBasicLog(String log) {
        this.log = log;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
