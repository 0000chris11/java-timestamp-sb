package com.cofii.ts.other;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDistinct;
import com.cofii.ts.store.main.Table;
import com.cofii2.methods.MString;
import com.cofii2.mysql.MSQLP;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Dist {

    private static VFController vf;
    //private ColumnS columns = ColumnS.getInstance();
    //private ColumnDS columnsd = ColumnDS.getInstance();
    // private Keys keys = Keys.getInstance();

    private List<String> imageCFiles = new ArrayList<>();
    private List<String> imageCFilesPath = new ArrayList<>();
    private MSQLP ms;

    // -----------------------------------------------------
    private void dist() {
        Table table = MSQL.getCurrentTable();
        String dist = MSQL.getCurrentTable().getDist();
        if (!dist.equals("NONE")) {
            String[] split = dist.split(",");
            if (split.length == 0) {
                split = new String[] { dist };
            }
            ms = vf.getMs();

            GridPane gp = vf.getGridPane();
            for (int a = 0; a < split.length; a++) {
                int c = table.getColumnIndex(split[a].replace("_", " "));
                if (vf.getTfsFKList().get(c).isEmpty()) {// NOT IF THIS COLUMN HAS FK
                    vf.getTfsAutoC().get(c).setTfParent(vf.getTfs()[c]);
                    vf.getTfs()[c].setStyle(CSS.TFS_DIST_LOOK);

                    if (table.getColumns().get(c).getExtra()) {
                        vf.getTfs()[c].setPromptText("AUTO_INCREMENT");
                    }

                    //???????????????????????
                    //columnsd.getList().get(c).setDist("Yes");
                    table.getColumns().get(c).setDist(true);
                    // QUERY --------------------------------------------------
                    String tableName = MSQL.getCurrentTable().getName().replace(" ", "_");
                    String column = table.getColumns().get(c).getName();

                    ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
                    ms.selectDistinctColumn(tableName, column.replace(" ", "_"), new SelectDistinct(vf, c));
                }

                gp.getRowConstraints().get(4).setMaxHeight(Short.MAX_VALUE);
            }
        }
    }

    private void imageC() {
        Table table = MSQL.getCurrentTable();
        String imageC = table.getImageC();
        String imageCPath = table.getImageCPath();

        if (!imageCPath.equals("NONE")) {
            int index = table.getColumnIndex(imageC);
            //columnsd.getList().get(index).setImageC("Yes");
            table.getColumns().get(index).setImageC(true);
            table.setImageCPath(imageCPath);
            //columnsd.getList().get(index).setImageCPath(imageCPath);

            vf.getSplitLeft().setDividerPositions(0.6);
            File imageCDirectory = new File(imageCPath);

            vf.getHbImages().getChildren().clear();
            if (imageCDirectory.exists()) {
                vf.getHbImages().getChildren().add(vf.getIvImageC()[0]);
                imageCFilesPath.clear();
                imageCFiles.clear();

                if (imageCDirectory.isDirectory()) {
                    int[] indexs = { 0 };
                    Arrays.asList(imageCDirectory.listFiles(f -> f.isDirectory() || f.isFile())).stream().forEach(f -> {
                        imageCFilesPath.add(f.getPath());
                        imageCFiles.add(MString.getRemoveCustomFormattedString(f.getName()));
                        indexs[0]++;
                    });
                }
            } else {
                // GET HBOX of imageView TO REPLACED WITH 'path to ImageC not found'
                vf.getHbImages().getChildren().add(new Label("Path '" + imageCPath + "' not found"));
            }
        } else {
            vf.getSplitLeft().setDividerPositions(1.0);
        }
    }

    public void distStart() {
        Table table = MSQL.getCurrentTable();
        //columnsd.clear();
        for (int a = 0; a < table.getColumns().size(); a++) {
            //columnsd.addColumnD(new ColumnD());
        }

        dist();
        imageC();
    }

    public void distAction() {
        Table table = MSQL.getCurrentTable();
        
        for (int a = 0; a < table.getColumns().size(); a++) {
            if (table.getColumns().get(a).getDist()){
                String tableName = MSQL.getCurrentTable().getName().replace(" ", "_");
                String column = table.getColumns().get(a).getName();
                ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
                ms.selectDistinctColumn(tableName, column, new SelectDistinct(vf, a));
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
