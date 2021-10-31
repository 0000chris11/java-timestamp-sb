package com.cofii.ts.cu;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.cofii.ts.cu.fk.Row;
import com.cofii.ts.cu.fk.VFK;
import com.cofii.ts.cu.fk.VFKController;
import com.cofii.ts.cu.imagec.VImageC;
import com.cofii.ts.cu.imagec.VImageCController;
import com.cofii.ts.cu.store.VCStore;
import com.cofii.ts.first.VF;
import com.cofii.ts.first.VFController;
import com.cofii.ts.login.VL;
import com.cofii.ts.other.CSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.SQLType;
import com.cofii.ts.store.SQLTypes;
import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.PK;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.LabelStatus;
import com.cofii2.components.javafx.ToggleGroupD;
import com.cofii2.components.javafx.TrueFalseWindow;
import com.cofii2.components.javafx.popup.Message;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.components.javafx.popup.PopupKV;
import com.cofii2.components.javafx.popup.PopupMenu;
import com.cofii2.components.javafx.popup.PopupMessage;
import com.cofii2.components.javafx.popup.PopupMessageControl2;
import com.cofii2.methods.MList;
import com.cofii2.mysql.CustomConnection;
import com.cofii2.mysql.MSQLCreate;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.store.ForeignKey;
import com.cofii2.stores.CC;
import com.cofii2.stores.DInt;
import com.cofii2.stores.IntBoolean;
import com.cofii2.stores.IntString;
import com.cofii2.stores.TString;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class VCController implements Initializable {

    public static final String LBH_COLUMN_NAMES = "Name";
    public static final String LBH_COLUMN_NAMES_ERROR = "Duplicated names or empty columns";
    public static final Insets INSETS = new Insets(2, 2, 2, 2);

    // -------------------------------------------------
    private int presetRowsLenght = 2;
    private int currentRowLength = 0;
    private List<SQLType> presetTypeSelected = new ArrayList<>(MSQL.MAX_COLUMNS);

    private ObservableList<String> listSameColumns = FXCollections.observableArrayList();

    @FXML
    private BorderPane bpMain;
    // TOP --------------------------------------------------
    @FXML
    private HBox hbTop;
    @FXML
    private Label lbTable;
    @FXML
    private TextField tfTable;
    @FXML
    private Button btnRenameTable;

    // HEADERS ----------------------------------------------
    @FXML
    private HBox headerId;
    @FXML
    private HBox headerColumns;
    @FXML
    private HBox headerTypes;
    @FXML
    private HBox headerNulls;
    @FXML
    private HBox headerPKS;
    @FXML
    private HBox headerDefaults;
    @FXML
    private HBox headerExtras;
    @FXML
    private HBox headerDists;
    @FXML
    private HBox headerTextAreas;
    // CENTER ---------------------------------------------
    @FXML
    private ScrollPane scGridPane;
    @FXML
    private GridPane gridPane;

    @FXML
    private Label lbhColumnNames;
    @FXML
    private Label lbhTypes;
    @FXML
    private Label lbhExtra;
    @FXML
    private Label lbhDist;
    @FXML
    private Label lbhTextArea;

    @FXML
    private Button btnErrorDisplay;
    // BOTTOM----------------------------------------
    @FXML
    private HBox hbUpdates;
    @FXML
    private Label lbUpdate;
    @FXML
    private Button btnUpdatePK;
    @FXML
    private Button btnUpdateExtra;
    @FXML
    private Button btnUpdateDist;
    @FXML
    private Button btnUpdateTextArea;

    @FXML
    private HBox hbSelects;
    @FXML
    private Button btnSelectFK;
    @FXML
    private Button btnSelectUI;
    @FXML
    private Button btnSelectIC;
    // --------------------------------------------------------
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnCreateUpdate;

    @FXML
    private HBox hbStatus;
    private LabelStatus lbStatus = new LabelStatus();
    // CENTER NODES LISTS
    // =================================================================
    private PopupMessageControl2 popupMessageControl;

    private Message tablePopupControl;
    private Message messageSameColumns;
    // FK --------------------------------------------------
    private String[] pksReferences;
    private Message fkPopupsControl;
    // BOTTOM ----------------------------------------------
    /*
    private final ObservableMap<String, Boolean> createHelpMap = FXCollections.observableHashMap();
    private final PopupKV createHelpPopup = new PopupKV(createHelpMap);

    private final ObservableMap<String, Boolean> updateAddColumnHelpMap = FXCollections.observableHashMap();
    private final PopupKV updateAddColumnHelpPopup = new PopupKV(updateAddColumnHelpMap);
    */
    // ---------------------------------------------
    private VFController vfc;

    private VFKController vfkc;
    private VImageCController vicc;

    private MSQLP ms;

    private Database currentDatabse = Users.getInstance().getCurrenUser().getCurrentDatabase();
    private Table currentTable = currentDatabse.getCurrentTable();
    private SQLTypes types = SQLTypes.getInstance();

    private Pattern patternBWTC = Pattern.compile("[A-Za-z](\\w| )*");
    // =============================================================================
    private boolean updateControl = false;
    // BOOLEAN CONTROL =============================================================
    // TABLE ----------------------------------------
    private boolean tableOk = false;

    private String getCustomStringBeforeUpdate(List<ToggleButton> btns) {
        StringBuilder sb = new StringBuilder();
        int[] indexs = { 0 };
        btns.forEach(btn -> {
            if (btn.isVisible() && btn.isSelected()) {
                // sb.append(updateTable.getColumns().get(indexs[0]).replace(" ",
                // "_")).append(",");
                //sb.append(tfsColumn.get(indexs[0]).getText().replace(" ", "_")).append(",");
                sb.append(VCRow.getRows().get(indexs[0]).getTfColumn().getText().replace(" ", "_")).append(",");
            }
            indexs[0]++;
        });

        return sb.length() == 0 ? "NONE" : sb.deleteCharAt(sb.length() - 1).toString();
    }

    // CONTROL =============================================================
    void masterControlTest(){
        if (!updateControl) {
            boolean allOk = popupMessageControl.getPopupMaster() != null
                    ? popupMessageControl.getPopupMaster().getVbox().getChildren().isEmpty()
                    : false;
            btnCreateUpdate.setDisable(!allOk);

        }
    }

    void selectsControl() {
        boolean ok = tableOk && VCRow.isAllColumnBWOK() && VCRow.isColumnsSameOk();

        btnSelectUI.setDisable(!ok);
        btnSelectIC.setDisable(!ok);
        btnSelectFK.setDisable(!ok);
    }

    // LISTENERS ============================================================
    // TABLE-------------------------------------
    private void tableTextProperty(String newValue) {
        String[] tableList = currentDatabse.getTablesNames();
        String text = tfTable.getText().toLowerCase().replace(" ", "_").trim().replaceAll("(^_|_$)", "");

        Matcher matcher = patternBWTC.matcher(text);
        if (matcher.matches()) {
            if (MList.isOnThisList(tableList, text, true) && !updateControl){
                tableOk = false;
                tablePopupControl.getItemList().add(VCStore.TABLE_EXISTS);
            } else {
                tableOk = true;
                tablePopupControl.getItemList().remove(VCStore.TABLE_EXISTS);
            }
            tablePopupControl.getItemList().remove(VCStore.ILLEGAL_CHARS);
        } else {
            tableOk = false;
            tablePopupControl.getItemList().add(VCStore.ILLEGAL_CHARS);
        }

        // UPDATE-----------------------------------------------
        tfTableUpdate(text);
        // -----------------------------------------
        selectsControl();
        masterControlTest();
    }

    private void tfTableUpdate(String text) {
        if (updateControl) {
            if (tableOk) {
                // String tableO = updateTable.getTable().toLowerCase().trim().replace(" ",
                // "_");
                String tableO = currentTable.getName().toLowerCase().trim().replace(" ", "_");
                if (!text.equals(tableO)) {
                    tfTable.setStyle(null);
                    btnRenameTable.setDisable(false);
                } else {
                    tfTable.setStyle(CSS.NODE_TEXTFILL_HINT);
                    btnRenameTable.setDisable(true);
                }
            } else {
                btnRenameTable.setDisable(true);
            }
        }
    }

    void btnRenameTableAction(ActionEvent e) {
        System.out.println(CC.CYAN + "Rename Table" + CC.RESET);
        // NOT TESTED------------------------------------
        String oldTable = currentTable.getName().toLowerCase().trim().replace(" ", "_");
        String newTable = tfTable.getText().toLowerCase().trim().replace(" ", "_");
        boolean renameTable = ms.renameTable(oldTable, newTable);
        if (renameTable) {
            boolean updateTableNames = ms.updateRow(MSQL.TABLE_NAMES, "Name", oldTable.replace("_", " "), "Name",
                    newTable.replace("_", " "));
            if (updateTableNames) {
                // updateTable.setTable(newTable);
                currentTable.setName(newTable);

                try {
                    ResultSet rs = ms.selectRow(MSQL.TABLE_NAMES, "Name", newTable.replace("_", " "));
                    Table ctable = null;
                    while (rs.next()) {
                        ctable = new Table(rs.getInt(1), rs.getString(2));
                    }

                    Users.getInstance().getCurrenUser().getCurrentDatabase().setCurrentTable(ctable);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                // Menus.getInstance(vf).addMenuItemsReset();// NOT TESTED
                btnRenameTable.setDisable(true);

                lbStatus.setText("Table '" + oldTable + "' has been rename to '" + newTable + "'",
                        lbStatus.getTextFillOk(), Duration.seconds(2));
                System.out.println("\ttable Renamed!");
            } else {
                lbStatus.setText("FATAL: table change its name but " + MSQL.TABLE_NAMES + " hasn't been updated",
                        lbStatus.getTextFillError());
                System.out.println("\tFATAL!");

            }

        } else {
            lbStatus.setText("Table '" + oldTable + "' fail to be renamed", lbStatus.getTextFillError());
            System.out.println("\tERROR!");

        }
    }

    // PKS/FKS======================================================
    void updatePK(ActionEvent e) {
        VL.LOG.info("Update PK (Starts)");

        String tableName = currentTable.getName();

        String[] errorMessage = { null };
        ms.setSQLException((ex, s) -> errorMessage[0] = ex.getMessage());
        boolean dropPK = true;
        // if (updateTable.getPks().stream().anyMatch(p -> p.equals("Yes"))) {
        if (!currentTable.getPKS().isEmpty()) {
            dropPK = ms.dropPrimaryKey(tableName);
        }

        int[] indexs = { 0 };
        if (dropPK) {
            List<RadioButton> rbsPK = VCRow.getRows().stream().map(VCRow::getRbPK).collect(Collectors.toList());
            if (rbsPK.stream().anyMatch(rb -> rb.isSelected())) {
                // ADDING PK------------------------------------------
                List<String> cols = new ArrayList<>(currentRowLength);
                indexs[0] = 0;
                rbsPK.forEach(el -> {
                    if (el.isSelected()) {
                        // cols.add(updateTable.getColumns().get(indexs[0]).replace(" ", "_"));
                        cols.add(currentTable.getColumns().get(indexs[0]).getName().replace(" ", "_"));
                        // updateTable.getPks().set(indexs[0], "Yes");
                        currentTable.getColumns().get(indexs[0]).setPk(true);
                    }
                    indexs[0]++;
                });
                boolean addPK = true;
                if (!cols.isEmpty()) {
                    addPK = ms.addPrimaryKey(tableName, cols.toArray(new String[cols.size()]));
                }
                if (addPK) {
                    btnUpdatePK.setDisable(true);

                    lbStatus.setText("Changed Primary Key", lbStatus.getTextFillOk(), Duration.seconds(2));
                    System.out.println("\tSUCCESS");
                } else {
                    lbStatus.setText(errorMessage[0] != null ? errorMessage[0] : "Failt to Change Primary Key",
                            lbStatus.getTextFillError());
                    System.out.println("\tFAILED");

                }
            } else {
                // SET PK TO UPDATE-TABLE---------------------------
                indexs[0] = 0;
                rbsPK.forEach(rb -> {
                    // if (!rb.isSelected() != updateTable.getPks().get(indexs[0]).equals("Yes")) {
                    if (!rb.isSelected() != currentTable.getColumns().get(indexs[0]).isPk()) {
                        // updateTable.getPks().set(indexs[0], "No");
                        currentTable.getColumns().get(indexs[0]).setPk(false);
                    }
                    indexs[0]++;
                });

                lbStatus.setText("Primary key has been deleted", lbStatus.getTextFillOk(), Duration.seconds(2));
                System.out.println("\tSUCCESS");
            }
        } else {
            lbStatus.setText(errorMessage[0] != null ? errorMessage[0] : "Failt to delete Primary Key",
                    lbStatus.getTextFillError());
            System.out.println("\tFAILED");
        }
    }

    // EXTRA =================================================
    void updateExtra(ActionEvent e) {
        VL.LOG.info("Update Extra (Starts)");

        String tableName = currentTable.getName();
        String columnName;
        String type;

        List<RadioButton> rbsExtra = VCRow.getRows().stream().map(VCRow::getRbExtra).collect(Collectors.toList());
        // CURRENT TABLE SELECTION -------------------------
        int currentExtra = currentTable.getExtra();
        // Nothing // oneSelected

        
        // CURRRENT SELECTION -------------------------------
        int[] indexs = { 0 };
        boolean extra = rbsExtra.stream().anyMatch(rb -> {
            boolean selected = rb.isSelected();
            indexs[0]++;// MAY WORK -> TEST MORE
            return selected;
        });
        // REMOVE PREVIOUS EXTRA --------------------
        if(currentExtra >= 0){
            ms.setNullValue(extra ? currentTable.getColumns().get(currentExtra).isNulll() : false);
            columnName = currentTable.getColumns().get(currentExtra).getName();
            type = currentTable.getColumns().get(currentExtra).getFullType();

            ms.changeType(tableName, columnName, type);
        }
        // ADD NEW EXTRA ---------------------------------
        ms.setNullValue(extra ? currentTable.getColumns().get(indexs[0]).isNulll() : false);
        ms.setExtraValue(extra);
        columnName = currentTable.getColumns().get(indexs[0]).getName();
        type = currentTable.getColumns().get(indexs[0]).getFullType();

        boolean updateExtra = ms.changeType(tableName, columnName, type);
        
        if (updateExtra) {
            System.out.println(indexs[0]);
            // updateTable.setExtra(extra ? indexs[0] : -1);
            currentTable.setExtra(extra ? indexs[0] : -1);

            btnUpdateExtra.setDisable(true);

            lbStatus.setText("Changed Extra", lbStatus.getTextFillOk(), Duration.seconds(2));
            VL.LOG.log(Level.FINE, "SUCCESS");
        } else {
            lbStatus.setText("Fail to change Extra", lbStatus.getTextFillError());
            VL.LOG.log(Level.SEVERE, "FAILED");
        }
    }

    // BOTTOM ===================================================
    private void btnCancelAction(ActionEvent e) {
        // vf.getStage().setScene(vf.getScene());
        VFK.setInstance(null);

        currentRowLength = 0;
        VCRow.getRows().clear();
        popupMessageControl.getMessageDataDisplay().clear();

        new VF(this);
    }

    // CREATE TABLE--------------------------------------------
    private boolean insertPaths(int tableId) {
        User currentUser = Users.getInstance().getCurrenUser();

        String imageCColumn = vicc.getCbColumnSelect().getSelectionModel().getSelectedItem();
        int imagesLength = Integer.parseInt(vicc.getTfNumberImageC().getText());
        String displayOrder = vicc.getCbDisplayOrder().getSelectionModel().getSelectedItem();
        String type = vicc.getCbType().getSelectionModel().getSelectedItem();

        List<String> paths = vicc.getTfsPath().stream().map(TextField::getText).collect(Collectors.toList());
        // INSERTING PATHS-------------------------
        paths.forEach(path -> {
            ms.setInsertIgnore(true);
            System.out.println("Insert in Path");
            ms.insert(MSQL.PATHS, new Object[] { null, path });
        });

        Users.getInstance().getCurrenUser().getPaths().clear();
        ms.selectData(MSQL.PATHS, vfc.getVf()::selectPathsForCurrentUser);
        // INSERTING TABLE-PATHS--------------------
        paths.forEach(path -> {
            ms.setInsertIgnore(true);
            System.out.println("Insert in Table_Paths");
            ms.insert(MSQL.TABLE_PATHS, new Object[] { tableId, currentUser.getPathIdByName(path) });
        });

        Object[] valuesTableImageC = new Object[] { tableId, imageCColumn, imagesLength, displayOrder, type };
        return ms.insert(MSQL.TABLE_IMAGECS, valuesTableImageC);
    }

    private void createTableAction(ActionEvent e) {
        System.out.println(CC.CYAN + "CREATE TABLE" + CC.RESET);
        String tableName = tfTable.getText().toLowerCase().trim().replace(" ", "_");
        // LEFT-------------------
        String[] columnsNames = new String[currentRowLength];
        String[] typesNames = new String[currentRowLength];
        int[] typesLengths = new int[currentRowLength];
        boolean[] nulls = new boolean[currentRowLength];
        String[] defaults = new String[currentRowLength];

        List<String> cpks = new ArrayList<>(MSQL.MAX_COLUMNS);
        List<TString> cfks = new ArrayList<>(MSQL.MAX_COLUMNS);
        // List<IntString> defaults = new ArrayList<>(MSQL.MAX_COLUMNS);
        int extra = 0;
        // CUSTOM -----------------------
        // String dist = Custom.getOldDist(currentRowLength, btnsDist.toArray(new
        // ToggleButton[btnsDist.size()]));
        String dist = getCustomStringBeforeUpdate(VCRow.getRows().stream().map(VCRow::getBtnDist).collect(Collectors.toList()));
        String textArea = getCustomStringBeforeUpdate(VCRow.getRows().stream().map(VCRow::getBtnTextArea).collect(Collectors.toList()));
        // ADDING VALUES -------------------------------------------------
        for (int a = 0; a < currentRowLength; a++) {

            VCRow row = VCRow.getRows().get(a);
            // LEFT-------------------
            columnsNames[a] = row.getTfColumn().getText().toLowerCase().trim().replace(" ", "_");
            typesNames[a] = row.getTfType().getText();

            if (row.getTfTypeLength().isVisible() && types.getTypeLength(typesNames[a]) > 0) {
                // typesNames[a] += tfsTypeLength[a].getText();
                typesLengths[a] = Integer.parseInt(row.getTfTypeLength().getText());
            }
            nulls[a] = row.getCkNull().isSelected();

            if (row.getRbPK().isSelected()) {
                cpks.add(columnsNames[a]);
            }
            // DEFAULT --------------------------------
            if (row.getCkDefault().isSelected()) {
                // defaults.add(new IntString(a + 1, tfsDefault[a].getText()));
                defaults[a] = row.getTfDefault().getText();
            } else {
                defaults[a] = null;
            }

            if (row.getRbExtra().isSelected()) {
                extra = a + 1;
            }

        }

        // CREATE UPDATE ---------------------------------------
        String databaseName = currentDatabse.getName();
        MSQLCreate msc = new MSQLCreate(new CustomConnection(databaseName));
        for (int a = 0; a < currentRowLength; a++) {
            msc.addTypesWidth(new DInt(a + 1, typesLengths[a]));
            msc.addAllowsNull(new IntBoolean(a + 1, nulls[a]));
            msc.addDefault(defaults[a] != null ? new IntString(a + 1, defaults[a]) : null);
        }
        // PRIMARY KEY ==========================================
        msc.addAllPrimaryKeys(cpks);
        // FOREIGN KEY ===========================================
        if (vfkc != null && vfkc.getFpColumns().getChildren().size() != currentRowLength) {
            Predicate<? super Node> onlyNonEmptyRows = node -> !((Row) node).isEmptyRow();
            Consumer<? super Node> setFKConsumer = node -> {
                ForeignKey foreignKey = new ForeignKey();

                VBox vbox = (VBox) node;
                FlowPane rowColumns = (FlowPane) vbox.getChildren().get(1);

                foreignKey.setColumnNames(rowColumns.getChildren().stream().filter(Button.class::isInstance)
                        .map(col -> ((Button) col).getText()).toArray(size -> new String[size]));

                Row row = (Row) node;
                String constraintName = row.getTfConstraintName().getText();
                String references = row.getTfReference().getText();
                ForeignKey fkReferences = ForeignKey.getForeignKeyThroughCustomString(references);

                foreignKey.setConstraintName(constraintName);
                foreignKey.setReferenceDatabaseName(fkReferences.getDatabaseName());
                foreignKey.setReferenceTableName(fkReferences.getTableName());
                foreignKey.setReferenceColumnsNames(fkReferences.getColumnNames());

                msc.getListFK().add(foreignKey);
            };
            // SINGLE --------------------------------------
            vfkc.getVbSingle().getChildren().stream().filter(onlyNonEmptyRows).forEach(setFKConsumer);
            // GROUP ---------------------------------------
            vfkc.getVbGroup().getChildren().stream().filter(onlyNonEmptyRows).forEach(setFKConsumer);

        }
        // EXTRA ===============================================
        msc.setAutoIncrement(extra);

        boolean createTable = msc.createTable(tableName, columnsNames, typesNames);
        // INSERT -----------------------------------------------
        StringBuilder message = new StringBuilder();
        boolean fullSuccess = true;
        if (createTable) {
            // TABLE NAMES INSERTION --------------------------------------------------
            Object[] valuesTableNames = new Object[] { null, tableName, "NONE", "NONE", "NONE" };
            boolean insertTableNames = ms.insert(MSQL.TABLE_NAMES, valuesTableNames);
            int newTableId = -1;
            if (insertTableNames) {
                newTableId = (int) ms.selectValues(MSQL.TABLE_NAMES, "id", "name", tableName)[0];

                message.append("Table '" + tableName.replace("_", " ") + "' has been created and Inserted!");
                // TABLE CUSTOM INSERTION --------------------------------------------
                Object[] valuesTableCustoms = new Object[] { newTableId, dist, textArea };
                boolean insertTableCustoms = ms.insert(MSQL.TABLE_CUSTOMS, valuesTableCustoms);
                if (insertTableCustoms) {
                    message.append(", inserted on Table_Custom too");
                } else {
                    message.append(", fail to be inserted on Table_Custom");
                    fullSuccess = false;
                }
                System.out.println("\tSUCCESS");
            } else {
                // DELETE CREATED TABLE
                fullSuccess = false;
                message.append("FATAL: (Table has been create but not inserted on " + MSQL.TABLE_NAMES);
                System.out.println("\tFATAL");
            }
            // TABLE PATHS & PATHS & IMAGECS INSERTION ---------------------------------
            if (vicc != null && !btnSelectIC.isDisable() && newTableId > -1) {
                boolean imageCS = insertPaths(newTableId);

                if (imageCS) {
                    message.append(", imagecs data has been inserted successfully!");
                } else {
                    message.append(", imagecs data failed to be inserted");
                    fullSuccess = false;
                }
            }
        } else {
            fullSuccess = false;
            message.append("Table Failed to be created");
            System.out.println("\tFAILED");
        }
        lbStatus.setText(message.toString(), fullSuccess ? lbStatus.getTextFillOk() : lbStatus.getTextFillWarning());
    }

    // CUSTOM =====================================================
    void updateDist(ActionEvent e) {
        VL.LOG.info("Update Dist (Starts)");
        String tableName = currentTable.getName();
    
        List<ToggleButton> btnsDist = VCRow.getRows().stream().map(VCRow::getBtnDist).collect(Collectors.toList());
        String dist = getCustomStringBeforeUpdate(btnsDist);

        boolean updateDist = ms.updateRow(MSQL.TABLE_NAMES, "Name", tableName, "Dist1", dist);
        if (updateDist) {
            int[] indexs = { 0 };
            btnsDist.forEach(btn -> {
                if (currentRowLength > indexs[0]) {
                    // updateTable.getDists().set(indexs[0], btnsDist.get(indexs[0]).isSelected());
                    currentTable.getColumns().get(indexs[0]).setDist(btnsDist.get(indexs[0]).isSelected());
                }
                indexs[0]++;
            });
            btnUpdateDist.setDisable(true);

            lbStatus.setText("Dist has change to '" + dist + "'", lbStatus.getTextFillOk(), Duration.seconds(2));
            VL.LOG.log(Level.FINE, "SUCCESS");
        } else {
            lbStatus.setText("Dist fail to changed", lbStatus.getTextFillError());
            VL.LOG.log(Level.FINE, "FAILED");
        }
    }

    // SELECT =======================================================
    private void fkSelectAction(ActionEvent e) {
        VFK.getInstance(this, !updateControl);
        vfkc = VFK.getVfkc();
    }

    private void uiSelectAction(ActionEvent e) {
        // CREATE UNIQUE INDEX !!!!!!!
    }

    private void icSelectAction(ActionEvent e) {
        VImageC vImageC = VImageC.getInstance(this, !updateControl);
        vicc = vImageC.getVicc();
    }

    // MAIN CONFIG ==================================================
    // MAP HELPS----------------------------------------------------
    /*
    void createHelpPopupReset() {
        createHelpMap.put("Table Name", tableOk);
        createHelpMap.put("Columns Names", VCRow.isColumnsSameOk() && VCRow.isAllColumnBWOK());
        createHelpMap.put("Types", VCRow.isAllTypeSelectionMatch() &&  VCRow.isAllTypeLengthOK());
        createHelpMap.put("Default Values", defaultBW && defaultOK);
        createHelpMap.put("Extra Value", extraPKOK && extraFKOK && extraDefaultOK);
        createHelpMap.put("Dist", extraDistOK);
        createHelpMap.put("ImageC", imageCOK);
    }   
    
    void createAddColumnHelpPopupReset() {
        updateAddColumnHelpMap.put("Columns", columnAddOk);
        updateAddColumnHelpMap.put("Type", typeAddOk);
        updateAddColumnHelpMap.put("Default", defaultAddOk);
    }
    */
    // INIT ---------------------------------------------
    /**
     * List all PK for each row of FKS (to reference them)
     */
    void fkReferencesInit() {
        List<String> listReferences = new ArrayList<>();
        Map<String, List<PK>> pksMap = Users.getInstance().getCurrenUser().getPKSInGroups();
        pksMap.forEach((s, lst) -> {
            StringBuilder sb = new StringBuilder(s);
            sb.append(" (");

            lst.forEach(pk -> sb.append(pk.getColumnName()).append(","));
            sb.deleteCharAt(sb.length() - 1).append(")");

            listReferences.add(sb.toString());
        });

        pksReferences = listReferences.toArray(new String[listReferences.size()]);

    }

    void resetGridPaneRowsTest() {
        // REMOVING ======================================================
        gridPane.getChildren().removeIf(node -> !node.getStyleClass().contains("vi-header"));
        // RE-ADDING ===========================================================
        for (int a = 0; a < currentRowLength; a++) {
            int row = a + 1;// + HEADER
            VCRow vcrow = VCRow.getRows().get(a);

            gridPane.getRowConstraints().add(new RowConstraints(32, 32, 32, Priority.ALWAYS, VPos.TOP, true));
            gridPane.add(vcrow.getHbN(), 0, row);
            gridPane.add(vcrow.getHbColumn(), 1, row);
            gridPane.add(vcrow.getHbType(), 2, row);
            gridPane.add(vcrow.getHbNull(), 3, row);
            gridPane.add(vcrow.getHbPK(), 4, row);
            gridPane.add(vcrow.getHbDefault(), 5, row);
            gridPane.add(vcrow.getHbExtra(), 6, row);

            gridPane.add(vcrow.getBtnDist(), 7, row);
            gridPane.add(vcrow.getBtnTextArea(), 8, row);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        popupMessageControl = new PopupMessageControl2(new PopupMessage("MASTER", btnErrorDisplay));
        popupMessageControl.getMessagesList().addAll(VCStore.getErrorsList());
        popupMessageControl.setNodeStyleClass("node-border-error");
        // NODES ====================================================
        // CENTER -------------------------------
        fkReferencesInit();
        // TOP ----------------------------------
        tablePopupControl = new Message("TABLE:", hbTop);
        popupMessageControl.getMessages().add(tablePopupControl);
        tablePopupControl.getItemList().add(VCStore.ILLEGAL_CHARS);

        messageSameColumns = new Message("COLUMNS:", headerColumns);
        popupMessageControl.getMessages().add(messageSameColumns);
        // HEADERS STYLING ===========================================
        headerId.getStyleClass().add("vi-header");
        headerColumns.getStyleClass().add("vi-header");
        headerTypes.getStyleClass().add("vi-header");
        headerNulls.getStyleClass().add("vi-header");
        headerPKS.getStyleClass().add("vi-header");
        headerDefaults.getStyleClass().add("vi-header");
        headerExtras.getStyleClass().add("vi-header");
        headerDists.getStyleClass().add("vi-header");
        headerTextAreas.getStyleClass().add("vi-header");

        hbUpdates.getStyleClass().add("hb-white-border");
        hbSelects.getStyleClass().add("hb-white-border");
        // TOP-----------------------------
        tfTable.setPromptText("Table name required");
        btnRenameTable.managedProperty().bind(btnRenameTable.visibleProperty());
        btnRenameTable.setDisable(true);
        // CENTER---------------------------
        scGridPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        gridPane.minWidthProperty().bind(scGridPane.widthProperty());

        // PopupMessage popupMaster = new PopupMessage("MASTER", btnErrorDisplay);
        
        // popupMaster.setPopupPosition(PPosition.TOP);
        btnErrorDisplay.setOnAction(e -> popupMessageControl.getPopupMaster().showPopup());
        // BOTTOM--------------------------
        lbUpdate.setDisable(true);
        lbStatus.getStyleClass().add("status");

        lbStatus.getBtnCloseStatus().setStyle(CSS.LB_STATUS_BUTTON);
        HBox.setHgrow(lbStatus, Priority.ALWAYS);
        hbStatus.getChildren().add(0, lbStatus);

        fkPopupsControl = new Message("FKSELECT:", btnSelectFK);
        popupMessageControl.getMessages().add(fkPopupsControl);
        // LISTENERS ===================================================
        // TOP-----------------------------------------------
        tfTable.textProperty().addListener((obs, oldValue, newValue) -> tableTextProperty(newValue));
        // BOTTOM --------------------------------------------
        btnCreateUpdate.managedProperty().bind(btnCreateUpdate.visibleProperty());
        btnCancel.setOnAction(this::btnCancelAction);
        btnCreateUpdate.setOnAction(this::createTableAction);
        //btnCreateHelp.setOnAction(this::helpAction);

        btnSelectFK.setOnAction(this::fkSelectAction);
        btnSelectUI.setOnAction(this::uiSelectAction);
        btnSelectIC.setOnAction(this::icSelectAction);

    }

    // GETTERS & SETTERS-------------------------------------------------------------
    public GridPane getGridPane() {
        return gridPane;
    }

    public void setGridPaneLeft(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public int getPresetRowsLenght() {
        return presetRowsLenght;
    }

    public void setPresetRowsLenght(int presetColumnsLenght) {
        this.presetRowsLenght = presetColumnsLenght;
    }

    public VFController getVf() {
        return vfc;
    }

    public void setVf(VFController vf) {
        this.vfc = vf;
    }

    public MSQLP getMs() {
        return ms;
    }

    public void setMs(MSQLP ms) {
        this.ms = ms;
    }

    public Button getBtnRenameTable() {
        return btnRenameTable;
    }

    public void setBtnRenameTable(Button btnRenameTable) {
        this.btnRenameTable = btnRenameTable;
    }

    public Button getBtnUpdatePK() {
        return btnUpdatePK;
    }

    public void setBtnUpdatePK(Button btnUpdatePK) {
        this.btnUpdatePK = btnUpdatePK;
    }

    public Button getBtnUpdateExtra() {
        return btnUpdateExtra;
    }

    public void setBtnUpdateExtra(Button btnUpdateExtra) {
        this.btnUpdateExtra = btnUpdateExtra;
    }

    public Button getBtnUpdateDist() {
        return btnUpdateDist;
    }

    public void setBtnUpdateDist(Button btnUpdateDist) {
        this.btnUpdateDist = btnUpdateDist;
    }

    public TextField getTfTable() {
        return tfTable;
    }

    public void setTfTable(TextField tfTable) {
        this.tfTable = tfTable;
    }

    public Button getBtnCreateUpdate() {
        return btnCreateUpdate;
    }

    public void setBtnCreateUpdate(Button btnCreateUpdate) {
        this.btnCreateUpdate = btnCreateUpdate;
    }

    public boolean isUpdateControl() {
        return updateControl;
    }

    public void setUpdateControl(boolean updateControl) {
        this.updateControl = updateControl;
    }

    public int getCurrentRowLength() {
        return currentRowLength;
    }

    public void setCurrentRowLength(int currentRowLength) {
        this.currentRowLength = currentRowLength;
    }

    public Label getLbUpdateLeft() {
        return lbUpdate;
    }

    public void setLbUpdateLeft(Label lbUpdateLeft) {
        this.lbUpdate = lbUpdateLeft;
    }

    public Button getBtnSelectIC() {
        return btnSelectIC;
    }

    public void setBtnSelectIC(Button btnSelectImageC) {
        this.btnSelectIC = btnSelectImageC;
    }

    public BorderPane getBpMain() {
        return bpMain;
    }

    public void setBpMain(BorderPane bpMain) {
        this.bpMain = bpMain;
    }

    public String[] getPksReferences() {
        return pksReferences;
    }

    public void setPksReferences(String[] pksReferences) {
        this.pksReferences = pksReferences;
    }

    public Button getBtnErrorDisplay() {
        return btnErrorDisplay;
    }

    public void setBtnErrorDisplay(Button btnErrorDisplay) {
        this.btnErrorDisplay = btnErrorDisplay;
    }

    public Message getMessageSameColumns() {
        return messageSameColumns;
    }

    public void setMessageSameColumns(Message messageSameColumns) {
        this.messageSameColumns = messageSameColumns;
    }

    public Database getCurrentDatabse() {
        return currentDatabse;
    }

    public void setCurrentDatabse(Database currentDatabse) {
        this.currentDatabse = currentDatabse;
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
    }

    public PopupMessageControl2 getPopupMessageControl() {
        return popupMessageControl;
    }

    public void setPopupMessageControl(PopupMessageControl2 popupMessageControl) {
        this.popupMessageControl = popupMessageControl;
    }

    public VFKController getVfkc() {
        return vfkc;
    }

    public void setVfkc(VFKController vfkc) {
        this.vfkc = vfkc;
    }

    public Button getBtnUpdateTextArea() {
        return btnUpdateTextArea;
    }

    public void setBtnUpdateTextArea(Button btnUpdateTextArea) {
        this.btnUpdateTextArea = btnUpdateTextArea;
    }

    public HBox getHbTop() {
        return hbTop;
    }

    public void setHbTop(HBox hbTop) {
        this.hbTop = hbTop;
    }

    public Button getBtnSelectFK() {
        return btnSelectFK;
    }

    public void setBtnSelectFK(Button btnSelectFK) {
        this.btnSelectFK = btnSelectFK;
    }

    public Button getBtnSelectUI() {
        return btnSelectUI;
    }

    public void setBtnSelectUI(Button btnSelectUI) {
        this.btnSelectUI = btnSelectUI;
    }

    public LabelStatus getLbStatus() {
        return lbStatus;
    }

    public void setLbStatus(LabelStatus lbStatus) {
        this.lbStatus = lbStatus;
    }
    
}
