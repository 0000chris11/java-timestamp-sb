package com.cofii.ts.store.main;

public class PK {

    private String databaseName;
    private String tableName;
    private int ordinalPosition;
    private String columnName;
    
    public PK(String databaseName, String tableName, int ordinalPosition, String columnName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.ordinalPosition = ordinalPosition;
        this.columnName = columnName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    
}
