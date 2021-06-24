package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.List;

public class PKS {

    private List<PK> pksList = new ArrayList<>();

    // --------------------------------------------
    public void addPK(PK pk) {
        String database = pk.getDatabase();
        String table = pk.getTable();

        int[] indexs = {-1};
        boolean exist = pksList.stream().anyMatch(e -> {
            indexs[0]++;
            return e.getDatabase().equals(database) && e.getTable().equals(table);
        });
        if (!exist) {
            pksList.add(pk);
        } else {
            pk.getColumns().forEach((i, s) -> pksList.get(indexs[0]).getColumns().put(i, s));
        }
    }

    public void clearPKS() {
        pksList.clear();
    }

    //getCurrentTableKeys
    // --------------------------------------------
    private static PKS instance;

    public static PKS getInstance() {
        if (instance == null) {
            instance = new PKS();
        }
        return instance;
    }
    // --------------------------------------------

    public List<PK> getPksList() {
        return pksList;
    }
}
