package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.Menus;
import com.cofii.ts.first.VFController;
import com.cofii.ts.store.main.FK;
import com.cofii.ts.store.main.PK;
import com.cofii.ts.store.main.Users;
import com.cofii2.myInterfaces.IActions;


public class SelectKeys implements IActions {

    private VFController vfc;

    private String databaseName;
    private String tableName;
    private String constraintType;
    private int ordinalPosition;
    private String columnName;
    private String referencedTableSchema;
    private String referencedTableName;
    private String referencedColumnName;

    // -----------------------------------------------------------
    public SelectKeys(VFController vf) {
        this.vfc = vf;
    }

    @Override
    public void beforeQuery() {
        Users.getInstance().getCurrenUser().getPks().clear();
        Users.getInstance().getCurrenUser().getFks().clear();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        databaseName = rs.getString(1);
        tableName = rs.getString(2);
        constraintType = rs.getString(3);
        ordinalPosition = rs.getInt(4);
        columnName = rs.getString(5);

        referencedTableSchema = rs.getString(6);
        referencedTableName = rs.getString(7);
        referencedColumnName = rs.getString(8);

        //keysImplement();
        //Menus.getInstance(vfc).resetCurrentTableKeys(vfc.getRows());
        // --------------------------------------------
        if (constraintType.equals("PRIMARY")) {
            //System.out.println("ADDING PRIMARY KEY (" + databaseName + " - " + constraintType + ")");
            //pks.addPK(new PK(databaseName, tableName, ordinalPosition, columnName));
            Users.getInstance().getCurrenUser().getPks().add(new PK(databaseName, tableName, ordinalPosition, columnName));
        } else if (referencedTableSchema != null && referencedTableName != null && referencedColumnName != null) {
            //System.out.println("ADDING FOREING KEY (" + databaseName + " - " + constraintType + ")");
            Users.getInstance().getCurrenUser().getFks().add(new FK(databaseName, tableName, constraintType, ordinalPosition, columnName, referencedTableSchema, referencedTableName, referencedColumnName));
            //fks.addFK(new FK(databaseName, tableName, constraintType, ordinalPosition, columnName,
              //      referencedTableSchema, referencedTableName, referencedColumnName));
        }

    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        // TODO Auto-generated method stub

    }
    // -----------------------------------------------------------
}
