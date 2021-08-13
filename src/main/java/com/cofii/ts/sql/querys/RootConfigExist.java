package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.MSQL;
import com.cofii2.methods.MList;
import com.cofii2.myInterfaces.IActions;

public class RootConfigExist implements IActions {

    private VLController vlc;

    public RootConfigExist(VLController vlc){
        this.vlc = vlc;
    }

    @Override
    public void beforeQuery() {
        //vlc.getTfDBAC().clearItems();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String db = rs.getString(1);
        //ROOTCONFIG EXIST
        if (db.equalsIgnoreCase(MSQL.ROOT_DB)) {
            MSQL.setDbRootconfigExist(true);
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(rsValue){
            //vlc.getTfDBAC().getLv().getSelectionModel().select(0);
            //MSQL.setDatabases(vlc.getTfDB().getItems().toArray(new String[vlc.getTfDB().getItems().size()]));
            //MSQL.setDatabases(vlc.getTfDBAC().getLv().getItems().toArray(new String[vlc.getTfDBAC().getLv().getItems().size()]));
        }
    }

}
