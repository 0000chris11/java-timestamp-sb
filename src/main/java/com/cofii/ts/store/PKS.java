package com.cofii.ts.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.sql.MSQL;

public class PKS {

    private List<PK> pksList = new ArrayList<>();

    // --------------------------------------------
    public void addPK(PK pk) {
        String database = pk.getDatabase();
        String table = pk.getTable();

        int[] indexs = { -1 };
        boolean exist = pksList.stream().anyMatch(e -> {
            indexs[0]++;
            return e.getDatabase().equalsIgnoreCase(database) && e.getTable().equalsIgnoreCase(table);
        });
        if (!exist) {
            pksList.add(pk);
        } else {
            pk.getColumns().forEach(is -> pksList.get(indexs[0]).getColumns().add(is));
        }
    }

    public void clearPKS() {
        pksList.clear();
    }

    public PK[] getCurrentTablePKS() {
        return pksList.stream().filter(e -> e.getDatabase().equalsIgnoreCase(MSQL.getDatabase())
                && e.getTable().equalsIgnoreCase(MSQL.getCurrentTable().getName().replace(" ", "_"))).toArray(PK[]::new);
    }

    public String[] getYesAndNoPKS() {
        String[] pks = new String[MSQL.getCurrentTable().getColumns().size()];
        Arrays.fill(pks, "No");
        PK[] cpks = getCurrentTablePKS();
        for (int a = 0; a < cpks.length; a++) {
            cpks[a].getColumns().forEach(is -> pks[is.index - 1] = "Yes");
        }
        return pks;
    }

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
