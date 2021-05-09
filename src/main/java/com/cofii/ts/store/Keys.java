package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.sql.MSQL;

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
            String table = key.getTableName();
            if (currentTable.equals(table)) {
                list.add(key);
            }
        }

        return list.toArray(new Key[list.size()]);
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
