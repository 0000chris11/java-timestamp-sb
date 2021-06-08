package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to Store all Dist, ImageC and so on...
 */
public class ColumnDS {
    
    private List<ColumnD> list = new ArrayList<>();
    //------------------------------------------
    public void clear(){
        list.clear();
    }
    public int size(){
        return list.size();
    }
    public void addColumnD(ColumnD columnd){
        list.add(columnd);
    }
    public void addColumnD(int index, ColumnD columnd){
        list.add(index, columnd);
    }
    //-----------------------------------------
    public String getDist(int index){
        return list.get(index).getDist();
    }
    public String[] getDists(){
        String[] dists = new String[size()];
        int c = 0;
        for(ColumnD columnd: list){
            dists[c++] = columnd.getDist();
        }
        return dists;
    }
    public String[] getImageCS(){
        String[] imageCS = new String[size()];
        int c = 0;
        for(ColumnD columnd: list){
            imageCS[c++] = columnd.getImageC();
        }
        return imageCS;
    }
    public String[] getImageCPaths(){
        String[] imageCPaths = new String[size()];
        int c = 0;
        for(ColumnD columnd: list){
            imageCPaths[c++] = columnd.getImageCPath();
        }
        return imageCPaths;
    }
    //------------------------------------------
    private static ColumnDS instance;
    public static ColumnDS getInstance(){
        if(instance == null){
            instance = new ColumnDS();
        }
        return instance;
    }

    private ColumnDS(){

    }
    //------------------------------------------
    public List<ColumnD> getList() {
        return list;
    }

}
