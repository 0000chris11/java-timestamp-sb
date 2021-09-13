package com.cofii.ts.store.main;

public class FK {
    
    private String databaseName;
    private String tableName;
    private String constraintType;
    private int ordinalPosition;
    private String columnName;
    private String referencedDatabaseName;
    private String referencedTableName;
    private String referencedColumnName;
    
    public FK(String databaseName, String tableName, String constraintType, int ordinalPosition, String columnName,
            String referencedDatabaseName, String referencedTableName, String referencedColumnName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.constraintType = constraintType;
        this.ordinalPosition = ordinalPosition;
        this.columnName = columnName;
        this.referencedDatabaseName = referencedDatabaseName;
        this.referencedTableName = referencedTableName;
        this.referencedColumnName = referencedColumnName;
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

    public String getReferencedDatabaseName() {
        return referencedDatabaseName;
    }

    public void setReferencedDatabaseName(String referencedDatabaseName) {
        this.referencedDatabaseName = referencedDatabaseName;
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
