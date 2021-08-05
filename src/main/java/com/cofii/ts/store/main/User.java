package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

public class User {
    
    private int id;
    private String name;
    private List<Database> databases = new ArrayList<>();
    //--------------------------------------------
    public User(int id, String name){
        this.id = id;
        this.name = name;
    }
}
