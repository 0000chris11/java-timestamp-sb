package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

public class SQLTypes {

    private List<SQLType> types = new ArrayList<>();
    //---------------------------------------------------
    public String[] getTypeNames(){
        String[] typeNames = new String[types.size()];
        int c = 0;
        for(SQLType x : types){
            typeNames[c++] = x.getTypeName();
        }
        return typeNames;
    }

    public String getTypeName(int index){
        return types.get(index).getTypeName();
    }
    public int getTypeLength(int index){
        return types.get(index).getTypeLength();
    }
    public int getTypeLength(String element){
        int returnValue = -1;
        for(SQLType type : types){
            if(element.equals(type.getTypeName())){
                returnValue = type.getTypeLength();
            }
        }
        return returnValue;
        
    }
    public int getTypeMaxLength(int index){
        return types.get(index).getTypeMaxLength();
    }
    public int getTypeMaxLength(String element){
        int returnValue = -1;
        for(SQLType type : types){
            if(element.equals(type.getTypeName())){
                returnValue = type.getTypeMaxLength();
            }
        }
        return returnValue;
    }
    public SQLType getType(String typeName){
        SQLType returnValue = null;
        for(SQLType type : types){
            if(typeName.equals(type.getTypeName())){
                returnValue = type;
                break;
            }
        }
        return returnValue;
    }
    //---------------------------------------------------
    private static SQLTypes instance;
    public static SQLTypes getInstance(){
        if(instance == null){
            instance = new SQLTypes();
        }
        return instance;
    }
    private SQLTypes(){
        types.add(new SQLType("INT", 11, 11));
        types.add(new SQLType("TINYINT", 4, 4));
        types.add(new SQLType("SMALLINT", 6, 6));
        types.add(new SQLType("MEDIUMINT", 9, 9));
        types.add(new SQLType("BIGINT", 20, 20));
        types.add(new SQLType("FLOAT", 0, 0));
        types.add(new SQLType("DOUBLE", 0, 0));
        types.add(new SQLType("CHAR", 1, 255));
        types.add(new SQLType("VARCHAR", 80, 16383));
        types.add(new SQLType("BOOLEAN/TINYINT(1)", 0, 0));
        types.add(new SQLType("TIME", 0, 0));
        types.add(new SQLType("DATE", 0, 0));
        types.add(new SQLType("DATETIME", 0, 0));
        types.add(new SQLType("TIMESTAMP", 0, 0));
        types.add(new SQLType("BINARY", 0, 0));
        types.add(new SQLType("VARBINARY", 0, 0));
    }
}
