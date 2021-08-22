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

    private MSQLP ms;

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

    // INIT----------------------------------------
    public static void readDefaultUser() {
        new ResourceXML(DEFAULT_RESOURCE, ResourceXML.UPDATE_XML, doc -> {
            Element currentUserElement = (Element) doc.getDocumentElement().getElementsByTagName("defaultUser").item(0);

            int defaultUserId = Integer.parseInt(currentUserElement.getAttributes().item(0).getTextContent());// DEFAULT

            if (defaultUserId > 0) {
                defaultUser = new User(defaultUserId, getUser(defaultUserId).getName());
            }

            return doc;
        });
    }

    private void userChangeSetDefaults(final int id) {
        new ResourceXML(DEFAULT_RESOURCE, ResourceXML.UPDATE_XML, doc -> {
            Element currentUserElement = (Element) doc.getDocumentElement().getElementsByTagName("defaultUser").item(0);

            ms.selectDataWhere(MSQL.TABLE_USER_DEFAULTS, "user_id", id, (rs, rsValues, ex) -> {
                if (rsValues) {
                    int defaultDatabaseId = rs.getInt(2);
                    String defaultTableId = rs.getString(3);

                    currentUserElement.getElementsByTagName("database").item(0).getAttributes().item(0)
                            .setTextContent(Integer.toString(defaultDatabaseId));
                    currentUserElement.getElementsByTagName("table").item(0).getAttributes().item(0)
                            .setTextContent(defaultTableId);
                }
            });

            Element options = (Element) currentUserElement.getElementsByTagName("options").item(0);
            ms.selectDataWhere(MSQL.TABLE_USER_DEFAULTS_OPTIONS, "id_user", id, (rs, rsValues, ex) -> {
                if (rsValues) {
                    boolean insertClear = rs.getBoolean(2);
                    boolean updateClear = rs.getBoolean(3);

                    options.getElementsByTagName("insertClear").item(0).getAttributes().item(0)
                            .setTextContent(Boolean.toString(insertClear));
                            options.getElementsByTagName("updateClear").item(0).getAttributes().item(0)
                            .setTextContent(Boolean.toString(updateClear));
                }
            });
            // currentUserElement.getAttributes().item(0).setTextContent(Integer.toString(defaultUserId));//

            return doc;
        });
    }

    private Users(){
        currentUser.addListener((obs, oldValue, newValue) -> {
            if(newValue != null && !usersList.isEmpty()){
                userChangeSetDefaults(newValue.getId());
            }
        });
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

    public MSQLP getMs() {
        return ms;
    }

    public void setMs(MSQLP ms) {
        this.ms = ms;
    }
}
