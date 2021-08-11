package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

public class User {

    private int id;
    private String name;

    private List<Database> databases = new ArrayList<>();
    private Database currentDatabase;

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

    // --------------------------------------------
    public User(int id, String name) {
        this.id = id;
        this.name = name;
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

}
