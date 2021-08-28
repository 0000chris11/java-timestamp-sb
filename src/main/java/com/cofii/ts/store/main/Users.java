package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

import com.cofii.ts.sql.MSQL;
import com.cofii2.mysql.MSQLP;
import com.cofii2.xml.ResourceXML;

import org.w3c.dom.Element;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class Users {

    private static List<User> usersList = new ArrayList<>();
    private List<Database> allDatabasesList = new ArrayList<>();

    private ObjectProperty<User> currentUser = new SimpleObjectProperty<>(null); 
    private static User defaultUser;

    public static final String DEFAULT_RESOURCE = "/com/cofii/ts/login/defaults.xml";
    // ---------------------------------------
    public void clearUsers() {
        usersList.clear();
    }

    public void addUser(User user) {
        usersList.add(user);
    }

    public static User getUser(final int id) {
        return usersList.stream().filter(u -> u.getId() == id).toArray(size -> new User[size])[0];
    }

    public User getUser(final String name) {
        return usersList.stream().filter(u -> u.getName().equals(name)).toArray(size -> new User[size])[0];
    }

    public void clearDatabases() {
        allDatabasesList.clear();
    }

    public void addDatabase(Database database) {
        allDatabasesList.add(database);
    }

    // INSTANCE------------------------------
    private static Users instance;

    public static Users getInstance() {
        if (instance == null) {
            instance = new Users();
        }
        return instance;
    }

    // GETTER & SETTERS----------------------------
    public List<User> getUsers() {
        return usersList;
    }

    public void setUsers(List<User> users) {
        this.usersList = users;
    }

    public User getCurrenUser() {
        return currentUser.getValue();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser.setValue(currentUser);
    }

    public List<Database> getAllDatabasesList() {
        return allDatabasesList;
    }

    public void setAllDatabasesList(List<Database> allDatabasesList) {
        this.allDatabasesList = allDatabasesList;
    }

    public static User getDefaultUser() {
        return defaultUser;
    }

    public static void setDefaultUser(User defaultUser) {
        Users.defaultUser = defaultUser;
    }

}
