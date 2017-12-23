package com.ihr360.excel.entity;

import java.lang.reflect.Field;

/**
 * The <code>FieldForSortting</code>
 * 
 * @author richey.liu
 * @version 1.0, Created at 2017-12-17
 */
public class FieldForSortting {
    private Field field;
    private int index;

    /**
     * @param field
     */
    public FieldForSortting(Field field) {
        this.field = field;
    }

    /**
     * @param field
     * @param index
     */
    public FieldForSortting(Field field, int index) {
        this.field = field;
        this.index = index;
    }

    /**
     * @return the field
     */
    public Field getField() {
        return field;
    }

    /**
     * @param field
     *            the field to set
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

}
