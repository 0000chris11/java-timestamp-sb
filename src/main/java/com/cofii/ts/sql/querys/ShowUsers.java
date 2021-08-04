package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.MSQL;
import com.cofii2.methods.MList;
import com.cofii2.myInterfaces.IActions;

public class ShowUsers implements IActions{

    private VLController vlc;

    public ShowUsers(VLController vlc){
        this.vlc = vlc;
    }

    @Override
    public void beforeQuery() {
        //vlc.getTfUser().getItems().clear();
        vlc.getTfUserAC().clearItems();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String user = rs.getString(2);
        System.out.println("TEST user: " + user);
        if(!MList.isOnThisList(MSQL.BAND_USERS, user, false)){
            vlc.getTfUserAC().addItem(user);
        }
        
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(rsValue){
            vlc.setUserOK(true);
            vlc.getTfUserAC().getLv().getSelectionModel().select(0);
        }
        
    }
    
}
