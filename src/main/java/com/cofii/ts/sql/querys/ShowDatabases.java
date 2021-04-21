package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.sql.MSQL;
import com.cofii2.myInterfaces.IActions;

public class ShowDatabases implements IActions {

    @Override
    public void beforeQuery() {
        // NOT IMPLEMENTED
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String db = rs.getString(1);
        if (db.equalsIgnoreCase(MSQL.ROOT_DB)) {
            MSQL.setDbRootconfigExist(true);
        }

    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        // NOT IMPLEMENTED
    }

}
