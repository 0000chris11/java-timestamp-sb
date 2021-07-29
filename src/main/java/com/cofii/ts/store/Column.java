package com.cofii.ts.store;

public class Column {
    
    private String name;
    private String type;
    private int typeLength;
    private boolean nulll;
    private String defaultt;
    private boolean extra;

    private boolean dist;
    private boolean imageC;

    public Column(String name, String type, int typeLength, boolean nulll, String defaultt, boolean extra) {
        this.name = name;
        this.type = type;
        this.typeLength = typeLength;
        this.nulll = nulll;
        this.defaultt = defaultt;
        this.extra = extra;
    }

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
    
}
