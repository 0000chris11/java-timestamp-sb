package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cofii2.xml.ResourceXML;

import org.w3c.dom.Element;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class User {

    private int id;
    private String name;

    private static final List<Database> databases = new ArrayList<>();
    private final List<PK> pks = new ArrayList<>();
    private final List<FK> fks = new ArrayList<>();

    private final List<Path> paths = new ArrayList<>();

    private Database currentDatabase;
    private static ObjectProperty<Database> defaultDatabaseProperty = new SimpleObjectProperty<>(null);

    // DATABASE--------------------------------------------
    public String getDatabaseNameById(int id) {
        return databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0].getName();
    }

    public String[] getDatabasesNames() {
        List<String> databasesNames = new ArrayList<>();
        for (Database database : databases) {
            databasesNames.add(database.getName());
        }
        return databasesNames.toArray(new String[databasesNames.size()]);
    }

    public Database getDatabaseById(int id) {
        return databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0];
    }

    public static void setDefaultDatabaseById(int id) {
        User.defaultDatabaseProperty
                .setValue(databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0]);
    }

    public void setCurrentDatabaseById(int id) {
        this.currentDatabase = databases.stream().filter(d -> d.getId() == id).toArray(size -> new Database[size])[0];
    }

    // KEYS-------------------------------------------------
    /**
     * Get all primary keys (user level) in a sinle or mix primary key
     * 
     * @return A map of each sinle or mix primary key
     */
    public Map<String, List<PK>> getPKSInGroups() {
        System.out.println("TEST getPKSInGroups °°°°°°°°°°°°°°°°°°°°°°°");
        Map<String, List<PK>> map = new HashMap<>(pks.size());
        pks.forEach(pk -> {
            String databaseName = pk.getDatabaseName();
            String tableName = pk.getTableName();
            String mix = databaseName + "." + tableName;

            if(!map.containsKey(mix)){
                List<PK> newPk = new ArrayList<>();
                newPk.add(pk);
                map.put(mix, newPk);
            }else{
                List<PK> aPk = map.get(mix);
                aPk.add(pk);
                map.put(mix, aPk);
            }
        });

        return map;
    }

    public String getConstraintName(String database, String table, int colIndex) {
        return fks.stream().filter(fk -> fk.getDatabaseName().equals(database) && fk.getTableName().equals(table)
                && fk.getOrdinalPosition() - 1 == colIndex).toArray(s -> new FK[s])[0].getConstraintType();
    }

    // PATH--------------------------------------------------
    public Path getPathById(int id) {
        return paths.stream().filter(path -> path.getId() == id).toArray(s -> new Path[s])[0];
    }

    public String getPathNameById(int id) {
        return paths.stream().filter(path -> path.getId() == id).toArray(s -> new Path[s])[0].getPathName();
    }

    public int getPathIdByName(String name) {
        return paths.stream().filter(path -> path.getPathName().equals(name)).toArray(s -> new Path[s])[0].getId();
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

    public List<Path> getPaths() {
        return paths;
    }

    public List<PK> getPks() {
        return pks;
    }

    public List<FK> getFks() {
        return fks;
    }

}
