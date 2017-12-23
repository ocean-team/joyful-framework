package com.ihr360.excel;

import com.ihr360.excel.annotation.ExcelCell;
import com.ihr360.excel.annotation.ExcelConfig;
import com.ihr360.excel.entity.ExcelI18nStrategyType;

import java.util.Date;

@ExcelConfig(i18nStrategy = ExcelI18nStrategyType.EXCEL_I18N_STRATEGY_PROPS,propsFileName = "excel-i18n-demo.properties")
public class Payroll4ExcelVo {

    public static final String EXCEL_TEST_NAME = "excel_test_name";
    public static final String EXCEL_TEST_YEAR = "excel_test_year";
    public static final String EXCEL_TEST_MONTH = "excel_test_month";
    public static final String EXCEL_TEST_SALARY = "excel_test_salary";
    public static final String EXCEL_TEST_TAX = "excel_test_tax";
    public static final String EXCEL_TEST_PAYTIME = "excel_test_payTime";
    public static final String EXCEL_TEST_TESTTIME = "excel_test_testTime";


    @ExcelCell(exportIndex = 0, importHeaderKey = EXCEL_TEST_NAME, defaultHeaderName = "姓名")
    @ExcelCell.Valid(allowNull = false)
    private String name;

    @ExcelCell(exportIndex = 1, importHeaderKey = EXCEL_TEST_YEAR, defaultHeaderName = "年")
    @ExcelCell.Valid(allowNull = false)
    private Integer year;


    @ExcelCell(exportIndex = 2, importHeaderKey = EXCEL_TEST_MONTH, defaultHeaderName = "月")
    @ExcelCell.Valid(allowNull = false, ge = 0, le = 12)
    private Integer month;

    @ExcelCell(exportIndex = 3, importHeaderKey = EXCEL_TEST_SALARY)
    @ExcelCell.Valid(ge = 0L)
    private Double salary;

    @ExcelCell(exportIndex = 4, importHeaderKey = EXCEL_TEST_TAX)
    private Double tax;

    @ExcelCell(exportIndex = 5, importHeaderKey = EXCEL_TEST_PAYTIME)
    private Date payTime;

    @ExcelCell(exportIndex = 6, importHeaderKey = EXCEL_TEST_TESTTIME)
    private Date testTime;


    public Payroll4ExcelVo() {
    }

    public Payroll4ExcelVo(String name, Integer year, Integer month, Double money, Double tax, Date payTime) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.salary = money;
        this.tax = tax;
        this.payTime = payTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getMoney() {
        return salary;
    }

    public void setMoney(Double money) {
        this.salary = money;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    @Override
    public String toString() {
        return "Payroll4ExcelVo{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", salary=" + salary +
                ", tax=" + tax +
                ", payTime=" + payTime +
                ", testTime=" + testTime +
                '}';
    }
}
