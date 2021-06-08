package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.Arrays;
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
    public int getColumnIndex(String name){
        int returnValue = -1;
        int c = 0;
        for(Column column : list){
            if(column.getName().equals(name)){
                returnValue = c;
                break;
            }
            c++;
        }
        return returnValue;
    }
    public String getColumn(int index){
        return list.get(index).getName();
    }
    public String[] getColumns(){
        String[] columns = new String[list.size()];
        int c = 0;
        for(Column column : list){
            columns[c++] = column.getName();
        }
        return columns;
    }
    public String getType(int index){
        return list.get(index).getType();
    }
    public String[] getTypes(){
        String[] types = new String[list.size()];
        int c = 0;
        for(Column column : list){
            types[c++] = column.getType();
        }
        return types;
    }
    public int getTypeLength(int index){
        return list.get(index).getTypeLength();
    }
    public int[] getTypesLength(){
        int[] typesLength = new int[list.size()];
        int c = 0;
        for(Column column : list){
            typesLength[c++] = column.getTypeLength();
        }
        return typesLength;
    }
    public boolean getNull(int index){
        return list.get(index).getNulll();
    }
    public boolean[] getNulls(){
        boolean[] nulls = new boolean[list.size()];
        int c = 0;
        for(Column column : list){
            nulls[c++] = column.getNulll();
        }
        return nulls;
    }
    public String getDefault(int index){
        return list.get(index).getDefaultt();
    }
    public String[] getDefaults(){
        String[] defaults = new String[list.size()];
        int c = 0;
        for(Column column : list){
            defaults[c++] = column.getDefaultt();
        }
        return defaults;
    }
    public String getExtraAsString(int index){
        return list.get(index).getExtra();
    }
    public int getExtra(){
        int extra = -1;
        int c = 0;
        for(Column column : list){
            if(column.getExtra().equals("Yes")){
                extra = c;
                break;
            }
            c++;
        }
        return extra;
    }
    //-----------------------------------------
    private ColumnS(){

    }
}
