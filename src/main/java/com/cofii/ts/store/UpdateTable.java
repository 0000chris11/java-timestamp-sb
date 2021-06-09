package com.cofii.ts.store;

import com.cofii.ts.sql.MSQL;
import com.cofii2.stores.IntDString;

public class UpdateTable {
    
    private int max = MSQL.MAX_COLUMNS;
    private String table = "none";
    private String[] columns = new String[max];
    private String[] types = new String[max];
    private int[] typesLength = new int[max];
    private boolean[] nulls = new boolean[max];
    private String[] pks = new String[max];
    private IntDString[] fks = new IntDString[max];
    private String[] defaults = new String[max];
    private int extra = -1;
    //----------------------------------------------------------------------------
    public UpdateTable(String table, String[] columns, String[] types, int[] typesLength, boolean[] nulls, String[] pks,
    IntDString[] fks, String[] defaults, int extra) {
        this.table = table;
        this.columns = columns;
        this.types = types;
        this.typesLength = typesLength;
        this.nulls = nulls;
        this.pks = pks;
        this.fks = fks;
        this.defaults = defaults;
        this.extra = extra;
    }
    //----------------------------------------------------------------------------
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public int[] getTypesLength() {
        return typesLength;
    }

    public void setTypesLength(int[] typesLength) {
        this.typesLength = typesLength;
    }

    public boolean[] getNulls() {
        return nulls;
    }

    public void setNulls(boolean[] nulls) {
        this.nulls = nulls;
    }

    public String[] getPks() {
        return pks;
    }

    public void setPks(String[] pks) {
        this.pks = pks;
    }

    public IntDString[] getFks() {
        return fks;
    }

    public void setFks(IntDString[] fks) {
        this.fks = fks;
    }

    public String[] getDefaults() {
        return defaults;
    }

    public void setDefaults(String[] defaults) {
        this.defaults = defaults;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    
}
