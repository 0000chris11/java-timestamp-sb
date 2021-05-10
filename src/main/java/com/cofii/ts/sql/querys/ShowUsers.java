package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.MSQL;
import com.cofii2.methods.MList;
import com.cofii2.myInterfaces.IActions;

public class ShowUsers implements IActions{

    private VLController c;

    public ShowUsers(VLController c){
        this.c = c;
    }

    @Override
    public void beforeQuery() {
        c.getCbUser().getItems().clear();
        
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String user = rs.getString(1);
        if(!MList.isOnThisList(MSQL.BAND_USERS, user, false)){
            c.getCbUser().getItems().add(user);
        }
        
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(rsValue){
            c.getCbUser().getSelectionModel().select(0);
        }
        
    }
    
}
