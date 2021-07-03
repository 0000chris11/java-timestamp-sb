package com.cofii.ts.cu;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.cofii.ts.first.VF;
import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.other.Timers;
import com.cofii.ts.sql.CurrenConnection;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.FK;
import com.cofii.ts.store.FKS;
import com.cofii.ts.store.PK;
import com.cofii.ts.store.PKS;
import com.cofii.ts.store.SQLType;
import com.cofii.ts.store.SQLTypes;
import com.cofii.ts.store.Table;
import com.cofii.ts.store.TableS;
import com.cofii.ts.store.UpdateTable;
import com.cofii.ts.store.VCGridNodes;
import com.cofii2.components.javafx.LabelStatus;
import com.cofii2.components.javafx.PopupAutoC;
import com.cofii2.components.javafx.PopupKV;
import com.cofii2.components.javafx.PopupMessage;
import com.cofii2.components.javafx.ToggleGroupD;
import com.cofii2.custom.Custom;
import com.cofii2.methods.MList;
import com.cofii2.mysql.MSQLCreate;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;
import com.cofii2.stores.DInt;
import com.cofii2.stores.IntBoolean;
import com.cofii2.stores.IntString;
import com.cofii2.stores.TString;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import javafx.util.Duration;

public class VCController implements Initializable {

    public static final String LBH_COLUMN_NAMES = "Name";
    public static final String LBH_COLUMN_NAMES_ERROR = "Columns have the same name";
    public static final Insets INSETS = new Insets(2, 2, 2, 2);

    private static final String ILLEGAL_CHARS = "Illegal Chars";
    private static final String SELECTION_UNMATCH = "Selection Unmatch";
    private static final String EMPTY_TEXT = "Column name field can't be empty";
    private static final String EXTRA_GENERAL_ERROR = "PK, FK or Default are not allowed to selected/unselected";
    // private static final String SAME_VALUE = "Can't update to the same value";
    // -------------------------------------------------
    private int presetRowsLenght = 2;
    private int currentRowLength = presetRowsLenght;
    private List<SQLType> presetTypeSelected = new ArrayList<>(MSQL.MAX_COLUMNS);

    private ObservableList<String> listColumns = FXCollections.observableArrayList();
    private ObservableList<Boolean> listImageC = FXCollections.observableArrayList();
    // HEADERS-------------------------------------------------
    @FXML
    private Label lbhColumnNames;
    @FXML
    private Label lbhTypes;
    @FXML
    private Label lbhFK;
    @FXML
    private Label lbhExtra;
    @FXML
    private Label lbhDist;
    @FXML
    private Label lbhImageC;
    // TABLE NAME--------------------------------------------------
    @FXML
    private Label lbTable;
    @FXML
    private TextField tfTable;
    @FXML
    private Button btnRenameTable;
    // LEFT---------------------------------------------
    @FXML
    private ScrollPane spGridPaneLeft;
    @FXML
    private GridPane gridPaneLeft;
    // LEFT-BOTTOM----------------------------------------
    @FXML
    private HBox hbLeftUpdate;

    @FXML
    private Label lbUpdateLeft;
    @FXML
    private Button btnUpdatePK;
    @FXML
    private Button btnUpdateFK;
    @FXML
    private Button btnUpdateExtra;
    @FXML
    private Button btnUpdateDist;
    // RIGHT---------------------------------------------
    @FXML
    private ScrollPane spGridPaneRight;
    @FXML
    private GridPane gridPaneRight;
    // RIGHT-BOTTOM-------------------------------------
    @FXML
    private HBox hbRightUpdate;

    @FXML
    private TextField tfImageCPath;
    @FXML
    private Button btnSelectImageC;
    @FXML
    private Button btnUpdateImageC;
    // BOTTOM------------------------------------------
    @FXML
    private HBox hbStatus;
    private LabelStatus lbStatus = new LabelStatus();
    @FXML
    private Button btnCreateUpdate;
    @FXML
    private Button btnCreateHelp;
    @FXML
    private Button btnCancel;

    @FXML
    private Region regionLeft;// IDR
    // ARRAYS-----------------------------------------------------------
    private List<HBox> hbsN = new ArrayList<>(MSQL.MAX_COLUMNS));// -----------
    private List<Label> lbsN = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<HBox> hbsName = new ArrayList<>(MSQL.MAX_COLUMNS));// -----------
    private List<TextField> tfsColumn = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> tfsColumnPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<Button> btnsRemoveColumn = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<Button> btnsAddColumn = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<Button> btnsRenameColumn = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<HBox> hbsType = new ArrayList<>(MSQL.MAX_COLUMNS));// -----------
    // private TextFieldAutoC[] tfasType = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<TextField> tfasType = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupAutoC> tfsTypePs = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> tfsTypePopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<TextField> tfsTypeLength = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> tfsTypeLengthPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<Button> btnsChangeType = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<HBox> hbsNull = new ArrayList<>(MSQL.MAX_COLUMNS));// -----------
    private List<CheckBox> cksNull = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<Button> btnsChangeNull = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<HBox> hbsPK = new ArrayList<>(MSQL.MAX_COLUMNS));// -----------
    private List<RadioButton> rbsPK = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> rbsPKPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    // private List<Button> btnsChangePK = new ArrayList<>(MSQL.MAX_COLUMNS));

    private List<HBox> hbsFK = new ArrayList<>(MSQL.MAX_COLUMNS));// -----------
    // private RadioList<Button> rbsFK = new ArrayList<>(MSQL.MAX_COLUMNS));
    // private TextFieldAutoC[] tfsFK = new ArrayList<>(MSQL.MAX_COLUMNS));
    // private List<PopupMessage> rbsFKPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<TextField> tfsFK = new ArrayList<>(MSQL.MAX_COLUMNS));
    private String[] pksReferences;
    private List<PopupAutoC> tfsFKPs = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> tfsFKPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<ToggleButton> btnsSelectedFK = new ArrayList<>(MSQL.MAX_COLUMNS));

    private List<HBox> hbsDefault = new ArrayList<>(MSQL.MAX_COLUMNS));// -----------
    private List<CheckBox> cksDefault = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> cksDefaultPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<TextField> tfsDefault = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> tfsDefaultPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<Button> btnsChangeDefault = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<HBox> hbsExtra = new ArrayList<>(MSQL.MAX_COLUMNS));// -----------
    private List<RadioButton> rbsExtra = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> rbsExtraPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    // private List<Button> btnsChangeExtra = new ArrayList<>(MSQL.MAX_COLUMNS));
    // RIGHT
    private List<ToggleButton> btnsDist = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> btnsDistPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<ToggleButton> btnsImageC = new ArrayList<>(MSQL.MAX_COLUMNS));
    private List<PopupMessage> btnsImageCPopups = new ArrayList<>(MSQL.MAX_COLUMNS));
    // RIGHT-SUB
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private PopupMessage tfImageCPathPopup;
    // BOTTOM
    // private Popup createHelpPopup = new Popup();
    private ObservableMap<String, Boolean> createHelpMap = FXCollections.observableHashMap();
    private PopupKV createHelpPopup = new PopupKV(createHelpMap);
    // ---------------------------------------------
    private VFController vf;
    private MSQLP ms;
    private TableS tables = TableS.getInstance();
    private SQLTypes types = SQLTypes.getInstance();
    // private Keys keys = Keys.getInstance();
    private PKS pks = PKS.getInstance();
    private FKS fks = FKS.getInstance();

    private Timers timers = Timers.getInstance(vf);
    private UpdateTable updateTable;
    private VCGridNodes storeNodes = VCGridNodes.getInstance();

    private Pattern patternBWTC = Pattern.compile("[A-Za-z](\\w| )*");
    private Pattern patternTypeLength = Pattern.compile("\\d{1,5}");
    private Matcher matcher;
    // ---------------------------------------------------
    private boolean tableOK = false;
    private boolean columnSNOK = false;
    private boolean columnBWOK = false;
    private boolean typeSelectionMatch = true;
    private boolean typeLengthOK = true;
    private boolean fkSelectionMatch = true;
    private boolean fkSelectionMatch2 = false;
    private boolean fkUpdate = false;
    private boolean defaultBW = true;
    private boolean defaultOK = true;

    private boolean extraPKOK = true;
    private boolean extraFKOK = true;
    private boolean extraDefaultOK = true;

    /**
     * Dist can't be use with Extra
     */
    private boolean distExtraOK = true;
    private boolean imageCPathOk = true;

    private boolean updateControl = false;
    // QOL-------------------------------------------------
    private int index;
    private String table;
    private String column;
    private String type;

    private void setQOLVariables(Event e) {
        if (e.getSource() instanceof ButtonBase) {
            index = Integer.parseInt(((ButtonBase) e.getSource()).getId());
        } else if (e.getSource() instanceof TextField) {
            index = Integer.parseInt(((TextField) e.getSource()).getId());
        }

        table = MSQL.getCurrentTable().getName().replace(" ", "_");
        if (index >= 0) {
            column = updateTable.getColumns().get(index).replace(" ", "_");
            type = tfasType.get(index).getText()
                    + (tfsTypeLength.get(index).isVisible() ? "(" + tfsTypeLength.get(index).getText() + ")" : "");
        }
    }

    // CONTROL---------------------------------------------
    private void createControl() {
        createHelpPopupReset();

        boolean allOk = tableOK && columnSNOK && columnBWOK && typeSelectionMatch && typeLengthOK && fkSelectionMatch
                && defaultBW && defaultOK && extraPKOK && extraFKOK && extraDefaultOK && distExtraOK && imageCPathOk;

        btnCreateUpdate.setDisable(!allOk);
    }

    void masterControl() {
        if (!updateControl) {
            createControl();
        } else {

        }
    }

    // LISTENERS---------------------------------------------
    // TABLE-------------------------------------
    private void tfTableKeyReleased(KeyEvent e) {
        if (!e.getCode().isArrowKey() && !e.getCode().isFunctionKey() && !e.getCode().isMediaKey()
                && !e.getCode().isModifierKey() && !e.getCode().isNavigationKey()) {
            String[] tableList = tables.getTables();
            String text = tfTable.getText().toLowerCase().trim().replace(" ", "_");

            matcher = patternBWTC.matcher(text);
            if (matcher.matches()) {
                if (MList.isOnThisList(tableList, text, true)) {
                    lbTable.setText("This table already exist");
                    lbTable.setTextFill(NonCSS.TEXT_FILL_ERROR);
                    tableOK = false;
                } else {
                    lbTable.setText("Table Name");
                    lbTable.setTextFill(NonCSS.TEXT_FILL);
                    tableOK = true;
                }
            } else {
                lbTable.setText("Not Accepted Characters");
                lbTable.setTextFill(NonCSS.TEXT_FILL_ERROR);
                tableOK = false;
            }

            // UPDATE-----------------------------------------------
            tfTableUpdate(text);
            // -----------------------------------------
            masterControl();
        }

    }

    private void tfTableUpdate(String text) {
        if (updateControl) {
            if (tableOK) {
                String tableO = updateTable.getTable().toLowerCase().trim().replace(" ", "_");
                if (!text.equals(tableO)) {
                    tfTable.setStyle(CSS.TEXT_FILL);
                    btnRenameTable.setDisable(false);
                } else {
                    tfTable.setStyle(CSS.TEXT_FILL_HINT);
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
        String oldTable = MSQL.getCurrentTable().getName().toLowerCase().trim().replace(" ", "_");
        String newTable = tfTable.getText().toLowerCase().trim().replace(" ", "_");
        boolean renameTable = ms.renameTable(oldTable, newTable);
        if (renameTable) {
            boolean updateTableNames = ms.updateRow(MSQL.TABLE_NAMES, "Name", oldTable.replace("_", " "), "Name",
                    newTable.replace("_", " "));
            if (updateTableNames) {
                updateTable.setTable(newTable);

                try {
                    ResultSet rs = ms.selectRow(MSQL.TABLE_NAMES, "Name", newTable.replace("_", " "));
                    Table ctable = null;
                    while (rs.next()) {
                        System.out.println("TEST HAPPEN");
                        ctable = new Table(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                                rs.getString(5));
                    }
                    MSQL.setCurrentTable(ctable);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                // Menus.getInstance(vf).addMenuItemsReset();// NOT TESTED
                btnRenameTable.setDisable(true);

                lbStatus.setText("Table '" + oldTable + "' has been rename to '" + newTable + "'", NonCSS.TEXT_FILL_OK,
                        Duration.seconds(2));
                System.out.println("\ttable Renamed!");
            } else {
                lbStatus.setText("FATAL: table change its name but " + MSQL.TABLE_NAMES + " hasn't been updated",
                        NonCSS.TEXT_FILL_ERROR);
                System.out.println("\tFATAL!");

            }

        } else {
            lbStatus.setText("Table '" + oldTable + "' fail to be renamed", NonCSS.TEXT_FILL_ERROR);
            System.out.println("\tERROR!");

        }
    }

    // COLUMNS=====================================================
    private void tfsColumnsKeyReleased(KeyEvent e) {
        setQOLVariables(e);

        String text = tfsColumn.get(index).getText().toLowerCase().trim().replace(" ", "_");
        listColumns.set(index, text);

        if (!text.trim().isEmpty()) {
            matcher = patternBWTC.matcher(text);
            if (matcher.matches()) {
                tfsColumn.get(index).setStyle(CSS.TEXT_FILL);
                // popupHide(tf);
                tfsColumnPopups.get(index).hide();

                columnBWOK = true;// REST CONTROL
                tfsColumnControl(index);
            } else {
                tfsColumn.get(index).setStyle(CSS.TEXT_FILL_ERROR);
                // popupShow(tf, ILLEGAL_CHARS);
                tfsColumnPopups.get(index).show(ILLEGAL_CHARS);
                columnBWOK = false;
            }
        } else {
            tfsColumnPopups.get(index).show(EMPTY_TEXT);
            columnBWOK = false;
        }
        // UPDATE---------------------------------------------------
        tfsColumnUpdate(index, text);
        // ---------------------------------------------------------
        masterControl();
    }

    private void listColumnsChange(Change<? extends String> c) {
        while (c.next()) {
            if (c.wasReplaced() || c.wasAdded() || c.wasRemoved()) {
                if (MList.areTheyDuplicatedElementsOnList(listColumns)) {
                    lbhColumnNames.setText(LBH_COLUMN_NAMES_ERROR);
                    lbhColumnNames.setStyle(CSS.TEXT_FILL_ERROR);
                    columnSNOK = false;
                } else {
                    lbhColumnNames.setText(LBH_COLUMN_NAMES);
                    lbhColumnNames.setStyle(CSS.TEXT_FILL);
                    columnSNOK = true;
                }
            }
        }
    }

    private void tfsColumnControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {
                String text = tfsColumn.get(a).getText().trim().replace(" ", "_");
                matcher = patternBWTC.matcher(text);
                if (text.trim().isEmpty()) {
                    columnBWOK = false;
                    break;
                } else {
                    if (!matcher.matches()) {
                        columnBWOK = false;
                        break;
                    }
                }

            }
        }
    }

    private void tfsColumnUpdate(int index, String text) {
        if (updateControl) {
            if (columnBWOK && columnSNOK) {
                if (!text.equals(column)) {
                    // tfsColumnPopups[index].hide();
                    tfsColumn.get(index).setStyle(CSS.TEXT_FILL);
                    btnsRenameColumn.get(index).setDisable(false);
                } else {
                    // tfsColumnPopups[index].show(SAME_VALUE);
                    tfsColumn.get(index).setStyle(CSS.TEXT_FILL_HINT);
                    btnsRenameColumn.get(index).setDisable(true);
                }
            } else {
                btnsRenameColumn.get(index).setDisable(true);
            }
        }
    }

    void btnsRenameColumn(ActionEvent e) {
        System.out.println(CC.CYAN + "\nRename Column" + CC.RESET);
        setQOLVariables(e);
        String newColumn = tfsColumn.get(index).getText().toLowerCase().trim().replace(" ", "_");

        boolean renameColumn = ms.renameColumn(table, column, newColumn);
        if (renameColumn) {
            System.out.println("\tSUCCESS");
            // RENAME THE COLUMN AND THEN TEST THE CANCEL BUTTON TO RESET
            updateTable.getColumns().set(index, newColumn);

            btnsRenameColumn.get(index).setDisable(true);
            lbStatus.setText("Column '" + column + "' changed to '" + newColumn + "'", NonCSS.TEXT_FILL_OK,
                    Duration.seconds(2));
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to be renamed", NonCSS.TEXT_FILL_ERROR);
        }
    }

    // ADD OR REMOVE COLUMNS-------------------------------------
    // CREATE=========================================
    void btnsAddCreateAction(ActionEvent e) {
        setQOLVariables(e);
        index++;
        int row = index + 1;

        btnsAddColumn.get(index - 1).setVisible(false);
        btnsRemoveColumn.get(index - 1).setVisible(false);
        btnsAddColumn.get(index).setVisible(true);
        btnsRemoveColumn.get(index).setVisible(true);
        // --------------------------------------------------
        gridPaneLeft.add(hbsN.get(index), 0, row);
        gridPaneLeft.add(hbsName.get(index), 1, row);
        gridPaneLeft.add(hbsType.get(index), 2, row);
        gridPaneLeft.add(hbsNull.get(index), 3, row);
        gridPaneLeft.add(hbsPK.get(index), 4, row);
        gridPaneLeft.add(hbsFK.get(index), 5, row);
        gridPaneLeft.add(hbsDefault.get(index), 6, row);
        gridPaneLeft.add(hbsExtra.get(index), 7, row);

        gridPaneRight.add(btnsDist.get(index), 0, row);
        gridPaneRight.add(btnsImageC.get(index), 1, row);
        // --------------------------------------------------
        listColumns.add(tfsColumn.get(index).getText());
        listImageC.add(false);
        // --------------------------------------------------
        currentRowLength++;
        tfsColumnControl(-1);
        tfasTypeControl(-1);
        tfsTypeLengthControl(-1);
        tfasFKControl(-1, true);
        tfsDefaultControl(-1);
        masterControl();
    }

    void btnsRemoveCreateAction(ActionEvent e) {
        setQOLVariables(e);
        btnsAddColumn.get(index - 1).setVisible(true);
        btnsRemoveColumn.get(index - 1).setVisible(true);
        // ---------------------------------------------------------
        gridPaneLeft.getChildren().removeAll(hbsN.get(index), hbsName.get(index), hbsType.get(index), hbsNull.get(index), hbsPK.get(index),
                hbsFK.get(index), hbsDefault.get(index), hbsExtra.get(index));

        gridPaneRight.getChildren().removeAll(btnsDist.get(index), btnsImageC.get(index));
        // ---------------------------------------------------------
        listColumns.remove(index);
        listImageC.remove(index);
        // ---------------------------------------------------------
        currentRowLength--;
        tfsColumnControl(-1);
        tfasTypeControl(-1);
        tfsTypeLengthControl(-1);
        tfasFKControl(-1, true);
        tfsDefaultControl(-1);

        masterControl();
    }

    // UPDATE========================================
    private void updateAddOrRemoveVisible2(int index){

    }

    private void updateAddOrRemoveVisible(int index, boolean add) {
        // ARRAY STORE CREATION---------------------------
        int storeLength;
        if (add) {
            storeLength = currentRowLength - index + 1; // +1 = empty row
        } else {
            storeLength = currentRowLength - index;
        }

        storeNodes.setColumnNames(new String[storeLength]);
        storeNodes.setTypes(new String[storeLength]);
        storeNodes.setTypesLength(new String[storeLength]);
        storeNodes.setNulls(new boolean[storeLength]);
        storeNodes.setPks(new boolean[storeLength]);
        storeNodes.setFks(new boolean[storeLength]);
        storeNodes.setFksText(new String[storeLength]);
        storeNodes.setDefaults(new boolean[storeLength]);
        storeNodes.setDefaultsText(new String[storeLength]);
        storeNodes.setExtra(new boolean[storeLength]);

        storeNodes.setDists(new boolean[storeLength]);
        storeNodes.setImageCs(new boolean[storeLength]);
        // STORE-----------------------------------------------------
        for (int a = 0; a < storeLength; a++) {
            int row = a + index;
            int rowE;
            if (add) {
                rowE = row - 1;//NEW ROW
            } else {
                rowE = row;//AFTER NEW ROW
            }
            if (row == index && add) {// NEW COLUMN
                storeNodes.getColumnNames()[a] = "";
                storeNodes.getTypes()[a] = "";
                storeNodes.getTypesLength()[a] = "";
                storeNodes.getNulls()[a] = false;
                storeNodes.getPks()[a] = false;
                storeNodes.getFks()[a] = false;
                storeNodes.getFksText()[a] = "";
                storeNodes.getDefaults()[a] = false;
                storeNodes.getDefaultsText()[a] = "";
                storeNodes.getExtra()[a] = false;

                storeNodes.getDists()[a] = false;
                storeNodes.getImageCs()[a] = false;
            } else {
                storeNodes.getColumnNames()[a] = tfsColumn[rowE].getText();
                storeNodes.getTypes()[a] = tfasType[rowE].getText();
                storeNodes.getTypesLength()[a] = tfsTypeLength[rowE].getText();
                storeNodes.getNulls()[a] = cksNull[rowE].isSelected();
                storeNodes.getPks()[a] = rbsPK[rowE].isSelected();
                // storeNodes.getFks()[a] = rbsFK[rowE].isSelected();
                storeNodes.getFksText()[a] = tfsFK[rowE].getText();
                storeNodes.getDefaults()[a] = cksDefault[rowE].isSelected();
                storeNodes.getDefaultsText()[a] = tfsDefault[rowE].getText();
                storeNodes.getExtra()[a] = rbsExtra[rowE].isSelected();

                storeNodes.getDists()[a] = btnsDist[rowE].isSelected();
                storeNodes.getImageCs()[a] = btnsImageC[rowE].isSelected();
            }
        }
        // REMOVE ROWS----------------------------------------
        int deleteSize;
        if (add) {
            deleteSize = storeLength - 1;
        } else {
            deleteSize = currentRowLength - index - 1;
        }
        for (int a = 0; a < deleteSize; a++) {// AFTER INDEX (NOT THE EXTRA ADDED)
            int row;
            if (add) {
                row = index + a;
            } else {
                row = a;
            }

            gridPaneLeft.getChildren().removeAll(hbsN.get(row), hbsName.get(row), hbsType.get(row), hbsNull.get(row), hbsPK.get(row),
                    hbsFK.get(row), hbsDefault.get(row), hbsExtra.get(row));

            gridPaneRight.getChildren().removeAll(btnsDist.get(row), btnsImageC.get(row));
        }

        // REPLACE ARRAY ELEMENTS-------------------------------
        leftGridPaneRestart(index, add);
        rightGridPaneRestart(index, add);
        // ADD ROWS-----------------------------------------
        for (int a = 0; a < storeLength; a++) {
            int row = index + a;
            int aRow = row + 1;
            gridPaneLeft.add(hbsN.get(row), 0, aRow);
            gridPaneLeft.add(hbsName.get(row), 1, aRow);
            gridPaneLeft.add(hbsType.get(row), 2, aRow);
            gridPaneLeft.add(hbsNull.get(row), 3, aRow);
            gridPaneLeft.add(hbsPK.get(row), 4, aRow);
            gridPaneLeft.add(hbsFK.get(row), 5, aRow);
            gridPaneLeft.add(hbsDefault.get(row), 6, aRow);
            gridPaneLeft.add(hbsExtra.get(row), 7, aRow);

            gridPaneRight.add(btnsDist.get(row), 0, aRow);
            gridPaneRight.add(btnsImageC.get(row), 1, aRow);
        }

    }

    void btnsColumnSetVisibleAction(ActionEvent e) {
        setQOLVariables(e);
        index++;// PLUS HEADER
        // first(index);
        updateAddOrRemoveVisible(index, true);
        column = tfsColumn.get(index - 1).getText().toLowerCase().trim().replace(" ", "_");
        listColumns.add(index - 1, column);

        tfsColumnControl(-1);
        tfasTypeControl(-1);
        tfsTypeLengthControl(-1);
        tfasFKControl(-1, true);
        tfsDefaultControl(-1);
        masterControl();
    }

    private void btnsAddColumnUpdateAction(ActionEvent e) {
        System.out.println(CC.CYAN + "\nADD COLUMN" + CC.RESET);
        setQOLVariables(e);

        String afterColumn;
        boolean addColumn;
        try {// ATTEMPTING TO GRAB THE PREVIOUS COLUMN
            afterColumn = updateTable.getColumns().get(index - 1).toLowerCase().trim().replace(" ", "_");

            addColumn = ms.addColumn(table, column, type, afterColumn);
        } catch (ArrayIndexOutOfBoundsException ex) {
            addColumn = ms.addColumn(table, column, type);
        }

        if (addColumn) {
            System.out.println("\tSUCCES");
            /*
             * IF BTN-DIST IS ENABLED String dist = Custom.getDist(updateTable.getDist());
             * if(!dist.equals(distN)){ }
             */
            updateTable.getColumns().add(index, column);
            updateTable.getTypes().add(index, tfasType.get(index).getText());
            updateTable.getTypesLength().add(index, Integer.parseInt(tfsTypeLength.get(index).getText()));
            updateTable.getNulls().add(index, cksNull.get(index).isSelected());
            updateTable.getDefaults().add(index, cksDefault.get(index).isSelected() ? tfsDefault.get(index).getText() : null);

            lbStatus.setText("Added column '" + column + "' to '" + MSQL.getCurrentTable().getName() + "'",
                    NonCSS.TEXT_FILL_OK, Duration.seconds(2));
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Couldn't add column '" + column + "'", NonCSS.TEXT_FILL_ERROR);
        }

    }

    // NOT FINISHED
    private void btnsCancelVisibleAction(ActionEvent e) {
        setQOLVariables(e);
        index++;// PLUS HEADER

        updateAddOrRemoveVisible(index, false);
        listColumns.remove(index - 1);

        tfsColumnControl(-1);
        tfasTypeControl(-1);
        tfsTypeLengthControl(-1);
        tfasFKControl(-1, true);
        tfsDefaultControl(-1);
        masterControl();
    }

    private void btnsCancelAddColumnUpdateAction(ActionEvent e) {
        btnsCancelVisibleAction(e);
    }

    void btnsRemoveUpdateAction(ActionEvent e) {
        btnsCancelVisibleAction(e);
    }

    // TYPES=====================================================
    private void tfasTypeTextProperty(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        index = Character.getNumericValue(tf.getId().charAt(tf.getId().length() - 1));
        boolean update = false;

        newValue = newValue.trim();
        matcher = patternBWTC.matcher(newValue);
        if (matcher.matches()) {
            if (MList.isOnThisList(tfsTypePs.get(index).getLv().getItems(), newValue, false)) {
                int typeLength = types.getTypeLength(newValue);
                if (typeLength > 0) {// TF-TYPE-LENGTH-POPUP
                    tfsTypeLength.get(index).setVisible(true);
                    tfsTypeLength.get(index).setText(Integer.toString(typeLength));
                } else {
                    tfsTypeLength.get(index).setVisible(false);
                    tfsTypeLength.get(index).setText("1");
                    typeLengthOK = true;
                }

                tf.setStyle(CSS.TEXT_FILL);
                tfsTypePopups.get(index).hide();

                typeSelectionMatch = true;
                update = true;
                tfasTypeControl(index);
            } else {
                tfsTypeLength.get(index).setVisible(false);
                tfsTypeLength.get(index).setText("1");

                tf.setStyle(CSS.TEXT_FILL_ERROR);
                tfsTypePopups.get(index).show(SELECTION_UNMATCH);

                typeSelectionMatch = false;
            }
        } else {
            tf.setStyle(CSS.TEXT_FILL_ERROR);
            tfsTypePopups.get(index).show(ILLEGAL_CHARS);

            typeSelectionMatch = false;
        }
        tfsDefaultTypeControl(index);
        // UPDATE----------------------------------------------------------
        tfsTypeUpdate(index, newValue, update);
        // ----------------------------------------------------------
        masterControl();
    }

    private void tfsTypeLengthTextProperty(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        if (tf.isVisible()) {
            index = Integer.parseInt(tf.getId());

            String text = tfsTypeLength.get(index).getText().toLowerCase().trim();
            int typeMaxLength = types.getTypeMaxLength(tfasType.get(index).getText());
            boolean update = false;

            matcher = patternTypeLength.matcher(text);
            if (matcher.matches()) {
                int length = Integer.parseInt(text);
                if (length <= typeMaxLength) {
                    tf.setStyle(CSS.TEXT_FILL);
                    tfsTypeLengthPopups.get(index).hide();

                    typeLengthOK = true;
                    update = true;
                    tfsTypeLengthControl(index);
                } else {
                    tf.setStyle(CSS.TEXT_FILL_ERROR);
                    tfsTypeLengthPopups.get(index).show("Wrong length (1 to " + typeMaxLength + ")");

                    typeLengthOK = false;
                }
            } else {
                tf.setStyle(CSS.TEXT_FILL_ERROR);
                tfsTypeLengthPopups.get(index).show("Wrong length (1 to " + typeMaxLength + ")");

                typeLengthOK = false;

            }
            tfsDefaultTypeControl(index);
            // UPDATE-----------------------------------------------------
            tfsTypeLengthUpdate(index, text, update);
        }
        // -----------------------------------------------------
        masterControl();
    }

    private void tfasTypeControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (index != a) {
                // String text = tfasType[a].getTf().getText();
                String text = tfasType.get(a).getText();
                matcher = patternBWTC.matcher(text);
                if (matcher.matches()) {
                    if (!MList.isOnThisList(/* tfasType[a].getLv().getItems() */ tfsTypePs.get(a).getLv().getItems(), text,
                            false)) {
                        typeSelectionMatch = false;
                        break;
                    }
                } else {
                    typeSelectionMatch = false;
                    break;
                }
            }

        }
    }

    private void tfsTypeLengthControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (index != a) {
                String text = tfsTypeLength.get(a).getText();
                int typeMaxLength = types.getTypeMaxLength(tfasType.get(a).getText());

                matcher = patternTypeLength.matcher(text);
                if (matcher.matches()) {
                    int length = Integer.parseInt(text);
                    if (length > typeMaxLength) {// IF NOT
                        typeLengthOK = false;
                        break;
                    }
                } else {
                    typeLengthOK = false;
                    break;
                }
            }
        }
    }

    private void tfsTypeUpdate(int index, String newValue, boolean update) {
        if (updateControl) {
            if (update) {
                type = updateTable.getTypes().get(index);
                if (!newValue.equals(type)) {
                    // tfsTypePopups[index].hide();
                    tfasType.get(index).setStyle(CSS.TEXT_FILL);
                    btnsChangeType.get(index).setDisable(false);
                } else {
                    tfasType.get(index).setStyle(CSS.TEXT_FILL_HINT);
                    btnsChangeType.get(index).setDisable(true);
                    // tfsTypePopups[index].show(SAME_VALUE);
                }
            } else {
                btnsChangeType.get(index).setDisable(true);
            }
        }
    }

    private void tfsTypeLengthUpdate(int index, String text, boolean update) {
        if (updateControl) {
            if (update) {
                String typeLength = Integer.toString(updateTable.getTypesLength().get(index)).toLowerCase().trim();
                if (!text.equals(typeLength)) {
                    // tfsTypeLengthPopups[index].hide();
                    tfsTypeLength.get(index).setStyle(CSS.TEXT_FILL);
                    btnsChangeType.get(index).setDisable(false);
                } else {
                    // tfsTypeLengthPopups[index].show(SAME_VALUE);
                    tfsTypeLength.get(index).setStyle(CSS.TEXT_FILL_HINT);
                    btnsChangeType.get(index).setDisable(true);
                }
            } else {
                btnsChangeType.get(index).setDisable(true);
            }
        }
    }

    void btnsChangeType(ActionEvent e) {
        System.out.println(CC.CYAN + "\nChange Type" + CC.RESET);

        setQOLVariables(e);
        boolean changeType = ms.changeType(table, column, type);
        if (changeType) {
            System.out.println("\tSUCCESS");

            updateTable.getTypes().set(index, tfasType.get(index).getText());
            updateTable.getTypesLength().set(index,
                    tfsTypeLength.get(index).isVisible() ? Integer.parseInt(tfsTypeLength.get(index).getText()) : 0);

            lbStatus.setText("Column '" + column + "' has change it's type to '" + type + "'", NonCSS.TEXT_FILL_OK,
                    Duration.seconds(2));

            btnsChangeType.get(index).setDisable(true);
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to change it's type", NonCSS.TEXT_FILL_ERROR);
        }

    }

    // NULLS======================================================
    void cksNullAction(ActionEvent e) {
        setQOLVariables(e);
        boolean nulllO = updateTable.getNulls().get(index);
        boolean nulll = cksNull.get(index).isSelected();

        if (nulllO != nulll) {
            cksNull.get(index).setStyle(CSS.CKS_BG);
            btnsChangeNull.get(index).setDisable(false);
        } else {
            cksNull.get(index).setStyle(CSS.CKS_BG_HINT);
            btnsChangeNull.get(index).setDisable(true);
        }
    }

    void btnsChangeNull(ActionEvent e) {
        System.out.println(CC.CYAN + "Change Null" + CC.RESET);
        setQOLVariables(e);
        type = updateTable.getTypes().get(index)
                + (updateTable.getTypesLength().get(index) > 0 ? "(" + updateTable.getTypesLength().get(index) + ")"
                        : "");
        boolean nulll = cksNull.get(index).isSelected();
        // ----------------------------------------------
        ms.setNullValue(nulll);
        ms.setExtraValue(index == updateTable.getExtra());
        boolean changeNull = ms.changeType(table, column, type);
        if (changeNull) {
            System.out.println("\tSUCCESS");
            updateTable.getNulls().set(index, nulll);

            cksNull.get(index).setStyle(CSS.CKS_BG_HINT);
            btnsChangeNull.get(index).setDisable(true);

            lbStatus.setText("Column '" + column + "' change to " + (nulll ? "NULL" : "NOT NULL"), NonCSS.TEXT_FILL_OK,
                    Duration.seconds(2));
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to be changed", NonCSS.TEXT_FILL_ERROR);
        }
    }

    // PKS/FKS======================================================
    void cksPKAction(ActionEvent e) {
        // ONLY FOR UPDATE
        setQOLVariables(e);
        boolean update = false;

        for (int c = 0; c < currentRowLength; c++) {
            boolean pkSelected = updateTable.getPks().get(c).equals("Yes");
            if (rbsPK.get(c).isSelected() != pkSelected) {
                update = true;
                break;
            }
        }

        if (update) {
            btnUpdatePK.setDisable(false);
        } else {
            btnUpdatePK.setDisable(true);
        }
    }

    void btnUpdatePK(ActionEvent e) {
        System.out.println(CC.CYAN + "Update PK" + CC.RESET);
        setQOLVariables(e);

        String[] errorMessage = { null };
        ms.setSQLException((ex, s) -> errorMessage[0] = ex.getMessage());
        boolean dropPK = true;
        if (updateTable.getPks().stream().anyMatch(p -> p.equals("Yes"))) {
            dropPK = ms.dropPrimaryKey(table);
        }

        int[] indexs = { 0 };
        if (dropPK) {
            if (rbsPK.stream().anyMatch(rb -> rb.isSelected())) {
                // ADDING PK------------------------------------------
                List<String> cols = new ArrayList<>(currentRowLength);
                indexs[0] = 0;
                rbsPK.forEach(el -> {
                    if (el.isSelected()) {
                        cols.add(updateTable.getColumns().get(indexs[0]).replace(" ", "_"));
                        updateTable.getPks().set(indexs[0], "Yes");
                    }
                    indexs[0]++;
                });
                boolean addPK = true;
                if (!cols.isEmpty()) {
                    addPK = ms.addPrimaryKey(table, cols.toArray(new String[cols.size()]));
                }
                if (addPK) {
                    btnUpdatePK.setDisable(true);

                    lbStatus.setText("Changed Primary Key", NonCSS.TEXT_FILL_OK, Duration.seconds(2));
                    System.out.println("\tSUCCESS");
                } else {
                    lbStatus.setText(errorMessage[0] != null ? errorMessage[0] : "Failt to Change Primary Key",
                            NonCSS.TEXT_FILL_ERROR);
                    System.out.println("\tFAILED");

                }
            } else {
                // SET PK TO UPDATE-TABLE---------------------------
                indexs[0] = 0;
                rbsPK.forEach(rb -> {
                    if (!rb.isSelected() != updateTable.getPks().get(indexs[0]).equals("Yes")) {
                        updateTable.getPks().set(indexs[0], "No");
                    }
                    indexs[0]++;
                });

                lbStatus.setText("Primary key has been deleted", NonCSS.TEXT_FILL_OK, Duration.seconds(2));
                System.out.println("\tSUCCESS");
            }
        } else {
            lbStatus.setText(errorMessage[0] != null ? errorMessage[0] : "Failt to delete Primary Key",
                    NonCSS.TEXT_FILL_ERROR);
            System.out.println("\tFAILED");
        }
    }

    // FKS==========================================================
    private void tfasFKTextProperty(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        index = Integer.parseInt(tf.getId());

        if (tf.isVisible()) {
            if (MList.isOnThisList(tfsFKPs.get(index).getLv().getItems(), newValue, false)) {
                tf.setStyle(CSS.TEXT_FILL);
                tfsFKPopups.get(index).hide();

                fkSelectionMatch = true;
                tfasFKControl(index, true);
            } else {
                tf.setStyle(CSS.TEXT_FILL_ERROR);
                tfsFKPopups.get(index).show(SELECTION_UNMATCH);

                fkSelectionMatch = false;
            }
        } else {
            tfsFKPopups.get(index).hide();

            fkSelectionMatch = true;
            tfasFKControl(index, true);
        }

        // UPDATE----------------------------------------------
        // fkUpdateControl(index, false);
        newFKControl();
        // ----------------------------------------------
        masterControl();
    }

    private void tfasFKControl(int index, boolean all) {
        for (int a = 0; a < currentRowLength; a++) {
            if ((a != index && tfsFK.get(a).isVisible() && all) || (a == index && !all)) {
                String text = tfsFK.get(a).getText();
                if (!MList.isOnThisList(tfsFKPs.get(a).getLv().getItems(), text, false)) {
                    fkSelectionMatch = false;
                    break;
                }

            }
        }
    }

    private void fkUpdateControl(int index, boolean cks) {
        if (updateControl) {
            if (fkSelectionMatch) {
                boolean update = false;
                // DEPENDS ON TFS-FK TO
                for (int c = 0; c < currentRowLength; c++) {
                    boolean fkSelectedO = updateTable.getFks().get(c).equals("Yes");
                    String fkText = updateTable.getFkFormed().get(c);

                    if (!tfsFK.get(c).getText().trim().isEmpty() != fkSelectedO
                            || (tfsFK.get(c).getText().trim().isEmpty() && !tfsFK.get(c).getText().equals(fkText))) {
                        update = true;
                        break;
                    }
                }
                if (update) {
                    if (cks) {
                        // rbsFK[index].setStyle(CSS.BG_COLOR);
                    } else {
                        tfsFK.get(index).setStyle(CSS.TEXT_FILL);
                    }
                    fkUpdate = true;
                    // cksFKPopups[index].hide();

                } else {
                    if (cks) {
                        // rbsFK[index].setStyle(CSS.BG_COLOR_HINT);
                    } else {
                        tfsFK.get(index).setStyle(CSS.TEXT_FILL_HINT);
                    }
                    fkUpdate = false;
                    // cksFKPopups[index].show(SAME_VALUE);
                }
            } else {
                fkUpdate = false;
            }

            btnUpdateFK.setDisable(
                    !(fkUpdate && btnsSelectedFK.stream().anyMatch(btn -> btn.isSelected())));

        }
    }

    private void newFKControl() {
        // fkSelectionMatch2 = true;
        int[] indexs = { 0 };
        int[] countMatchSingleAction = { 0 };
        int[] countMatchSelection = { 0 };
        boolean[] actionAdd = { false };
        boolean[] actionRem = { false };
        int[] splitLength = { 0 };

        Stream<ToggleButton> list = btnsSelectedFK.stream().filter(ToggleButton::isSelected);
        // list.filter(ToggleButton::isSelected).forEach(btn -> {});
        list.forEach(btn -> {
            int id = Integer.parseInt(btn.getId());
            // ALL OF THEM HAVE TO HAVE THE SAME ACTION
            if (btn.getText().contains("ADD") && !actionRem[0]) {
                actionAdd[0] = true;
                countMatchSingleAction[0]++;
            } else if (btn.getText().contains("REM") && !actionAdd[0]) {
                String text = tfsFK.get(id).getText();
                text = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
                // MIX FK----------------
                splitLength[0] = text.split(",").length;

                actionRem[0] = true;
                countMatchSingleAction[0]++;
            }

            String text = tfsFK.get(id).getText();
            System.out.println("Text: [" + text + "]");
            if (Arrays.asList(pksReferences).stream().anyMatch(s -> {
                System.out.println("\ts: [" + s + "]");
                return s.equals(text);
            })) {
                countMatchSelection[0]++;
            }
            indexs[0]++;
        });
        // time_stamp.radotable3 (id,name_updated)
        // time_stamp.radotable3 (id,YAM2)
        boolean disable = indexs[0] == countMatchSingleAction[0] && indexs[0] == countMatchSelection[0] && indexs[0] > 0
                && (actionRem[0] ? indexs[0] == splitLength[0] : true);
        btnUpdateFK.setDisable(!disable);

    }

    void btnsSelectedFK(ActionEvent e) {
        setQOLVariables(e);

        if (!btnsSelectedFK.get(index).getText().contains("REM")) {
            tfsFK.get(index).setVisible(btnsSelectedFK.get(index).isSelected());
        }
        // ---------------------------------------------------------
        int[] indexs = { -1 };
        int[] scount = { 0 };
        boolean[] actionAdd = { false };
        boolean[] actionRem = { false };

        btnsSelectedFK.forEach(btn -> {
            indexs[0]++;
            if (currentRowLength > indexs[0]) {
                if (btn.isSelected()) {
                    scount[0]++;
                    if (updateTable.getFks().get(indexs[0]).equals("No") && !actionRem[0]) {
                        actionAdd[0] = true;
                        if (scount[0] == 1) {
                            btn.setText("ADD");
                        } else if (scount[0] > 1) {
                            // btn.setText("ADD (A)");
                            btnsSelectedFK.stream().filter(btnn -> btnn.isSelected()).forEach(btnn -> btnn.setText("ADD (A)"));
                        }
                    } else if (updateTable.getFks().get(indexs[0]).equals("Yes") && !actionAdd[0]) {
                        actionRem[0] = true;
                        if (!btn.getText().equals("REM (A)")) {
                            btn.setText("REM");
                        }
                    }
                } else {
                    if (updateTable.getFks().get(indexs[0]).equals("No")) {
                        btn.setText("ADD");
                    }
                }
            }
        });
        // ----------------------------------------
        // btnUpdateFK.setDisable(!(fkUpdate &&
        // Arrays.asList(btnsSelectedFK).stream().anyMatch(btn -> btn.isSelected())));
        tfasFKControl(-1, true);
        // UPDATE------------------------------------------------
        // fkUpdateControl(index, true);
        newFKControl();
        // ------------------------------------------------
        masterControl();
    }

    void btnUpdateFKS(ActionEvent e) {
        System.out.println(CC.CYAN + "Update FK" + CC.RESET);
        setQOLVariables(e);

        // FIRST INDEX-------------------------------
        int[] indexs = { -1 };

        btnsSelectedFK.stream().anyMatch(btn -> {
            indexs[0]++;
            return btn.isSelected();
        });
        // EXCEPTION CONTROL-------------------------
        String[] errorMessage = { null };
        ms.setSQLException((ex, s) -> {
            if (ex.getMessage().contains("Cannot add or update a child row: a foreign key constraint fails")) {
                errorMessage[0] = "Cannot add or update a child row: one or more rows in this table doesn’t match the pk values of the referenced table, so this FOREIGN KEY can be added";
            } else {
                errorMessage[0] = ex.getMessage();
            }
        });
        // DROP FK-----------------------------------
        boolean dropFK = true;
        if (updateTable.getFks().get(indexs[0]++).equals("Yes")) {// DROP FOREIGN KEY
            String constraint = fks.getConstraintName(MSQL.getDatabase(), table, indexs[0]++);
            dropFK = ms.dropForeignKey(table, constraint);
        }
        if (dropFK) {
            List<Integer> indexsList = new ArrayList<>(MSQL.MAX_COLUMNS);

            List<String> cols = new ArrayList<>(currentRowLength);
            indexs[0] = 0;
            btnsSelectedFK.forEach(btn -> {
                if (btn.isSelected()) {
                    indexs[0] = Integer.parseInt(btn.getId());
                    indexsList.add(indexs[0]);
                    cols.add(updateTable.getColumns().get(indexs[0]).replace(" ", "_"));
                }
            });
            if (btnsSelectedFK.get(indexs[0]++).getText().contains("ADD")) {
                // ADD VALUES---------------------------------
                String tableReference;

                String text = tfsFK.get(indexs[0]).getText();
                tableReference = text.substring(text.indexOf(".") + 1, text.indexOf("(") - 1);
                text = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
                String[] columnsReference = text.split(",");

                System.out.println("\ttable: " + table);
                System.out.println("\tcolumn: ");
                cols.forEach(System.out::println);
                System.out.println("\ttableReference: " + tableReference);
                System.out.println("\tcolumnsReference: " + columnsReference);
                // CONSTRAINT NAME EX:fk_messages_users_user_id-----------------------
                StringBuilder sb = new StringBuilder();
                sb.append("fk__").append(table).append("__").append(tableReference).append("__");
                Arrays.asList(columnsReference).forEach(s -> sb.append(s).append("__"));
                sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
                ms.setConstraintName(sb.toString());
                // -------------------------------------------
                boolean addFK = ms.addForeignKey(table, cols.toArray(new String[cols.size()]), tableReference,
                        columnsReference);
                // RESULTS-------------------------------------
                if (addFK) {

                    btnUpdateFK.setDisable(true);
                    indexsList.forEach(i -> {
                        updateTable.getFks().set(i, "Yes");
                        btnsSelectedFK.get(i).setSelected(false);
                    });

                    if (indexsList.size() == 1) {
                        btnsSelectedFK.get(indexsList.get(0)).setText("REM");
                    } else if (indexsList.size() > 1) {
                        indexsList.forEach(i -> btnsSelectedFK.get(i).setText("REM (A)"));
                    }

                    lbStatus.setText("FOREIGN KEY added!", NonCSS.TEXT_FILL_OK, Duration.seconds(2));
                    System.out.println("\tSUCCESS");
                } else {
                    // FK FAIL ADDED
                    System.out.println("\tFAILED");
                    lbStatus.setText(errorMessage[0] != null ? errorMessage[0] : "Fail to add FOREING KEY",
                            NonCSS.TEXT_FILL_ERROR);
                }
            } else {
                // FK REMOVED
                btnUpdateFK.setDisable(true);
                indexsList.forEach(i -> {
                    updateTable.getFks().set(i, "No");
                    btnsSelectedFK.get(i).setSelected(false);
                    btnsSelectedFK.get(i).setText("ADD");
                });

                lbStatus.setText("FOREIGN KEY removed!", NonCSS.TEXT_FILL_OK, Duration.seconds(2));
                System.out.println("\tSUCCESS");
            }
        } else {
            // FK FAIL REMOVE
            System.out.println("\tFAILED");
            lbStatus.setText(errorMessage[0] != null ? errorMessage[0] : "Fail to add FOREING KEY",
                    NonCSS.TEXT_FILL_ERROR);
        }
    }

    // DEFAULTS=================================================
    private void cksDefaultAction(ActionEvent e) {
        setQOLVariables(e);
        if (cksDefault.get(index).isSelected()) {
            tfsDefault.get(index).setVisible(true);
        } else {
            tfsDefault.get(index).setVisible(false);
            defaultOK = true;
        }

        boolean update = tfsDefaultControl(-1);
        // UPDATE-------------------------------------------
        defaultUpdate(index, update, true);
        // -------------------------------------------
        masterControl();
    }

    private void tfsDefaultTypeControl(int index) {
        TextField tf = tfsDefault.get(index);
        String text = tf.getText();
        boolean update = false;

        if (tf.isVisible()) {
            if (!tfasType.get(index).getStyle().contains(CSS.TEXT_FILL_ERROR)
                    && (!tfsTypeLength.get(index).getStyle().contains(CSS.TEXT_FILL_ERROR)
                            || !tfsTypeLength.get(index).isVisible())) {
                type = tfasType.get(index).getText().trim();
                int typeLength = Integer.parseInt(tfsTypeLength.get(index).getText());
                String typeChar = types.getTypeChar(type);

                Pattern pattern = null;
                if (tfsTypeLength.get(index).isVisible()) {
                    if (typeChar.equals("NUMBER")) {
                        pattern = Pattern.compile("\\d{1," + typeLength + "}");
                    } else if (typeChar.equals("STRING")) {
                        if (typeLength == 1) {
                            pattern = Pattern.compile(".{1}");
                        } else {
                            pattern = Pattern.compile(".{1," + typeLength + "}");
                        }
                    }
                }

                if (pattern != null ? pattern.matcher(text).matches() : true) {
                    tf.setStyle(CSS.TEXT_FILL);
                    tfsDefaultPopups.get(index).hide();

                    defaultBW = true;
                    update = tfsDefaultControl(index);
                } else {
                    tf.setStyle(CSS.TEXT_FILL_ERROR);
                    tfsDefaultPopups.get(index).show(ILLEGAL_CHARS + "- Match (" + typeChar.toLowerCase()
                            + (tfsTypeLength.get(index).isVisible() ? ": must' be " + typeLength + " max)" : ")"));

                    defaultBW = false;
                }

            } else {
                tfsDefaultPopups.get(index).show("Select a correct Type and lenght (if needed)");
            }

            // UPDATE-------------------------------------------
            defaultUpdate(index, update, false);
            // -------------------------------------------
        } else {
            tfsDefaultPopups.get(index).hide();
            defaultBW = true;
            tfsDefaultControl(index);
        }
    }

    private void tfsDefaultKeyReleased(KeyEvent e) {
        setQOLVariables(e);

        tfsDefaultTypeControl(index);// ++++++++++++++++
        masterControl();
    }

    private boolean tfsDefaultControl(int index) {
        boolean single = true;
        for (int a = 0; a < currentRowLength; a++) {
            if (!updateControl && (a != index)) {
                if (tfsDefault.get(a).isVisible()) {
                    String text = tfsDefault.get(a).getText().trim();
                    matcher = patternBWTC.matcher(text);
                    // ADD THE REST OF DIFERENT PATTERNS
                    if (!matcher.matches()) {
                        defaultBW = false;
                        break;
                    }
                }

            } else if (updateControl && tfsDefault.get(a).isVisible()) {
                String text = tfsDefault.get(a).getText().trim();
                matcher = patternBWTC.matcher(text);
                // ADD THE REST OF DIFERENT PATTERNS
                if (!matcher.matches()) {
                    single = false;
                    break;
                }

            }
        }

        return single;
    }

    private void defaultUpdate(int index, boolean update, boolean cks) {
        if (updateControl) {
            if (currentRowLength <= updateTable.getRowLength()) {// INDEX OUT OF RANGE
                if (update) {
                    boolean defaultSelected = updateTable.getDefaults().get(index) != null;
                    String defaultValue = updateTable.getDefaults().get(index) != null
                            ? updateTable.getDefaults().get(index).toString()
                            : "";
                    if (cksDefault.get(index).isSelected() != defaultSelected
                            || (cksDefault.get(index).isSelected() && !tfsDefault.get(index).getText().equals(defaultValue))) {
                        // cksDefaultPopups[index].hide();
                        if (cks) {
                            cksDefault.get(index).setStyle(CSS.BG_COLOR);
                        } else {
                            tfsDefault.get(index).setStyle(CSS.TEXT_FILL);
                        }
                        btnsChangeDefault.get(index).setDisable(false);
                    } else {
                        // cksDefaultPopups[index].show(SAME_VALUE);
                        if (cks) {
                            cksDefault.get(index).setStyle(CSS.BG_COLOR_HINT);
                        } else {
                            tfsDefault.get(index).setStyle(CSS.TEXT_FILL_HINT);
                        }
                        btnsChangeDefault.get(index).setDisable(true);
                    }
                } else {
                    btnsChangeDefault.get(index).setDisable(true);
                }
            } else {
                // COLUMN REMOVED OR ADDED STATE
                /*
                 * cksDefaultPopups[index].hide(); btnsChangeDefault[index].setDisable(false);
                 */
            }
        }
    }

    void btnsChangeDefault(ActionEvent e) {
        System.out.println(CC.CYAN + "Update Default" + CC.RESET);
        setQOLVariables(e);
        // FINISH
        Object defaultValue = null;
        if (cksDefault.get(index).isSelected()) {
            if (types.getTypeChar(type).equals("STRING")) {
                defaultValue = tfsDefault.get(index).getText();
            } else if (types.getTypeChar(type).equals("NUMBER")) {
                defaultValue = Integer.parseInt(tfsDefault.get(index).getText());
            } else if (types.getTypeChar(type).equals("NUMBER") && type.startsWith("BIGINT")) {
                // NOT TESTED
                defaultValue = Long.parseLong(tfsDefault.get(index).getText());
            } else if (types.getTypeChar(type).equals("DECIMAL") && type.startsWith("FLOAT")) {
                // NOT TESTED
                defaultValue = Float.parseFloat(tfsDefault.get(index).getText());
            } else if (types.getTypeChar(type).equals("DECIMAL") && type.startsWith("DOUBLE")) {
                // NOT TESTED
                defaultValue = Double.parseDouble(tfsDefault.get(index).getText());
            } else if (types.getTypeChar(type).equals("BOOLEAN")) {
                // NOT TESTED
                defaultValue = tfsDefault.get(index).getText().equals("true");
            }
        }

        boolean setDefaultValue = ms.setDefaultValue(table, column, defaultValue);
        if (setDefaultValue) {
            updateTable.getDefaults().set(index, defaultValue);

            tfsDefault.get(index).setStyle(CSS.TEXT_FILL_HINT);
            cksDefault.get(index).setStyle(CSS.CKS_BG_HINT);
            btnsChangeDefault.get(index).setDisable(true);

            lbStatus.setText(
                    "Default value for column '" + column + "' has change to "
                            + (defaultValue != null ? defaultValue.toString() : "NULL"),
                    NonCSS.TEXT_FILL_OK, Duration.seconds(2));
        } else {
            lbStatus.setText("Failt to change Default Value of column '" + column + "'", NonCSS.TEXT_FILL_ERROR);
        }

    }

    // EXTRA=================================================
    private void rbsExtraAction(ActionEvent e) {
        setQOLVariables(e);
        int errorCount = 0;
        // ---------------------------------------------
       rbsExtraPopups.forEach(Popup::hide);
        // ---------------------------------------------
        if (rbsExtra.get(index).isSelected()) {
            if (rbsPK.get(index).isSelected()) {
                rbsExtraPopups.get(index).hide();
                extraPKOK = true;
            } else {
                // lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
                rbsExtraPopups.get(index).show("An AUTO_INCREMENT column has to be a PRIMARY KEY");

                errorCount++;

                extraPKOK = false;
            }
            // ---------------------------------------------
            if (Arrays.asList(pksReferences).stream().anyMatch(s -> tfsFK.get(index).getText().equals(s))) {
                // lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
                if (errorCount == 0) {
                    rbsExtraPopups.get(index).show("There's no need to have a FOREIGN KEY column with AUTO_INCREMENT");
                } else {
                    rbsExtraPopups.get(index).show(EXTRA_GENERAL_ERROR);
                }
                errorCount++;

                extraFKOK = false;
            } else {

                extraFKOK = true;
            }
            // ---------------------------------------------
            if (cksDefault.get(index).isSelected()) {
                // lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
                if (errorCount == 0) {
                    rbsExtraPopups.get(index)
                            .show("There's no need to have a DEFAULT value in a column with AUTO_INCREMENT");

                } else {
                    rbsExtraPopups.get(index).show(EXTRA_GENERAL_ERROR);

                }
                extraDefaultOK = false;
            } else {
                extraDefaultOK = true;
            }
            // ---------------------------------------------
        } else {
            // lbhExtra.setTextFill(NonCSS.TEXT_FILL);
            rbsExtraPopups.get(index).hide();

            extraPKOK = true;
            extraFKOK = true;
            extraDefaultOK = true;
        }
        // UPDATE------------------------------------------------
        extraUpdate(index);
        // ------------------------------------------------
        masterControl();
    }

    private void extraUpdate(int index) {
        if (updateControl) {
            if (extraDefaultOK && extraPKOK && extraFKOK) {
                int extraO = updateTable.getExtra();
                int extra = rbsExtra.get(index).isSelected() ? index : -1;
                if (extra != extraO) {
                    // rbsExtraPopups[index].hide();
                    rbsExtra.get(index).setStyle(CSS.BG_COLOR);
                    btnUpdateExtra.setDisable(false);
                } else {
                    // rbsExtraPopups[index].show(SAME_VALUE);
                    rbsExtra.get(index).setStyle(CSS.BG_COLOR_HINT);
                    btnUpdateExtra.setDisable(true);
                }
            } else {
                btnUpdateExtra.setDisable(true);
            }
        }
    }

    void btnUpdateExtra(ActionEvent e) {
        System.out.println(CC.CYAN + "Update Extra" + CC.RESET);
        setQOLVariables(e);

        int[] indexs = { 0 };
        boolean extra = rbsExtra.stream().anyMatch(rb -> {
            boolean selected = rb.isSelected();
            indexs[0]++;// MAY WORK -> TEST MORE
            return selected;
        });
        ms.setNullValue(extra ? updateTable.getNulls().get(indexs[0]) : false);
        ms.setExtraValue(extra);
        boolean updateExtra = ms.changeType(table, column, type);
        if (updateExtra) {
            System.out.println(indexs[0]);
            updateTable.setExtra(extra ? indexs[0] : -1);

            btnUpdateExtra.setDisable(true);

            lbStatus.setText("Changed Extra", NonCSS.TEXT_FILL_OK, Duration.seconds(2));
        } else {
            lbStatus.setText("Fail to change Extra", NonCSS.TEXT_FILL_ERROR);
        }
    }

    // BOTTOM===================================================
    private void btnCancelAction(ActionEvent e) {
        // vf.getStage().setScene(vf.getScene());
        new VF(this);
    }

    private void btnCreateAction(ActionEvent e) {
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
        // RIGHT-------------------
        String dist = Custom.getOldDist(currentRowLength, btnsDist.toArray( new ToggleButton[btnsDist.size()]));
        String imageC = Custom.getOldImageC(currentRowLength, btnsImageC.toArray(new ToggleButton[btnsImageC.size()]));
        String imageCPath = Custom.getImageCPath(currentRowLength, tfImageCPath.getText(), btnsImageC.toArray(new ToggleButton[btnsImageC.size()]));
        /*
         * boolean distPresent = false; int distCount = 0;
         */
        // -------------------------------------------------
        for (int a = 0; a < currentRowLength; a++) {
            // LEFT-------------------
            columnsNames[a] = tfsColumn.get(a).getText().toLowerCase().trim().replace(" ", "_");
            typesNames[a] = tfasType.get(a).getText();

            if (tfsTypeLength.get(a).isVisible() && types.getTypeLength(typesNames[a]) > 0) {
                // typesNames[a] += tfsTypeLength[a].getText();
                typesLengths[a] = Integer.parseInt(tfsTypeLength.get(a).getText());
            }
            nulls[a] = cksNull.get(a).isSelected();

            if (rbsPK.get(a).isSelected()) {
                cpks.add(columnsNames[a]);
            }
            final int aa = a;
            if (Arrays.asList(pksReferences).stream().anyMatch(s -> s.equals(tfsFK.get(aa).getText()))) {
                String fkText = tfsFK.get(a).getText();
                String[] split = fkText.split(".");

                cfks.add(new TString(columnsNames[a], split[1], split[2]));
                // listFK.add(new TString(colNames[a], tableR, colR));
            }
            if (cksDefault.get(a).isSelected()) {
                // defaults.add(new IntString(a + 1, tfsDefault[a].getText()));
                defaults[a] = tfsDefault.get(a).getText();
            } else {
                defaults[a] = null;
            }

            if (rbsExtra.get(a).isSelected()) {
                extra = a + 1;
            }

        }
        // CREATE UPDATE ---------------------------------------
        MSQLCreate msc = new MSQLCreate(new CurrenConnection());
        for (int a = 0; a < currentRowLength; a++) {
            msc.addTypesWidth(new DInt(a + 1, typesLengths[a]));
            msc.addAllowsNull(new IntBoolean(a + 1, nulls[a]));
            msc.addDefault(defaults[a] != null ? new IntString(a + 1, defaults[a]) : null);
        }
        msc.addAllPrimaryKeys(cpks);
        msc.addAllForeignKeys(cfks);
        msc.setAutoIncrement(extra);
        boolean createTable = msc.createTable(tableName, columnsNames, typesNames);
        // INSERT -----------------------------------------------
        if (createTable) {
            Object[] values = new Object[] { null, tableName.replace("_", " "), dist, imageC, imageCPath };
            boolean insert = ms.insert(MSQL.TABLE_NAMES, values);
            if (insert) {
                // Menus.getInstance(vf).addMenuItemsReset();// NOT TESTED

                lbStatus.setText("Table '" + tableName.replace("_", " ") + "' has been created!", NonCSS.TEXT_FILL_OK,
                        Duration.seconds(3));
            } else {
                // DELETE CREATED TABLE
                lbStatus.setText("FATAL: (Table has been create but not inserted on " + MSQL.TABLE_NAMES,
                        NonCSS.TEXT_FILL_ERROR);
            }
        } else {
            lbStatus.setText("Table Failed to be created", NonCSS.TEXT_FILL_ERROR);
        }

    }

    private void btnCreateHelpAction(ActionEvent e) {
        Bounds sb = btnCreateHelp.localToScreen(btnCreateHelp.getBoundsInLocal());
        createHelpPopup.show(btnCreateHelp, sb.getMinX(), sb.getMinY());
    }

    // RIGHT-----------------------------------------
    // DIST=================================================
    private void btnsDistAction(ActionEvent e) {
        // MAY HAVE TO ADD DIST BUTTONS TO AN OBSERVABLE LIST (ADD OR REMOVE COLUMN)
        setQOLVariables(e);
        // int errorCount = 0;
        if (btnsDist.get(index).isSelected()) {
            if (!rbsExtra.get(index).isSelected()) {
                lbhDist.setTextFill(NonCSS.TEXT_FILL);
                btnsDistPopups.get(index).hide();

                distExtraOK = true;
            } else {
                lbhDist.setTextFill(NonCSS.TEXT_FILL_ERROR);
                btnsDistPopups.get(index).show("Unnecesary selection when this column is already AUTO_INCREMENT");

                distExtraOK = true;
                btnsDistControl(index);
                // errorCount++;
            }
        } else {
            lbhDist.setTextFill(NonCSS.TEXT_FILL);
            btnsDistPopups.get(index).hide();

            distExtraOK = true;
            btnsDistControl(index);
        }

        // UPDATE--------------------------------------------------------------
        distUpdate();
        // --------------------------------------------------------------
        masterControl();
    }

    private void btnsDistControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {
                ToggleButton btn = btnsDist.get(a);
                if (btn.isSelected() && rbsExtra.get(a).isSelected()) {
                    distExtraOK = false;
                    break;
                }
            }
        }
    }

    private void distUpdate() {
        if (updateControl) {
            if (distExtraOK) {
                boolean update = false;
                for (int a = 0; a < currentRowLength; a++) {
                    boolean dist = updateTable.getDist().get(a).equals("Yes");
                    if (btnsDist.get(a).isSelected() != dist) {
                        update = true;
                        break;
                    }
                }

                btnUpdateDist.setDisable(!update);
            } else {
                btnUpdateDist.setDisable(true);
            }
        }
    }

    void btnUpdateDist(ActionEvent e) {
        System.out.println(CC.CYAN + "Update Dist" + CC.RESET);
        setQOLVariables(e);

        // String disto = Custom.getOldDist(updateTable.getDist().toArray(new
        // String[updateTable.getDist().size()]));
        String dist = Custom.getOldDist(currentRowLength, btnsDist.toArray(new ToggleButton[btnsDist.size()]));

        boolean updateDist = ms.updateRow(MSQL.TABLE_NAMES, "Name", table.replace("_", " "), "Dist1", dist);
        if (updateDist) {
            int[] indexs = { 0 };
            btnsDist.forEach(btn -> {
                if (currentRowLength > indexs[0]) {
                    updateTable.getDist().set(indexs[0], btnsDist.get(indexs[0]).isSelected() ? "Yes" : "No");
                }
                indexs[0]++;
            });
            btnUpdateDist.setDisable(true);

            lbStatus.setText("Dist has change to '" + dist + "'", NonCSS.TEXT_FILL_OK, Duration.seconds(2));
            System.out.println("\tSUCCESS");
        } else {
            lbStatus.setText("Dist fail to changed", NonCSS.TEXT_FILL_ERROR);
            System.out.println("\tFAILED");

        }
    }

    // IMAGEC================================================
    private void btnsImageCAction(ActionEvent e) {
        setQOLVariables(e);
        if (btnsImageC.get(index).isSelected()) {
            Boolean[] bools = new Boolean[currentRowLength];
            Arrays.fill(bools, false);
            listImageC.setAll(bools);

            listImageC.set(index, true);
        } else {
            listImageC.set(index, false);
        }

        // UPDATE---------------------------------------
        imageCUpdate();
    }

    private void listImageCChange(Change<? extends Boolean> c) {
        while (c.next()) {
            if (c.wasAdded() || c.wasRemoved() || c.wasUpdated() || c.wasReplaced()) {
                if (listImageC.stream().allMatch(bool -> !bool)) {
                    tfImageCPath.setDisable(true);
                    btnSelectImageC.setDisable(true);

                    // imageCPathOk = true;
                } else {
                    tfImageCPath.setDisable(false);
                    btnSelectImageC.setDisable(false);

                    // imageCPathOk = false;
                }
            }
        }

        masterControl();
    }

    private void btnSelectImageCAction(ActionEvent e) {
        File file = directoryChooser.showDialog(vf.getStage());
        if (file != null) {
            tfImageCPath.setText(file.getPath());
        }
    }

    private void tfImageCPathTextProperty(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
        Path path = Paths.get(tfImageCPath.getText());
        if (!tfImageCPath.isDisable()) {
            if (Files.exists(path)) {
                tfImageCPath.setStyle(CSS.TEXT_FILL);
                tfImageCPathPopup.hide();

                imageCPathOk = true;
            } else {
                tfImageCPath.setStyle(CSS.TEXT_FILL_ERROR);
                tfImageCPathPopup.show("This path doesn't exist");

                imageCPathOk = false;
            }
        } else {
            tfImageCPath.setStyle(CSS.TEXT_FILL);
            tfImageCPathPopup.hide();

            imageCPathOk = true;
        }

        imageCUpdate();
        masterControl();
    }

    private void imageCUpdate() {
        if (updateControl) {
            if (currentRowLength <= updateTable.getRowLength()) {
                if (imageCPathOk) {

                    String imageCO = Custom.getOldImageC(currentRowLength,
                            updateTable.getImageC().toArray(new String[updateTable.getImageC().size()]));
                    String imageC = Custom.getOldImageC(currentRowLength, btnsImageC.toArray(new ToggleButton[btnsImageC.size()]));

                    String imageCPathO = updateTable.getImageCPath();
                    String imageCPath = !tfImageCPath.isDisable() ? tfImageCPath.getText() : "NONE";

                    boolean pathExist = !tfImageCPath.isDisable() ? new File(imageCPath).exists() : true;
                    boolean imageCDifferent = !imageCO.equals(imageC);
                    boolean imageCPathDiferent = !imageCPathO.equals(imageCPath);

                    boolean disable = pathExist && (imageCDifferent || imageCPathDiferent);
                    btnUpdateImageC.setDisable(!disable);

                } else {
                    btnUpdateImageC.setDisable(true);
                }
            } else {
                System.out.println("ADD OR REMOVE COLUMN STATE");
                // ADD OR REMOVE COLUMN STATE
            }
        }
    }

    void btnUpdateImageC(ActionEvent e) {
        System.out.println(CC.CYAN + "Update ImageC" + CC.RESET);
        setQOLVariables(e);
        // ms.updateRow(, valuesWhere, newValues);
        String imageCO = Custom.getOldImageC(currentRowLength,
                updateTable.getImageC().toArray(new String[updateTable.getImageC().size()]));
        String imageC = Custom.getOldImageC(currentRowLength, btnsImageC.toArray(new ToggleButton[btnsImageC.size()]));

        String imageCPathO = updateTable.getImageCPath();
        String imageCPath = !tfImageCPath.isDisable() ? tfImageCPath.getText() : "NONE";

        int allOk = 0;
        String message = null;
        // IMAGEC UPDATE-------------------------------------
        if (!imageCO.equals(imageC)) {
            boolean updateImageC = ms.updateRow(MSQL.TABLE_NAMES, "Name", table.replace("_", " "), "ImageC", imageC);
            if (updateImageC) {
                if (!imageC.equals("NONE")) {
                    int id = Character.getNumericValue(imageC.charAt(1)) - 1;
                    for (int a = 0; a < currentRowLength; a++) {
                        if (a == id) {
                            updateTable.getImageC().set(id, "Yes");
                        }else{
                            updateTable.getImageC().set(a, "No");
                        }
                    }
                } else {
                    for (int a = 0; a < currentRowLength; a++) {
                        updateTable.getImageC().set(a, "No");
                    }
                }

                message = "ImageC updated";
                allOk++;
            } else {
                message = "ImageC failed";
            }

        } else {
            allOk++;
        }
        // IMAGECPATH UPDATE--------------------------------------
        if (!imageCPathO.equals(imageCPath)) {
            boolean updateImageCPath = ms.updateRow(MSQL.TABLE_NAMES, "Name", table.replace("_", " "), "ImageC_Path",
                    imageCPath.replace("\\", "\\\\"));
            if (updateImageCPath) {
                updateTable.setImageCPath(imageCPath);
                if(imageCPath.equals("NONE")){
                    tfImageCPath.setText("");
                }
                // LBSTATUS
                message = message != null ? message + " & ImageCPath updated" : "ImageCPath updated";
                allOk++;
            } else {
                message = message != null ? message + " & ImageCPath failed" : "ImageCPath failed";
            }
        } else {
            allOk++;
        }
        // MESSAGE--------------------------------------
        Color color = null;
        if (allOk == 2) {
            color = NonCSS.TEXT_FILL_OK;
            System.out.println("\tSUCCESS");
        } else if (allOk == 1) {
            color = NonCSS.TEXT_FILL_PK;
            System.out.println("\tHALF SUCCESS");
        } else if (allOk == 0) {
            color = NonCSS.TEXT_FILL_ERROR;
            System.out.println("\tFAILED");
        }

        lbStatus.setText(message, color);
        if (allOk > 0) {
            btnUpdateImageC.setDisable(true);
        }
    }

    // INIT ---------------------------------------------
    private void fkReferencesInit() {
        System.out.println("\n#####TEST FOR fkReferencesInit####");
        // Key[] row = keys.getRowPrimaryKeys();
        List<String> list = new ArrayList<>();
        List<PK> pksList = pks.getPksList();

        for (int a = 0; a < pksList.size(); a++) {
            String databaseName = pksList.get(a).getDatabase();
            String tableName = pksList.get(a).getTable();
            List<IntString> columnsNames = pksList.get(a).getColumns();
            // String column = row[a].getColumnName();
            StringBuilder sb = new StringBuilder(databaseName).append(".").append(tableName).append(" (");
            columnsNames.forEach(is -> sb.append(is.string).append(","));
            /*
             * long count = fksList.stream().filter(fk ->
             * fk.getReferencedDatabase().equalsIgnoreCase(databaseName) &&
             * fk.getReferencedTable().equalsIgnoreCase(tableName)).count();
             * System.out.println("count: " + count); fksList.stream().filter(fk ->
             * fk.getReferencedDatabase().equalsIgnoreCase(databaseName) &&
             * fk.getReferencedTable().equalsIgnoreCase(tableName)).forEach(fk -> { // SHOUL
             * BE ONE System.out.println("databaseName: " + databaseName);
             * System.out.println("tableName: " + tableName);
             * fk.getReferencedColumns().forEach(s -> { sb.append(s).append(","); }); });
             */
            sb.deleteCharAt(sb.length() - 1);// TEST
            sb.append(")");
            list.add(sb.toString());
        }

        pksReferences = list.toArray(new String[list.size()]);
        tfsFKPs.forEach(e -> {
            e.setLvOriginalItems(pksReferences);
            e.getLv().getItems().addAll(pksReferences);
        });
        tfsFKPs.get(0).getLv().getSelectionModel().select(0);
    }

    private int getAddSize(int index, boolean add) {
        int forCount;
        if (index == 0) {
            // NORMAL INIT
            forCount = MSQL.MAX_COLUMNS;
        } else {
            if (add) {
                // UPDATE ADD
                forCount = currentRowLength - index + 1;// +1 (empy row added)
            } else {
                // UPDATE REMOVE
                forCount = currentRowLength - index - 1;
            }
        }
        return forCount;
    }

    private void initLeftNodes(int index, boolean add) {
        int forCount = getAddSize(index, add);
        // ---------------------------------------------------------
        spGridPaneLeft.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        for (int a = 0; a < forCount; a++) {
            int row = a + index;

            lbsN.add(row, new Label("Column " + (row + 1)));
            hbsN.add(row, new HBox(lbsN.get(row)));
            // COLUMN NAMES---------------------------------------
            String columnNameStore = storeNodes.getColumnNames() != null ? storeNodes.getColumnNames()[a] : "";
            tfsColumn.add(row, new TextField(columnNameStore));
            tfsColumnPopups.add(row, new PopupMessage(tfsColumn.get(row)));

            btnsRemoveColumn.add(row, new Button("X"));
            btnsAddColumn.add(row, new Button("+"));
            btnsRenameColumn.add(row, new Button("C"));

            hbsName.add(row, new HBox(tfsColumn.get(row), btnsRemoveColumn.get(row), btnsAddColumn.get(row), btnsRenameColumn.get(row)));
            hbsName.get(row).setPadding(new Insets(0, 2, 0, 2));
            // TYPES----------------------------------------------
            // tfasType.get(row) = new TextFieldAutoC(row, types.getTypeNames());
            String typesStore = storeNodes.getTypes() != null ? storeNodes.getTypes()[a] : "";
            tfasType.add(row, new TextField(typesStore));
            tfsTypePs.add(row, new PopupAutoC(tfasType.get(row), types.getTypeNames()));
            tfsTypePopups.add(row, new PopupMessage(tfasType.get(row)));

            String typesLengthStore = storeNodes.getTypesLength() != null ? storeNodes.getTypesLength()[a] : "1";
            tfsTypeLength.add(row, new TextField(typesLengthStore));
            tfsTypeLengthPopups.add(row, new PopupMessage(tfsTypeLength.get(row)));

            btnsChangeType.add(row, new Button("C"));

            hbsType.add(row, new HBox(tfasType.get(row), tfsTypeLength.get(row), btnsChangeType.get(row)));
            hbsType.get(row).setPadding(new Insets(0, 2, 0, 2));
            // NULLS----------------------------------------------
            boolean nullStore = storeNodes.getNulls() != null ? storeNodes.getNulls()[a] : false;
            cksNull.add(row, new CheckBox());
            cksNull.get(row).setSelected(nullStore);
            btnsChangeNull.add(row, new Button("C"));
            hbsNull.add(row, new HBox(cksNull.get(row), btnsChangeNull.get(row)));
            hbsNull.get(row).setPadding(new Insets(0, 2, 0, 2));
            // PKS----------------------------------------------
            boolean pkStore = storeNodes.getPks() != null ? storeNodes.getPks()[a] : false;
            rbsPK.add(row, new RadioButton());
            rbsPK.get(row).setSelected(pkStore);
            rbsPKPopups.add(row, new PopupMessage(rbsPK.get(row)));
            // btnsChangePK.get(row) = new Button("C");
            hbsPK.add(row, new HBox(rbsPK.get(row)));
            // FKS----------------------------------------------
            /*
             * boolean fkStore = storeNodes.getFks() != null ? storeNodes.getFks()[a] :
             * false; rbsFK.get(row) = new RadioButton(); rbsFK.get(row).setSelected(fkStore);
             * rbsFKPopups.get(row) = new PopupMessage(rbsFK.get(row));
             */

            String fkTextStore = storeNodes.getFksText() != null ? storeNodes.getFksText()[a] : "";
            tfsFK.add(row, new TextField(fkTextStore));
            tfsFKPs.add(row, new PopupAutoC(tfsFK.get(row)));
            tfsFKPopups.add(row, new PopupMessage(tfsFK.get(row)));

            btnsSelectedFK.add(row, new ToggleButton("ADD"));
            hbsFK.add(row, new HBox(tfsFK.get(row), btnsSelectedFK.get(row)));
            // DEFAULTS----------------------------------------------
            boolean defaultStore = storeNodes.getDefaults() != null ? storeNodes.getDefaults()[a] : false;
            cksDefault.add(row, new CheckBox());
            cksDefault.get(row).setSelected(defaultStore);
            cksDefaultPopups.add(row, new PopupMessage(cksDefault.get(row)));

            String defaultTextStore = storeNodes.getDefaultsText() != null ? storeNodes.getDefaultsText()[a] : "";
            tfsDefault.add(row, new TextField(defaultTextStore));
            tfsDefaultPopups.add(row, new PopupMessage(tfsDefault.get(row)));

            btnsChangeDefault.add(row, new Button("C"));
            hbsDefault.add(row, new HBox(cksDefault.get(row), tfsDefault.get(row), btnsChangeDefault.get(row)));
            hbsDefault.get(row).setPadding(new Insets(0, 2, 0, 2));
            // EXTRA----------------------------------------------
            boolean extratStore = storeNodes.getExtra() != null ? storeNodes.getExtra()[a] : false;
            rbsExtra.add(row, new RadioButton());
            rbsExtra.get(row).setSelected(extratStore);
            rbsExtraPopups.add(row, new PopupMessage(rbsExtra.get(row)));
            hbsExtra.add(row, new HBox(rbsExtra.get(row)/* , btnsChangeExtra.get(row) */));
            // -----------------------------------------
            // OTHERS ---------------------------------
            tfsColumn.get(row).setId(Integer.toString(row));
            btnsRemoveColumn.get(row).setId(Integer.toString(row));
            btnsAddColumn.get(row).setId(Integer.toString(row));
            btnsRenameColumn.get(row).setId(Integer.toString(row));
            tfasType.get(row).setId("TF-TYPE-" + row);
            tfsTypeLength.get(row).setId(Integer.toString(row));
            btnsChangeType.get(row).setId(Integer.toString(row));
            cksNull.get(row).setId(Integer.toString(row));
            btnsChangeNull.get(row).setId(Integer.toString(row));
            rbsPK.get(row).setId(Integer.toString(row));
            // rbsFK.get(row).setId(Integer.toString(row));
            tfsFK.get(row).setId(Integer.toString(row));
            btnsSelectedFK.get(row).setId(Integer.toString(row));
            cksDefault.get(row).setId(Integer.toString(row));
            tfsDefault.get(row).setId(Integer.toString(row));
            btnsChangeDefault.get(row).setId(Integer.toString(row));
            rbsExtra.get(row).setId(Integer.toString(row));
            // ----------------------------------------------
            tfsColumn.get(row).setPromptText("Column name required");
            tfsDefault.get(row).setPromptText("Value Required");
            tfsFK.get(row).setPromptText("NO FOREING KEY");
            // ----------------------------------------------
            tfasType.get(row).setStyle(CSS.TFAS_DEFAULT_LOOK);
            // TYPE DEFAULT SELECTION----------------------------
            tfsTypePs.get(row).getLv().getSelectionModel().select(presetTypeSelected.get(row).getTypeName());
            tfsTypeLength.get(row).setText(Integer.toString(presetTypeSelected.get(row).getTypeLength()));
            // ----------------------------------------------

            tfsColumn.get(row).setPrefWidth(-1);
            btnsRemoveColumn.get(row).setMinWidth(40);
            btnsRemoveColumn.get(row).setMaxWidth(40);
            btnsAddColumn.get(row).setMinWidth(40);
            btnsAddColumn.get(row).setMaxWidth(40);
            tfasType.get(row).setPrefWidth(140);
            tfsTypeLength.get(row).setMinWidth(40);
            tfsTypeLength.get(row).setMaxWidth(40);
            // tfasFK.get(row).setPrefWidth(-1);
            // tfasFK.get(row).setMaxHeight(30);
            hbsFK.get(row).setPrefWidth(-1);
            tfsFK.get(row).setPrefWidth(-1);
            // SOME PROPERTIES AND LISTENERS---------------------------
            btnsRenameColumn.get(row).managedProperty().bind(btnsRenameColumn.get(row).visibleProperty());
            tfsTypeLength.get(row).managedProperty().bind(tfsTypeLength.get(row).visibleProperty());
            btnsChangeType.get(row).managedProperty().bind(btnsChangeType.get(row).visibleProperty());
            btnsChangeNull.get(row).managedProperty().bind(btnsChangeNull.get(row).visibleProperty());
            // btnsChangePK.get(row).managedProperty().bind(btnsChangePK.get(row).visibleProperty());
            tfsFK.get(row).managedProperty().bind(tfsFK.get(row).visibleProperty());
            btnsSelectedFK.get(row).managedProperty().bind(btnsSelectedFK.get(row).visibleProperty());
            tfsDefault.get(row).managedProperty().bind(tfsDefault.get(row).visibleProperty());
            btnsChangeDefault.get(row).managedProperty().bind(btnsChangeDefault.get(row).visibleProperty());
            // ----------------------------------------------------------
            tfsFK.get(row).setVisible(false);
            tfsDefault.get(row).setVisible(false);

            btnsRenameColumn.get(row).setDisable(true);
            btnsChangeType.get(row).setDisable(true);
            btnsChangeNull.get(row).setDisable(true);
            btnsChangeDefault.get(row).setDisable(true);
            // STYLE-------------------------------------------------------
            // tfasType.get(row).getStyleClass().clear();
            tfasType.get(row).setStyle(null);

            hbsN.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsName.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsType.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);
            tfsTypeLength.get(row).setStyle(CSS.TEXT_FILL);
            hbsNull.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsPK.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsFK.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsDefault.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsExtra.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);
            // ADDING ROW------------------------------------------------
            if (index != 0 && add) {// FIRST ROW ADDED
                if (row == index) {// ADDED COLUMN
                    hbsN.get(row).setStyle(CSS.NEW_ROW);
                    hbsExtra.get(row).setStyle(CSS.NEW_ROW);
                } else {
                    hbsN.get(row).setDisable(true);
                    hbsName.get(row).setDisable(true);
                    hbsType.get(row).setDisable(true);
                    hbsNull.get(row).setDisable(true);
                    hbsPK.get(row).setDisable(true);
                    hbsFK.get(row).setDisable(true);
                    hbsDefault.get(row).setDisable(true);
                    hbsExtra.get(row).setDisable(true);
                }

            }
            // -------------------------------------------------------
            GridPane.setMargin(hbsN.get(row), INSETS);
            GridPane.setMargin(hbsName.get(row), INSETS);
            GridPane.setMargin(hbsType.get(row), INSETS);
            GridPane.setMargin(hbsNull.get(row), INSETS);
            GridPane.setMargin(hbsPK.get(row), INSETS);
            GridPane.setMargin(hbsFK.get(row), INSETS);
            GridPane.setMargin(hbsDefault.get(row), INSETS);
            GridPane.setMargin(hbsExtra.get(row), INSETS);
        }
        new ToggleGroupD<>(rbsExtra.toArray(new RadioButton[rbsExtra.size()]));
        // DISABLE PREVIOUS ROW-----------------------------------
        if (add) {
            for (int a = 0; a < index; a++) {
                hbsN.get(a).setDisable(true);
                hbsName.get(a).setDisable(true);
                hbsType.get(a).setDisable(true);
                hbsNull.get(a).setDisable(true);
                hbsPK.get(a).setDisable(true);
                hbsFK.get(a).setDisable(true);
                hbsDefault.get(a).setDisable(true);
                hbsExtra.get(a).setDisable(true);
            }
        }
    }

    private void leftGridPaneRestart(int index, boolean add) {
        initLeftNodes(index, add);
        // LISTENERS-------------------
        tfsColumn.forEach(e -> {
            e.setOnKeyReleased(this::tfsColumnsKeyReleased);
        });
        listColumns.removeListener(this::listColumnsChange);
        listColumns.addListener(this::listColumnsChange);

        int[] indexs = { 0 };
        btnsRemoveColumn.forEach(e -> {
            if (indexs[0]++ != index) {
                e.setOnAction(this::btnsRemoveUpdateAction);
            }
        });
        indexs[0] = 0;
        btnsAddColumn.forEach(e -> {
            if (indexs[0]++ != index) {
                e.setOnAction(this::btnsColumnSetVisibleAction);
            }
        });

        tfasType.forEach(e -> {
            e.textProperty().removeListener(this::tfasTypeTextProperty);
            e.textProperty().addListener(this::tfasTypeTextProperty);
        });
        tfsTypeLength.forEach(e -> {
            e.textProperty().removeListener(this::tfsTypeLengthTextProperty);
            e.textProperty().addListener(this::tfsTypeLengthTextProperty);
        });
        rbsPK.forEach(e -> e.setOnAction(this::cksPKAction));
        // Arrays.asList(rbsFK).forEach(e -> e.setOnAction(this::rbsFKAction));
        tfsFK.forEach(e -> {
            e.textProperty().removeListener(this::tfasFKTextProperty);
            e.textProperty().addListener(this::tfasFKTextProperty);
        });
        cksDefault.forEach(e -> e.setOnAction(this::cksDefaultAction));
        tfsDefault.forEach(e -> e.setOnKeyReleased(this::tfsDefaultKeyReleased));
        rbsExtra.forEach(e -> {
            e.removeEventHandler(ActionEvent.ACTION, this::rbsExtraAction);
            e.addEventHandler(ActionEvent.ACTION, this::rbsExtraAction);
        });
        // ADD 'ADD COLUMN' LISTENER--------------------
        if (index != 0) {
            btnsAddColumn.get(index).setText("A");
            btnsAddColumn.get(index).setStyle(CSS.ADD_COL_BUTTON);

            btnsAddColumn.get(index).setOnAction(this::btnsAddColumnUpdateAction);

            btnsRemoveColumn.get(index).setText("C");
            btnsRemoveColumn.get(index).setStyle(CSS.REMOVE_COL_BUTTON);

            btnsRemoveColumn.get(index).setOnAction(this::btnsCancelAddColumnUpdateAction);

            rbsPK.get(index).setDisable(true);
            // rbsFK.get(index).setDisable(true);
            rbsExtra.get(index).setDisable(true);
        }
    }

    private void initRightNodes(int index, boolean add) {
        int forCount = getAddSize(index, add);
        // ---------------------------------------------------------
        spGridPaneRight.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        for (int a = 0; a < forCount; a++) {
            int row = a + index;
            // DIST----------------------
            boolean distStore = storeNodes.getDists() != null ? storeNodes.getDists()[a] : false;
            btnsDist.add(row, new ToggleButton("" + (row + 1)));
            btnsDist.get(row).setSelected(distStore);
            btnsDistPopups.add(row, new PopupMessage(btnsDist.get(row)));
            // IMAGEC--------------------
            boolean imageCStore = storeNodes.getImageCs() != null ? storeNodes.getImageCs()[a] : false;
            btnsImageC.add(row, new ToggleButton("" + (row + 1)));
            btnsImageC.get(row).setSelected(imageCStore);
            btnsImageCPopups.add(row, new PopupMessage(btnsImageC.get(row)));
            // ADD ROW-------------------------------------
            if (index != 0 && add) {
                if (row == index) {
                    // NOTHING YET
                } else {
                    btnsDist.get(row).setDisable(true);
                    btnsImageC.get(row).setDisable(true);
                }
            }
            // --------------------------------------------
            btnsDist.get(row).setId(Integer.toString(row));
            btnsImageC.get(row).setId(Integer.toString(row));

            btnsDist.get(row).setMinWidth(40);
            btnsDist.get(row).setMaxWidth(40);
            btnsImageC.get(row).setMinWidth(40);
            btnsImageC.get(row).setMaxWidth(40);

            btnsDist.get(row).managedProperty().bind(btnsDist.get(row).visibleProperty());
            btnsImageC.get(row).managedProperty().bind(btnsImageC.get(row).visibleProperty());

            btnsDist.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);
            btnsImageC.get(row).setStyle(CSS.BORDER_GRID_BOTTOM);

            GridPane.setMargin(btnsDist.get(row), INSETS);
            GridPane.setMargin(btnsImageC.get(row), INSETS);
        }
        new ToggleGroupD<>(btnsImageC.toArray(new ToggleButton[btnsImageC.size()]));
        tfImageCPathPopup = new PopupMessage(tfImageCPath);
        // SUB-------------------------------------
        lbUpdateLeft.setDisable(true);
        directoryChooser.setTitle("Select Image for a column");
        // DISABLE PREVIOUS ROW-----------------------------------
        if (add) {
            for (int a = 0; a < index; a++) {
                btnsDist.get(a).setDisable(true);
                btnsImageC.get(a).setDisable(true);
            }
        }
    }

    private void rightGridPaneRestart(int index, boolean add) {
        initRightNodes(index, add);
        // LISTENERS----------------------------
        btnsDist.forEach(e -> e.setOnAction(this::btnsDistAction));
        btnsImageC.forEach(e -> {
            e.removeEventHandler(ActionEvent.ACTION, this::btnsImageCAction);
            e.addEventHandler(ActionEvent.ACTION, this::btnsImageCAction);
        });

        if (index != 0) {
            btnsDist.get(index).setDisable(true);
            btnsImageC.get(index).setDisable(true);
        }
    }

    void btnAddRemoveColumnInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (a < presetRowsLenght - 1) {
                btnsAddColumn.get(a).setVisible(false);
                btnsRemoveColumn.get(a).setVisible(false);
            }
        }
        btnsRemoveColumn.get(0).setDisable(true);
        btnsAddColumn.get(MSQL.MAX_COLUMNS - 1).setDisable(true);
    }

    void pesetListInit(int rowLength) {
        for (int a = 0; a < rowLength; a++) {
            listColumns.add(tfsColumn.get(a).getText().trim());
            listImageC.add(false);
        }
    }

    private void presetSomeInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (a == 0) {
                presetTypeSelected.add(types.getType("INT"));
            } else {
                presetTypeSelected.add(types.getType("CHAR"));
            }
        }
    }

    private void createHelpPopupReset() {
        createHelpMap.put("Table Name", tableOK);
        createHelpMap.put("Columns Names", columnSNOK && columnBWOK);
        createHelpMap.put("Types", typeSelectionMatch && typeLengthOK);
        createHelpMap.put("Foreign Keys", fkSelectionMatch);
        createHelpMap.put("Default Values", defaultBW && defaultOK);
        createHelpMap.put("Extra Value", extraPKOK && extraFKOK && extraDefaultOK);
        createHelpMap.put("Dist", distExtraOK);
        createHelpMap.put("ImageC Path", imageCPathOk);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presetSomeInit();
        leftGridPaneRestart(0, false);
        rightGridPaneRestart(0, false);
        // TOP-----------------------------------------------
        tfTable.setPromptText("Table name required");
        btnRenameTable.managedProperty().bind(btnRenameTable.visibleProperty());
        btnRenameTable.setDisable(true);
        tfTable.setOnKeyReleased(this::tfTableKeyReleased);
        // --------------------------------------------------
        fkReferencesInit();
        // btnAddRemoveColumnInit();
        createHelpPopupReset();

        btnCreateUpdate.managedProperty().bind(btnCreateUpdate.visibleProperty());
        // RIGHT LISTENERS--------------------------------------
        btnSelectImageC.setOnAction(this::btnSelectImageCAction);
        listImageC.addListener(this::listImageCChange);
        tfImageCPath.textProperty().addListener(this::tfImageCPathTextProperty);
        // BOTTOM -------------------------------------
        lbStatus.setStyle(CSS.LB_STATUS);
        lbStatus.getBtnCloseStatus().setStyle(CSS.LB_STATUS_BUTTON);
        HBox.setHgrow(lbStatus, Priority.ALWAYS);
        hbStatus.getChildren().add(0, lbStatus);

        btnCancel.setOnAction(this::btnCancelAction);
        btnCreateUpdate.setOnAction(this::btnCreateAction);
        btnCreateHelp.setOnAction(this::btnCreateHelpAction);
    }

    // -------------------------------------------------------------
    public GridPane getGridPaneLeft() {
        return gridPaneLeft;
    }

    public void setGridPaneLeft(GridPane gridPaneLeft) {
        this.gridPaneLeft = gridPaneLeft;
    }

    public GridPane getGridPaneRight() {
        return gridPaneRight;
    }

    public void setGridPaneRight(GridPane gridPaneRight) {
        this.gridPaneRight = gridPaneRight;
    }

    public List<Label> getLbsN() {
        return lbsN;
    }

    public void setLbsN(List<Label> lbsN) {
        this.lbsN = lbsN;
    }

    public List<HBox> getHbsName() {
        return hbsName;
    }

    public void setHbsName(List<HBox> hbsName) {
        this.hbsName = hbsName;
    }

    public List<TextField> getTfsColumn() {
        return tfsColumn;
    }

    public void setTfsColumn(List<TextField> tfsColumn) {
        this.tfsColumn = tfsColumn;
    }

    public List<Button> getBtnsRemoveColumn() {
        return btnsRemoveColumn;
    }

    public void setBtnsRemoveColumn(List<Button> btnsRemoveColumn) {
        this.btnsRemoveColumn = btnsRemoveColumn;
    }

    public List<Button> getBtnsAddColumn() {
        return btnsAddColumn;
    }

    public void setBtnsAddColumn(List<Button> btnsAddColumn) {
        this.btnsAddColumn = btnsAddColumn;
    }

    public List<Button> getBtnsRenameColumn() {
        return btnsRenameColumn;
    }

    public void setBtnsRenameColumn(List<Button> btnsRenameColumn) {
        this.btnsRenameColumn = btnsRenameColumn;
    }

    public List<HBox> getHbsType() {
        return hbsType;
    }

    public void setHbsType(List<HBox> hbsType) {
        this.hbsType = hbsType;
    }

    public List<TextField> getTfasType() {
        return tfasType;
    }

    public void setTfasType(List<TextField> tfasType) {
        this.tfasType = tfasType;
    }

    public List<TextField> getTfsTypeLength() {
        return tfsTypeLength;
    }

    public void setTfsTypeLength(List<TextField> tfsTypeLength) {
        this.tfsTypeLength = tfsTypeLength;
    }

    public List<Button> getBtnsChangeType() {
        return btnsChangeType;
    }

    public void setBtnsChangeType(List<Button> btnsChangeType) {
        this.btnsChangeType = btnsChangeType;
    }

    public List<HBox> getHbsNull() {
        return hbsNull;
    }

    public void setHbsNull(List<HBox> hbsNull) {
        this.hbsNull = hbsNull;
    }

    public List<CheckBox> getCksNull() {
        return cksNull;
    }

    public void setCksNull(List<CheckBox> cksNull) {
        this.cksNull = cksNull;
    }

    public List<Button> getBtnsChangeNull() {
        return btnsChangeNull;
    }

    public void setBtnsChangeNull(List<Button> btnsChangeNull) {
        this.btnsChangeNull = btnsChangeNull;
    }

    public List<HBox> getHbsPK() {
        return hbsPK;
    }

    public void setHbsPK(List<HBox> hbsPK) {
        this.hbsPK = hbsPK;
    }

    public List<RadioButton> getRbsPK() {
        return rbsPK;
    }

    public void setRbsPK(List<RadioButton> cksPK) {
        this.rbsPK = cksPK;
    }

    public List<HBox> getHbsFK() {
        return hbsFK;
    }

    public void setHbsFK(List<HBox> hbsFK) {
        this.hbsFK = hbsFK;
    }

    public List<TextField> getTfasFK() {
        return tfsFK;
    }

    public void setTfasFK(List<TextField> tfasFK) {
        this.tfsFK = tfasFK;
    }

    public List<HBox> getHbsDefault() {
        return hbsDefault;
    }

    public void setHbsDefault(List<HBox> hbsDefault) {
        this.hbsDefault = hbsDefault;
    }

    public List<CheckBox> getCksDefault() {
        return cksDefault;
    }

    public void setCksDefault(List<CheckBox> cksDefault) {
        this.cksDefault = cksDefault;
    }

    public List<TextField> getTfsDefault() {
        return tfsDefault;
    }

    public void setTfsDefault(List<TextField> tfsDefault) {
        this.tfsDefault = tfsDefault;
    }

    public List<Button> getBtnsChangeDefault() {
        return btnsChangeDefault;
    }

    public void setBtnsChangeDefault(List<Button> btnsChangeDefault) {
        this.btnsChangeDefault = btnsChangeDefault;
    }

    public List<HBox> getHbsExtra() {
        return hbsExtra;
    }

    public void setHbsExtra(List<HBox> hbsExtra) {
        this.hbsExtra = hbsExtra;
    }

    public List<RadioButton> getRbsExtra() {
        return rbsExtra;
    }

    public void setRbsExtra(List<RadioButton> rbsExtra) {
        this.rbsExtra = rbsExtra;
    }

    public List<ToggleButton> getBtnsDist() {
        return btnsDist;
    }

    public void setBtnsDist(List<ToggleButton> btnsDist) {
        this.btnsDist = btnsDist;
    }

    public int getPresetRowsLenght() {
        return presetRowsLenght;
    }

    public void setPresetRowsLenght(int presetColumnsLenght) {
        this.presetRowsLenght = presetColumnsLenght;
    }

    public VFController getVf() {
        return vf;
    }

    public void setVf(VFController vf) {
        this.vf = vf;
    }

    public List<ToggleButton> getBtnsImageC() {
        return btnsImageC;
    }

    public void setBtnsImageC(List<ToggleButton> btnsImageC) {
        this.btnsImageC = btnsImageC;
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

    public Button getBtnUpdateFK() {
        return btnUpdateFK;
    }

    public void setBtnUpdateFK(Button btnUpdateFK) {
        this.btnUpdateFK = btnUpdateFK;
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

    public UpdateTable getUpdateTable() {
        return updateTable;
    }

    public void setUpdateTable(UpdateTable updateTable) {
        this.updateTable = updateTable;
    }

    public TextField getTfTable() {
        return tfTable;
    }

    public void setTfTable(TextField tfTable) {
        this.tfTable = tfTable;
    }

    public TextField getTfImageCPath() {
        return tfImageCPath;
    }

    public void setTfImageCPath(TextField tfImageCPath) {
        this.tfImageCPath = tfImageCPath;
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
        return lbUpdateLeft;
    }

    public void setLbUpdateLeft(Label lbUpdateLeft) {
        this.lbUpdateLeft = lbUpdateLeft;
    }

    public List<HBox> getHbsN() {
        return hbsN;
    }

    public void setHbsN(List<HBox> hbsN) {
        this.hbsN = hbsN;
    }

    public Button getBtnUpdateImageC() {
        return btnUpdateImageC;
    }

    public void setBtnUpdateImageC(Button btnUpdateImageC) {
        this.btnUpdateImageC = btnUpdateImageC;
    }

    public List<ToggleButton> getBtnsSelectedFK() {
        return btnsSelectedFK;
    }

    public void setBtnsSelectedFK(List<ToggleButton> btnsChangeFK) {
        this.btnsSelectedFK = btnsChangeFK;
    }

    public Button getBtnSelectImageC() {
        return btnSelectImageC;
    }

    public void setBtnSelectImageC(Button btnSelectImageC) {
        this.btnSelectImageC = btnSelectImageC;
    }

}
