package com.cofii.ts.store;

public class SQLType {

    private String typeName;
    private int typeLength;
    private int typeMaxLength;
    private String typeChar;
    //--------------------------------------------
    public SQLType(String typeName, int typeLength, int typeMaxLength, String typeChar) {
        this.typeName = typeName;
        this.typeLength = typeLength;
        this.typeMaxLength = typeMaxLength;
        this.typeChar = typeChar;
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
    public String getTypeChar() {
        return typeChar;
    }
    public void setTypeChar(String typeChar) {
        this.typeChar = typeChar;
    }
    
}
