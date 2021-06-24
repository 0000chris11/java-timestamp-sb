package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FK {

    private String database;
    private String table;
    private Map<Integer, String> columns = new HashMap<>();
    private String referencedDatabase;
    private String referencedTable;
    private List<String> referencedColumns = new ArrayList<>();
    //----------------------------------------------------  
    public FK(String database, String table, int ordinalPosition, String column, String referencedDatabase,
            String referencedTable, String referencedColumn) {
        this.database = database;
        this.table = table;
        columns.put(ordinalPosition, column);
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

    public Map<Integer, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<Integer, String> columns) {
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

    
}
