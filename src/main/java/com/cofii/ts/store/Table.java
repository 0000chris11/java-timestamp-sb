package com.cofii.ts.store;

public class Table {

    private int id;
    private String name;
    private String dist;

    public Table(int id, String name, String dist) {
        this.id = id;
        this.name = name;
        this.dist = dist;
    }

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
    
}
