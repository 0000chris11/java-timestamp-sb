package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.myInterfaces.IActions;

public class SelectTableDefault implements IActions {

    @Override
    public void beforeQuery() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        Users.getInstance().getCurrenUser().getCurrentDatabase().setCurrentTable(
                new Table(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        // TODO Auto-generated method stub
    }

}
