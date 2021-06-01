package com.cofii.ts.other;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDistinct;
import com.cofii.ts.store.ColumnD;
import com.cofii.ts.store.ColumnDS;
import com.cofii.ts.store.ColumnS;
import com.cofii.ts.store.Keys;
import com.cofii2.methods.MString;
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
    // private Keys keys = Keys.getInstance();

    private List<String> imageCFiles = new ArrayList<>();

    private MSQLP ms;

    // -----------------------------------------------------
    private void dist() {
        String dist = MSQL.getCurrentTable().getDist();
        ms = vf.getMs();

        int length = dist.length();
        int p = 5;
        columnsd.clear();
        for (int a = 0; a < columns.size(); a++) {
            columnsd.addColumnD(new ColumnD());
        }
        // X2: 3_4 :: 7
        // TEST
        GridPane gp = vf.getGridPane();
        while (p <= length) {
            int c = Character.getNumericValue(dist.charAt(p - 1)) - 1;
            if (vf.getTfas()[c].isNeedsLayout()) {
                gp.getChildren().remove(vf.getTfs()[c]);
                gp.add(vf.getTfas()[c], 1, c);
                if (columns.getExtra(c).equals("auto_increment")) {
                    vf.getTfas()[c].getTf().setPromptText("AUTO_INCREMENT");
                }
            }

            columnsd.addColumnD(c, new ColumnD("Yes"));
            // --------------------------------------------------
            String table = MSQL.getCurrentTable().getName().replace(" ", "_");
            String column = columns.getColumn(c);

            ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
            ms.selectDistinctColumn(table, column, new SelectDistinct(vf, c));
            p += 2;
        }
        gp.getRowConstraints().get(4).setMaxHeight(Short.MAX_VALUE);
    }

    private void imageC() {
        String imageCPath = MSQL.getCurrentTable().getImageCPath();
        File imageCDirectory = new File(imageCPath);
        //imageCFiles = Arrays.asList(imageCDirectory.listFiles()).stream().filter(e -> e.getName()).collect(Collectors.toList());
        for(File file: imageCDirectory.listFiles()){
            imageCFiles.add(MString.getRemoveCustomFormattedString(file.getName()));
        }

        
    }

    public void distStart() {
        dist();
        imageC();
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
    //-------------------------------------------------------

    public List<String> getImageCFiles() {
        return imageCFiles;
    }

    public void setImageCFiles(List<String> imageCFiles) {
        this.imageCFiles = imageCFiles;
    }
    
}
