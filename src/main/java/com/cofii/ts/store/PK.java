package com.cofii.ts.store;

import java.util.HashMap;
import java.util.Map;

public class PK {
    
    private String database;
    private String table;
    private Map<Integer, String> columns = new HashMap<>();
    //--------------------------------------------------------
    public PK(String database, String table, int ordinalPosition, String column) {
        this.database = database;
        this.table = table;
        columns.put(ordinalPosition, column);
    }
    //--------------------------------------------------------
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
}
