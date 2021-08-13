package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

public class Users {

    private List<User> usersList = new ArrayList<>();
    private User currenUser;
    private User defaultUser;

    private final String defaultResource = "/com/cofii/ts/login/defaults.xml";
    //---------------------------------------
    public void clearUsers(){
        usersList.clear();
    }
    public void addUser(User user){
        usersList.add(user);
    }
    public User getUser(final int id){
        return usersList.stream().filter(u -> u.getId() == id).toArray(size -> new User[size])[0];
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
    public String getDefaultResource() {
        return defaultResource;
    }
    public User getDefaultUser() {
        return defaultUser;
    }
    public void setDefaultUser(User defaultUser) {
        this.defaultUser = defaultUser;
    }
    
}
