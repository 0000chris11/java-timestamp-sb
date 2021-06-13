package com.cofii.ts.store;

public class VCGridNodes {

    private String[] columnNames;
    private String[] types;
    private String[] typesLength;
    private boolean[] nulls;
    private boolean[] pks;
    private boolean[] fks;
    private String[] fksText;
    private boolean[] defaults;
    private String[] defaultsText;
    private boolean[] extra;

    private boolean[] dists;
    private boolean[] imageCs;
    //-------------------------------------------------
    private static VCGridNodes instance;
    public static VCGridNodes getInstance(){
        if(instance == null){
            instance = new VCGridNodes();
        }
        return instance;
    }
    //-------------------------------------------------
    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String[] getTypesLength() {
        return typesLength;
    }

    public void setTypesLength(String[] typesLength) {
        this.typesLength = typesLength;
    }

    public boolean[] getNulls() {
        return nulls;
    }

    public void setNulls(boolean[] nulls) {
        this.nulls = nulls;
    }

    public boolean[] getPks() {
        return pks;
    }

    public void setPks(boolean[] pks) {
        this.pks = pks;
    }

    public boolean[] getFks() {
        return fks;
    }

    public void setFks(boolean[] fks) {
        this.fks = fks;
    }

    public String[] getFksText() {
        return fksText;
    }

    public void setFksText(String[] fksText) {
        this.fksText = fksText;
    }

    public boolean[] getDefaults() {
        return defaults;
    }

    public void setDefaults(boolean[] defaults) {
        this.defaults = defaults;
    }

    public String[] getDefaultsText() {
        return defaultsText;
    }

    public void setDefaultsText(String[] defaultsText) {
        this.defaultsText = defaultsText;
    }

    public boolean[] getExtra() {
        return extra;
    }

    public void setExtra(boolean[] extra) {
        this.extra = extra;
    }
    public boolean[] getDists() {
        return dists;
    }
    public void setDists(boolean[] dists) {
        this.dists = dists;
    }
    public boolean[] getImageCs() {
        return imageCs;
    }
    public void setImageCs(boolean[] imageCs) {
        this.imageCs = imageCs;
    }

}
