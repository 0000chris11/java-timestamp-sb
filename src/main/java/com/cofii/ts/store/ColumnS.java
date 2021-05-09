package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

public class ColumnS {
    
    private static ColumnS instance;
    public static ColumnS getInstance(){
        if(instance == null){
            instance = new ColumnS();
        }
        return instance;
    }
    //-----------------------------------------
    private List<Column> list = new ArrayList<>();

    public void addColumn(Column column){
        list.add(column);
    }

    public void clearColumn(){
        list.clear();
    }

    public int size(){
        return list.size();
    }
    //---------------------------------------------------
    public String getColumn(int index){
        return list.get(index).getName();
    }
    public String getType(int index){
        return list.get(index).getType();
    }
    public int getTypeLength(int index){
        return list.get(index).getTypeLength();
    }
    public boolean getNull(int index){
        return list.get(index).isNulll();
    }
    public String getDefault(int index){
        return list.get(index).getDefaultt();
    }
    public String getExtra(int index){
        return list.get(index).getExtra();
    }
    //-----------------------------------------
    private ColumnS(){

    }
}
