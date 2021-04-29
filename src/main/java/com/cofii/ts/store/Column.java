package com.cofii.ts.store;

public class Column {
    
    private String name;
    private String type;
    private int typeLength;
    private boolean nulll;
    private String key;
    private String defaultt;
    private String extra;

    public Column(String name, String type, int typeLength, boolean nulll, String key, String defaultt, String extra) {
        this.name = name;
        this.type = type;
        this.typeLength = typeLength;
        this.nulll = nulll;
        this.key = key;
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

    public boolean isNulll() {
        return nulll;
    }

    public void setNulll(boolean nulll) {
        this.nulll = nulll;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultt() {
        return defaultt;
    }

    public void setDefaultt(String defaultt) {
        this.defaultt = defaultt;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
    
}
