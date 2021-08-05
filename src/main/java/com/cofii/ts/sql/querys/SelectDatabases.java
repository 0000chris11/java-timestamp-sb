package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VF;
import com.cofii.ts.store.main.Users;
import com.cofii2.myInterfaces.IActions;

public class SelectDatabases implements IActions{

    private VF vf;
    private Users users;
    public SelectDatabases(VF vf){
        this.vf = vf;
    }
    //-------------------------------
    @Override
    public void beforeQuery() {
        users = Users.getInstance();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        int id = rs.getInt(1);
        String database = rs.getString(2);
        int currentId = users.getCurrenUser().getId();
        if(id == currentId){
            users.getCurrenUser().addDatabase(new );   
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(!rsValue){
            vf.setNoDatabases(true);
        }
        
    }
    
}
