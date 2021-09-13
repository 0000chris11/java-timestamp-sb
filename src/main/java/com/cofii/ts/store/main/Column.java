package com.cofii.ts.store.main;

public class Column {
    
    private String name;
    private String type;
    private int typeLength;
    private boolean nulll;
    private String defaultt;
    private boolean extra;

    private boolean pk = false;
    private boolean fk = false;

    private boolean dist = false;
    private boolean textArea = false;
    private boolean imageC = false;
    //QOL---------------------------------------------------------
    public String getFullType(){
        return type + (typeLength > 0 ? "(" + typeLength + ")" : "");
    }
    //CONSTRUCTOR---------------------------------------------------
    public Column(String name, String type, int typeLength, boolean nulll, String defaultt, boolean extra) {
        this.name = name;
        this.type = type;
        this.typeLength = typeLength;
        this.nulll = nulll;
        this.defaultt = defaultt;
        this.extra = extra;
    }
    //GETTERS & SETTERS----------------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTypeLength() {
        return typeLength;
    }

    public void setTypeLength(int typeLength) {
        this.typeLength = typeLength;
    }

    public boolean getNulll() {
        return nulll;
    }

    public void setNulll(boolean nulll) {
        this.nulll = nulll;
    }

    public String getDefaultt() {
        return defaultt;
    }

    public void setDefaultt(String defaultt) {
        this.defaultt = defaultt;
    }

    public boolean getExtra() {
        return extra;
    }

    public void setExtra(boolean extra) {
        this.extra = extra;
    }

    public boolean getDist() {
        return dist;
    }

    public void setDist(boolean dist) {
        this.dist = dist;
    }

    public boolean getImageC() {
        return imageC;
    }

    public void setImageC(boolean imageC) {
        this.imageC = imageC;
    }

    public boolean isTextArea() {
        return textArea;
    }

    public void setTextArea(boolean textArea) {
        this.textArea = textArea;
    }
    public boolean isPk() {
        return pk;
    }
    public void setPk(boolean pk) {
        this.pk = pk;
    }
    public boolean isFk() {
        return fk;
    }
    public void setFk(boolean fk) {
        this.fk = fk;
    }
    
}
