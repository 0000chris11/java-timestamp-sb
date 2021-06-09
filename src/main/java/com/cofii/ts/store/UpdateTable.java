package com.cofii.ts.store;

import com.cofii.ts.sql.MSQL;
import com.cofii2.stores.QString;

public class UpdateTable {
    
    private int max = MSQL.MAX_COLUMNS;
    private String table = "none";
    private String[] columns = new String[max];
    private String[] types = new String[max];
    private int[] typesLength = new int[max];
    private boolean[] nulls = new boolean[max];
    private String[] pks = new String[max];
    private QString[] fks = new QString[max];
    private String[] fkFormed = new String[max];
    private String[] defaults = new String[max];
    private int extra = -1;

    private String[] dist = new String[max];
    private String[] imageC = new String[max];
    private String[] imageCPath = new String[max];
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

    public QString[] getFks() {
        return fks;
    }

    public void setFks(QString[] fks) {
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
    public String[] getFkFormed() {
        return fkFormed;
    }
    public void setFkFormed(String[] fkFormed) {
        this.fkFormed = fkFormed;
    }

    public String[] getDist() {
        return dist;
    }

    public void setDist(String[] dist) {
        this.dist = dist;
    }

    public String[] getImageC() {
        return imageC;
    }

    public void setImageC(String[] imageC) {
        this.imageC = imageC;
    }

    public String[] getImageCPath() {
        return imageCPath;
    }

    public void setImageCPath(String[] imageCPath) {
        this.imageCPath = imageCPath;
    }

    
}
