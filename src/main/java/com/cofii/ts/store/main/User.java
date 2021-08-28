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

    private static final List<Database> databases = new ArrayList<>();
    private Database currentDatabase;

    private static ObjectProperty<Database> defaultDatabaseProperty = new SimpleObjectProperty<>(null);

    private final List<Option> options = new ArrayList<>();

    private static boolean insertClear = false;
    private static boolean updateClear = false;

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

    public String[] getDatabasesNames() {
        List<String> databasesNames = new ArrayList<>();
        for (Database database : databases) {
            databasesNames.add(database.getName());
        }
        return databasesNames.toArray(new String[databasesNames.size()]);
    }

    public Database getDatabase(int id) {
        return databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0];
    }

    public static void setDefaultDatabaseById(int id) {
        User.defaultDatabaseProperty
                .setValue(databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0]);
    }

    public void setCurrentDatabaseById(int id) {
        this.currentDatabase = databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0];
    }
    // CONSTRUCTORS----------------------------------------
    public User(int id, String name) {
        this.id = id;
        this.name = name;

    }

    /*
     * public static void startDefaultDatabaseProperty(){
     * 
     * // defaultDatabaseProperty.addListener(User::readDefaultDatabase); }
     */
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

    public static List<Database> getDatabases() {
        return databases;
    }

    public Database getCurrentDatabase() {
        return currentDatabase;
    }

    public void setCurrentDatabase(Database currentDatabase) {
        this.currentDatabase = currentDatabase;
    }

    public static Database getDefaultDatabase() {
        return defaultDatabaseProperty.getValue();
    }

    public static void setDefaultDatabase(Database defaultDatabase) {
        User.defaultDatabaseProperty.setValue(defaultDatabase);
    }

    public static boolean getInsertClear() {
        return insertClear;
    }

    public static void setInsertClear(boolean insertClear) {
        User.insertClear = insertClear;
    }

    public static boolean getUpdateClear() {
        return updateClear;
    }

    public static void setUpdateClear(boolean updateClear) {
        User.updateClear = updateClear;
    }

    public List<Option> getOptions() {
        return options;
    }


    
}
