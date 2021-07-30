package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.sql.MSQL;
import com.cofii2.stores.QString;

public class FKS {

    private List<FK> fksList = new ArrayList<>();

    // -------------------------------------------
    public void addFK(FK fk) {
        String database = fk.getDatabase();
        String table = fk.getTable();
        String referencedDatabase = fk.getReferencedDatabase();
        String referencedTable = fk.getReferencedTable();

        int[] indexs = { -1 };
        boolean exist = fksList.stream().anyMatch(e -> {
            indexs[0]++;
            String tdb = e.getDatabase();
            String ttable = e.getTable();
            return tdb.equals(database) && ttable.equals(table)
                    && (e.getReferencedDatabase().equals(referencedDatabase)
                            && e.getReferencedTable().equals(referencedTable));
        });
        if (!exist) {
            fksList.add(fk);
        } else {
            fk.getColumns().forEach(is -> fksList.get(indexs[0]).getColumns().add(is));
            fk.getReferencedColumns().forEach(s -> fksList.get(indexs[0]).getReferencedColumns().add(s));
        }
    }

    public void clearFKS() {
        fksList.clear();
    }

    public int size() {
        return fksList.size();
    }

    public FK[] getCurrentTableFKS() {
        return fksList.stream().filter(e -> e.getDatabase().equals(MSQL.getDatabase())
                && e.getTable().equals(MSQL.getCurrentTable().getName().replace(" ", "_"))).toArray(FK[]::new);
    }

    public String[] getYesAndNoFKS() {
        String[] fks = new String[MSQL.getCurrentTable().getColumns().size()];
        Arrays.fill(fks, "No");
        FK[] cfks = getCurrentTableFKS();
        for (int a = 0; a < cfks.length; a++) {
            cfks[a].getColumns().forEach(is -> fks[is.index - 1] = "Yes");
        }
        return fks;
        /**
         * QString[] fks = new QString[ColumnS.getInstance().size()]; //NULL FILL
         * 
         * Key[] currentKeys = getCurrentTableKeys(); for(int a = 0;a <
         * currentKeys.length; a++){ int ordinalPosition =
         * currentKeys[a].getOrdinalPosition() - 1; String columnName =
         * currentKeys[a].getColumnName();
         * 
         * String databaseR = currentKeys[a].getReferencedTableSchema(); String tableR =
         * currentKeys[a].getReferencedTableName(); String columnR =
         * currentKeys[a].getReferencedColumnName();
         * 
         * String contraintType = currentKeys[a].getConstraintType();
         * if(contraintType.equals("FOREIGN KEY")){ fks[ordinalPosition] = new
         * QString(columnName, databaseR, tableR, columnR); } } return fks;
         */
    }

    public String getConstraintName(String database, String table, int ordinalPosition) {
        String[] constraintName = { null };
        fksList.forEach(fk -> {
            if (fk.getDatabase().equals(database) && fk.getTable().equals(table)) {
                fk.getColumns().forEach(is -> {
                    if (is.index - 1 == ordinalPosition) {
                        constraintName[0] = fk.getConstraint();
                    }
                });
            }
        });
        return constraintName[0];
    }

    // -------------------------------------------
    private static FKS instance;

    public static FKS getInstance() {
        if (instance == null) {
            instance = new FKS();
        }
        return instance;
    }
    // -------------------------------------------

    public List<FK> getFksList() {
        return fksList;
    }

    public void setFksList(List<FK> fksList) {
        this.fksList = fksList;
    }
    
}
