package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VF;
import com.cofii.ts.first.VFController;
import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Users;
import com.cofii2.myInterfaces.IActions;

/**
 * QUERY FROM THE TABLE users_databases
 * 
 * @author C0FII
 */
public class SelectDatabases implements IActions{

    private VFController vfc;
    
    private boolean urrentUserRsValues = false;

    private Users users;
    public SelectDatabases(VFController vfc){
        this.vfc = vfc;
    }
    //-------------------------------
    @Override
    public void beforeQuery() {
        users = Users.getInstance();

        users.clearDatabases();
        users.getCurrenUser().getDatabases().clear();
        vfc.getTfDatabaseAutoC().clearItems();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        int id = rs.getInt(1);
        int idUsers = rs.getInt(2);
        int currentUserId = users.getCurrenUser().getId();
        String database = rs.getString(3);
        
        users.addDatabase(new Database(id, database));

        if(idUsers == currentUserId){
            urrentUserRsValues = true;
            users.getCurrenUser().getDatabases().add(new Database(id, database));
            vfc.getTfDatabaseAutoC().addItem(database);
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(!urrentUserRsValues){
            vfc.getVf().setnoDatabasesForCurrentUser(true);
            vfc.getTfDatabase().setPromptText(VFController.NO_DATABASES);
        }else{
            vfc.getTfDatabase().setPromptText("Databases");
        }
        
    }
    
}
