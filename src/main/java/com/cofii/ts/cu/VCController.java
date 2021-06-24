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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cofii.ts.first.VF;
import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.other.Timers;
import com.cofii.ts.sql.CurrenConnection;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.FKS;
import com.cofii.ts.store.Key;
import com.cofii.ts.store.Keys;
import com.cofii.ts.store.PK;
import com.cofii.ts.store.PKS;
import com.cofii.ts.store.SQLType;
import com.cofii.ts.store.SQLTypes;
import com.cofii.ts.store.Table;
import com.cofii.ts.store.TableS;
import com.cofii.ts.store.UpdateTable;
import com.cofii.ts.store.VCGridNodes;
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
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;

public class VCController implements Initializable {

    public static final String LBH_COLUMN_NAMES = "Name";
    public static final String LBH_COLUMN_NAMES_ERROR = "Columns have the same name";
    public static final Insets INSETS = new Insets(2, 2, 2, 2);

    private static final String ILLEGAL_CHARS = "Illegal Chars";
    private static final String SELECTION_UNMATCH = "Selection Unmatch";
    private static final String EMPTY_TEXT = "Column name field can't be empty";
    private static final String EXTRA_GENERAL_ERROR = "PK, FK or Default are not allowed to selected/unselected";
    private static final String SAME_VALUE = "Can't update to the same value";
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
    private Label lbStatus;
    @FXML
    private Button btnCreateUpdate;
    @FXML
    private Button btnCreateHelp;
    @FXML
    private Button btnCancel;

    @FXML
    private Region regionLeft;// IDR
    // ARRAYS-----------------------------------------------------------
    private HBox[] hbsN = new HBox[MSQL.MAX_COLUMNS];// -----------
    private Label[] lbsN = new Label[MSQL.MAX_COLUMNS];
    private HBox[] hbsName = new HBox[MSQL.MAX_COLUMNS];// -----------
    private TextField[] tfsColumn = new TextField[MSQL.MAX_COLUMNS];
    private PopupMessage[] tfsColumnPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private Button[] btnsRemoveColumn = new Button[MSQL.MAX_COLUMNS];
    private Button[] btnsAddColumn = new Button[MSQL.MAX_COLUMNS];
    private Button[] btnsRenameColumn = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsType = new HBox[MSQL.MAX_COLUMNS];// -----------
    // private TextFieldAutoC[] tfasType = new TextFieldAutoC[MSQL.MAX_COLUMNS];
    private TextField[] tfasType = new TextField[MSQL.MAX_COLUMNS];
    private PopupAutoC[] tfsTypePs = new PopupAutoC[MSQL.MAX_COLUMNS];
    private PopupMessage[] tfsTypePopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private TextField[] tfsTypeLength = new TextField[MSQL.MAX_COLUMNS];
    private PopupMessage[] tfsTypeLengthPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeType = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsNull = new HBox[MSQL.MAX_COLUMNS];// -----------
    private CheckBox[] cksNull = new CheckBox[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeNull = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsPK = new HBox[MSQL.MAX_COLUMNS];// -----------
    private RadioButton[] rbsPK = new RadioButton[MSQL.MAX_COLUMNS];
    private PopupMessage[] rbsPKPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    // private Button[] btnsChangePK = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsFK = new HBox[MSQL.MAX_COLUMNS];// -----------
    private RadioButton[] rbsFK = new RadioButton[MSQL.MAX_COLUMNS];
    // private TextFieldAutoC[] tfasFK = new TextFieldAutoC[MSQL.MAX_COLUMNS];
    private PopupMessage[] rbsFKPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private TextField[] tfasFK = new TextField[MSQL.MAX_COLUMNS];
    private PopupAutoC[] tfsFKPs = new PopupAutoC[MSQL.MAX_COLUMNS];
    private PopupMessage[] tfsFKPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    // private Button[] btnsChangeFK = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsDefault = new HBox[MSQL.MAX_COLUMNS];// -----------
    private CheckBox[] cksDefault = new CheckBox[MSQL.MAX_COLUMNS];
    private PopupMessage[] cksDefaultPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private TextField[] tfsDefault = new TextField[MSQL.MAX_COLUMNS];
    private PopupMessage[] tfsDefaultPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeDefault = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsExtra = new HBox[MSQL.MAX_COLUMNS];// -----------
    private RadioButton[] rbsExtra = new RadioButton[MSQL.MAX_COLUMNS];
    private PopupMessage[] rbsExtraPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    // private Button[] btnsChangeExtra = new Button[MSQL.MAX_COLUMNS];
    // RIGHT
    private ToggleButton[] btnsDist = new ToggleButton[MSQL.MAX_COLUMNS];
    private PopupMessage[] btnsDistPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private ToggleButton[] btnsImageC = new ToggleButton[MSQL.MAX_COLUMNS];
    private PopupMessage[] btnsImageCPopups = new PopupMessage[MSQL.MAX_COLUMNS];
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
            type = tfasType[index].getText()
                    + (tfsTypeLength[index].isVisible() ? "(" + tfsTypeLength[index].getText() + ")" : "");
        }
    }

    // CONTROL---------------------------------------------
    private void createControl() {
        createHelpPopupReset();

        if (tableOK && columnSNOK && columnBWOK && typeSelectionMatch && typeLengthOK && fkSelectionMatch && defaultBW
                && defaultOK && extraPKOK && extraFKOK && extraDefaultOK && distExtraOK && imageCPathOk) {
            btnCreateUpdate.setDisable(false);
        } else {
            btnCreateUpdate.setDisable(true);
        }
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
                    Table table = null;
                    while (rs.next()) {
                        System.out.println("TEST HAPPEN");
                        table = new Table(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                                rs.getString(5));
                    }
                    MSQL.setCurrentTable(table);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                // Menus.getInstance(vf).addMenuItemsReset();// NOT TESTED
                btnRenameTable.setDisable(true);

                lbStatus.setText("Table '" + oldTable + "' has been rename to '" + newTable + "'");
                lbStatus.setTextFill(NonCSS.TEXT_FILL_OK);
                System.out.println("\ttable Renamed!");
            } else {
                lbStatus.setText("FATAL: table change its name but " + MSQL.TABLE_NAMES + " hasn't been updated");
                lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
                System.out.println("\tFATAL!");

            }

        } else {
            lbStatus.setText("Table '" + oldTable + "' fail to be renamed");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
            System.out.println("\tERROR!");

        }

        timers.playLbStatusReset(lbStatus);
    }

    // COLUMNS=====================================================
    private void tfsColumnsKeyReleased(KeyEvent e) {
        setQOLVariables(e);

        String text = tfsColumn[index].getText().toLowerCase().trim().replace(" ", "_");
        listColumns.set(index, text);

        if (!text.trim().isEmpty()) {
            matcher = patternBWTC.matcher(text);
            if (matcher.matches()) {
                tfsColumn[index].setStyle(CSS.TEXT_FILL);
                // popupHide(tf);
                tfsColumnPopups[index].hide();

                columnBWOK = true;// REST CONTROL
                tfsColumnControl(index);
            } else {
                tfsColumn[index].setStyle(CSS.TEXT_FILL_ERROR);
                // popupShow(tf, ILLEGAL_CHARS);
                tfsColumnPopups[index].show(ILLEGAL_CHARS);
                columnBWOK = false;
            }
        } else {
            tfsColumnPopups[index].show(EMPTY_TEXT);
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
                String text = tfsColumn[a].getText().trim().replace(" ", "_");
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
                    tfsColumn[index].setStyle(CSS.TEXT_FILL);
                    btnsRenameColumn[index].setDisable(false);
                } else {
                    // tfsColumnPopups[index].show(SAME_VALUE);
                    tfsColumn[index].setStyle(CSS.TEXT_FILL_HINT);
                    btnsRenameColumn[index].setDisable(true);
                }
            } else {
                btnsRenameColumn[index].setDisable(true);
            }
        }
    }

    void btnsRenameColumn(ActionEvent e) {
        System.out.println(CC.CYAN + "\nRename Column" + CC.RESET);
        setQOLVariables(e);
        String newColumn = tfsColumn[index].getText().toLowerCase().trim().replace(" ", "_");

        boolean renameColumn = ms.renameColumn(table, column, newColumn);
        if (renameColumn) {
            System.out.println("\tSUCCESS");
            // RENAME THE COLUMN AND THEN TEST THE CANCEL BUTTON TO RESET
            updateTable.getColumns().set(index, newColumn);

            btnsRenameColumn[index].setDisable(true);
            lbStatus.setText("Column '" + column + "' changed to '" + newColumn + "'");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_OK);
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to be renamed");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
        }

        timers.playLbStatusReset(lbStatus);
    }

    // ADD OR REMOVE COLUMNS-------------------------------------
    // CREATE=========================================
    void btnsAddCreateAction(ActionEvent e) {
        Button btn = (Button) e.getSource();
        int index = Integer.parseInt(btn.getId()) + 1;
        int row = index + 1;

        btnsAddColumn[index - 1].setVisible(false);
        btnsRemoveColumn[index - 1].setVisible(false);
        btnsAddColumn[index].setVisible(true);
        btnsRemoveColumn[index].setVisible(true);
        // --------------------------------------------------
        gridPaneLeft.add(hbsN[index], 0, row);
        gridPaneLeft.add(hbsName[index], 1, row);
        gridPaneLeft.add(hbsType[index], 2, row);
        gridPaneLeft.add(hbsNull[index], 3, row);
        gridPaneLeft.add(hbsPK[index], 4, row);
        gridPaneLeft.add(hbsFK[index], 5, row);
        gridPaneLeft.add(hbsDefault[index], 6, row);
        gridPaneLeft.add(hbsExtra[index], 7, row);

        gridPaneRight.add(btnsDist[index], 0, row);
        gridPaneRight.add(btnsImageC[index], 1, row);
        // --------------------------------------------------
        listColumns.add(tfsColumn[index].getText());
        listImageC.add(false);
        // --------------------------------------------------
        currentRowLength++;
        tfsColumnControl(-1);
        tfasTypeControl(-1);
        tfsTypeLengthControl(-1);
        tfasFKControl(-1);
        tfsDefaultControl(-1);
        masterControl();
    }

    void btnsRemoveCreateAction(ActionEvent e) {
        Button btn = (Button) e.getSource();
        int index = Integer.parseInt(btn.getId());

        btnsAddColumn[index - 1].setVisible(true);
        btnsRemoveColumn[index - 1].setVisible(true);
        // ---------------------------------------------------------
        gridPaneLeft.getChildren().removeAll(hbsN[index], hbsName[index], hbsType[index], hbsNull[index], hbsPK[index],
                hbsFK[index], hbsDefault[index], hbsExtra[index]);

        gridPaneRight.getChildren().removeAll(btnsDist[index], btnsImageC[index]);
        // ---------------------------------------------------------
        listColumns.remove(index);
        listImageC.remove(index);
        // ---------------------------------------------------------
        currentRowLength--;
        tfsColumnControl(-1);
        tfasTypeControl(-1);
        tfsTypeLengthControl(-1);
        tfasFKControl(-1);
        tfsDefaultControl(-1);

        masterControl();
    }

    // UPDATE========================================
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
                rowE = row - 1;
            } else {
                rowE = row;
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
                storeNodes.getFks()[a] = rbsFK[rowE].isSelected();
                storeNodes.getFksText()[a] = tfasFK[rowE].getText();
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

            gridPaneLeft.getChildren().removeAll(hbsN[row], hbsName[row], hbsType[row], hbsNull[row], hbsPK[row],
                    hbsFK[row], hbsDefault[row], hbsExtra[row]);

            gridPaneRight.getChildren().removeAll(btnsDist[row], btnsImageC[row]);
        }

        // REPLACE ARRAY ELEMENTS-------------------------------
        leftGridPaneRestart(index, add);
        rightGridPaneRestart(index, add);
        // ADD ROWS-----------------------------------------
        for (int a = 0; a < storeLength; a++) {
            int row = index + a;
            int aRow = row + 1;
            gridPaneLeft.add(hbsN[row], 0, aRow);
            gridPaneLeft.add(hbsName[row], 1, aRow);
            gridPaneLeft.add(hbsType[row], 2, aRow);
            gridPaneLeft.add(hbsNull[row], 3, aRow);
            gridPaneLeft.add(hbsPK[row], 4, aRow);
            gridPaneLeft.add(hbsFK[row], 5, aRow);
            gridPaneLeft.add(hbsDefault[row], 6, aRow);
            gridPaneLeft.add(hbsExtra[row], 7, aRow);

            gridPaneRight.add(btnsDist[row], 0, aRow);
            gridPaneRight.add(btnsImageC[row], 1, aRow);
        }

    }

    void btnsColumnSetVisibleAction(ActionEvent e) {
        Button btn = (Button) e.getSource();
        int index = Integer.parseInt(btn.getId()) + 1;// PLUS HEADER
        // first(index);
        updateAddOrRemoveVisible(index, true);
        listColumns.add(index - 1, tfsColumn[index - 1].getText().toLowerCase().trim().replace(" ", "_"));

        tfsColumnControl(-1);
        tfasTypeControl(-1);
        tfsTypeLengthControl(-1);
        tfasFKControl(-1);
        tfsDefaultControl(-1);
        masterControl();
    }

    private void btnsAddColumnUpdateAction(ActionEvent e) {
        System.out.println(CC.CYAN + "\nADD COLUMN" + CC.RESET);
        int index = Integer.parseInt(((Button) e.getSource()).getId());

        String table = MSQL.getCurrentTable().getName().toLowerCase().trim().replace(" ", "_");
        String newColumn = tfsColumn[index].getText().toLowerCase().trim().replace(" ", "_");
        String type = tfasType[index].getText()
                + (tfsTypeLength[index].isVisible() ? "(" + tfsTypeLength[index].getText() + ")" : "");
        /*
         * String distN = Custom.getOldDist(currentRowLength, btnsDist); String imageCN
         * = Custom.getOldImageC(currentRowLength, btnsImageC); String imageCPathN =
         * Custom.getImageCPath(currentRowLength, tfImageCPath, btnsImageC);
         */
        String afterColumn;
        boolean addColumn;
        try {// ATTEMPTING TO GRAB THE PREVIOUS COLUMN
            afterColumn = updateTable.getColumns().get(index - 1).toLowerCase().trim().replace(" ", "_");

            addColumn = ms.addColumn(table, newColumn, type, afterColumn);
        } catch (ArrayIndexOutOfBoundsException ex) {
            addColumn = ms.addColumn(table, newColumn, type);
        }

        if (addColumn) {
            System.out.println("\tSUCCES");
            /*
             * IF BTN-DIST IS ENABLED String dist = Custom.getDist(updateTable.getDist());
             * if(!dist.equals(distN)){ }
             */
            updateTable.getColumns().add(index, newColumn);
            updateTable.getTypes().add(index, tfasType[index].getText());
            updateTable.getTypesLength().add(index, Integer.parseInt(tfsTypeLength[index].getText()));
            updateTable.getNulls().add(index, cksNull[index].isSelected());
            updateTable.getDefaults().add(index, cksDefault[index].isSelected() ? tfsDefault[index].getText() : null);

            lbStatus.setText("Added column '" + newColumn + "' to '" + MSQL.getCurrentTable().getName() + "'");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_OK);
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Couldn't add column '" + newColumn + "'");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
        }

        timers.playLbStatusReset(lbStatus);
    }

    // NOT FINISHED
    private void btnsCancelVisibleAction(ActionEvent e) {
        Button btn = (Button) e.getSource();
        int index = Integer.parseInt(btn.getId()) + 1;// PLUS HEADER

        updateAddOrRemoveVisible(index, false);
        listColumns.remove(index - 1);

        tfsColumnControl(-1);
        tfasTypeControl(-1);
        tfsTypeLengthControl(-1);
        tfasFKControl(-1);
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
        int index = Character.getNumericValue(tf.getId().charAt(tf.getId().length() - 1));
        boolean update = false;

        newValue = newValue.trim();
        matcher = patternBWTC.matcher(newValue);
        if (matcher.matches()) {
            if (MList.isOnThisList(tfsTypePs[index].getLv().getItems(), newValue, false)) {
                int typeLength = types.getTypeLength(newValue);
                if (typeLength > 0) {// TF-TYPE-LENGTH-POPUP
                    tfsTypeLength[index].setVisible(true);
                    tfsTypeLength[index].setText(Integer.toString(typeLength));
                } else {
                    tfsTypeLength[index].setVisible(false);
                    tfsTypeLength[index].setText("1");
                    typeLengthOK = true;
                }

                tf.setStyle(CSS.TEXT_FILL);
                tfsTypePopups[index].hide();

                typeSelectionMatch = true;
                update = true;
                tfasTypeControl(index);
            } else {
                tfsTypeLength[index].setVisible(false);
                tfsTypeLength[index].setText("1");

                tf.setStyle(CSS.TEXT_FILL_ERROR);
                tfsTypePopups[index].show(SELECTION_UNMATCH);

                typeSelectionMatch = false;
            }
        } else {
            tf.setStyle(CSS.TEXT_FILL_ERROR);
            tfsTypePopups[index].show(ILLEGAL_CHARS);

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
            int index = Integer.parseInt(tf.getId());

            String text = tfsTypeLength[index].getText().toLowerCase().trim();
            int typeMaxLength = types.getTypeMaxLength(tfasType[index].getText());
            boolean update = false;

            matcher = patternTypeLength.matcher(text);
            if (matcher.matches()) {
                int length = Integer.parseInt(text);
                if (length <= typeMaxLength) {
                    tf.setStyle(CSS.TEXT_FILL);
                    tfsTypeLengthPopups[index].hide();

                    typeLengthOK = true;
                    update = true;
                    tfsTypeLengthControl(index);
                } else {
                    tf.setStyle(CSS.TEXT_FILL_ERROR);
                    tfsTypeLengthPopups[index].show("Wrong length (1 to " + typeMaxLength + ")");

                    typeLengthOK = false;
                }
            } else {
                tf.setStyle(CSS.TEXT_FILL_ERROR);
                tfsTypeLengthPopups[index].show("Wrong length (1 to " + typeMaxLength + ")");

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
                String text = tfasType[a].getText();
                matcher = patternBWTC.matcher(text);
                if (matcher.matches()) {
                    if (!MList.isOnThisList(/* tfasType[a].getLv().getItems() */ tfsTypePs[a].getLv().getItems(), text,
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
                String text = tfsTypeLength[a].getText();
                int typeMaxLength = types.getTypeMaxLength(tfasType[a].getText());

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
                String type = updateTable.getTypes().get(index);
                if (!newValue.equals(type)) {
                    // tfsTypePopups[index].hide();
                    tfasType[index].setStyle(CSS.TEXT_FILL);
                    btnsChangeType[index].setDisable(false);
                } else {
                    tfasType[index].setStyle(CSS.TEXT_FILL_HINT);
                    btnsChangeType[index].setDisable(true);
                    // tfsTypePopups[index].show(SAME_VALUE);
                }
            } else {
                btnsChangeType[index].setDisable(true);
            }
        }
    }

    private void tfsTypeLengthUpdate(int index, String text, boolean update) {
        if (updateControl) {
            if (update) {
                String typeLength = Integer.toString(updateTable.getTypesLength().get(index)).toLowerCase().trim();
                if (!text.equals(typeLength)) {
                    // tfsTypeLengthPopups[index].hide();
                    tfsTypeLength[index].setStyle(CSS.TEXT_FILL);
                    btnsChangeType[index].setDisable(false);
                } else {
                    // tfsTypeLengthPopups[index].show(SAME_VALUE);
                    tfsTypeLength[index].setStyle(CSS.TEXT_FILL_HINT);
                    btnsChangeType[index].setDisable(true);
                }
            } else {
                btnsChangeType[index].setDisable(true);
            }
        }
    }

    void btnsChangeType(ActionEvent e) {
        System.out.println(CC.CYAN + "\nChange Type" + CC.RESET);

        setQOLVariables(e);
        boolean changeType = ms.changeType(table, column, type);
        if (changeType) {
            System.out.println("\tSUCCESS");

            updateTable.getTypes().set(index, tfasType[index].getText());
            updateTable.getTypesLength().set(index,
                    tfsTypeLength[index].isVisible() ? Integer.parseInt(tfsTypeLength[index].getText()) : 0);

            lbStatus.setText("Column '" + column + "' has change it's type to '" + type + "'");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_OK);

            btnsChangeType[index].setDisable(true);
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to change it's type");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
        }

        timers.playLbStatusReset(lbStatus);
    }

    // NULLS======================================================
    void cksNullAction(ActionEvent e) {
        int index = Integer.parseInt(((CheckBox) e.getSource()).getId());
        boolean nulllO = updateTable.getNulls().get(index);
        boolean nulll = cksNull[index].isSelected();

        if (nulllO != nulll) {
            cksNull[index].setStyle(CSS.CKS_BG);
            btnsChangeNull[index].setDisable(false);
        } else {
            cksNull[index].setStyle(CSS.CKS_BG_HINT);
            btnsChangeNull[index].setDisable(true);
        }
    }

    void btnsChangeNull(ActionEvent e) {
        System.out.println(CC.CYAN + "Change Null" + CC.RESET);
        setQOLVariables(e);
        String type = updateTable.getTypes().get(index)
                + (updateTable.getTypesLength().get(index) > 0 ? "(" + updateTable.getTypesLength().get(index) + ")"
                        : "");
        boolean nulll = cksNull[index].isSelected();
        // ----------------------------------------------
        ms.setNullValue(nulll);
        ms.setExtraValue(index == updateTable.getExtra());
        boolean changeNull = ms.changeType(table, column, type);
        if (changeNull) {
            System.out.println("\tSUCCESS");
            updateTable.getNulls().set(index, nulll);

            cksNull[index].setStyle(CSS.CKS_BG_HINT);
            btnsChangeNull[index].setDisable(true);

            lbStatus.setText("Column '" + column + "' change to " + (nulll ? "NULL" : "NOT NULL"));
            lbStatus.setTextFill(NonCSS.TEXT_FILL_OK);
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to be changed");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
        }

        timers.playLbStatusReset(lbStatus);
    }

    // PKS/FKS======================================================
    void cksPKAction(ActionEvent e) {
        // ONLY FOR UPDATE
        setQOLVariables(e);
        boolean update = false;

        for (int c = 0; c < currentRowLength; c++) {
            boolean pkSelected = updateTable.getPks().get(c).equals("Yes");
            if (rbsPK[c].isSelected() != pkSelected) {
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

        boolean dropPK = true;
        if (updateTable.getPks().stream().anyMatch(p -> p.equals("Yes"))) {
            dropPK = ms.dropPrimaryKey(table);
        }

        if (dropPK) {
            List<String> cols = new ArrayList<>(currentRowLength);
            int[] indexs = { 0 };
            Arrays.asList(rbsPK).forEach(el -> {
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

                lbStatus.setText("Changed Primary Key");
                lbStatus.setTextFill(NonCSS.TEXT_FILL_OK);

                System.out.println("\tSUCCESS");
            } else {
                lbStatus.setText("Failt to Change Primary Key");
                lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);

                System.out.println("\tFAILED");

            }

            timers.playLbStatusReset(lbStatus);
        }
    }

    // FKS==========================================================
    private void cksFKAction(ActionEvent e) {
        CheckBox ck = (CheckBox) e.getSource();
        int index = Integer.parseInt(ck.getId());

        tfasFK[index].setVisible(ck.isSelected());

        tfasFKControl(-1);
        // UPDATE------------------------------------------------
        fkUpdateControl(index, true);
        // ------------------------------------------------
        masterControl();
    }

    private void tfasFKTextProperty(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        int index = Integer.parseInt(tf.getId());

        if (tf.isVisible()) {
            if (MList.isOnThisList(tfsFKPs[index].getLv().getItems(), newValue, false)) {
                tf.setStyle(CSS.TEXT_FILL);
                tfsFKPopups[index].hide();

                fkSelectionMatch = true;
                tfasFKControl(index);
            } else {
                tf.setStyle(CSS.TEXT_FILL_ERROR);
                tfsFKPopups[index].show(SELECTION_UNMATCH);

                fkSelectionMatch = false;
            }
        } else {
            tfsFKPopups[index].hide();

            fkSelectionMatch = true;
            tfasFKControl(index);
        }

        // UPDATE----------------------------------------------
        fkUpdateControl(index, false);
        // ----------------------------------------------
        masterControl();
    }

    private void tfasFKControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index && tfasFK[a].isVisible()) {
                String text = tfasFK[a].getText();
                if (!MList.isOnThisList(tfsFKPs[a].getLv().getItems(), text, false)) {
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
                    boolean fkSelected = updateTable.getFks().get(c) != null;
                    String fkText = updateTable.getFkFormed().get(c);

                    if (rbsFK[c].isSelected() != fkSelected
                            || (rbsFK[c].isSelected() && !tfasFK[c].getText().equals(fkText))) {
                        update = true;
                        break;
                    }
                }
                if (update) {
                    if (cks) {
                        rbsFK[index].setStyle(CSS.BG_COLOR);
                    } else {
                        tfasFK[index].setStyle(CSS.TEXT_FILL);
                    }
                    btnUpdateFK.setDisable(false);
                    // cksFKPopups[index].hide();

                } else {
                    if (cks) {
                        rbsFK[index].setStyle(CSS.BG_COLOR_HINT);
                    } else {
                        tfasFK[index].setStyle(CSS.TEXT_FILL_HINT);
                    }
                    btnUpdateFK.setDisable(true);
                    // cksFKPopups[index].show(SAME_VALUE);
                }
            } else {
                btnUpdateFK.setDisable(true);
            }
        }
    }

    void btnUpdateFK(ActionEvent e) {
        System.out.println(CC.CYAN + "Update FK" + CC.RESET);
        if (!updateTable.getFks().stream().allMatch(fk -> fk == null)) {
            // DROP FOREIGN KEY
        }
    }

    // DEFAULTS=================================================
    private void cksDefaultAction(ActionEvent e) {
        CheckBox ck = (CheckBox) e.getSource();
        int index = Integer.parseInt(ck.getId());
        if (ck.isSelected()) {
            tfsDefault[index].setVisible(true);
        } else {
            tfsDefault[index].setVisible(false);
            defaultOK = true;
        }

        boolean update = tfsDefaultControl(-1);
        // UPDATE-------------------------------------------
        defaultUpdate(index, update, true);
        // -------------------------------------------
        masterControl();
    }

    private void tfsDefaultTypeControl(int index) {
        TextField tf = tfsDefault[index];
        String text = tf.getText();
        boolean update = false;

        if (tf.isVisible()) {
            if (!tfasType[index].getStyle().contains(CSS.TEXT_FILL_ERROR)
                    && (!tfsTypeLength[index].getStyle().contains(CSS.TEXT_FILL_ERROR)
                            || !tfsTypeLength[index].isVisible())) {
                String type = tfasType[index].getText().trim();
                int typeLength = Integer.parseInt(tfsTypeLength[index].getText());
                String typeChar = types.getTypeChar(type);

                Pattern pattern = null;
                if (tfsTypeLength[index].isVisible()) {
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
                    tfsDefaultPopups[index].hide();

                    defaultBW = true;
                    update = tfsDefaultControl(index);
                } else {
                    tf.setStyle(CSS.TEXT_FILL_ERROR);
                    tfsDefaultPopups[index].show(ILLEGAL_CHARS + "- Match (" + typeChar.toLowerCase()
                            + (tfsTypeLength[index].isVisible() ? ": must' be " + typeLength + " max)" : ")"));

                    defaultBW = false;
                }

            } else {
                tfsDefaultPopups[index].show("Select a correct Type and lenght (if needed)");
            }

            // UPDATE-------------------------------------------
            defaultUpdate(index, update, false);
            // -------------------------------------------
        } else {
            tfsDefaultPopups[index].hide();
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
                if (tfsDefault[a].isVisible()) {
                    String text = tfsDefault[a].getText().trim();
                    matcher = patternBWTC.matcher(text);
                    // ADD THE REST OF DIFERENT PATTERNS
                    if (!matcher.matches()) {
                        defaultBW = false;
                        break;
                    }
                }

            } else if (updateControl && tfsDefault[a].isVisible()) {
                String text = tfsDefault[a].getText().trim();
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
                    if (cksDefault[index].isSelected() != defaultSelected
                            || (cksDefault[index].isSelected() && !tfsDefault[index].getText().equals(defaultValue))) {
                        // cksDefaultPopups[index].hide();
                        if (cks) {
                            cksDefault[index].setStyle(CSS.BG_COLOR);
                        } else {
                            tfsDefault[index].setStyle(CSS.TEXT_FILL);
                        }
                        btnsChangeDefault[index].setDisable(false);
                    } else {
                        // cksDefaultPopups[index].show(SAME_VALUE);
                        if (cks) {
                            cksDefault[index].setStyle(CSS.BG_COLOR_HINT);
                        } else {
                            tfsDefault[index].setStyle(CSS.TEXT_FILL_HINT);
                        }
                        btnsChangeDefault[index].setDisable(true);
                    }
                } else {
                    btnsChangeDefault[index].setDisable(true);
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
        if (cksDefault[index].isSelected()) {
            if (types.getTypeChar(type).equals("STRING")) {
                defaultValue = tfsDefault[index].getText();
            } else if (types.getTypeChar(type).equals("NUMBER")) {
                defaultValue = Integer.parseInt(tfsDefault[index].getText());
            } else if (types.getTypeChar(type).equals("NUMBER") && type.startsWith("BIGINT")) {
                // NOT TESTED
                defaultValue = Long.parseLong(tfsDefault[index].getText());
            } else if (types.getTypeChar(type).equals("DECIMAL") && type.startsWith("FLOAT")) {
                // NOT TESTED
                defaultValue = Float.parseFloat(tfsDefault[index].getText());
            } else if (types.getTypeChar(type).equals("DECIMAL") && type.startsWith("DOUBLE")) {
                // NOT TESTED
                defaultValue = Double.parseDouble(tfsDefault[index].getText());
            } else if (types.getTypeChar(type).equals("BOOLEAN")) {
                // NOT TESTED
                defaultValue = tfsDefault[index].getText().equals("true");
            }
        }

        boolean setDefaultValue = ms.setDefaultValue(table, column, defaultValue);
        if (setDefaultValue) {
            updateTable.getDefaults().set(index, defaultValue);

            tfsDefault[index].setStyle(CSS.TEXT_FILL_HINT);
            cksDefault[index].setStyle(CSS.CKS_BG_HINT);
            btnsChangeDefault[index].setDisable(true);

            lbStatus.setText("Default value for column '" + column + "' has change to "
                    + (defaultValue != null ? defaultValue.toString() : "NULL"));
            lbStatus.setTextFill(NonCSS.TEXT_FILL_OK);
        } else {
            lbStatus.setText("Failt to change Default Value of column '" + column + "'");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
        }

        timers.playLbStatusReset(lbStatus);
    }

    // EXTRA=================================================
    private void rbsExtraAction(ActionEvent e) {
        setQOLVariables(e);
        int errorCount = 0;
        // ---------------------------------------------
        Arrays.asList(rbsExtraPopups).forEach(p -> p.hide());
        // ---------------------------------------------
        if (rbsExtra[index].isSelected()) {
            if (rbsPK[index].isSelected()) {
                rbsExtraPopups[index].hide();
                extraPKOK = true;
            } else {
                // lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
                rbsExtraPopups[index].show("An AUTO_INCREMENT column has to be a PRIMARY KEY");

                errorCount++;

                extraPKOK = false;
            }
            // ---------------------------------------------
            if (rbsFK[index].isSelected()) {
                // lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
                if (errorCount == 0) {
                    rbsExtraPopups[index].show("There's no need to have a FOREIGN KEY column with AUTO_INCREMENT");
                } else {
                    rbsExtraPopups[index].show(EXTRA_GENERAL_ERROR);
                }
                errorCount++;

                extraFKOK = false;
            } else {

                extraFKOK = true;
            }
            // ---------------------------------------------
            if (cksDefault[index].isSelected()) {
                // lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
                if (errorCount == 0) {
                    rbsExtraPopups[index]
                            .show("There's no need to have a DEFAULT value in a column with AUTO_INCREMENT");

                } else {
                    rbsExtraPopups[index].show(EXTRA_GENERAL_ERROR);

                }
                extraDefaultOK = false;
            } else {
                extraDefaultOK = true;
            }
            // ---------------------------------------------
        } else {
            // lbhExtra.setTextFill(NonCSS.TEXT_FILL);
            rbsExtraPopups[index].hide();

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
                int extra = rbsExtra[index].isSelected() ? index : -1;
                if (extra != extraO) {
                    // rbsExtraPopups[index].hide();
                    rbsExtra[index].setStyle(CSS.BG_COLOR);
                    btnUpdateExtra.setDisable(false);
                } else {
                    // rbsExtraPopups[index].show(SAME_VALUE);
                    rbsExtra[index].setStyle(CSS.BG_COLOR_HINT);
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
        boolean extra = Arrays.asList(rbsExtra).stream().anyMatch(rb -> {
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

            lbStatus.setText("Changed Extra");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_OK);
        } else {
            lbStatus.setText("Fail to change Extra");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
        }

        timers.playLbStatusReset(lbStatus);
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

        List<String> pks = new ArrayList<>(MSQL.MAX_COLUMNS);
        List<TString> fks = new ArrayList<>(MSQL.MAX_COLUMNS);
        // List<IntString> defaults = new ArrayList<>(MSQL.MAX_COLUMNS);
        int extra = 0;
        // RIGHT-------------------
        String dist = Custom.getOldDist(currentRowLength, btnsDist);
        String imageC = Custom.getOldImageC(currentRowLength, btnsImageC);
        String imageCPath = Custom.getImageCPath(currentRowLength, tfImageCPath, btnsImageC);
        /*
         * boolean distPresent = false; int distCount = 0;
         */
        // -------------------------------------------------
        for (int a = 0; a < currentRowLength; a++) {
            // LEFT-------------------
            columnsNames[a] = tfsColumn[a].getText().toLowerCase().trim().replace(" ", "_");
            typesNames[a] = tfasType[a].getText();

            if (tfsTypeLength[a].isVisible() && types.getTypeLength(typesNames[a]) > 0) {
                // typesNames[a] += tfsTypeLength[a].getText();
                typesLengths[a] = Integer.parseInt(tfsTypeLength[a].getText());
            }
            nulls[a] = cksNull[a].isSelected();

            if (rbsPK[a].isSelected()) {
                pks.add(columnsNames[a]);
            }
            if (rbsFK[a].isSelected()) {
                String fkText = tfasFK[a].getText();
                String[] split = fkText.split(".");

                fks.add(new TString(columnsNames[a], split[1], split[2]));
                // listFK.add(new TString(colNames[a], tableR, colR));
            }
            if (cksDefault[a].isSelected()) {
                // defaults.add(new IntString(a + 1, tfsDefault[a].getText()));
                defaults[a] = tfsDefault[a].getText();
            } else {
                defaults[a] = null;
            }

            if (rbsExtra[a].isSelected()) {
                extra = a + 1;
            }
            // RIGHT-------------------
            /*
             * if (btnsDist[a].isSelected()) {// OLD WAY if (!distPresent) { dist.delete(0,
             * dist.length()); dist.append("X0: "); distPresent = true; }
             * dist.deleteCharAt(1); dist.insert(1, Integer.toString(++distCount));
             * dist.append(Integer.toString(a + 1) + "_"); }
             * 
             * if (btnsImageC[a].isSelected()) { //imageC.delete(0, imageC.length());
             * //imageC.append("C" + (a + 1));
             * 
             * imageCPath.delete(0, imageCPath.length());
             * imageCPath.append(tfImageCPath.getText()); }
             */
        }
        /*
         * if (distCount > 0) {// DELETE LAST '_' dist.deleteCharAt(dist.length() - 1);
         * }
         */
        // CREATE UPDATE ---------------------------------------
        MSQLCreate msc = new MSQLCreate(new CurrenConnection());
        for (int a = 0; a < currentRowLength; a++) {
            msc.addTypesWidth(new DInt(a + 1, typesLengths[a]));
            msc.addAllowsNull(new IntBoolean(a + 1, nulls[a]));
            msc.addDefault(defaults[a] != null ? new IntString(a + 1, defaults[a]) : null);
        }
        msc.addAllPrimaryKeys(pks);
        msc.addAllForeignKeys(fks);
        msc.setAutoIncrement(extra);
        boolean createTable = msc.createTable(tableName, columnsNames, typesNames);
        // INSERT -----------------------------------------------
        if (createTable) {
            Object[] values = new Object[] { null, tableName.replace("_", " "), dist, imageC, imageCPath };
            boolean insert = ms.insert(MSQL.TABLE_NAMES, values);
            if (insert) {
                // Menus.getInstance(vf).addMenuItemsReset();// NOT TESTED

                lbStatus.setText("Table '" + tableName.replace("_", " ") + "' has been created!");
                lbStatus.setTextFill(NonCSS.TEXT_FILL_OK);
            } else {
                // DELETE CREATED TABLE
                lbStatus.setText("Mayor Error (Table has been create but not inserted on " + MSQL.TABLE_NAMES);
                lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
            }
        } else {
            lbStatus.setText("Table Failed to be created");
            lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
        }

        timers.playLbStatusReset(lbStatus);
    }

    private void btnCreateHelpAction(ActionEvent e) {
        Bounds sb = btnCreateHelp.localToScreen(btnCreateHelp.getBoundsInLocal());
        createHelpPopup.show(btnCreateHelp, sb.getMinX(), sb.getMinY());
    }

    // RIGHT-----------------------------------------
    // DIST=================================================
    private void btnsDistAction(ActionEvent e) {
        // MAY HAVE TO ADD DIST BUTTONS TO AN OBSERVABLE LIST (ADD OR REMOVE COLUMN)
        ToggleButton btn = (ToggleButton) e.getSource();
        int index = Integer.parseInt(btn.getId());
        // int errorCount = 0;
        if (btn.isSelected()) {
            if (!rbsExtra[index].isSelected()) {
                lbhDist.setTextFill(NonCSS.TEXT_FILL);
                btnsDistPopups[index].hide();

                distExtraOK = true;
            } else {
                lbhDist.setTextFill(NonCSS.TEXT_FILL_ERROR);
                btnsDistPopups[index].show("Unnecesary selection when this column is already AUTO_INCREMENT");

                distExtraOK = true;
                btnsDistControl(index);
                // errorCount++;
            }
        } else {
            lbhDist.setTextFill(NonCSS.TEXT_FILL);
            btnsDistPopups[index].hide();

            distExtraOK = true;
            btnsDistControl(index);
        }

        // UPDATE--------------------------------------------------------------
        distUpdate(index);
        // --------------------------------------------------------------
        masterControl();
    }

    private void btnsDistControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {
                ToggleButton btn = btnsDist[a];
                if (btn.isSelected() && rbsExtra[a].isSelected()) {
                    distExtraOK = false;
                    break;
                }
            }
        }
    }

    private void distUpdate(int index) {
        if (updateControl) {
            if (distExtraOK) {
                boolean update = false;
                for (int a = 0; a < currentRowLength; a++) {
                    boolean dist = updateTable.getDist().get(index).equals("Yes");
                    if (btnsDist[index].isSelected() != dist) {
                        update = true;
                        break;
                    }
                }
                if (update) {
                    // btnsDistPopups[index].hide();
                    btnUpdateDist.setStyle(CSS.TEXT_FILL);
                    btnUpdateDist.setDisable(false);
                } else {
                    // btnsDistPopups[index].show(SAME_VALUE);
                    btnUpdateDist.setStyle(CSS.TEXT_FILL_HINT);
                    btnUpdateDist.setDisable(true);
                }
            } else {
                btnUpdateDist.setDisable(true);
            }
        }
    }

    // IMAGEC================================================
    private void btnsImageCAction(ActionEvent e) {
        ToggleButton btn = (ToggleButton) e.getSource();
        int index = Integer.parseInt(btn.getId());
        if (btn.isSelected()) {
            Boolean[] bools = new Boolean[currentRowLength];
            Arrays.fill(bools, false);
            listImageC.setAll(bools);

            listImageC.set(index, true);
        } else {
            listImageC.set(index, false);
        }

        // UPDATE---------------------------------------
        imageCUpdate(index);
    }

    private void listImageCChange(Change<? extends Boolean> c) {
        while (c.next()) {
            if (c.wasAdded() || c.wasRemoved() || c.wasUpdated() || c.wasReplaced()) {
                if (listImageC.stream().allMatch(bool -> !bool)) {
                    tfImageCPath.setDisable(true);
                    btnSelectImageC.setDisable(true);

                    imageCPathOk = true;
                } else {
                    tfImageCPath.setDisable(false);
                    btnSelectImageC.setDisable(false);

                    imageCPathOk = false;
                }
            }
        }

        masterControl();
    }

    private void btnSelectImageCAction(ActionEvent e) {
        File file = directoryChooser.showDialog(vf.getStage());
        tfImageCPath.setText(file.getPath());
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

        masterControl();
    }

    private void imageCUpdate(int index) {
        if (updateControl) {
            if (currentRowLength <= updateTable.getRowLength()) {
                if (imageCPathOk) {
                    boolean imageCSelected = updateTable.getImageC().get(index).equals("Yes");
                    int[] indexs = { -1 };
                    updateTable.getImageCPath().stream().anyMatch(p -> {
                        indexs[0]++;
                        return !p.equals("NONE");
                    });
                    String pathO = updateTable.getImageCPath().get(indexs[0]);
                    if (btnsImageC[index].isSelected() != imageCSelected
                            || (!tfImageCPath.isDisabled() && !tfImageCPath.getText().equals(pathO))) {
                        btnUpdateImageC.setStyle(CSS.TEXT_FILL);
                        btnUpdateImageC.setDisable(false);
                    } else {
                        // btnsImageCPopups[index].show(SAME_VALUE);
                        btnUpdateImageC.setStyle(CSS.TEXT_FILL_HINT);
                        btnUpdateImageC.setDisable(true);
                    }
                } else {
                    btnUpdateImageC.setDisable(true);
                }
            } else {
                System.out.println("ADD OR REMOVE COLUMN STATE");
                // ADD OR REMOVE COLUMN STATE
            }
        }
    }

    // INIT ---------------------------------------------
    private void fkReferencesInit() {
        //Key[] row = keys.getRowPrimaryKeys();
        List<String> list = new ArrayList<>();
        List<PK> pksList = pks.getPksList();

        for (int a = 0; a < pks.getPksList().size(); a++) {
            String databaseName = pksList.get(a).getDatabase();
            String tableName = pksList.get(a).getTable();
            Map<Integer, String> columns = pksList.get(a).getColumns();
            //String column = row[a].getColumnName();
            StringBuilder sb = new StringBuilder(databaseName).append(".").append(tableName).append(" (");
            columns.forEach((i, s) -> sb.append(s).append(","));
            sb.deleteCharAt(sb.length() - 1);//TEST
            sb.append(")");
            list.add(sb.toString());
        }

        String[] elements = list.toArray(new String[list.size()]);
        Arrays.asList(tfsFKPs).forEach(e -> {
            e.setLvOriginalItems(elements);
            e.getLv().getItems().addAll(elements);
            e.getLv().getSelectionModel().select(0);// UNTESTED
        });
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

            lbsN[row] = new Label("Column " + (row + 1));
            hbsN[row] = new HBox(lbsN[row]);
            // COLUMN NAMES---------------------------------------
            String columnNameStore = storeNodes.getColumnNames() != null ? storeNodes.getColumnNames()[a] : "";
            tfsColumn[row] = new TextField(columnNameStore);
            tfsColumnPopups[row] = new PopupMessage(tfsColumn[row]);

            btnsRemoveColumn[row] = new Button("X");
            btnsAddColumn[row] = new Button("+");
            btnsRenameColumn[row] = new Button("C");

            hbsName[row] = new HBox(tfsColumn[row], btnsRemoveColumn[row], btnsAddColumn[row], btnsRenameColumn[row]);
            hbsName[row].setPadding(new Insets(0, 2, 0, 2));
            // TYPES----------------------------------------------
            // tfasType[row] = new TextFieldAutoC(row, types.getTypeNames());
            String typesStore = storeNodes.getTypes() != null ? storeNodes.getTypes()[a] : "";
            tfasType[row] = new TextField(typesStore);
            tfsTypePs[row] = new PopupAutoC(tfasType[row], types.getTypeNames());
            tfsTypePopups[row] = new PopupMessage(tfasType[row]);

            String typesLengthStore = storeNodes.getTypesLength() != null ? storeNodes.getTypesLength()[a] : "1";
            tfsTypeLength[row] = new TextField(typesLengthStore);
            tfsTypeLengthPopups[row] = new PopupMessage(tfsTypeLength[row]);

            btnsChangeType[row] = new Button("C");

            hbsType[row] = new HBox(tfasType[row], tfsTypeLength[row], btnsChangeType[row]);
            hbsType[row].setPadding(new Insets(0, 2, 0, 2));
            // NULLS----------------------------------------------
            boolean nullStore = storeNodes.getNulls() != null ? storeNodes.getNulls()[a] : false;
            cksNull[row] = new CheckBox();
            cksNull[row].setSelected(nullStore);
            btnsChangeNull[row] = new Button("C");
            hbsNull[row] = new HBox(cksNull[row], btnsChangeNull[row]);
            hbsNull[row].setPadding(new Insets(0, 2, 0, 2));
            // PKS----------------------------------------------
            boolean pkStore = storeNodes.getPks() != null ? storeNodes.getPks()[a] : false;
            rbsPK[row] = new RadioButton();
            rbsPK[row].setSelected(pkStore);
            rbsPKPopups[row] = new PopupMessage(rbsPK[row]);
            // btnsChangePK[row] = new Button("C");
            hbsPK[row] = new HBox(rbsPK[row]);
            // FKS----------------------------------------------
            boolean fkStore = storeNodes.getFks() != null ? storeNodes.getFks()[a] : false;
            rbsFK[row] = new RadioButton();
            rbsFK[row].setSelected(fkStore);
            rbsFKPopups[row] = new PopupMessage(rbsFK[row]);

            String fkTextStore = storeNodes.getFksText() != null ? storeNodes.getFksText()[a] : "";
            tfasFK[row] = new TextField(fkTextStore);
            tfsFKPs[row] = new PopupAutoC(tfasFK[row]);
            tfsFKPopups[row] = new PopupMessage(tfasFK[row]);

            // btnsChangeFK[row] = new Button("C");
            hbsFK[row] = new HBox(rbsFK[row], tfasFK[row]);
            // DEFAULTS----------------------------------------------
            boolean defaultStore = storeNodes.getDefaults() != null ? storeNodes.getDefaults()[a] : false;
            cksDefault[row] = new CheckBox();
            cksDefault[row].setSelected(defaultStore);
            cksDefaultPopups[row] = new PopupMessage(cksDefault[row]);

            String defaultTextStore = storeNodes.getDefaultsText() != null ? storeNodes.getDefaultsText()[a] : "";
            tfsDefault[row] = new TextField(defaultTextStore);
            tfsDefaultPopups[row] = new PopupMessage(tfsDefault[row]);

            btnsChangeDefault[row] = new Button("C");
            hbsDefault[row] = new HBox(cksDefault[row], tfsDefault[row], btnsChangeDefault[row]);
            hbsDefault[row].setPadding(new Insets(0, 2, 0, 2));
            // EXTRA----------------------------------------------
            boolean extratStore = storeNodes.getExtra() != null ? storeNodes.getExtra()[a] : false;
            rbsExtra[row] = new RadioButton();
            rbsExtra[row].setSelected(extratStore);
            rbsExtraPopups[row] = new PopupMessage(rbsExtra[row]);
            hbsExtra[row] = new HBox(rbsExtra[row]/* , btnsChangeExtra[row] */);
            // -----------------------------------------
            // OTHERS ---------------------------------
            tfsColumn[row].setId(Integer.toString(row));
            btnsRemoveColumn[row].setId(Integer.toString(row));
            btnsAddColumn[row].setId(Integer.toString(row));
            btnsRenameColumn[row].setId(Integer.toString(row));
            tfasType[row].setId("TF-TYPE-" + row);
            tfsTypeLength[row].setId(Integer.toString(row));
            btnsChangeType[row].setId(Integer.toString(row));
            cksNull[row].setId(Integer.toString(row));
            btnsChangeNull[row].setId(Integer.toString(row));
            rbsPK[row].setId(Integer.toString(row));
            rbsFK[row].setId(Integer.toString(row));
            tfasFK[row].setId(Integer.toString(row));
            cksDefault[row].setId(Integer.toString(row));
            tfsDefault[row].setId(Integer.toString(row));
            btnsChangeDefault[row].setId(Integer.toString(row));
            rbsExtra[row].setId(Integer.toString(row));
            // ----------------------------------------------
            tfsColumn[row].setPromptText("Column name required");
            tfsDefault[row].setPromptText("Value Required");
            // ----------------------------------------------
            tfasType[row].setStyle(CSS.TFAS_DEFAULT_LOOK);
            // TYPE DEFAULT SELECTION----------------------------
            tfsTypePs[row].getLv().getSelectionModel().select(presetTypeSelected.get(row).getTypeName());
            tfsTypeLength[row].setText(Integer.toString(presetTypeSelected.get(row).getTypeLength()));
            // ----------------------------------------------

            tfsColumn[row].setPrefWidth(-1);
            btnsRemoveColumn[row].setMinWidth(40);
            btnsRemoveColumn[row].setMaxWidth(40);
            btnsAddColumn[row].setMinWidth(40);
            btnsAddColumn[row].setMaxWidth(40);
            tfasType[row].setPrefWidth(140);
            tfsTypeLength[row].setMinWidth(40);
            tfsTypeLength[row].setMaxWidth(40);
            // tfasFK[row].setPrefWidth(-1);
            // tfasFK[row].setMaxHeight(30);
            hbsFK[row].setPrefWidth(-1);
            tfasFK[row].setPrefWidth(-1);
            // SOME PROPERTIES AND LISTENERS---------------------------
            btnsRenameColumn[row].managedProperty().bind(btnsRenameColumn[row].visibleProperty());
            tfsTypeLength[row].managedProperty().bind(tfsTypeLength[row].visibleProperty());
            btnsChangeType[row].managedProperty().bind(btnsChangeType[row].visibleProperty());
            btnsChangeNull[row].managedProperty().bind(btnsChangeNull[row].visibleProperty());
            // btnsChangePK[row].managedProperty().bind(btnsChangePK[row].visibleProperty());
            tfasFK[row].managedProperty().bind(tfasFK[row].visibleProperty());
            // btnsChangeFK[row].managedProperty().bind(btnsChangeFK[row].visibleProperty());
            tfsDefault[row].managedProperty().bind(tfsDefault[row].visibleProperty());
            btnsChangeDefault[row].managedProperty().bind(btnsChangeDefault[row].visibleProperty());
            // ----------------------------------------------------------
            tfasFK[row].setVisible(false);
            tfsDefault[row].setVisible(false);

            btnsRenameColumn[row].setDisable(true);
            btnsChangeType[row].setDisable(true);
            btnsChangeNull[row].setDisable(true);
            btnsChangeDefault[row].setDisable(true);
            // STYLE-------------------------------------------------------
            // tfasType[row].getStyleClass().clear();
            tfasType[row].setStyle(null);

            hbsN[row].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsName[row].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsType[row].setStyle(CSS.BORDER_GRID_BOTTOM);
            tfsTypeLength[row].setStyle(CSS.TEXT_FILL);
            hbsNull[row].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsPK[row].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsFK[row].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsDefault[row].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsExtra[row].setStyle(CSS.BORDER_GRID_BOTTOM);
            // ADDING ROW------------------------------------------------
            if (index != 0 && add) {// FIRST ROW ADDED
                if (row == index) {// ADDED COLUMN
                    hbsN[row].setStyle(CSS.NEW_ROW);
                    hbsExtra[row].setStyle(CSS.NEW_ROW);
                } else {
                    hbsN[row].setDisable(true);
                    hbsName[row].setDisable(true);
                    hbsType[row].setDisable(true);
                    hbsNull[row].setDisable(true);
                    hbsPK[row].setDisable(true);
                    hbsFK[row].setDisable(true);
                    hbsDefault[row].setDisable(true);
                    hbsExtra[row].setDisable(true);
                }

            }
            // -------------------------------------------------------
            GridPane.setMargin(hbsN[row], INSETS);
            GridPane.setMargin(hbsName[row], INSETS);
            GridPane.setMargin(hbsType[row], INSETS);
            GridPane.setMargin(hbsNull[row], INSETS);
            GridPane.setMargin(hbsPK[row], INSETS);
            GridPane.setMargin(hbsFK[row], INSETS);
            GridPane.setMargin(hbsDefault[row], INSETS);
            GridPane.setMargin(hbsExtra[row], INSETS);
        }
        new ToggleGroupD<>(rbsExtra);
        // DISABLE PREVIOUS ROW-----------------------------------
        if (add) {
            for (int a = 0; a < index; a++) {
                hbsN[a].setDisable(true);
                hbsName[a].setDisable(true);
                hbsType[a].setDisable(true);
                hbsNull[a].setDisable(true);
                hbsPK[a].setDisable(true);
                hbsFK[a].setDisable(true);
                hbsDefault[a].setDisable(true);
                hbsExtra[a].setDisable(true);
            }
        }
    }

    private void leftGridPaneRestart(int index, boolean add) {
        initLeftNodes(index, add);
        // LISTENERS-------------------
        Arrays.asList(tfsColumn).forEach(e -> {
            e.setOnKeyReleased(this::tfsColumnsKeyReleased);
        });
        listColumns.removeListener(this::listColumnsChange);
        listColumns.addListener(this::listColumnsChange);

        int[] indexs = { 0 };
        Arrays.asList(btnsRemoveColumn).forEach(e -> {
            if (indexs[0]++ != index) {
                e.setOnAction(this::btnsRemoveUpdateAction);
            }
        });
        indexs[0] = 0;
        Arrays.asList(btnsAddColumn).forEach(e -> {
            if (indexs[0]++ != index) {
                e.setOnAction(this::btnsColumnSetVisibleAction);
            }
        });

        Arrays.asList(tfasType).forEach(e -> {
            e.textProperty().removeListener(this::tfasTypeTextProperty);
            e.textProperty().addListener(this::tfasTypeTextProperty);
        });
        Arrays.asList(tfsTypeLength).forEach(e -> {
            e.textProperty().removeListener(this::tfsTypeLengthTextProperty);
            e.textProperty().addListener(this::tfsTypeLengthTextProperty);
        });
        Arrays.asList(rbsPK).forEach(e -> e.setOnAction(this::cksPKAction));
        Arrays.asList(rbsFK).forEach(e -> e.setOnAction(this::cksFKAction));
        Arrays.asList(tfasFK).forEach(e -> {
            e.textProperty().removeListener(this::tfasFKTextProperty);
            e.textProperty().addListener(this::tfasFKTextProperty);
        });
        Arrays.asList(cksDefault).forEach(e -> e.setOnAction(this::cksDefaultAction));
        Arrays.asList(tfsDefault).forEach(e -> e.setOnKeyReleased(this::tfsDefaultKeyReleased));
        Arrays.asList(rbsExtra).forEach(e -> {
            e.removeEventHandler(ActionEvent.ACTION, this::rbsExtraAction);
            e.addEventHandler(ActionEvent.ACTION, this::rbsExtraAction);
        });
        // ADD 'ADD COLUMN' LISTENER--------------------
        if (index != 0) {
            btnsAddColumn[index].setText("A");
            btnsAddColumn[index].setStyle(CSS.ADD_COL_BUTTON);

            btnsAddColumn[index].setOnAction(this::btnsAddColumnUpdateAction);

            btnsRemoveColumn[index].setText("C");
            btnsRemoveColumn[index].setStyle(CSS.REMOVE_COL_BUTTON);

            btnsRemoveColumn[index].setOnAction(this::btnsCancelAddColumnUpdateAction);

            rbsPK[index].setDisable(true);
            rbsFK[index].setDisable(true);
            rbsExtra[index].setDisable(true);
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
            btnsDist[row] = new ToggleButton("" + (row + 1));
            btnsDist[row].setSelected(distStore);
            btnsDistPopups[row] = new PopupMessage(btnsDist[row]);
            // IMAGEC--------------------
            boolean imageCStore = storeNodes.getImageCs() != null ? storeNodes.getImageCs()[a] : false;
            btnsImageC[row] = new ToggleButton("" + (row + 1));
            btnsImageC[row].setSelected(imageCStore);
            btnsImageCPopups[row] = new PopupMessage(btnsImageC[row]);
            // ADD ROW-------------------------------------
            if (index != 0 & add) {
                if (row == index) {
                    // NOTHING YET
                } else {
                    btnsDist[row].setDisable(true);
                    btnsImageC[row].setDisable(true);
                }
            }
            // --------------------------------------------
            btnsDist[row].setId(Integer.toString(row));
            btnsImageC[row].setId(Integer.toString(row));

            btnsDist[row].setMinWidth(40);
            btnsDist[row].setMaxWidth(40);
            btnsImageC[row].setMinWidth(40);
            btnsImageC[row].setMaxWidth(40);

            btnsDist[row].managedProperty().bind(btnsDist[row].visibleProperty());
            btnsImageC[row].managedProperty().bind(btnsImageC[row].visibleProperty());

            btnsDist[row].setStyle(CSS.BORDER_GRID_BOTTOM);
            btnsImageC[row].setStyle(CSS.BORDER_GRID_BOTTOM);

            GridPane.setMargin(btnsDist[row], INSETS);
            GridPane.setMargin(btnsImageC[row], INSETS);
        }
        new ToggleGroupD<>(btnsImageC);
        tfImageCPathPopup = new PopupMessage(tfImageCPath);
        // SUB-------------------------------------
        lbUpdateLeft.setDisable(true);
        directoryChooser.setTitle("Select Image for a column");
        // DISABLE PREVIOUS ROW-----------------------------------
        if (add) {
            for (int a = 0; a < index; a++) {
                btnsDist[a].setDisable(true);
                btnsImageC[a].setDisable(true);
            }
        }
    }

    private void rightGridPaneRestart(int index, boolean add) {
        initRightNodes(index, add);
        // LISTENERS----------------------------
        Arrays.asList(btnsDist).forEach(e -> e.setOnAction(this::btnsDistAction));
        Arrays.asList(btnsImageC).forEach(e -> {
            e.removeEventHandler(ActionEvent.ACTION, this::btnsImageCAction);
            e.addEventHandler(ActionEvent.ACTION, this::btnsImageCAction);
        });

        if (index != 0) {
            btnsDist[index].setDisable(true);
            btnsImageC[index].setDisable(true);
        }
    }

    void btnAddRemoveColumnInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (a < presetRowsLenght - 1) {
                btnsAddColumn[a].setVisible(false);
                btnsRemoveColumn[a].setVisible(false);
            }
        }
        btnsRemoveColumn[0].setDisable(true);
        btnsAddColumn[MSQL.MAX_COLUMNS - 1].setDisable(true);
    }

    void pesetListInit(int rowLength) {
        for (int a = 0; a < rowLength; a++) {
            listColumns.add(tfsColumn[a].getText().trim());
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

    public Label[] getLbsN() {
        return lbsN;
    }

    public void setLbsN(Label[] lbsN) {
        this.lbsN = lbsN;
    }

    public HBox[] getHbsName() {
        return hbsName;
    }

    public void setHbsName(HBox[] hbsName) {
        this.hbsName = hbsName;
    }

    public TextField[] getTfsColumn() {
        return tfsColumn;
    }

    public void setTfsColumn(TextField[] tfsColumn) {
        this.tfsColumn = tfsColumn;
    }

    public Button[] getBtnsRemoveColumn() {
        return btnsRemoveColumn;
    }

    public void setBtnsRemoveColumn(Button[] btnsRemoveColumn) {
        this.btnsRemoveColumn = btnsRemoveColumn;
    }

    public Button[] getBtnsAddColumn() {
        return btnsAddColumn;
    }

    public void setBtnsAddColumn(Button[] btnsAddColumn) {
        this.btnsAddColumn = btnsAddColumn;
    }

    public Button[] getBtnsRenameColumn() {
        return btnsRenameColumn;
    }

    public void setBtnsRenameColumn(Button[] btnsRenameColumn) {
        this.btnsRenameColumn = btnsRenameColumn;
    }

    public HBox[] getHbsType() {
        return hbsType;
    }

    public void setHbsType(HBox[] hbsType) {
        this.hbsType = hbsType;
    }

    public TextField[] getTfasType() {
        return tfasType;
    }

    public void setTfasType(TextField[] tfasType) {
        this.tfasType = tfasType;
    }

    public TextField[] getTfsTypeLength() {
        return tfsTypeLength;
    }

    public void setTfsTypeLength(TextField[] tfsTypeLength) {
        this.tfsTypeLength = tfsTypeLength;
    }

    public Button[] getBtnsChangeType() {
        return btnsChangeType;
    }

    public void setBtnsChangeType(Button[] btnsChangeType) {
        this.btnsChangeType = btnsChangeType;
    }

    public HBox[] getHbsNull() {
        return hbsNull;
    }

    public void setHbsNull(HBox[] hbsNull) {
        this.hbsNull = hbsNull;
    }

    public CheckBox[] getCksNull() {
        return cksNull;
    }

    public void setCksNull(CheckBox[] cksNull) {
        this.cksNull = cksNull;
    }

    public Button[] getBtnsChangeNull() {
        return btnsChangeNull;
    }

    public void setBtnsChangeNull(Button[] btnsChangeNull) {
        this.btnsChangeNull = btnsChangeNull;
    }

    public HBox[] getHbsPK() {
        return hbsPK;
    }

    public void setHbsPK(HBox[] hbsPK) {
        this.hbsPK = hbsPK;
    }

    public RadioButton[] getRbsPK() {
        return rbsPK;
    }

    public void setRbsPK(RadioButton[] cksPK) {
        this.rbsPK = cksPK;
    }

    public HBox[] getHbsFK() {
        return hbsFK;
    }

    public void setHbsFK(HBox[] hbsFK) {
        this.hbsFK = hbsFK;
    }

    public RadioButton[] getCksFK() {
        return rbsFK;
    }

    public void setCksFK(RadioButton[] rbsFK) {
        this.rbsFK = rbsFK;
    }

    public TextField[] getTfasFK() {
        return tfasFK;
    }

    public void setTfasFK(TextField[] tfasFK) {
        this.tfasFK = tfasFK;
    }

    public HBox[] getHbsDefault() {
        return hbsDefault;
    }

    public void setHbsDefault(HBox[] hbsDefault) {
        this.hbsDefault = hbsDefault;
    }

    public CheckBox[] getCksDefault() {
        return cksDefault;
    }

    public void setCksDefault(CheckBox[] cksDefault) {
        this.cksDefault = cksDefault;
    }

    public TextField[] getTfsDefault() {
        return tfsDefault;
    }

    public void setTfsDefault(TextField[] tfsDefault) {
        this.tfsDefault = tfsDefault;
    }

    public Button[] getBtnsChangeDefault() {
        return btnsChangeDefault;
    }

    public void setBtnsChangeDefault(Button[] btnsChangeDefault) {
        this.btnsChangeDefault = btnsChangeDefault;
    }

    public HBox[] getHbsExtra() {
        return hbsExtra;
    }

    public void setHbsExtra(HBox[] hbsExtra) {
        this.hbsExtra = hbsExtra;
    }

    public RadioButton[] getRbsExtra() {
        return rbsExtra;
    }

    public void setRbsExtra(RadioButton[] rbsExtra) {
        this.rbsExtra = rbsExtra;
    }

    public ToggleButton[] getBtnsDist() {
        return btnsDist;
    }

    public void setBtnsDist(ToggleButton[] btnsDist) {
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

    public ToggleButton[] getBtnsImageC() {
        return btnsImageC;
    }

    public void setBtnsImageC(ToggleButton[] btnsImageC) {
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

    public HBox[] getHbsN() {
        return hbsN;
    }

    public void setHbsN(HBox[] hbsN) {
        this.hbsN = hbsN;
    }

    public Button getBtnUpdateImageC() {
        return btnUpdateImageC;
    }

    public void setBtnUpdateImageC(Button btnUpdateImageC) {
        this.btnUpdateImageC = btnUpdateImageC;
    }

}
