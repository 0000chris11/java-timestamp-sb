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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Users {

    private List<User> usersList = new ArrayList<>();
    private User currentUser;
    private ObjectProperty<User> defaultUserProperty = new SimpleObjectProperty<>(null);

    private List<Database> allDatabasesList = new ArrayList<>();

    public static final String DEFAULT_RESOURCE = "/com/cofii/ts/login/defaults.xml";

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
    public void updateDefaultUser(boolean setCurrents) {
        new ResourceXML(DEFAULT_RESOURCE, ResourceXML.UPDATE_XML, doc -> {
            Element currentUserElement = (Element) doc.getDocumentElement().getElementsByTagName("currentUser").item(0);
            if (Users.getInstance().getCurrenUser() != null) {
                System.out.println(
                        "\tcurrent Database 1: " + Users.getInstance().getCurrenUser().getCurrentDatabase() != null
                                ? "YES"
                                : "NO");
            }
            int defaultUserId = Integer.parseInt(currentUserElement.getAttributes().item(0).getTextContent());// DEFAULT
            // currentUserElement.getAttributes().item(0).setTextContent(Integer.toString(defaultUserId));//
            // CURRENT ID

            if (defaultUserId > 0) {
                defaultUserProperty.setValue(new User(defaultUserId, getUser(defaultUserId).getName()));
                if (setCurrents) {
                    currentUser = defaultUserProperty.getValue();
                }
                if (Users.getInstance().getCurrenUser() != null) {
                    System.out.println(
                            "\tcurrent Database 2: " + Users.getInstance().getCurrenUser().getCurrentDatabase() != null
                                    ? "YES"
                                    : "NO");
                }

                ms.selectDataWhere(MSQL.TABLE_USER_DEFAULTS, "user_id", defaultUserId > 0 ? defaultUserId : null,
                        (rs, v, ex) -> {
                            if (v) {
                                int defaultDatabaseId = rs.getInt(2);
                                String defaultTableName = rs.getString(3);

                                if (defaultDatabaseId > 0 && setCurrents && !User.getDatabases().isEmpty()
                                        && !Database.getTables().isEmpty()) {
                                    User.setDefaultDatabaseById(defaultDatabaseId);

                                    Database.setDefaultTableByName(defaultTableName);
                                    getCurrenUser().getCurrentDatabase().setCurrentTable(Database.getDefaultTable());
                                }
                                currentUserElement.getElementsByTagName("database").item(0).getAttributes().item(0)
                                        .setTextContent(Integer.toString(defaultDatabaseId));
                                currentUserElement.getElementsByTagName("table").item(0).getAttributes().item(0)
                                        .setTextContent(defaultTableName);
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

    public void defaultUserChange(ObservableValue<? extends User> obs, User oldValue, User newValue) {
        new ResourceXML(DEFAULT_RESOURCE, ResourceXML.UPDATE_XML, doc -> {
            Element currentUserElement = (Element) doc.getDocumentElement().getElementsByTagName("currentUser").item(0);

            int defaultUserId = newValue != null ? newValue.getId() : 0;
            // currentUserElement.getAttributes().item(0).setTextContent(Integer.toString(defaultUserId));//
            // CURRENT ID

            if (defaultUserId > 0) {
                ms.selectDataWhere(MSQL.TABLE_USER_DEFAULTS, "user_id", defaultUserId > 0 ? defaultUserId : null,
                        (rs, v, ex) -> {
                            if (v) {
                                int defaultDatabaseId = rs.getInt(2);
                                String defaultTableName = rs.getString(3);

                                if (defaultDatabaseId > 0) {
                                    User.setDefaultDatabaseById(defaultDatabaseId);
                                    if (defaultTableName != null) {
                                        Database.setDefaultTableByName(defaultTableName);
                                    }
                                }

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

    // CREATE DIFFERENT METHOD TO STAR THIS PROPERTIES
    public void startDefaultProperty(MSQLP ms) {
        this.ms = ms;
        updateDefaultUser(true);
        // LISTENERS--------------
        defaultUserProperty.addListener(this::defaultUserChange);
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

    public List<Database> getAllDatabasesList() {
        return allDatabasesList;
    }

    public void setAllDatabasesList(List<Database> allDatabasesList) {
        this.allDatabasesList = allDatabasesList;
    }

    public User getDefaultUser() {
        return defaultUserProperty.getValue();
    }

    public void setDefaultUser(User user) {
        this.defaultUserProperty.setValue(user);
    }

}
