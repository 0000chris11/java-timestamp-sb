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
    
    private List<Table> list = new ArrayList<>();
    //------------------------------------------
    public void addTable(Table table){
        list.add(table);
    }

    public void clearTables(){
        list.clear();
    }

    public int size(){
        return list.size();
    }
    
    public String getTable(int index){
        return list.get(index).getName();
    }
    //------------------------------------------
    private TableS(){

    }
}
