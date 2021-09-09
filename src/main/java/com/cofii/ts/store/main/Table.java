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
    //IMAGECS------------------------------
    private String imageCColumnName = "NONE";
    private int imageCLength = 0;
    private String displayOrder = "Ascended";
    private String imageType = "File";
    private String[] imageCPaths = null;
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
    public Table(int id, String name, String dist) {
        this.id = id;
        this.name = name;
        this.dist = dist;
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

    public String getImageCColumnName() {
        return imageCColumnName;
    }

    public void setImageCColumnName(String imageCColumnName) {
        this.imageCColumnName = imageCColumnName;
    }

    public String[] getImageCPaths() {
        return imageCPaths;
    }

    public void setImageCPaths(String[] imageCPaths) {
        this.imageCPaths = imageCPaths;
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
    public int getImageCLength() {
        return imageCLength;
    }
    public void setImageCLength(int imageCLength) {
        this.imageCLength = imageCLength;
    }
    public String getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }
    public String getImageType() {
        return imageType;
    }
    public void setImageType(String image_type) {
        this.imageType = image_type;
    }
    
}
