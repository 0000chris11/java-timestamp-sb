package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cofii2.stores.IntString;

public class PK {
    
    private String database;
    private String table;
    private List<IntString> columns = new ArrayList<>();
    //--------------------------------------------------------
    public PK(String database, String table, int ordinalPosition, String column) {
        this.database = database;
        this.table = table;
        columns.add(new IntString(ordinalPosition, column));
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
    public List<IntString> getColumns() {
        return columns;
    }
    public void setColumns(List<IntString> columns) {
        this.columns = columns;
    }
}
