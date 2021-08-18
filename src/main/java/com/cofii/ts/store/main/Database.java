package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

import com.cofii2.xml.ResourceXML;

import org.w3c.dom.Element;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class Database {
    
    private int id;
    private String name;

    private List<Table> tables = new ArrayList<>();
    private Table currentTable;

    private ObjectProperty<Table> defaultTableProperty = new SimpleObjectProperty<>(null);
    //INIT------------------------------------------
    public void defaultTableChange(ObservableValue<? extends Table> obs, Table oldValue, Table newValue) {
        new ResourceXML(Users.getInstance().getDefaultResource(), ResourceXML.UPDATE_XML, doc -> {
            Element currentUserElement = (Element) doc.getDocumentElement().getElementsByTagName("currentUser").item(0);

            String defaultTableName = newValue.getName();
            currentUserElement.getElementsByTagName("table").item(0).getAttributes().item(0)
                    .setTextContent(defaultTableName);
            return doc;
        });
    }

    public Database(int id, String name){
        this.id = id;
        this.name = name;

        defaultTableProperty.addListener(this::defaultTableChange);
    }
    //------------------------------------------
    public void addTable(Table table){
        tables.add(table);
    }

    public void clearTables(){
        tables.clear();
    }

    public int size(){
        return tables.size();
    }
    //------------------------------------------
    public boolean setDefaultTableByName(String name){
        Table[] tablesResult = tables.stream().filter(t -> t.getName().equals(name)).toArray(size -> new Table[size]);
        if(tablesResult.length > 0){
            this.defaultTableProperty.setValue(tablesResult[0]);
            return true;
        }else{
            return false;
        }
    }
    public boolean setCurrentTableByName(String name){
        Table[] tablesResult = tables.stream().filter(t -> t.getName().equals(name)).toArray(size -> new Table[size]);
        if(tablesResult.length > 0){
            this.currentTable = tablesResult[0];
            return true;
        }else{
            return false;
        }
    }

    public String getTableName(int index){
        return tables.get(index).getName();
    }

    public Table getTable(int id){
        return tables.stream().filter(t -> t.getId() == id).toArray(size -> new Table[size])[0];
    }
    public Table getTable(String name){
        return tables.stream().filter(t -> t.getName().equals(name)).toArray(size -> new Table[size])[0];
    }
    
    public String[] getTablesNames(){
        List<String> list = new ArrayList<>();
        for(Table table : tables){
            list.add(table.getName());
        }
        return list.toArray(new String[list.size()]);
    }
    //GETTERS & SETTERS------------------------------------------
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
    public Table getCurrentTable() {
        return currentTable;
    }
    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Table getDefaultTable() {
        return defaultTableProperty.getValue();
    }
    public void setDefaultTable(Table defaultTable) {
        this.defaultTableProperty.setValue(defaultTable);
    }
    public List<Table> getTables() {
        return tables;
    }
    
}
