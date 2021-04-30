package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

public class ColumnDS {
    
    private List<ColumnD> list = new ArrayList<>();
    //------------------------------------------
    public void clear(){
        list.clear();
    }
    public void addColumnD(ColumnD columnd){
        list.add(columnd);
    }
    public void addColumnD(int index, ColumnD columnd){
        list.add(index, columnd);
    }
    public String getDist(int index){
        return list.get(index).getDist();
    }
    //------------------------------------------
    private static ColumnDS instance;
    public static ColumnDS getInstance(){
        if(instance == null){
            instance = new ColumnDS();
        }
        return instance;
    }

    private ColumnDS(){

    }
}
