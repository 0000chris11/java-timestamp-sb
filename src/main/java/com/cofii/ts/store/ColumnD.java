package com.cofii.ts.store;

public class ColumnD {

    private String dist = "No";
    private String imageC = "No";
    private String imageCPath = "NONE";
    //-------------------------------------------------
    public ColumnD(String dist, String imageC, String imageCPath){
        this.dist = dist;
        this.imageC = imageC;
        this.imageCPath = imageCPath;
    }
    public ColumnD(){

    }
    //-------------------------------------------------
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
