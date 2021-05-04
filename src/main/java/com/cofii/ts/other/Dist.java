package com.cofii.ts.other;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDistinct;
import com.cofii.ts.store.ColumnD;
import com.cofii.ts.store.ColumnDS;
import com.cofii.ts.store.ColumnS;
import com.cofii2.mysql.MSQLP;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class Dist {

    private static VFController vf;
    private ColumnS columns = ColumnS.getInstance();
    private ColumnDS columnsd = ColumnDS.getInstance();
    private MSQLP ms;

    // -----------------------------------------------------
    public void distInitOldWay(String dist) {
        ms = vf.getMs();

        int length = dist.length();
        int p = 5;
        columnsd.clear();
        for (int a = 0; a < columns.size(); a++) {
            columnsd.addColumnD(new ColumnD());
        }
        // X2: 3_4 :: 7
        //TEST
        GridPane gp = vf.getGridPane();
        RowConstraints row4 = gp.getRowConstraints().get(3);
        RowConstraints row5 = gp.getRowConstraints().get(4);

        Button btn5 = vf.getBtns()[4];
        while (p <= length) {
            int c = Character.getNumericValue(dist.charAt(p - 1)) - 1;
            if (vf.getTfas()[c].isNeedsLayout()) {
                gp.getChildren().remove(vf.getTfs()[c]);
                gp.add(vf.getTfas()[c], 1, c);

                //gp.getRowConstraints().get(c).setPrefHeight(180);
                if (gp.getRowConstraints().get(c).getValignment() == null || gp.getRowConstraints().get(c).getValignment() == VPos.CENTER) {
                    System.out.println("changing vpos");
                    gp.getRowConstraints().get(c).setValignment(VPos.TOP);
                }

                GridPane.setMargin(vf.getLbs()[c], new Insets(4, 0, 0, 0));
                GridPane.setMargin(vf.getTfas()[c], new Insets(4, 0, 0, 0));
                GridPane.setMargin(vf.getBtns()[c], new Insets(4, 0, 0, 0));

                gp.getRowConstraints().get(c).setVgrow(Priority.ALWAYS);
            }else{
                gp.getRowConstraints().get(c).setVgrow(Priority.NEVER);
            }

            columnsd.addColumnD(c, new ColumnD("Yes"));
            // --------------------------------------------------
            String table = MSQL.getCurrentTable().getName().replace(" ", "_");
            String column = columns.getColumn(c);

            ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
            ms.selectDistinctColumn(table, column, new SelectDistinct(vf, c));
            p += 2;
        }
        System.out.println("lb 4 padding: " + vf.getLbs()[3].getPadding());
        System.out.println("lb 5 padding: " + vf.getLbs()[4].getPadding());
        System.out.println("lb 4" + vf.getLbs()[3].getAlignment());
        System.out.println("\trow 4 padding (BEFORE): " + gp.getVgap());
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

    // -----------------------------------------------------
    private static Dist instance;

    public static Dist getInstance(VFController vf) {
        Dist.vf = vf;
        if (instance == null) {
            instance = new Dist();
        }
        return instance;
    }

    private Dist() {

    }
}
