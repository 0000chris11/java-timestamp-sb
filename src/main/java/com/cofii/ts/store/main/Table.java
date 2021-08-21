package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

import com.cofii.ts.sql.MSQL;

public class Table {

    private int id;
    private String name;

    private List<Column> columns = new ArrayList<>(MSQL.MAX_COLUMNS);
    private int extra = -1;

    private String dist = "NONE";
    private List<Boolean> distYN;

    private String imageC = "NONE";
    private String imageCPath = "NONE";
    //-------------------------------------------------
    private int max = MSQL.MAX_COLUMNS;
    // GETS -------------------------------------------------
    public int getColumnIndex(String columnName){
        int index = -1;
        for(int a = 0;a < columns.size(); a++){
            if(columns.get(a).getName().equals(columnName)){
                index = a;
                break;
            }
        }
        return index;
    }
    public List<String> getColumnNames(){
        List<String> columnNames = new ArrayList<>(max);
        for(Column col : columns){
            columnNames.add(col.getName());
        }
        return columnNames;
    }
    public List<String> getColumnTypes(){
        List<String> columnTypes = new ArrayList<>(max);
        for(Column col : columns){
            columnTypes.add(col.getType());
        }
        return columnTypes;
    }
    public List<Integer> getColumnTypeLengths(){
        List<Integer> columnTypeLengths = new ArrayList<>(max);
        for(Column col : columns){
            columnTypeLengths.add(col.getTypeLength());
        }
        return columnTypeLengths;
    }
    public List<Boolean> getColumnNulls(){
        List<Boolean> columnNulls = new ArrayList<>(max);
        for(Column col : columns){
            columnNulls.add(col.getNulll());
        }
        return columnNulls;
    }
    public List<String> getColumnDefaults(){
        List<String> columnDefaults = new ArrayList<>(max);
        for(Column col : columns){
            columnDefaults.add(col.getDefaultt());
        }
        return columnDefaults;
    }
    public List<Boolean> getDistList(){
        List<Boolean> columnDists = new ArrayList<>(max);
        for(Column col : columns){
            columnDists.add(col.getDist());
        }
        return columnDists;
    }
    public List<Boolean> getImageCList(){
        List<Boolean> columnImageCS = new ArrayList<>(max);
        for(Column col : columns){
            columnImageCS.add(col.getImageC());
        }
        return columnImageCS;
    }
    //------------------------------------------------
    public void setDistYN(){
        distYN = new ArrayList<>();
    }
    //-------------------------------------------------
    public Table(int id, String name, String dist, String imageC, String imageCPath) {
        this.id = id;
        this.name = name;
        this.dist = dist;
        this.imageC = imageC;
        this.imageCPath = imageCPath;
    }
    //-------------------------------------------------
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getImageC() {
        return imageC;
    }

    public void setImageC(String imageC) {
        this.imageC = imageC;
    }

    public String getImageCPath() {
        return imageCPath;
    }

    public void setImageCPath(String imageCPath) {
        this.imageCPath = imageCPath;
    }
    public List<Column> getColumns() {
        return columns;
    }
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
    public List<Boolean> getDistYN() {
        return distYN;
    }
    public void setDistYN(List<Boolean> distYN) {
        this.distYN = distYN;
    }
    public int getExtra() {
        return extra;
    }
    public void setExtra(int extra) {
        this.extra = extra;
    }
    
}
