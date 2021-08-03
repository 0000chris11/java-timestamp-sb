package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.MSQL;
import com.cofii2.methods.MList;
import com.cofii2.myInterfaces.IActions;

public class ShowDatabases implements IActions {

    private VLController vlc;

    public ShowDatabases(VLController vlc){
        this.vlc = vlc;
    }

    @Override
    public void beforeQuery() {
        vlc.getTfDBAC().clearItems();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String db = rs.getString(1);
        //ROOTCONFIG EXIST
        if (db.equalsIgnoreCase(MSQL.ROOT_DB)) {
            MSQL.setDbRootconfigExist(true);
        }
        //ADD TO CBDB
        if(!MList.isOnThisList(MSQL.BAND_DB, db, false)){
            //vlc.getTfDB().getItems().add(db);
            vlc.getTfDBAC().addItem(db);
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(rsValue){
            vlc.getTfDBAC().getLv().getSelectionModel().select(0);
            //MSQL.setDatabases(vlc.getTfDB().getItems().toArray(new String[vlc.getTfDB().getItems().size()]));
            MSQL.setDatabases(vlc.getTfDBAC().getLv().getItems().toArray(new String[vlc.getTfDBAC().getLv().getItems().size()]));
        }
    }

}
