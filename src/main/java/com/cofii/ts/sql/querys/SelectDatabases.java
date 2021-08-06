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

    private VF vf;
    private VFController vfc;

    private Users users;
    public SelectDatabases(VF vf, VFController vfc){
        this.vf = vf;
        this.vfc = vfc;
    }
    //-------------------------------
    @Override
    public void beforeQuery() {
        users = Users.getInstance();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        int id = rs.getInt(1);
        int idUsers = rs.getInt(2);
        int currentUserId = users.getCurrenUser().getId();
        String database = rs.getString(3);
        
        if(idUsers == currentUserId){
            users.getCurrenUser().addDatabase(new Database(id, database));
            vfc.getTfDatabaseAutoC().addItem(database);
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(!rsValue){
            vf.setNoDatabases(true);
        }
        
    }
    
}
