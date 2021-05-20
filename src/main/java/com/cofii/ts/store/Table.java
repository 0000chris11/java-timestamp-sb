package com.cofii.ts.store;

public class Table {

    private int id;
    private String name;
    private String dist;
    private String imageC;
    private String imageCPath;

    public Table(int id, String name, String dist, String imageC, String imageCPath) {
        this.id = id;
        this.name = name;
        this.dist = dist;
        this.imageC = imageC;
        this.imageCPath = imageCPath;
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
    
}
