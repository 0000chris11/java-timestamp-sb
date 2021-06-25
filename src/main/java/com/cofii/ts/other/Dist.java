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
import com.cofii2.methods.MString;
import com.cofii2.mysql.MSQLP;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class Dist {

    private static VFController vf;
    private ColumnS columns = ColumnS.getInstance();
    private ColumnDS columnsd = ColumnDS.getInstance();
    // private Keys keys = Keys.getInstance();

    private List<String> imageCFiles = new ArrayList<>();
    private List<String> imageCFilesPath = new ArrayList<>();
    private MSQLP ms;

    // -----------------------------------------------------
    private void dist() {
        String dist = MSQL.getCurrentTable().getDist();
        ms = vf.getMs();

        int length = dist.length();
        int p = 5;

        GridPane gp = vf.getGridPane();
        while (p <= length) {
            int c = Character.getNumericValue(dist.charAt(p - 1)) - 1;
            vf.getTfsPs()[c].setTfParent(vf.getTfs()[c]);
            vf.getTfs()[c].setStyle(CSS.TFAS_DEFAULT_LOOK);

            if (columns.getExtraAsString(c).equals("auto_increment")) {
                vf.getTfs()[c].setPromptText("AUTO_INCREMENT");
            }

            columnsd.getList().get(c).setDist("Yes");
            // --------------------------------------------------
            String table = MSQL.getCurrentTable().getName().replace(" ", "_");
            String column = columns.getColumn(c);

            ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
            ms.selectDistinctColumn(table, column.replace(" ", "_"), new SelectDistinct(vf, c));
            p += 2;
        }
        gp.getRowConstraints().get(4).setMaxHeight(Short.MAX_VALUE);
    }

    private void imageC() {
        String imageC = MSQL.getCurrentTable().getImageC();
        String imageCPath = MSQL.getCurrentTable().getImageCPath();

        if (!imageCPath.equals("NONE")) {
            int index = Character.getNumericValue(imageC.charAt(1)) - 1;
            columnsd.getList().get(index).setImageC("Yes");
            columnsd.getList().get(index).setImageCPath(imageCPath);

            vf.getSplitLeft().setDividerPositions(0.6);
            File imageCDirectory = new File(imageCPath);

            vf.getFpImages().getChildren().clear();
            if (imageCDirectory.exists()) {
                vf.getFpImages().getChildren().add(vf.getIvImageC()[0]);
                imageCFilesPath.clear();
                imageCFiles.clear();

                if (imageCDirectory.isDirectory()) {
                    for (File file : imageCDirectory.listFiles()) {
                        imageCFilesPath.add(file.getPath());
                        imageCFiles.add(MString.getRemoveCustomFormattedString(file.getName()));
                    }
                }
            } else {
                // GET HBOX of imageView TO REPLACED WITH 'path to ImageC not found'
                vf.getFpImages().getChildren().add(new Label("Path '" + imageCPath + "' not found"));
            }
        } else {
            vf.getSplitLeft().setDividerPositions(1.0);
        }
    }

    public void distStart() {
        columnsd.clear();
        for (int a = 0; a < columns.size(); a++) {
            columnsd.addColumnD(new ColumnD());
        }

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
    // -------------------------------------------------------

    public List<String> getImageCFiles() {
        return imageCFiles;
    }

    public void setImageCFiles(List<String> imageCFiles) {
        this.imageCFiles = imageCFiles;
    }

    public List<String> getImageCFilesPath() {
        return imageCFilesPath;
    }

    public void setImageCFilesPath(List<String> imageCFilesPath) {
        this.imageCFilesPath = imageCFilesPath;
    }

}
