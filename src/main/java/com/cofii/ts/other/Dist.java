package com.cofii.ts.other;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDistinct;
import com.cofii.ts.store.ColumnD;
import com.cofii.ts.store.ColumnDS;
import com.cofii.ts.store.ColumnS;
import com.cofii2.mysql.MSQLP;

public class Dist {

    private static VFController vf;
    private ColumnS columns = ColumnS.getInstance();
    private ColumnDS columnsd = ColumnDS.getInstance();
    private MSQLP ms;
    //-----------------------------------------------------
    public void distInitOldWay(String dist) {
        ms = vf.getMs();

        int length = dist.length();
        int p = 5;
        columnsd.clear();
        for (int a = 0; a < columns.size(); a++) {
            columnsd.addColumnD(new ColumnD());
        }
        // X2: 3_4 :: 7
        while (p <= length) {
            int c = Character.getNumericValue(dist.charAt(p - 1)) - 1;
            vf.getGridPane().getChildren().remove(vf.getTfs()[c]);
            vf.getGridPane().add(vf.getCbs()[c], 1, c);

            columnsd.addColumnD(c, new ColumnD("Yes"));
            // --------------------------------------------------
            String table = MSQL.getCurrentTable().getName().replace(" ", "_");
            String column = columns.getColumn(c);

            ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
            ms.selectDistinctColumn(table, column, new SelectDistinct(vf, c));
            p += 2;
        }
    }
    
    public void distAction() {
        for (int a = 0; a < columnsd.size(); a++) {
            String dist = columnsd.getDist(a);
            if (dist.equals("Yes")) {
                String table = MSQL.getCurrentTable().getName().replace(" ", "_");
                String column = columns.getColumn(a);
                ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
                ms.selectDistinctColumn(table, column, new SelectDistinct(vf, a));
            }
        }
    }
    //-----------------------------------------------------
    private static Dist instance;
    public static Dist getInstance(VFController vf){
        Dist.vf = vf;
        if(instance == null){
            instance = new Dist();
        }
        return instance;
    }
    private Dist(){

    }
}
