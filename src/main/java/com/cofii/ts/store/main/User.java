package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

import com.cofii2.xml.ResourceXML;

import org.w3c.dom.Element;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class User {

    private int id;
    private String name;

    private List<Database> databases = new ArrayList<>();
    private Database currentDatabase;

    private ObjectProperty<Database> defaultDatabaseProperty = new SimpleObjectProperty<>(null);

    // --------------------------------------------
    public void clearDatabases() {
        databases.clear();
    }

    public void addDatabase(Database database) {
        databases.add(database);
    }

    public String getDatabaseName(int id) {
        return databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0].getName();
    }
    public String[] getDatabasesNames(){
        List<String> databasesNames = new ArrayList<>();
        for(Database database : databases){
            databasesNames.add(database.getName());
        }
        return databasesNames.toArray(new String[databasesNames.size()]);
    }
    public Database getDatabase(int id){
        return databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0];
    }

    public void setDefaultDatabaseById(int id){
        this.defaultDatabaseProperty.setValue(databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0]);
    }
    public void setCurrentDatabaseById(int id){
        this.currentDatabase = databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0];
    }
    // INIT --------------------------------------------
    public void defaultDatabaseChange(ObservableValue<? extends Database> obs, Database oldValue, Database newValue) {
        new ResourceXML(Users.getInstance().getDefaultResource(), ResourceXML.UPDATE_XML, doc -> {
            Element currentUserElement = (Element) doc.getDocumentElement().getElementsByTagName("currentUser").item(0);

            int defaultDatabaseId = newValue.getId();
            currentUserElement.getElementsByTagName("database").item(0).getAttributes().item(0)
                    .setTextContent(Integer.toString(defaultDatabaseId));

            return doc;
        });
    }
    public User(int id, String name) {
        this.id = id;
        this.name = name;

        defaultDatabaseProperty.addListener(this::defaultDatabaseChange);
    }

    // GETTERS && SETTERS-----------------------------------------------
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

    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }

    public Database getCurrentDatabase() {
        return currentDatabase;
    }

    public void setCurrentDatabase(Database currentDatabase) {
        this.currentDatabase = currentDatabase;
    }

    public Database getDefaultDatabase() {
        return defaultDatabaseProperty.getValue();
    }

    public void setDefaultDatabase(Database defaultDatabase) {
        this.defaultDatabaseProperty.setValue(defaultDatabase);
    }

}
