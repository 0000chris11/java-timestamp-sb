package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

public class TableS {

    private static TableS instance;
    public static TableS getInstance(){
        if(instance == null){
            instance = new TableS();
        }
        return instance;
    }
    
    private List<Table> tables = new ArrayList<>();
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
    //------------------------------------------
    private TableS(){

    }
}
