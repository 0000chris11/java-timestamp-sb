package com.cofii.ts.cu.store;

import com.cofii2.stores.DString;

public class ICStore {
    
    private DString column = null;
    private int imageSize = 0;
    private String[] paths = new String[0];
    //-------------------------------------------------
    private static ICStore instance;
    public static ICStore getInstance(){
        if(instance == null){
            instance = new ICStore();
        }
        return instance;
    }
    //-------------------------------------------------
    public DString getColumn() {
        return column;
    }
    public void setColumn(DString column) {
        this.column = column;
    }
    public int getImageSize() {
        return imageSize;
    }
    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }
    public String[] getPaths() {
        return paths;
    }
    public void setPaths(String[] paths) {
        this.paths = paths;
    }
    
}
