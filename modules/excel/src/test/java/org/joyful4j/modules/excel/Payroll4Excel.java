package org.joyful4j.modules.excel;

import java.util.Date;

public class Payroll4Excel {

    @ExcelCell(index = 0, header = {"姓名", "Name"})
    @ExcelCell.Valid(allowNull = false)
    private String name;

    @ExcelCell(index = 1, header = {"年", "Year"})
    @ExcelCell.Valid(allowNull = false)
    private Integer year;

    @ExcelCell(index = 2, header = {"月", "Month"})
    @ExcelCell.Valid(allowNull = false, ge = 0L, le = 12L)
    private Integer month;

    @ExcelCell(index = 3, header = {"工资", "Salary"})
    @ExcelCell.Valid(ge = 0L)
    private Double salary;

    @ExcelCell(index = 4, header = {"税额", "Tax"})
    private Double tax;

    @ExcelCell(index = 5, header = {"发放时间", "Pay Time"})
    private Date payTime;


    public Payroll4Excel() {
    }

    public Payroll4Excel(String name, Integer year, Integer month, Double money, Double tax, Date payTime) {
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
}
