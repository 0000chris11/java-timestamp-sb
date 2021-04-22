package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.MSQL;
import com.cofii2.methods.MList;
import com.cofii2.myInterfaces.IActions;

public class ShowDatabases implements IActions {

    private VLController c;

    public ShowDatabases(VLController c){
        this.c = c;
    }

    @Override
    public void beforeQuery() {
        c.getCbDB().getItems().clear();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String db = rs.getString(1);
        //ROOTCONFIG EXIST
        if (db.equalsIgnoreCase(MSQL.ROOT_DB)) {
            MSQL.setDbRootconfigExist(true);
        }
        //ADD TO CBDB
        if(!MList.isOnThisArray(MSQL.BAND_DB, db)){
            c.getCbDB().getItems().add(db);
        }
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if(rsValue){
            c.getCbDB().getSelectionModel().select(0);
        }
    }

}
