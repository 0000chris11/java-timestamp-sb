package com.cofii.ts.store.main;

public class Option {
    
    private int id;
    private String optionName;
    private String value;
    //---------------------------------------------
    public Option(int id, String optionName, String value) {
        this.id = id;
        this.optionName = optionName;
        this.value = value;
    }
    //---------------------------------------------
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getOptionName() {
        return optionName;
    }
    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    
}
