package com.cofii.ts.store;

public class Key {
    private String database;
    private String tableName;
    private String constraintType;
    private int ordinalPosition;
    private String columnName;
    private String referencedTableName;
    private String referencedColumnName;
    //----------------------------------------------
    public Key(String database, String tableName, String constraintType, int ordinalPosition, String columnName,
            String referencedTableName, String referencedColumnName) {
        this.database = database;
        this.tableName = tableName;
        this.constraintType = constraintType;
        this.ordinalPosition = ordinalPosition;
        this.columnName = columnName;
        this.referencedTableName = referencedTableName;
        this.referencedColumnName = referencedColumnName;
    }
    //----------------------------------------------
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

}
