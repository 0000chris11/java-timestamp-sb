package com.cofii.ts.store.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cofii.ts.sql.MSQL;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.enums.QueryResult;
import com.cofii2.xml.ResourceXML;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class Users {

    private List<User> usersList = new ArrayList<>();
    private User currentUser;
    private User defaultUser;

    private List<Database> allDatabasesList = new ArrayList<>();

    private final String defaultResource = "/com/cofii/ts/login/defaults.xml";

    private MSQLP ms;

    // ---------------------------------------
    public void clearUsers() {
        usersList.clear();
    }

    public void addUser(User user) {
        usersList.add(user);
    }

    public User getUser(final int id) {
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

    // INIT----------------------------------------
    public void updateUser() {
        new ResourceXML(Users.getInstance().getDefaultResource(), ResourceXML.UPDATE_XML, doc -> {
            Element currentUserElement = (Element) doc.getDocumentElement().getElementsByTagName("currentUser").item(0);
            int defaultUserId = Integer.parseInt(currentUserElement.getAttributes().item(1).getTextContent());// DEFAULT
            currentUserElement.getAttributes().item(0).setTextContent(Integer.toString(defaultUserId));// CURRENT ID

            if (defaultUserId > 0) {
                currentUser = new User(defaultUserId, getUser(defaultUserId).getName());

                ms.selectDataWhere(MSQL.TABLE_USER_DEFAULTS, "user_id", defaultUserId > 0 ? defaultUserId : null,
                        (rs, v, ex) -> {
                            if (v) {
                                int databaseId = rs.getInt(2);
                                String tableName = rs.getString(3);

                                if (databaseId > 0) {
                                    getCurrenUser().setCurrentDatabaseById(databaseId);
                                    getCurrenUser().getCurrentDatabase().setCurrentTableByName(tableName);
                                }
                                currentUserElement.getElementsByTagName("database").item(0).getAttributes().item(0)
                                            .setTextContent(Integer.toString(databaseId));
                                currentUserElement.getElementsByTagName("table").item(0).getAttributes().item(0)
                                        .setTextContent(tableName);
                            }
                        });
            } else {
                currentUserElement.getElementsByTagName("database").item(0).getAttributes().item(0)
                        .setTextContent(Integer.toString(0));
                currentUserElement.getElementsByTagName("table").item(0).getAttributes().item(0).setTextContent("null");
            }
            return doc;
        });
    }

    private Users() {
        updateUser();
    }

    // GETTER & SETTERS----------------------------
    public List<User> getUsers() {
        return usersList;
    }

    public void setUsers(List<User> users) {
        this.usersList = users;
    }

    public User getCurrenUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
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

    public List<Database> getAllDatabasesList() {
        return allDatabasesList;
    }

    public void setAllDatabasesList(List<Database> allDatabasesList) {
        this.allDatabasesList = allDatabasesList;
    }

}
