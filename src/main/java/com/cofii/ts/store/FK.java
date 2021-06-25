package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cofii2.stores.IntString;

public class FK {

    private String database;
    private String table;
    private String constraint;
    private List<IntString> columns = new ArrayList<>();
    private String referencedDatabase;
    private String referencedTable;
    private List<String> referencedColumns = new ArrayList<>();
    //----------------------------------------------------  
    public FK(String database, String table, String constraint, int ordinalPosition, String column, String referencedDatabase,
            String referencedTable, String referencedColumn) {
        this.database = database;
        this.table = table;
        this.constraint = constraint;
        columns.add(new IntString(ordinalPosition, column));
        this.referencedDatabase = referencedDatabase;
        this.referencedTable = referencedTable;
        referencedColumns.add(referencedColumn);
    }
    //----------------------------------------------------
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<IntString> getColumns() {
        return columns;
    }

    public void setColumns(List<IntString> columns) {
        this.columns = columns;
    }

    public String getReferencedDatabase() {
        return referencedDatabase;
    }

    public void setReferencedDatabase(String referencedDatabase) {
        this.referencedDatabase = referencedDatabase;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public void setReferencedTable(String referencedTable) {
        this.referencedTable = referencedTable;
    }

    public List<String> getReferencedColumns() {
        return referencedColumns;
    }

    public void setReferencedColumns(List<String> referencedColumns) {
        this.referencedColumns = referencedColumns;
    }
    public String getConstraint() {
        return constraint;
    }
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    
}
