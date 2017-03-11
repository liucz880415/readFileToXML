package com.lcz.file.entity;

public class CellEntity {
    private String cellName;//列的英文下标
    private Integer rowIndex;//行数
    private Integer cellIndex;//列的数字下标
    private Object object;

    public Integer getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(Integer cellIndex) {
        this.cellIndex = cellIndex;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public CellEntity(String cellName, Integer cellIndex, Integer rowIndex, Object object) {
        this.cellName = cellName;
        this.rowIndex = rowIndex;
        this.cellIndex = cellIndex;
        this.object = object;
    }

    public CellEntity() {
    }

    @Override
    public String toString() {
        return "CellEntity{" +
                "cellName='" + cellName + '\'' +
                ", rowIndex=" + rowIndex +
                ", cellIndex=" + cellIndex +
                ", object=" + object +
                '}';
    }
}
