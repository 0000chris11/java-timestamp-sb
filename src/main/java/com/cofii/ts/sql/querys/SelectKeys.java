package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.store.Key;
import com.cofii.ts.store.Keys;
import com.cofii2.myInterfaces.IActions;

public class SelectKeys implements IActions {

    private Keys keys = Keys.getInstance();

    @Override
    public void beforeQuery() {
        keys.clearKeys();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String tableName = rs.getString(1);
        String constraintType = rs.getString(2);
        int ordinalPosition = rs.getInt(3);
        String columnName = rs.getString(4);
        String referencedTableName = rs.getString(5);
        String referencedColumnName = rs.getString(6);

        keys.addKey(new Key(tableName, constraintType, ordinalPosition, columnName, referencedTableName, referencedColumnName));
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        // TODO Auto-generated method stub

    }

}
