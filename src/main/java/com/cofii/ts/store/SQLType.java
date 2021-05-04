package com.cofii.ts.store;

public class SQLType {

    private String typeName;
    private int typeLength;
    private int typeMaxLength;
    //--------------------------------------------
    public SQLType(String typeName, int typeLength, int typeMaxLength) {
        this.typeName = typeName;
        this.typeLength = typeLength;
        this.typeMaxLength = typeMaxLength;
    }
    //--------------------------------------------
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getTypeLength() {
        return typeLength;
    }

    public void setTypeLength(int typeLength) {
        this.typeLength = typeLength;
    }
    public int getTypeMaxLength() {
        return typeMaxLength;
    }
    public void setTypeMaxLength(int typeMaxLength) {
        this.typeMaxLength = typeMaxLength;
    }
    
}
