package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.Key;
import com.cofii.ts.store.Keys;
import com.cofii2.myInterfaces.IActions;

import javafx.scene.text.Text;

public class SelectKeys implements IActions {

    private VFController vf;
    private Keys keys = Keys.getInstance();

    public SelectKeys(VFController vf) {
        this.vf = vf;
    }

    @Override
    public void beforeQuery() {
        keys.clearKeys();
    }

    @Override
    public void setData(ResultSet rs, int row) throws SQLException {
        String databaseName = rs.getString(1);
        String tableName = rs.getString(2);
        String constraintType = rs.getString(3);
        int ordinalPosition = rs.getInt(4);
        String columnName = rs.getString(5);
        String referencedTableName = rs.getString(6);
        String referencedColumnName = rs.getString(7);
        // KEY IMPLEMENT --------------------------------------------
        String currentDatabase = MSQL.getDatabase().toLowerCase();
        String currentTable = MSQL.getCurrentTable().getName().replace(" ", "_").toLowerCase();
        if (databaseName.equals(currentDatabase) && tableName.equals(currentTable)) {
            vf.getLbs()[ordinalPosition - 1].getChildren().clear();
            Text textColumnName = new Text(columnName);
            textColumnName.setFill(NonCSS.TEXT_FILL);

            if (constraintType.equals("PRIMARY KEY")) {
                Text textPk = new Text("(P) ");
                textPk.setFill(NonCSS.TEXT_FILL_PK);

                vf.getLbs()[ordinalPosition - 1].getChildren().addAll(textPk, textColumnName);
            } else if (constraintType.equals("FOREIGN KEY")) {
                Text textFk = new Text("(F) ");
                textFk.setFill(NonCSS.TEXT_FILL_PK);

                vf.getLbs()[ordinalPosition - 1].getChildren().addAll(textFk, textColumnName);
            }
        }
        // --------------------------------------------
        keys.addKey(new Key(databaseName, tableName, constraintType, ordinalPosition, columnName, referencedTableName,
                referencedColumnName));
    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        // TODO Auto-generated method stub

    }

}