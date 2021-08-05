package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

public class Users {

    private List<User> usersList = new ArrayList<>();
    private User currenUser;
    //---------------------------------------
    public void clearUsers(){
        usersList.clear();
    }
    public void addUser(User user){
        usersList.add(user);
    }
    //INSTANCE------------------------------
    private static Users instance;
    public static Users getInstance(){
        if(instance == null){
            instance = new Users();
        }
        return instance;
    }
    //GETTER & SETTERS----------------------------
    public List<User> getUsers() {
        return usersList;
    }
    public void setUsers(List<User> users) {
        this.usersList = users;
    }
    public User getCurrenUser() {
        return currenUser;
    }
    public void setCurrenUser(User currenUser) {
        this.currenUser = currenUser;
    }
    
}
