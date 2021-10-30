package com.cofii.ts.other;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDistinct;
import com.cofii.ts.store.main.Path;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.methods.MFile;
import com.cofii2.methods.MString;
import com.cofii2.mysql.MSQLP;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class Dist {

    private static VFController vfc;
    // private ColumnS columns = ColumnS.getInstance();
    // private ColumnDS columnsd = ColumnDS.getInstance();
    // private Keys keys = Keys.getInstance();

    private List<String> imageCFilesFormatted = new ArrayList<>();
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

            for (int a = 0; a < split.length; a++) {
                String columnName = split[a];
                int c = currentTable.getColumnIndex(columnName);
                //int c = currentTable.getColumnIndex(split[a]);

                    vfc.getRows().get(c).getTf().setStyle(CSS.TFS_DIST_LOOK);

                    currentTable.getColumns().get(c).setDist(true);
                    // QUERY --------------------------------------------------
                    String tableName = currentTable.getName().replace(" ", "_");
                    String column = currentTable.getColumns().get(c).getName();

                    ms.setDistinctOrder(MSQLP.MOST_USE_ORDER);// WORK 50 50 WITH TAGS
                    ms.selectDistinctColumn(tableName, column.replace(" ", "_"), new SelectDistinct(vfc, c));
                

                //gp.getRowConstraints().get(4).setMaxHeight(Short.MAX_VALUE);
            }
        }
    }

    private void imageC(Table currentTable) {
        String imageCColumnName = currentTable.getImageCColumnName();

        if (!currentTable.getImageCPaths().isEmpty()) {
            List<Path> imageCPath = currentTable.getImageCPaths();

            int index = currentTable.getColumnIndex(imageCColumnName);
            currentTable.getColumns().get(index).setImageC(true);

            //vfc.getTfs()[index].getStyleClass().add("imageCTF");
            vfc.getRows().get(index).getTf().getStyleClass().add("imageCTF");
            vfc.getSplitLeft().setDividerPositions(0.6);
            // ALL PATH EXIST CHECK-------------------------------------------
            boolean allPathsExists = imageCPath.stream().allMatch(path -> {
                File file = new File(path.getPathName());
                return file.exists() && file.isDirectory();
            });

            vfc.getHbImages().getChildren().clear();
            if (allPathsExists) {
                // RESET IMAGEC-DISPLAY AND PATHS-LISTS----------------
                vfc.getHbImages().getChildren().add(vfc.getIvImageC()[0]); // DEFAULT 1
                imageCFilesPath.clear();
                imageCFilesFormatted.clear();
                // ADDING PATHS FROM THE ALL THE IMAGECS-PATHS------------
                imageCPath.forEach(path -> {
                    File imageCDirectory = new File(path.getPathName());

                    FileFilter fileFilter = File::isFile;
                    if (currentTable.getImageType().equals("File")) {
                        fileFilter = File::isFile;
                    } else if (currentTable.getImageType().equals("Folder")) {
                        fileFilter = File::isDirectory;
                    } else if (currentTable.getImageType().equals("All-Sub-Files")) {
                        fileFilter = null;
                    }
                    if (fileFilter != null) {
                        // FOR EACH PATH -----------------------------------
                        Arrays.asList(imageCDirectory.listFiles(fileFilter)).stream().forEach(f -> {
                            imageCFilesPath.add(f.getPath());
                            imageCFilesFormatted.add(MString.getRemoveCustomFormattedString(f.getName()));
                        });
                    } else {
                        // FOR EACH PATH -----------------------------------
                        imageCFilesPath.addAll(MFile
                                .getAllSubFilesInDirectory(path.getPathName(),
                                        Arrays.asList(imageCDirectory.listFiles()))
                                .stream().map(File::getPath).collect(Collectors.toList()));
                    }
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
        /*
         * for (int a = 0; a < currentTable.getColumns().size(); a++) { //
         * columnsd.addColumnD(new ColumnD());
         * currentTable.getColumns().get(a).setDist(false);
         * currentTable.getColumns().get(a).setImageC(false); }
         */
        dist(currentTable);
        imageC(currentTable);
    }

    public void distAction() {
        Table currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();

        for (int a = 0; a < currentTable.getColumns().size(); a++) {
            if (currentTable.getColumns().get(a).isDist()) {
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
        return imageCFilesFormatted;
    }

    public void setImageCFiles(List<String> imageCFiles) {
        this.imageCFilesFormatted = imageCFiles;
    }

    public List<String> getImageCFilesPath() {
        return imageCFilesPath;
    }

    public void setImageCFilesPath(List<String> imageCFilesPath) {
        this.imageCFilesPath = imageCFilesPath;
    }

}
