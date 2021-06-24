package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

public class FKS {

    private List<FK> list = new ArrayList<>();

    // -------------------------------------------
    public void addFK(FK fk) {
        String database = fk.getDatabase();
        String table = fk.getTable();
        String referencedDatabase = fk.getReferencedDatabase();
        String referencedTable = fk.getReferencedTable();

        int[] indexs = { -1 };
        boolean exist = list.stream().anyMatch(e -> {
            indexs[0]++;
            return e.getDatabase().equals(database) && e.getTable().equals(table)
                    && (e.getReferencedDatabase().equals(referencedDatabase)
                            && e.getReferencedTable().equals(referencedTable));
        });
        if (!exist) {
            list.add(fk);
        } else {
            fk.getColumns().forEach((i, s) -> list.get(indexs[0]).getColumns().put(i, s));
            fk.getReferencedColumns().forEach(s -> list.get(indexs[0]).getReferencedColumns().add(s));
        }
    }

    public void clearFKS() {
        list.clear();
    }

    public int size(){
        return list.size();
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
}
