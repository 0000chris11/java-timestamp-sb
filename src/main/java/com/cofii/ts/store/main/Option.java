package com.cofii.ts.store.main;

public class Option {
    
    public static final String INSERT_CLEAR = "insert_clear";
    public static final String UPDATE_CLEAR = "update_clear";
    public static final String ALWAYS_ON_TOP = "always_on_top";

    private int id;
    private String optionName;
    private String value;

    private String defaultValue;
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
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
}
