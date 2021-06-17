package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

import com.cofii.ts.sql.MSQL;
import com.cofii2.stores.QString;

public class UpdateTable {
    
    private int max = MSQL.MAX_COLUMNS;
    private int rowLength = 2;

    private String table = "none";
    private List<String> columns = new ArrayList<>(max);
    private List<String> types = new ArrayList<>(max);
    private List<Integer> typesLength = new ArrayList<>(max);
    private List<Boolean> nulls = new ArrayList<>(max);
    private List<String> pks = new ArrayList<>(max);
    private List<QString> fks = new ArrayList<>(max);
    private List<String> fkFormed = new ArrayList<>(max);
    private List<String> defaults = new ArrayList<>(max);
    private int extra = -1;

    private List<String> dist = new ArrayList<>(max);
    private List<String> imageC = new ArrayList<>(max);
    private List<String> imageCPath = new ArrayList<>(max);
    //----------------------------------------------------------------------------
    public int getRowLength() {
        return rowLength;
    }
    public void setRowLength(int rowLength) {
        this.rowLength = rowLength;
    }
    public String getTable() {
        return table;
    }
    public void setTable(String table) {
        this.table = table;
    }
    public List<String> getColumns() {
        return columns;
    }
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
    public List<String> getTypes() {
        return types;
    }
    public void setTypes(List<String> types) {
        this.types = types;
    }
    public List<Integer> getTypesLength() {
        return typesLength;
    }
    public void setTypesLength(List<Integer> typesLength) {
        this.typesLength = typesLength;
    }
    public List<Boolean> getNulls() {
        return nulls;
    }
    public void setNulls(List<Boolean> nulls) {
        this.nulls = nulls;
    }
    public List<String> getPks() {
        return pks;
    }
    public void setPks(List<String> pks) {
        this.pks = pks;
    }
    public List<QString> getFks() {
        return fks;
    }
    public void setFks(List<QString> fks) {
        this.fks = fks;
    }
    public List<String> getFkFormed() {
        return fkFormed;
    }
    public void setFkFormed(List<String> fkFormed) {
        this.fkFormed = fkFormed;
    }
    public List<String> getDefaults() {
        return defaults;
    }
    public void setDefaults(List<String> defaults) {
        this.defaults = defaults;
    }
    public int getExtra() {
        return extra;
    }
    public void setExtra(int extra) {
        this.extra = extra;
    }
    public List<String> getDist() {
        return dist;
    }
    public void setDist(List<String> dist) {
        this.dist = dist;
    }
    public List<String> getImageC() {
        return imageC;
    }
    public void setImageC(List<String> imageC) {
        this.imageC = imageC;
    }
    public List<String> getImageCPath() {
        return imageCPath;
    }
    public void setImageCPath(List<String> imageCPath) {
        this.imageCPath = imageCPath;
    }
 
    
}
