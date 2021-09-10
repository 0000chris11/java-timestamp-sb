package com.cofii.ts.other;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDistinct;
import com.cofii.ts.store.main.Path;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.methods.MString;
import com.cofii2.mysql.MSQLP;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Dist {

    private static VFController vfc;
    // private ColumnS columns = ColumnS.getInstance();
    // private ColumnDS columnsd = ColumnDS.getInstance();
    // private Keys keys = Keys.getInstance();

    private List<String> imageCFiles = new ArrayList<>();
    private List<String> imageCFilesPath = new ArrayList<>();
    private MSQLP ms;

    // -----------------------------------------------------
    private void dist(Table currentTable) {
        String dist = currentTable.getDist();
        if (!dist.equals("NONE")) {
            String[] split = dist.split(",");
            if (split.length == 0) {
                split = new String[] { dist };
            }
            ms = vfc.getMs();

            GridPane gp = vfc.getGridPane();
            for (int a = 0; a < split.length; a++) {
                int c = currentTable.getColumnIndex(split[a].replace("_", " "));
                if (vfc.getTfsFKList().get(c).isEmpty()) {// NOT IF THIS COLUMN HAS FK
                    vfc.getTfsAutoC().get(c).setTfParent(vfc.getTfs()[c]);
                    vfc.getTfs()[c].setStyle(CSS.TFS_DIST_LOOK);

                    if (currentTable.getColumns().get(c).getExtra()) {
                        vfc.getTfs()[c].setPromptText("AUTO_INCREMENT");
                    }

                    // ???????????????????????
                    // columnsd.getList().get(c).setDist("Yes");
                    currentTable.getColumns().get(c).setDist(true);
                    // QUERY --------------------------------------------------
                    String tableName = currentTable.getName().replace(" ", "_");
                    String column = currentTable.getColumns().get(c).getName();

                    ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
                    ms.selectDistinctColumn(tableName, column.replace(" ", "_"), new SelectDistinct(vfc, c));
                }

                gp.getRowConstraints().get(4).setMaxHeight(Short.MAX_VALUE);
            }
        }
    }

    private void imageC(Table currentTable) {
        String imageCColumnName = currentTable.getImageCColumnName();

        if (!currentTable.getImageCPaths().isEmpty()) {
            List<Path> imageCPath = currentTable.getImageCPaths();

            int index = currentTable.getColumnIndex(imageCColumnName);
            currentTable.getColumns().get(index).setImageC(true);
            // currentTable.setImageCPaths(imageCPath); ????
            vfc.getTfs()[index].getStyleClass().add("imageCTF");
            vfc.getSplitLeft().setDividerPositions(0.6);
            boolean allPathsExists = imageCPath.stream().allMatch(path -> {
                File file = new File(path.getPathName());
                return file.exists() && file.isDirectory();
            });

            vfc.getHbImages().getChildren().clear();
            if (allPathsExists) {
                // RESET IMAGEC-DISPLAY AND PATHS-LISTS
                vfc.getHbImages().getChildren().add(vfc.getIvImageC()[0]); // DEFAULT 1
                imageCFilesPath.clear();
                imageCFiles.clear();
                //ADDING PATHS FROM THE ALL THE IMAGECS-PATHS
                imageCPath.forEach(path -> {
                    File imageCDirectory = new File(path.getPathName());
                    Arrays.asList(imageCDirectory.listFiles(f -> f.isDirectory() || f.isFile())).stream().forEach(f -> {
                        imageCFilesPath.add(f.getPath());
                        imageCFiles.add(MString.getRemoveCustomFormattedString(f.getName()));
                    });
                });

            } else {
                // GET HBOX of imageView TO REPLACED WITH 'path to ImageC not found'
                vfc.getHbImages().getChildren().add(new Label("No Paths found"));
            }
        } else {
            vfc.getSplitLeft().setDividerPositions(1.0);
        }
    }

    public void distStart() {
        Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        // columnsd.clear();
        for (int a = 0; a < currentTable.getColumns().size(); a++) {
            // columnsd.addColumnD(new ColumnD());
            currentTable.getColumns().get(a).setDist(false);
            currentTable.getColumns().get(a).setImageC(false);
        }

        dist(currentTable);
        imageC(currentTable);
    }

    public void distAction() {
        Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();

        for (int a = 0; a < currentTable.getColumns().size(); a++) {
            if (currentTable.getColumns().get(a).getDist()) {
                String tableName = currentTable.getName().replace(" ", "_");
                String column = currentTable.getColumns().get(a).getName();
                ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
                ms.selectDistinctColumn(tableName, column, new SelectDistinct(vfc, a));
            }
        }
    }

    // -----------------------------------------------------
    private static Dist instance;

    public static Dist getInstance(VFController vf) {
        Dist.vfc = vf;
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
