package com.cofii.ts.store.main;

public class Path {

    private int id;
    private String pathName;
    //--------------------------------------
    public Path(int id, String pathName) {
        this.id = id;
        this.pathName = pathName;
    }
    //--------------------------------------
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPathName() {
        return pathName;
    }
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }
    
    
}
