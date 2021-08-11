package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

public class Database {
    
    private int id;
    private String name;

    private List<Table> tables = new ArrayList<>();
    private Table currentTable;
    //------------------------------------------
    public Database(int id, String name){
        this.id = id;
        this.name = name;
    }
    //------------------------------------------
    public void addTable(Table table){
        tables.add(table);
    }

    public void clearTables(){
        tables.clear();
    }

    public int size(){
        return tables.size();
    }
    //------------------------------------------
    public String getTable(int index){
        return tables.get(index).getName();
    }
    
    public String[] getTables(){
        List<String> list = new ArrayList<>();
        for(Table table : tables){
            list.add(table.getName());
        }
        return list.toArray(new String[list.size()]);
    }
    //GETTERS & SETTERS------------------------------------------
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
    public Table getCurrentTable() {
        return currentTable;
    }
    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
}
