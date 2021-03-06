package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.sql.MSQL;
import com.cofii2.stores.IntDString;
import com.cofii2.stores.QString;
import com.cofii2.stores.TString;

public class Keys {

    private List<Key> keys = new ArrayList<>();

    // -------------------------------------------
    public void clearKeys() {
        keys.clear();
    }

    public void addKey(Key key) {
        keys.add(key);
    }

    public Key[] getRowPrimaryKeys(){
        List<Key> constraintTypes = new ArrayList<>();
        for(Key key: keys){
            if(key.getConstraintType().equals("PRIMARY KEY")){
                constraintTypes.add(key);
            }
        }
        return constraintTypes.toArray(new Key[constraintTypes.size()]);
    }

    public Key[] getCurrentTableKeys() {
        List<Key> list = new ArrayList<>();
        for (Key key : keys) {
            String currentTable = MSQL.getCurrentTable().getName().replace(" ", "_").toLowerCase();
            String currentDatabase = MSQL.getDatabase().toLowerCase();

            String database = key.getDatabase();
            String table = key.getTableName();
            if (currentDatabase.equals(database) && currentTable.equals(table)) {
                list.add(key);
            }
        }

        return list.toArray(new Key[list.size()]);
    }

    public String[] getPKS(){
        String[] pks = new String[MSQL.getCurrentTable().getColumns().size()];
        Arrays.fill(pks, "No");

        Key[] currentKeys = getCurrentTableKeys();
        for(int a = 0;a < currentKeys.length; a++){
            int ordinalPosition = currentKeys[a].getOrdinalPosition() - 1;
            String contraintType = currentKeys[a].getConstraintType();
            if(contraintType.equals("PRIMARY KEY")){
                pks[ordinalPosition] = "Yes";
            }
        }
        return pks;
    }
    
    public QString[] getFKS(){
        QString[] fks = new QString[MSQL.getCurrentTable().getColumns().size()];
        //NULL FILL

        Key[] currentKeys = getCurrentTableKeys();
        for(int a = 0;a < currentKeys.length; a++){
            int ordinalPosition = currentKeys[a].getOrdinalPosition() - 1;
            String columnName = currentKeys[a].getColumnName();

            String databaseR = currentKeys[a].getReferencedTableSchema();
            String tableR = currentKeys[a].getReferencedTableName();
            String columnR = currentKeys[a].getReferencedColumnName();

            String contraintType = currentKeys[a].getConstraintType();
            if(contraintType.equals("FOREIGN KEY")){
                fks[ordinalPosition] = new QString(columnName, databaseR, tableR, columnR);
            }
        }
        return fks;
    }
    
    public IntDString[] getFKSWithIndex(){
        IntDString[] fks = new IntDString[MSQL.getCurrentTable().getColumns().size()];
        //NULL FILL

        Key[] currentKeys = getCurrentTableKeys();
        for(int a = 0;a < currentKeys.length; a++){
            int ordinalPosition = currentKeys[a].getOrdinalPosition() - 1;
            String databaseR = currentKeys[a].getDatabase();
            
            String tableR = currentKeys[a].getReferencedTableName();
            String columnR = currentKeys[a].getReferencedColumnName();
            String contraintType = currentKeys[a].getConstraintType();
            if(contraintType.equals("FOREIGN KEY")){
                fks[ordinalPosition] = new IntDString(ordinalPosition, tableR, columnR);
            }
        }
        return fks;
    }

    public int getOrdinalPosition(int index) {
        return keys.get(index).getOrdinalPosition();
    }

    // -------------------------------------------
    private static Keys instance;

    public static Keys getInstance() {
        if (instance == null) {
            instance = new Keys();
        }
        return instance;
    }

    private Keys() {

    }
}
