package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

public class Keys {

    private List<Key> keys = new ArrayList<>();
    //-------------------------------------------
    public void clearKeys(){
        keys.clear();
    }
    public void addKey(Key key){
        keys.add(key);
    }
    //-------------------------------------------
    private static Keys instance;
    public static Keys getInstance(){
        if(instance == null){
            instance = new Keys();
        }
        return instance;
    }
    private Keys(){

    }
}
