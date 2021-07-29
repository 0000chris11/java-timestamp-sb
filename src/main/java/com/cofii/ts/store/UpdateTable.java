package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

import com.cofii.ts.sql.MSQL;

public class UpdateTable {
    
    private int max = MSQL.MAX_COLUMNS;
    private int rowLength = 2;

    private String table = "none";
    private List<String> columns = new ArrayList<>(max);
    private List<String> types = new ArrayList<>(max);
    private List<Integer> typesLength = new ArrayList<>(max);
    private List<Boolean> nulls = new ArrayList<>(max);
    private List<String> pks = new ArrayList<>(max);

    private List<String> fks = new ArrayList<>(max);
    //private List<String> fksConstraint = new ArrayList<>(max);
    private List<String> fkFormed = new ArrayList<>(max);

    private List<Object> defaults = new ArrayList<>(max);
    private int extra = -1;

    private String dist = "NONE";
    private List<Boolean> dists = new ArrayList<>(max);

    private String imageC = "NONE";
    private List<Boolean> imageCS = new ArrayList<>(max);
    private String imageCPath = "NONE";
    //private List<String> imageCPath = new ArrayList<>(max);//DELETE
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
    public List<String> getFks() {
        return fks;
    }
    public void setFks(List<String> fks) {
        this.fks = fks;
    }
    public List<String> getFkFormed() {
        return fkFormed;
    }
    public void setFkFormed(List<String> fkFormed) {
        this.fkFormed = fkFormed;
    }
    public List<Object> getDefaults() {
        return defaults;
    }
    public void setDefaults(List<Object> defaults) {
        this.defaults = defaults;
    }
    public int getExtra() {
        return extra;
    }
    public void setExtra(int extra) {
        this.extra = extra;
    }
    //-------------------------------------------
    public String getDist() {
        return dist;
    }
    public void setDist(String dist) {
        this.dist = dist;
    }
    public List<Boolean> getDists() {
        return dists;
    }
    public void setDists(List<Boolean> dists) {
        this.dists = dists;
    }

    public String getImageC() {
        return imageC;
    }
    public void setImageC(String imageC) {
        this.imageC = imageC;
    }
    public List<Boolean> getImageCS() {
        return imageCS;
    }
    public void setImageCS(List<Boolean> imageCS) {
        this.imageCS = imageCS;
    }
    public String getImageCPath() {
        return imageCPath;
    }
    public void setImageCPath(String imageCPath) {
        this.imageCPath = imageCPath;
    }
    
}
