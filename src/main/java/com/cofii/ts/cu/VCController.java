package com.cofii.ts.cu;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cofii.ts.first.VF;
import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.FKS;
import com.cofii.ts.store.PK;
import com.cofii.ts.store.PKS;
import com.cofii.ts.store.SQLType;
import com.cofii.ts.store.SQLTypes;
import com.cofii.ts.store.UpdateTable;
import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Path;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.LabelStatus;
import com.cofii2.components.javafx.ToggleGroupD;
import com.cofii2.components.javafx.TrueFalseWindow;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.components.javafx.popup.PopupKV;
import com.cofii2.components.javafx.popup.PopupMenu;
import com.cofii2.components.javafx.popup.PopupMessage;
import com.cofii2.custom.Custom;
import com.cofii2.methods.MList;
import com.cofii2.mysql.MSQLCreate;
import com.cofii2.mysql.MSQLP;
import com.cofii2.mysql.RootConfigConnection;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.util.Duration;

public class VCController implements Initializable {

    public static final String LBH_COLUMN_NAMES = "Name";
    public static final String LBH_COLUMN_NAMES_ERROR = "Duplicated names or empty columns";
    public static final Insets INSETS = new Insets(2, 2, 2, 2);

    private static final String ILLEGAL_CHARS = "Illegal Chars";
    private static final String SELECTION_UNMATCH = "Selection Unmatch";
    private static final String EMPTY_TEXT = "Column name field can't be empty";
    private static final String EXTRA_GENERAL_ERROR = "PK, FK or Default are not allowed to selected/unselected";
    // -------------------------------------------------
    private int presetRowsLenght = 2;
    private int currentRowLength = presetRowsLenght;
    private List<SQLType> presetTypeSelected = new ArrayList<>(MSQL.MAX_COLUMNS);

    private ObservableList<String> listColumns = FXCollections.observableArrayList();
    private ObservableList<Boolean> listImageC = FXCollections.observableArrayList();

    @FXML
    private BorderPane bpMain;
    // TOP--------------------------------------------------
    @FXML
    private HBox hbTop;
    @FXML
    private Label lbTable;
    @FXML
    private TextField tfTable;
    @FXML
    private Button btnRenameTable;
    // CENTER---------------------------------------------
    @FXML
    private ScrollPane scGridPane;
    @FXML
    private GridPane gridPane;

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
    private Label lbhTextArea;
    // BOTTOM----------------------------------------
    @FXML
    private HBox hbUpdates;
    @FXML
    private Label lbUpdate;
    @FXML
    private Button btnUpdatePK;
    @FXML
    private Button btnUpdateFK;
    @FXML
    private Button btnUpdateExtra;
    @FXML
    private Button btnUpdateDist;

    @FXML
    private Button btnSelectImageC;

    @FXML
    private Button btnCancel;
    @FXML
    private Button btnCreateUpdate;
    @FXML
    private Button btnCreateHelp;

    @FXML
    private HBox hbStatus;
    private LabelStatus lbStatus = new LabelStatus();
    // ARRAYS-----------------------------------------------------------
    private List<HBox> hbsN = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private List<Label> lbsN = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<HBox> hbsName = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private List<TextField> tfsColumn = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> tfsColumnPopups = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<Button> btnsRemoveColumn = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<Button> btnsAddColumn = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final PopupMenu beforeAfterOptionMenu = new PopupMenu("Before", "After");
    private final Tooltip beforeAfterOptionTooltip = new Tooltip("Right click to add...");
    private List<Button> btnsRenameColumn = new ArrayList<>(MSQL.MAX_COLUMNS);

    private List<HBox> hbsType = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private List<TextField> tfsType = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupAutoC> tfsTypePs = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> tfsTypePopups = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<TextField> tfsTypeLength = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> tfsTypeLengthPopups = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<Button> btnsChangeType = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<HBox> hbsNull = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private List<CheckBox> cksNull = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<Button> btnsChangeNull = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<HBox> hbsPK = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private List<RadioButton> rbsPK = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> rbsPKPopups = new ArrayList<>(MSQL.MAX_COLUMNS);

    private List<HBox> hbsFK = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private List<TextField> tfsFK = new ArrayList<>(MSQL.MAX_COLUMNS);
    private String[] pksReferences;
    private List<PopupAutoC> tfsFKPs = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> tfsFKPopups = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<ToggleButton> btnsSelectedFK = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> btnsSelectedFKPopups = new ArrayList<>(MSQL.MAX_COLUMNS);

    private List<HBox> hbsDefault = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private List<CheckBox> cksDefault = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> cksDefaultPopups = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<TextField> tfsDefault = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> tfsDefaultPopups = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<Button> btnsChangeDefault = new ArrayList<>(MSQL.MAX_COLUMNS);

    private List<HBox> hbsExtra = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private List<RadioButton> rbsExtra = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> rbsExtraPopups = new ArrayList<>(MSQL.MAX_COLUMNS);

    private List<ToggleButton> btnsDist = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> btnsDistPopups = new ArrayList<>(MSQL.MAX_COLUMNS);

    private List<ToggleButton> btnsTextArea = new ArrayList<>(MSQL.MAX_COLUMNS);
    private List<PopupMessage> btnsTextAreaPopups = new ArrayList<>(MSQL.MAX_COLUMNS);

    private ObservableMap<String, Boolean> createHelpMap = FXCollections.observableHashMap();
    private PopupKV createHelpPopup = new PopupKV(createHelpMap);

    private ObservableMap<String, Boolean> updateAddColumnHelpMap = FXCollections.observableHashMap();
    private PopupKV updateAddColumnHelpPopup = new PopupKV(updateAddColumnHelpMap);
    // ---------------------------------------------
    private VFController vfc;
    private VImageCController vicc;
    private MSQLP ms;

    private Database currentDatabse = Users.getInstance().getCurrenUser().getCurrentDatabase();
    private Table currentTable = currentDatabse.getCurrentTable();
    private SQLTypes types = SQLTypes.getInstance();
    private PKS pks = PKS.getInstance();
    private FKS fks = FKS.getInstance();

    private UpdateTable updateTable;

    private Pattern patternBWTC = Pattern.compile("[A-Za-z](\\w| )*");
    private Pattern patternTypeLength = Pattern.compile("\\d{1,5}");
    private Matcher matcher;
    // LISTENERS---------------------------------------------
    private final EventHandler<KeyEvent> tfsColumnsKeyReleasedListener = this::tfsColumnsKeyReleased;
    private final ListChangeListener<? super String> listColumnsChangeListener = this::listColumnsChange;

    private final EventHandler<ActionEvent> btnsAddColumnUpdateActionListener = this::btnsAddColumnUpdateAction;
    private final EventHandler<ActionEvent> btnsCancelAddColumnUpdateActionListener = this::btnsCancelAddColumnUpdateAction;
    private final EventHandler<ActionEvent> btnsRemoveUpdateActionListener = this::btnsRemoveUpdateAction;
    private final EventHandler<ActionEvent> btnsColumnSetVisibleActionListener = this::btnsColumnSetVisibleAction;

    private final ChangeListener<? super String> tfasTypeTextPropertyListener = this::tfasTypeTextProperty;
    private final ChangeListener<? super String> tfsTypeLengthTextPropertyListener = this::tfsTypeLengthTextProperty;

    private final EventHandler<ActionEvent> cksPKActionListener = this::cksPKAction;
    private final ChangeListener<? super String> tfasFKTextPropertyListener = this::tfasFKTextProperty;
    private final EventHandler<ActionEvent> cksDefaultActionListener = this::cksDefaultAction;
    private final EventHandler<KeyEvent> tfsDefaultKeyReleasedListener = this::tfsDefaultKeyReleased;
    private final EventHandler<ActionEvent> rbsExtraActionListener = this::rbsExtraAction;
    // CUSTOM---------------------
    private final EventHandler<ActionEvent> btnsDistActionListener = this::btnsDistAction;
    private final EventHandler<ActionEvent> btnsTextAreaActionListener = this::btnsTextAreaAction;
    // ---------------------------------------------------
    private int addIndex = -1;
    private boolean columnAdd = false;

    private boolean tableOK = false;

    private boolean columnSNOK = false;
    private boolean columnBWOK = false;
    private boolean columnAddOk = false;

    private boolean typeSelectionMatch = true;
    private boolean typeLengthOK = true;
    private boolean typeAddOk = true;
    private boolean typeLenghtAddOk = true;

    private boolean fkSelectionMatch = true;
    private boolean fkSelectionMatch2 = false;
    private boolean fkUpdate = false;

    private boolean defaultBW = true;
    private boolean defaultOK = true;
    private boolean defaultAddOk = true;

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
    private String tableName;
    private String column;
    private String type;

    private void setQOLVariables(Event e) {
        if (e.getSource() instanceof ButtonBase) {
            index = Integer.parseInt(((ButtonBase) e.getSource()).getId());
        } else if (e.getSource() instanceof TextField) {
            index = Integer.parseInt(((TextField) e.getSource()).getId());
        }

        tableName = currentTable.getName().replace(" ", "_");
        if (index >= 0 && !columnAdd && updateControl) {
            // column = updateTable.getColumns().get(index).replace(" ", "_");
            column = currentTable.getColumns().get(index).getName().replace("_", "_");
            type = tfsType.get(index).getText()
                    + (tfsTypeLength.get(index).isVisible() ? "(" + tfsTypeLength.get(index).getText() + ")" : "");
        } else if (columnAdd) {
            column = null;
            type = null;
        }
    }

    private String getCustomStringBeforeUpdate(List<ToggleButton> btns) {
        StringBuilder sb = new StringBuilder();
        int[] indexs = { 0 };
        btns.forEach(btn -> {
            if (btn.isVisible() && btn.isSelected()) {
                // sb.append(updateTable.getColumns().get(indexs[0]).replace(" ",
                // "_")).append(",");
                sb.append(currentTable.getColumns().get(indexs[0]).getName().replace(" ", "_")).append(",");
            }
            indexs[0]++;
        });

        return sb.length() == 0 ? "NONE" : sb.deleteCharAt(sb.length() - 1).toString();
    }

    private String getCustomStringFromUpdateTable(String type, int newColumnIndex, String newColumnName) {
        StringBuilder sb = new StringBuilder();
        int[] indexs = { 0 };

        List<Boolean> list = null;
        if (type.equals("dist")) {
            // list = updateTable.getDists();
            list = currentTable.getDistList();
        } else if (type.equals("textArea")) {
            list = currentTable.getTextAreaList();
        }
        list.forEach(s -> {
            boolean dist = s == null ? false : s;
            if (dist) {
                if (indexs[0] != newColumnIndex) {
                    sb.append(currentTable.getColumns().get(indexs[0]).getName().replace(" ", "_")).append(",");
                } else {
                    sb.append(newColumnName.replace(" ", "_")).append(",");
                }
            }
            indexs[0]++;
        });

        return sb.length() == 0 ? "NONE" : sb.deleteCharAt(sb.length() - 1).toString();
    }
    /*
     * private String getImageCBeforeUpdate() { int[] indexs = { -1 }; boolean
     * imageCIndexMatch = btnsImageC.stream().anyMatch(btn -> { indexs[0]++; return
     * btn.isVisible() && btn.isSelected(); });
     * 
     * return imageCIndexMatch ? currentTable.getColumns().get(indexs[0]).getName()
     * : "NONE"; }
     */

    // MASTER CONTROL---------------------------------------------
    private void createControl() {
        createHelpPopupReset();
        boolean allOk = tableOK && columnSNOK && columnBWOK && typeSelectionMatch && typeLengthOK && fkSelectionMatch
                && defaultBW && defaultOK && extraPKOK && extraFKOK && extraDefaultOK && distExtraOK && imageCPathOk;

        btnCreateUpdate.setDisable(!allOk);
    }

    private void addControl() {
        boolean disable = columnAddOk && typeAddOk && typeLenghtAddOk && defaultAddOk;
        createAddColumnHelpPopupReset();
        btnsAddColumn.get(addIndex).setDisable(!disable);
    }

    void masterControl() {
        if (!updateControl) {
            createControl();
        } else {

        }
    }

    private void btnSelectImageCControl() {
        boolean ok = columnSNOK && columnBWOK;
        btnSelectImageC.setDisable(!ok);
    }

    // LISTENERS---------------------------------------------
    // TABLE-------------------------------------
    private void tfTableKeyReleased(KeyEvent e) {
        if (!e.getCode().isArrowKey() && !e.getCode().isFunctionKey() && !e.getCode().isMediaKey()
                && !e.getCode().isModifierKey() && !e.getCode().isNavigationKey()) {
            String[] tableList = currentDatabse.getTablesNames();
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
                // String tableO = updateTable.getTable().toLowerCase().trim().replace(" ",
                // "_");
                String tableO = currentTable.getName().toLowerCase().trim().replace(" ", "_");
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
    private boolean getColumnMatch(String text) {
        final String textt = text.toUpperCase();
        matcher = patternBWTC.matcher(text);
        return matcher.matches() && MSQL.BAND_COLUMNS_NAMES.stream().anyMatch(word -> textt.equals(word));
    }

    private void tfsColumnsKeyReleased(KeyEvent e) {
        setQOLVariables(e);

        String text = tfsColumn.get(index).getText().toLowerCase().trim().replace(" ", "_");
        listColumns.set(index, text);

        if (!text.trim().isEmpty()) {
            matcher = patternBWTC.matcher(text);
            if (getColumnMatch(text)) {
                tfsColumn.get(index).setStyle(CSS.TEXT_FILL);
                // popupHide(tf);
                tfsColumnPopups.get(index).hide();

                columnBWOK = true;// REST CONTROL
                tfsColumnCreateControl(index);
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
        btnSelectImageCControl();
    }

    private void listColumnsChange(Change<? extends String> c) {
        while (c.next()) {
            columnSameControl();
        }
    }

    private void columnSameControl() {
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

    private void tfsColumnCreateControl(int index) {
        columnBWOK = true;
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
            boolean ok = false;
            if (columnBWOK && columnSNOK) {
                if (!text.equals(column)) {
                    // tfsColumnPopups[index].hide();
                    tfsColumn.get(index).setStyle(CSS.TEXT_FILL);
                    ok = true;
                } else {
                    // tfsColumnPopups[index].show(SAME_VALUE);
                    tfsColumn.get(index).setStyle(CSS.TEXT_FILL_HINT);
                    ok = false;

                }
            } else {
                ok = false;

            }
            if (!columnAdd) {
                btnsRenameColumn.get(index).setDisable(!ok);
            } else {
                columnAddOk = ok;
                addControl();
            }
        }
    }

    void btnsRenameColumn(ActionEvent e) {
        System.out.println(CC.CYAN + "\nRename Column" + CC.RESET);
        setQOLVariables(e);
        String newColumn = tfsColumn.get(index).getText().toLowerCase().trim().replace(" ", "_");

        boolean renameColumn = ms.renameColumn(tableName, column, newColumn);
        if (renameColumn) {
            System.out.println("\tSUCCESS");
            // RENAME THE COLUMN AND THEN TEST THE CANCEL BUTTON TO RESET
            // updateTable.getColumns().set(index, newColumn);
            currentTable.getColumns().get(index).setName(newColumn);

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
        gridPane.add(hbsN.get(index), 0, row);
        gridPane.add(hbsName.get(index), 1, row);
        gridPane.add(hbsType.get(index), 2, row);
        gridPane.add(hbsNull.get(index), 3, row);
        gridPane.add(hbsPK.get(index), 4, row);
        gridPane.add(hbsFK.get(index), 5, row);
        gridPane.add(hbsDefault.get(index), 6, row);
        gridPane.add(hbsExtra.get(index), 7, row);

        // NO IDEA WHY CKS ARE NOT VISIBLE
        cksNull.get(index).setVisible(true);
        cksDefault.get(index).setVisible(true);

        // gridPaneRight.add(btnsDist.get(index), 0, row);
        // gridPaneRight.add(btnsImageC.get(index), 1, row);
        // --------------------------------------------------
        listColumns.add(tfsColumn.get(index).getText());
        listImageC.add(false);
        // --------------------------------------------------
        currentRowLength++;
        tfsColumnCreateControl(-1);
        // columnSameControl();
        tfasTypeCreateControl(-1);
        tfsTypeLengthCreateControl(-1);
        tfasFKControl(-1, true);
        tfsDefaultControl(-1);

        masterControl();
        btnSelectImageCControl();
    }

    void btnsRemoveCreateAction(ActionEvent e) {
        setQOLVariables(e);
        btnsAddColumn.get(index - 1).setVisible(true);
        btnsRemoveColumn.get(index - 1).setVisible(true);
        // ---------------------------------------------------------
        gridPane.getChildren().removeAll(hbsN.get(index), hbsName.get(index), hbsType.get(index), hbsNull.get(index),
                hbsPK.get(index), hbsFK.get(index), hbsDefault.get(index), hbsExtra.get(index));

        // gridPaneRight.getChildren().removeAll(btnsDist.get(index),
        // btnsImageC.get(index));
        // ---------------------------------------------------------
        listColumns.remove(index);
        listImageC.remove(index);
        // ---------------------------------------------------------
        currentRowLength--;
        tfsColumnCreateControl(-1);
        // columnSameControl();
        tfasTypeCreateControl(-1);
        tfsTypeLengthCreateControl(-1);
        tfasFKControl(-1, true);
        tfsDefaultControl(-1);

        masterControl();
        btnSelectImageCControl();
    }

    // UPDATE========================================
    // DISPLAY
    private void updateAddVisible(int index) {
        // ADD---------------------------------------
        currentRowLength++;
        restartNodes(index + 1);
        // REMOVING-------------------------
        for (int a = 0; a < currentRowLength; a++) {// SKIP THE ONE ADDED
            if (index + 1 != a) {
                gridPane.getChildren().removeAll(hbsN.get(a), hbsName.get(a), hbsType.get(a), hbsNull.get(a),
                        hbsPK.get(a), hbsFK.get(a), hbsDefault.get(a), hbsExtra.get(a));

                // gridPaneRight.getChildren().removeAll(btnsDist.get(a), btnsImageC.get(a));
            }
        }
        // ADDING-------------------------
        for (int a = 0; a < currentRowLength; a++) {
            int row = a + 1;
            gridPane.add(hbsN.get(a), 0, row);
            gridPane.add(hbsName.get(a), 1, row);
            gridPane.add(hbsType.get(a), 2, row);
            gridPane.add(hbsNull.get(a), 3, row);
            gridPane.add(hbsPK.get(a), 4, row);
            gridPane.add(hbsFK.get(a), 5, row);
            gridPane.add(hbsDefault.get(a), 6, row);
            gridPane.add(hbsExtra.get(a), 7, row);

            // gridPaneRight.add(btnsDist.get(a), 0, row);
            // gridPaneRight.add(btnsImageC.get(a), 1, row);
        }

    }

    private void updateRemoveVisible(int index) {
        // REMOVING ROWS-------------------------
        for (int a = 0; a < currentRowLength; a++) {// SKIP THE ONE ADDED
            gridPane.getChildren().removeAll(hbsN.get(a), hbsName.get(a), hbsType.get(a), hbsNull.get(a), hbsPK.get(a),
                    hbsFK.get(a), hbsDefault.get(a), hbsExtra.get(a));

            // gridPaneRight.getChildren().removeAll(btnsDist.get(a), btnsImageC.get(a));
        }
        // REMOVING INDEX---------------------------
        currentRowLength--;
        hbsN.remove(index);
        lbsN.remove(index);
        // COLUMN NAMES---------------------------------------
        tfsColumn.remove(index);
        tfsColumnPopups.remove(index);
        btnsRemoveColumn.remove(index);
        btnsAddColumn.remove(index);
        btnsRenameColumn.remove(index);
        hbsName.remove(index);
        // TYPES-------------------------------
        tfsType.remove(index);
        tfsTypePs.remove(index);
        tfsTypePopups.remove(index);
        tfsTypeLength.remove(index);
        tfsTypeLengthPopups.remove(index);
        btnsChangeType.remove(index);
        hbsType.remove(index);
        // NULLS----------------------------------------------
        cksNull.remove(index);
        btnsChangeNull.remove(index);
        hbsNull.remove(index);
        // PKS----------------------------------------------
        rbsPK.remove(index);
        rbsPKPopups.remove(index);
        hbsPK.remove(index);
        // FKS----------------------------------------------
        tfsFK.remove(index);
        tfsFKPs.remove(index);
        tfsFKPopups.remove(index);
        btnsSelectedFK.remove(index);
        hbsFK.remove(index);
        // DEFAULTS----------------------------------------------
        cksDefault.remove(index);
        cksDefaultPopups.remove(index);
        tfsDefault.remove(index);
        tfsDefaultPopups.remove(index);
        btnsChangeDefault.remove(index);
        hbsDefault.remove(index);
        // EXTRA----------------------------------------------
        rbsExtra.remove(index);
        rbsExtraPopups.remove(index);
        hbsExtra.remove(index);
        // DISTS-----------------------------------------------
        btnsDist.remove(index);
        btnsTextArea.remove(index);

        restartIds();
        restartFirstUpdateAddButton();
        // ADDING-------------------------
        for (int a = 0; a < currentRowLength; a++) {
            int row = a + 1;
            gridPane.add(hbsN.get(a), 0, row);
            gridPane.add(hbsName.get(a), 1, row);
            gridPane.add(hbsType.get(a), 2, row);
            gridPane.add(hbsNull.get(a), 3, row);
            gridPane.add(hbsPK.get(a), 4, row);
            gridPane.add(hbsFK.get(a), 5, row);
            gridPane.add(hbsDefault.get(a), 6, row);
            gridPane.add(hbsExtra.get(a), 7, row);

            // gridPaneRight.add(btnsDist.get(a), 0, row);
            // gridPaneRight.add(btnsImageC.get(a), 1, row);
        }
    }

    // ADD WITHOUT UPDATE QUERY
    void firstBtnColumnSetVisibleAction(ActionEvent e) {
        // ADD CSS & TOOLTIP
        btnsAddColumn.get(0).setContextMenu(beforeAfterOptionMenu);
    }

    void btnsColumnSetVisibleAction(ActionEvent e) {
        setQOLVariables(e);
        // ---------------------------------------
        updateAddVisible(index);
        // ---------------------------------------
        column = tfsColumn.get(index).getText().toLowerCase().trim().replace(" ", "_");
        listColumns.add(index, column);
        // ----------------------------------------
        tfsColumnCreateControl(-1);
        tfasTypeCreateControl(-1);
        tfsTypeLengthCreateControl(-1);
        tfasFKControl(-1, true);
        tfsDefaultControl(-1);

        masterControl();
        btnSelectImageCControl();
    }

    // ADD UPDATE QUERY----------
    private void btnsAddColumnUpdateAction(ActionEvent e) {
        System.out.println(CC.CYAN + "\nADD COLUMN" + CC.RESET);
        setQOLVariables(e);

        column = tfsColumn.get(index).getText().toLowerCase().trim().replace(" ", "_");
        type = tfsType.get(index).getText()
                + (tfsTypeLength.get(index).isVisible() ? "(" + tfsTypeLength.get(index).getText() + ")" : "");
        String afterBeforeColumn;
        boolean addColumn;

        ms.setNullValue(cksNull.get(index).isSelected());

        String defaultText = tfsDefault.get(index).getText();//
        String typeChar = types.getTypeChar(tfsType.get(index).getText());
        // WILL FAIL WITH DECIMALS
        Object defaultValue = !defaultText.isEmpty() && (typeChar.equals("NUMBER") || typeChar.equals("DECIMAL"))
                ? Integer.parseInt(defaultText)
                : defaultText;
        defaultValue = cksDefault.get(index).isSelected() ? defaultValue : null;
        ms.setDefaultValue(defaultValue);

        // ADD COLUMN-------------------
        try {// ATTEMPTING TO GRAB THE PREVIOUS COLUMN
             // afterBeforeColumn = updateTable.getColumns().get(index -
             // 1).toLowerCase().trim().replace(" ", "_");
            afterBeforeColumn = currentTable.getColumns().get(index - 1).getName().toLowerCase().trim().replace(" ",
                    "_");

            ms.setAfterOrBeforeColumn(MSQLP.AFTER);
            addColumn = ms.addColumn(tableName, column, type, afterBeforeColumn);
        } catch (ArrayIndexOutOfBoundsException ex) {
            // afterBeforeColumn =
            // updateTable.getColumns().get(0).toLowerCase().trim().replace(" ", "_");
            afterBeforeColumn = currentTable.getColumns().get(0).getName().toLowerCase().trim().replace(" ", "_");
            ;

            ms.setAfterOrBeforeColumn(MSQLP.BEFORE);
            addColumn = ms.addColumn(tableName, column, type, afterBeforeColumn);
        }
        // ADD CUSTOM---------------------
        boolean addDist = true;
        // String dist = getCustomStringFromUpdateTable("dist", index, column);
        if (btnsDist.get(index).isSelected()) {
            // updateTable.getDists().set(index, true);
            currentTable.getColumns().get(index).setDist(true);
            String dist = getCustomStringFromUpdateTable("dist", index, column);
            addDist = ms.updateRow(MSQL.TABLE_CUSTOMS, "id_table", currentTable.getId(), "dist", dist);
        }
        boolean addTextArea = true;
        // String textArea = getCustomStringFromUpdateTable("textArea", index, column);
        if (btnsTextArea.get(index).isSelected()) {
            currentTable.getColumns().get(index).setTextArea(true);
            String textArea = getCustomStringFromUpdateTable("textArea", index, column);
            addTextArea = ms.updateRow(MSQL.TABLE_CUSTOMS, "id_table", currentTable.getId(), "textArea", textArea);
        }

        if (addColumn && addDist && addTextArea) {
            // ADDING VALUES TO EMPTY ROW (PREVIOUSLY ADDED)
            // updateTable.getColumns().set(index, column);
            currentTable.getColumns().get(index).setName(column);
            // updateTable.getTypes().set(index, tfsType.get(index).getText());
            currentTable.getColumns().get(index).setType(tfsType.get(index).getText());
            // updateTable.getTypesLength().set(index,
            // Integer.parseInt(tfsTypeLength.get(index).getText()));
            currentTable.getColumns().get(index).setTypeLength(Integer.parseInt(tfsTypeLength.get(index).getText()));
            // updateTable.getNulls().set(index, cksNull.get(index).isSelected());
            currentTable.getColumns().get(index).setNulll(cksNull.get(index).isSelected());
            // updateTable.getDefaults().set(index,
            // cksDefault.get(index).isSelected() ? tfsDefault.get(index).getText() : null);
            currentTable.getColumns().get(index)
                    .setDefaultt(cksDefault.get(index).isSelected() ? tfsDefault.get(index).getText() : null);

            // updateTable.getDists().set(index, btnsDist.get(index).isSelected());
            currentTable.getColumns().get(index).setDist(btnsDist.get(index).isSelected());
            // updateTable.setDistHole(dist);

            exitColumnAddState(index);

            lbStatus.setText("Added column '" + column + "' to '" + currentTable.getName() + "'", NonCSS.TEXT_FILL_OK,
                    Duration.seconds(4));
            System.out.println("\tSUCCES");
        } else {
            lbStatus.setText("Couldn't add column '" + column + "'", NonCSS.TEXT_FILL_ERROR);
            System.out.println("\tFAILED");
        }

    }

    // REMOVE & CANCEL -----------
    private void btnsCancelAddColumnUpdateAction(ActionEvent e) {
        System.out.println(CC.GREEN + "CANCEL ADD COLUMN" + CC.RESET);
        setQOLVariables(e);
        // -------------------------------------
        updateRemoveVisible(index);
        // -------------------------------------
        listColumns.remove(index);
        removeIndexUpdateTable(index);
        exitColumnAddState(index);
        // --------------------------------
        tfsColumnCreateControl(-1);
        tfasTypeCreateControl(-1);
        tfsTypeLengthCreateControl(-1);
        tfasFKControl(-1, true);
        tfsDefaultControl(-1);

        masterControl();
        btnSelectImageCControl();
    }

    void btnsRemoveUpdateAction(ActionEvent e) {
        column = tfsColumn.get(index).getText().trim();
        TrueFalseWindow w = new TrueFalseWindow("Delete Column '" + column + "'?");
        w.getBtnFalse().setOnAction(ef -> w.hide());
        w.getBtnTrue().setOnAction(et -> {
            System.out.println(CC.GREEN + "REMOVE COLUMN" + CC.RESET);
            setQOLVariables(e);

            boolean dropColumn = ms.dropColumn(tableName, column);
            if (dropColumn) {
                // if (Boolean.TRUE.equals(updateTable.getDists().get(index))) {
                // REMOVE CUSTOM----------------------------------
                // UNTESTED
                if (currentTable.getColumns().get(index).getDist()) {
                    // String dist = updateTable.getDistHole();
                    String dist = currentTable.getDist();
                    dist = dist.replace(column, "").replaceAll("(^,|,$)", "");// removing ',' at the beggining an end
                    dist = dist.isEmpty() ? "NONE" : dist;

                    boolean removeDistTest = ms.updateRow(MSQL.TABLE_CUSTOMS, "id_table", currentTable.getId(), "dist",
                            dist);
                    System.out.println("TEST removeDistTest: " + removeDistTest);
                }
                if (currentTable.getColumns().get(index).isTextArea()) {
                    String textArea = currentTable.getTextArea();
                    textArea = textArea.replace(column, "").replaceAll("(^,|,$)", "");
                    textArea = textArea.isEmpty() ? "NONE" : textArea;

                    boolean removeTextArea = ms.updateRow(MSQL.TABLE_CUSTOMS, "id_table", currentTable.getId(),
                            "textArea", textArea);
                    System.out.println("TEST removeTextArea: " + removeTextArea);
                }
                // -------------------------------------
                updateRemoveVisible(index);
                // -------------------------------------
                listColumns.remove(index);
                listImageC.remove(index);
                // -------------------------------------
                removeIndexUpdateTable(index);

                lbStatus.setText("Column '" + column.replace("_", " ") + "' has been removed", NonCSS.TEXT_FILL_OK,
                        Duration.seconds(3));
                System.out.println("\tSUCCESS");
            } else {
                lbStatus.setText("Column '" + column.replace("_", " ") + "' fail to be removed",
                        NonCSS.TEXT_FILL_ERROR);
                System.out.println("\tFAILED");
            }
            // --------------------------------------
            w.hide();
        });
        w.show();

        // --------------------------------
        tfsColumnCreateControl(-1);
        tfasTypeCreateControl(-1);
        tfsTypeLengthCreateControl(-1);
        tfasFKControl(-1, true);
        tfsDefaultControl(-1);

        masterControl();
        btnSelectImageCControl();
    }

    // EXIT & OTHERS----------
    private void exitColumnAddState(int index) {
        columnAdd = false;
        columnAddOk = false;
        typeAddOk = true;
        typeLenghtAddOk = true;
        defaultAddOk = true;

        hbTop.setDisable(false);
        // RESET NEW ROW------------------
        hbsN.get(index).setStyle(CSS.ROW);
        tfsColumn.get(index).setStyle(CSS.TEXT_FILL_HINT);
        tfsType.get(index).setStyle(CSS.TEXT_FILL_HINT);
        tfsTypeLength.get(index).setStyle(CSS.TEXT_FILL_HINT);
        cksNull.get(index).setStyle(CSS.CKS_BG_HINT);
        rbsPK.get(index).setDisable(false);
        btnsSelectedFK.get(index).setDisable(false);
        cksDefault.get(index).setStyle(CSS.CKS_BG_HINT);
        tfsDefault.get(index).setStyle(CSS.TEXT_FILL_HINT);
        rbsExtra.get(index).setDisable(false);
        hbsExtra.get(index).setStyle(CSS.ROW);

        btnsRemoveColumn.get(index).setText("X");
        btnsRemoveColumn.get(index).setStyle(CSS.ROW_REMOVE_BUTTON);

        btnsAddColumn.get(index).setText("+");
        btnsAddColumn.get(index).setStyle(CSS.ROW_ADD_BUTTON);
        btnsAddColumn.get(index).setTooltip(null);

        btnsDist.get(index).setStyle(CSS.ROW_DIST_BUTTONS);
        // ENABLED---------------------
        for (int a = 0; a < currentRowLength; a++) {
            // LEFT---------------
            hbsN.get(a).setDisable(false);
            hbsName.get(a).setDisable(false);
            hbsType.get(a).setDisable(false);
            hbsNull.get(a).setDisable(false);
            hbsPK.get(a).setDisable(false);
            hbsFK.get(a).setDisable(false);
            hbsDefault.get(a).setDisable(false);
            hbsExtra.get(a).setDisable(false);
            // RIGHT-------------
            btnsDist.get(a).setDisable(false);
        }
        btnUpdatePK.setDisable(false);
        btnUpdateFK.setDisable(false);
        btnUpdateExtra.setDisable(false);
        btnUpdateDist.setDisable(false);
    }

    private void removeIndexUpdateTable(int index) {
        currentTable.getColumns().remove(index);
        /*
         * updateTable.getColumns().remove(index); updateTable.getTypes().remove(index);
         * updateTable.getTypesLength().remove(index);
         * updateTable.getNulls().remove(index); updateTable.getPks().remove(index);
         * updateTable.getFks().remove(index); updateTable.getFkFormed().remove(index);
         * updateTable.getDefaults().remove(index);
         * 
         * updateTable.getDists().remove(index); updateTable.getImageCS().remove(index);
         */
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
                tfasTypeCreateControl(index);
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
        typeUpdateControl(index, newValue, update);
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
            int typeMaxLength = types.getTypeMaxLength(tfsType.get(index).getText());
            boolean update = false;

            matcher = patternTypeLength.matcher(text);
            if (matcher.matches()) {
                int length = Integer.parseInt(text);
                if (length <= typeMaxLength) {
                    tf.setStyle(CSS.TEXT_FILL);
                    tfsTypeLengthPopups.get(index).hide();

                    typeLengthOK = true;
                    update = true;
                    tfsTypeLengthCreateControl(index);
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
            typeLengthUpdateControl(index, text, update);
        }
        // -----------------------------------------------------
        masterControl();
    }

    private void tfasTypeCreateControl(int index) {
        typeSelectionMatch = true;
        for (int a = 0; a < currentRowLength; a++) {
            if (index != a) {
                // String text = tfasType[a].getTf().getText();
                String text = tfsType.get(a).getText();
                matcher = patternBWTC.matcher(text);
                if (matcher.matches()) {
                    if (!MList.isOnThisList(/* tfasType[a].getLv().getItems() */ tfsTypePs.get(a).getLv().getItems(),
                            text, false)) {
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

    private void tfsTypeLengthCreateControl(int index) {
        typeLengthOK = true;
        for (int a = 0; a < currentRowLength; a++) {
            if (index != a) {
                String text = tfsTypeLength.get(a).getText();
                int typeMaxLength = types.getTypeMaxLength(tfsType.get(a).getText());

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

    private void typeUpdateControl(int index, String newValue, boolean update) {
        if (updateControl) {
            boolean ok = false;
            if (update) {
                // type = updateTable.getTypes().get(index);
                type = currentTable.getColumns().get(index).getType();
                if (!newValue.equals(type)) {
                    // tfsTypePopups[index].hide();
                    tfsType.get(index).setStyle(CSS.TEXT_FILL);
                    ok = true;
                } else {
                    tfsType.get(index).setStyle(CSS.TEXT_FILL_HINT);
                    ok = false;
                    // tfsTypePopups[index].show(SAME_VALUE);
                }
            } else {
                ok = false;
            }
            if (!columnAdd) {
                btnsChangeType.get(index).setDisable(!ok);
            } else {
                typeAddOk = ok;
                addControl();
            }
        }
    }

    private void typeLengthUpdateControl(int index, String text, boolean update) {
        if (updateControl) {
            boolean ok = false;
            if (update) {
                // String typeLength =
                // Integer.toString(updateTable.getTypesLength().get(index)).toLowerCase().trim();
                String typeLength = Integer.toString(currentTable.getColumns().get(index).getTypeLength());
                if (!text.equals(typeLength)) {
                    // tfsTypeLengthPopups[index].hide();
                    tfsTypeLength.get(index).setStyle(CSS.TEXT_FILL);
                    ok = true;
                } else {
                    // tfsTypeLengthPopups[index].show(SAME_VALUE);
                    tfsTypeLength.get(index).setStyle(CSS.TEXT_FILL_HINT);
                    ok = false;
                }
            } else {
                ok = false;
            }

            if (!columnAdd) {
                btnsChangeType.get(index).setDisable(!ok);
            } else {
                typeLenghtAddOk = ok;
                addControl();
            }
        }
    }

    void btnsChangeType(ActionEvent e) {
        System.out.println(CC.CYAN + "\nChange Type" + CC.RESET);

        setQOLVariables(e);
        boolean changeType = ms.changeType(tableName, column, type);
        if (changeType) {
            System.out.println("\tSUCCESS");

            // updateTable.getTypes().set(index, tfsType.get(index).getText());
            currentTable.getColumns().get(index).setType(tfsType.get(index).getText());
            // updateTable.getTypesLength().set(index,
            // tfsTypeLength.get(index).isVisible() ?
            // Integer.parseInt(tfsTypeLength.get(index).getText()) : 0);
            currentTable.getColumns().get(index).setTypeLength(
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
        // boolean nulllO = updateTable.getNulls().get(index);
        boolean nulllO = currentTable.getColumns().get(index).getNulll();
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
        /*
         * type = updateTable.getTypes().get(index) +
         * (updateTable.getTypesLength().get(index) > 0 ? "(" +
         * updateTable.getTypesLength().get(index) + ")" : "");
         */
        type = currentTable.getColumns().get(index).getFullType();
        boolean nulll = cksNull.get(index).isSelected();
        // ----------------------------------------------
        ms.setNullValue(nulll);
        //ms.setExtraValue(index == updateTable.getExtra());
        ms.setExtraValue(index == currentTable.getExtra());
        boolean changeNull = ms.changeType(tableName, column, type);
        if (changeNull) {
            System.out.println("\tSUCCESS");
            //updateTable.getNulls().set(index, nulll);
            currentTable.getColumns().get(index).setNulll(nulll);

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

        extraPKOK = !(rbsExtra.get(index).isSelected() && !rbsPK.get(index).isSelected());
        if (extraFKOK) {
            rbsExtraPopups.get(index).hide();
        }
        // UPDATE--------------------------------
        if (updateControl) {
            for (int c = 0; c < currentRowLength; c++) {
                //boolean pkSelected = updateTable.getPks().get(c).equals("Yes");
                boolean pkSelected = currentTable.getColumns().get(c).isPk();
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
        masterControl();
    }

    void btnUpdatePK(ActionEvent e) {
        System.out.println(CC.CYAN + "Update PK" + CC.RESET);
        setQOLVariables(e);

        String[] errorMessage = { null };
        ms.setSQLException((ex, s) -> errorMessage[0] = ex.getMessage());
        boolean dropPK = true;
        if (updateTable.getPks().stream().anyMatch(p -> p.equals("Yes"))) {
            dropPK = ms.dropPrimaryKey(tableName);
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
                    addPK = ms.addPrimaryKey(tableName, cols.toArray(new String[cols.size()]));
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
        fkSelectionMatch = true;
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

            btnUpdateFK.setDisable(!(fkUpdate && btnsSelectedFK.stream().anyMatch(btn -> btn.isSelected())));

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
            if (btnsSelectedFK.get(index).isSelected()) {
                tfsFK.get(index).setVisible(true);
                extraFKOK = !rbsExtra.get(index).isSelected();
                if (!extraFKOK) {
                    btnsSelectedFKPopups.get(index).show("FK shouldn't be AUTO_INCREMENT");
                } else {
                    btnsSelectedFKPopups.get(index).hide();
                }
            } else {
                tfsFK.get(index).setVisible(false);
                extraFKOK = true;
                btnsSelectedFKPopups.get(index).hide();

            }
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
                    if (!updateControl ? true : updateTable.getFks().get(indexs[0]).equals("No") && !actionRem[0]) {
                        actionAdd[0] = true;
                        if (scount[0] == 1) {
                            btn.setText("ADD");
                        } else if (scount[0] > 1) {
                            // btn.setText("ADD (A)");
                            btnsSelectedFK.stream().filter(btnn -> btnn.isSelected())
                                    .forEach(btnn -> btnn.setText("ADD (A)"));
                        }
                    } else if (!updateControl ? false
                            : updateTable.getFks().get(indexs[0]).equals("Yes") && !actionAdd[0]) {
                        actionRem[0] = true;
                        if (!btn.getText().equals("REM (A)")) {
                            btn.setText("REM");
                        }
                    }
                } else {
                    if (!updateControl ? true : updateTable.getFks().get(indexs[0]).equals("No")) {
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
            String constraint = fks.getConstraintName(
                    Users.getInstance().getCurrenUser().getCurrentDatabase().getName(), tableName, indexs[0]++);
            dropFK = ms.dropForeignKey(tableName, constraint);
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

                System.out.println("\ttable: " + tableName);
                System.out.println("\tcolumn: ");
                cols.forEach(System.out::println);
                System.out.println("\ttableReference: " + tableReference);
                System.out.println("\tcolumnsReference: " + columnsReference);
                // CONSTRAINT NAME EX:fk_messages_users_user_id-----------------------
                StringBuilder sb = new StringBuilder();
                sb.append("fk__").append(tableName).append("__").append(tableReference).append("__");
                Arrays.asList(columnsReference).forEach(s -> sb.append(s).append("__"));
                sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
                ms.setConstraintName(sb.toString());
                // -------------------------------------------
                boolean addFK = ms.addForeignKey(tableName, cols.toArray(new String[cols.size()]), tableReference,
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
            extraDefaultOK = !rbsExtra.get(index).isSelected();
            if (!extraDefaultOK) {
                cksDefaultPopups.get(index).show("No need to have a default value if the\nis AUTO INCREMENT");
            } else {
                cksDefaultPopups.get(index).hide();
            }
        } else {
            tfsDefault.get(index).setVisible(false);
            extraDefaultOK = true;
            cksDefaultPopups.get(index).hide();
            defaultOK = true;
        }

        tfsDefaultControl(!updateControl ? -1 : index);// UNECESSARY
        // UPDATE-------------------------------------------
        tfsDefaultTypeControl(index);
        // defaultUpdate(index, update, true);
        // -------------------------------------------
        masterControl();
    }

    private void tfsDefaultTypeControl(int index) {
        TextField tf = tfsDefault.get(index);
        String text = tf.getText();
        boolean update = false;

        if (tf.isVisible()) {
            if (!tfsType.get(index).getStyle().contains(CSS.TEXT_FILL_ERROR)
                    && (!tfsTypeLength.get(index).getStyle().contains(CSS.TEXT_FILL_ERROR)
                            || !tfsTypeLength.get(index).isVisible())) {
                type = tfsType.get(index).getText().trim();
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
                    if (!updateControl) {
                        tfsDefaultControl(index);
                    }
                } else {
                    tf.setStyle(CSS.TEXT_FILL_ERROR);
                    tfsDefaultPopups.get(index).show(ILLEGAL_CHARS + "- Match (" + typeChar.toLowerCase()
                            + (tfsTypeLength.get(index).isVisible() ? ": must' be " + typeLength + " max)" : ")"));

                    defaultBW = false;
                }

            } else {
                tfsDefaultPopups.get(index).show("Select a correct Type and lenght (if needed)");
            }

        } else {
            tfsDefaultPopups.get(index).hide();
            defaultBW = true;
            tfsDefaultControl(index);
        }
        // UPDATE-------------------------------------------
        defaultUpdate(index, defaultBW, false);
    }

    private void tfsDefaultKeyReleased(KeyEvent e) {
        setQOLVariables(e);

        tfsDefaultTypeControl(index);// ++++++++++++++++
        masterControl();
    }

    private void tfsDefaultControl(int index) {
        defaultBW = true;
        for (int a = 0; a < currentRowLength; a++) {
            if (!updateControl && (a != index)) {
                if (tfsDefault.get(a).isVisible()) {
                    if (!tfsType.get(a).getStyle().contains(CSS.TEXT_FILL_ERROR)
                            && (!tfsTypeLength.get(a).getStyle().contains(CSS.TEXT_FILL_ERROR)
                                    || !tfsTypeLength.get(a).isVisible())) {
                        type = tfsType.get(a).getText().trim();
                        int typeLength = Integer.parseInt(tfsTypeLength.get(a).getText());
                        String typeChar = types.getTypeChar(type);

                        Pattern pattern = null;
                        if (tfsTypeLength.get(a).isVisible()) {
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
                        boolean match = pattern != null ? pattern.matcher(tfsDefault.get(a).getText()).matches() : true;
                        if (!match) {
                            defaultBW = false;
                            break;
                        }

                    } else {
                        defaultBW = false;
                        break;
                    }
                }
            }
        }
    }

    /*
     * private void tfsDefaultControl(int index) { defaultBW = true; for (int a = 0;
     * a < currentRowLength; a++) { if (!updateControl && (a != index)) { if
     * (tfsDefault.get(a).isVisible()) { String text =
     * tfsDefault.get(a).getText().trim(); matcher = patternBWTC.matcher(text); //
     * ADD THE REST OF DIFERENT PATTERNS if (!matcher.matches()) { defaultBW =
     * false; break; } }
     * 
     * }
     * 
     * } }
     */

    private void defaultUpdate(int index, boolean update, boolean cks) {
        if (updateControl) {
            boolean ok = false;
            if (update) {
                boolean defaultSelected = updateTable.getDefaults().get(index) != null;
                String defaultValue = updateTable.getDefaults().get(index) != null
                        ? updateTable.getDefaults().get(index).toString()
                        : "";
                if (cksDefault.get(index).isSelected() != defaultSelected
                        || (cksDefault.get(index).isSelected() && !tfsDefault.get(index).getText().equals(defaultValue))
                        || columnAdd) {
                    // cksDefaultPopups[index].hide();
                    if (cks) {
                        cksDefault.get(index).setStyle(CSS.BG_COLOR);
                    } else {
                        tfsDefault.get(index).setStyle(CSS.TEXT_FILL);
                    }
                    ok = true;
                } else {
                    // cksDefaultPopups[index].show(SAME_VALUE);
                    if (cks) {
                        cksDefault.get(index).setStyle(CSS.BG_COLOR_HINT);
                    } else {
                        tfsDefault.get(index).setStyle(CSS.TEXT_FILL_HINT);
                    }
                    ok = false;
                }
            } else {
                ok = false;
            }

            if (!columnAdd) {
                btnsChangeDefault.get(index).setDisable(!ok);
            } else {
                defaultAddOk = ok;
                addControl();
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

        boolean setDefaultValue = ms.addDefaultValue(tableName, column, defaultValue);
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
    void rbsExtraAction(ActionEvent e) {
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
            if (btnsSelectedFK.get(index).isSelected()
                    && Arrays.asList(pksReferences).stream().anyMatch(s -> tfsFK.get(index).getText().equals(s))) {
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
        boolean updateExtra = ms.changeType(tableName, column, type);
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

    private void btnCreateTableAction(ActionEvent e) {
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
        String dist = getCustomStringBeforeUpdate(btnsDist);
        String textArea = getCustomStringBeforeUpdate(btnsTextArea);
        // ADDING VALUES -------------------------------------------------
        for (int a = 0; a < currentRowLength; a++) {
            // LEFT-------------------
            columnsNames[a] = tfsColumn.get(a).getText().toLowerCase().trim().replace(" ", "_");
            typesNames[a] = tfsType.get(a).getText();

            if (tfsTypeLength.get(a).isVisible() && types.getTypeLength(typesNames[a]) > 0) {
                // typesNames[a] += tfsTypeLength[a].getText();
                typesLengths[a] = Integer.parseInt(tfsTypeLength.get(a).getText());
            }
            nulls[a] = cksNull.get(a).isSelected();

            if (rbsPK.get(a).isSelected()) {
                cpks.add(columnsNames[a]);
            }
            final int aa = a;
            if (btnsSelectedFK.get(index).isSelected()
                    && Arrays.asList(pksReferences).stream().anyMatch(s -> s.equals(tfsFK.get(aa).getText()))) {
                String fkText = tfsFK.get(a).getText();
                String[] split = fkText.split("\\.");

                // column - database - table
                cfks.add(new TString(columnsNames[a], split[0],
                        split[1].replaceAll("[\\(||\\)]", "").replace(" ", "__").replace(",", "_")));
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
        MSQLCreate msc = new MSQLCreate(new RootConfigConnection());
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
        StringBuilder message = new StringBuilder();
        boolean fullSuccess = true;
        if (createTable) {
            Object[] valuesTableNames = new Object[] { null, tableName, dist, "NONE", "NONE" };

            boolean insertTableNames = ms.insert(MSQL.TABLE_NAMES, valuesTableNames);
            if (insertTableNames) {
                message.append("Table '" + tableName.replace("_", " ") + "' has been created!");
                System.out.println("\tSUCCESS");
            } else {
                // DELETE CREATED TABLE
                fullSuccess = false;
                message.append("FATAL: (Table has been create but not inserted on " + MSQL.TABLE_NAMES);
                System.out.println("\tFATAL");
            }
            // IMAGECS-----------------------
            if (vicc != null && !btnSelectImageC.isDisable()) {
                int newTableId = (int) ms.selectValues(MSQL.TABLE_NAMES, "id", "name", tableName)[0];
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
        lbStatus.setText(message.toString(), fullSuccess ? Color.GREEN : Color.YELLOW);
    }

    private void btnHelpAction(ActionEvent e) {
        Bounds sb = btnCreateHelp.localToScreen(btnCreateHelp.getBoundsInLocal());
        if (!updateControl) {
            createHelpPopup.show(btnCreateHelp, sb.getMinX(), sb.getMinY());
        } else if (columnAdd) {
            updateAddColumnHelpPopup.show(btnCreateHelp, sb.getMinX(), sb.getMinY());
        }
    }

    // RIGHT-----------------------------------------
    // CUSTOM=================================================
    private void btnsDistAction(ActionEvent e) {
        // MAY HAVE TO ADD DIST BUTTONS TO AN OBSERVABLE LIST (ADD OR REMOVE COLUMN)
        setQOLVariables(e);
        // int errorCount = 0;
        if (btnsDist.get(index).isSelected()) {
            if (!rbsExtra.get(index).isSelected()) {
                lbhDist.setTextFill(NonCSS.TEXT_FILL);
                btnsDistPopups.get(index).hide();

                distExtraOK = true;
                btnsDistControl(index);
            } else {
                lbhDist.setTextFill(NonCSS.TEXT_FILL_ERROR);
                btnsDistPopups.get(index).show("Unnecesary selection when this column is already AUTO_INCREMENT");

                distExtraOK = false;
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

    private void btnsTextAreaAction(ActionEvent e) {
        // CONTROL ONLY TYPE TEXT-BASE
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
                    boolean dist = false;
                    if (updateTable.getDists().get(a) != null) {
                        dist = updateTable.getDists().get(a);
                    }
                    if (btnsDist.get(a).isSelected() != dist || (columnAdd)) {
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

        // String dist = Custom.getOldDist(currentRowLength, btnsDist.toArray(new
        // ToggleButton[btnsDist.size()]));
        String dist = getCustomStringBeforeUpdate(btnsDist);

        boolean updateDist = ms.updateRow(MSQL.TABLE_NAMES, "Name", tableName.replace("_", " "), "Dist1", dist);
        if (updateDist) {
            int[] indexs = { 0 };
            btnsDist.forEach(btn -> {
                if (currentRowLength > indexs[0]) {
                    updateTable.getDists().set(indexs[0], btnsDist.get(indexs[0]).isSelected());
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
    /*
     * private void btnsImageCAction(ActionEvent e) { setQOLVariables(e); if
     * (btnsImageC.get(index).isSelected()) { Boolean[] bools = new
     * Boolean[currentRowLength]; Arrays.fill(bools, false);
     * listImageC.setAll(bools);
     * 
     * listImageC.set(index, true); } else { listImageC.set(index, false); }
     * 
     * // UPDATE--------------------------------------- imageCUpdate(); }
     */

    private void listImageCChange(Change<? extends Boolean> c) {
        while (c.next()) {
            if (c.wasAdded() || c.wasRemoved() || c.wasUpdated() || c.wasReplaced()) {
                if (listImageC.stream().allMatch(bool -> !bool)) {
                    // tfImageCPath.setDisable(true);
                    btnSelectImageC.setDisable(true);

                    imageCPathOk = true;
                } else {
                    // tfImageCPath.setDisable(false);
                    btnSelectImageC.setDisable(false);

                    // boolean exist = new File(tfImageCPath.getText()).exists();
                    // imageCPathOk = exist;

                }
            }
        }

        masterControl();
    }

    private void btnSelectImageCAction(ActionEvent e) {
        VImageC vImageC = VImageC.getInstance(this, !updateControl);
        vicc = vImageC.getVicc();
    }

    /*
     * private void tfImageCPathTextProperty(ObservableValue<? extends String>
     * observable, String oldValue, String newValue) { Path path =
     * Paths.get(tfImageCPath.getText()); if (!tfImageCPath.isDisable()) { if
     * (Files.exists(path)) { tfImageCPath.setStyle(CSS.TEXT_FILL);
     * tfImageCPathPopup.hide();
     * 
     * imageCPathOk = true; } else { tfImageCPath.setStyle(CSS.TEXT_FILL_ERROR);
     * tfImageCPathPopup.show("This path doesn't exist");
     * 
     * imageCPathOk = false; } } else { tfImageCPath.setStyle(CSS.TEXT_FILL);
     * tfImageCPathPopup.hide();
     * 
     * 
     * imageCPathOk = true; }
     * 
     * imageCUpdate(); masterControl(); }
     */
    /*
     * private void imageCUpdate() { if (updateControl) { if (currentRowLength <=
     * updateTable.getRowLength()) { if (imageCPathOk) {
     * 
     * /* String imageCO = Custom.getOldImageC(currentRowLength,
     * updateTable.getImageCHole().toArray(new
     * String[updateTable.getImageCHole().size()]));
     * 
     * String imageC = Custom.getOldImageC(currentRowLength, btnsImageC.toArray(new
     * ToggleButton[btnsImageC.size()]));
     * 
     * String imageCO = updateTable.getImageCHole(); String imageC =
     * getImageCBeforeUpdate();
     * 
     * String imageCPathO = updateTable.getImageCPathHole(); String imageCPath =
     * !tfImageCPath.isDisable() ? tfImageCPath.getText() : "NONE";
     * 
     * boolean pathExist = !tfImageCPath.isDisable() ? new File(imageCPath).exists()
     * : true; boolean imageCDifferent = !imageCO.equals(imageC); boolean
     * imageCPathDiferent = !imageCPathO.equals(imageCPath);
     * 
     * boolean disable = pathExist && (imageCDifferent || imageCPathDiferent);
     * btnUpdateImageC.setDisable(!disable);
     * 
     * } else { btnUpdateImageC.setDisable(true); } } else {
     * System.out.println("ADD OR REMOVE COLUMN STATE"); // ADD OR REMOVE COLUMN
     * STATE } } }
     */

    void btnUpdateImageC(ActionEvent e) {
        System.out.println(CC.CYAN + "Update ImageC" + CC.RESET);
        setQOLVariables(e);
        // ms.updateRow(, valuesWhere, newValues);
        String imageCO = updateTable.getImageCHole();
        String imageC = null; // !!!!!!!!

        String imageCPathO = updateTable.getImageCPathHole();
        // String imageCPath = !tfImageCPath.isDisable() ? tfImageCPath.getText() :
        // "NONE";

        int allOk = 0;
        String message = null;
        // IMAGEC UPDATE-------------------------------------
        if (!imageCO.equals(imageC)) {
            boolean updateImageC = ms.updateRow(MSQL.TABLE_NAMES, "Name", tableName.replace("_", " "), "ImageC",
                    imageC);
            if (updateImageC) {
                if (!imageC.equals("NONE")) {
                    int id = Character.getNumericValue(imageC.charAt(1)) - 1;
                    for (int a = 0; a < currentRowLength; a++) {
                        if (a == id) {

                            updateTable.getImageCS().set(id, true);
                        } else {
                            updateTable.getImageCS().set(a, false);
                        }
                    }
                    // updateTable.setImageCHole(getImageCBeforeUpdate());
                } else {
                    for (int a = 0; a < currentRowLength; a++) {
                        updateTable.getImageCS().set(a, false);
                    }
                    updateTable.setImageCHole("NONE");
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
        /*
         * if (!imageCPathO.equals(imageCPath)) { boolean updateImageCPath =
         * ms.updateRow(MSQL.TABLE_NAMES, "Name", tableName.replace("_", " "),
         * "ImageC_Path", imageCPath.replace("\\", "\\\\")); if (updateImageCPath) {
         * updateTable.setImageCPathHole(imageCPath); if (imageCPath.equals("NONE")) {
         * tfImageCPath.setText(""); } // LBSTATUS message = message != null ? message +
         * " & ImageCPath updated" : "ImageCPath updated"; allOk++; } else { message =
         * message != null ? message + " & ImageCPath failed" : "ImageCPath failed"; } }
         * else { allOk++; }
         */
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
            // btnUpdateImageC.setDisable(true);
        }
    }

    // INIT ---------------------------------------------
    /**
     * List all PK for each row of FKS (to reference them)
     */
    private void fkReferencesInit() {
        List<String> list = new ArrayList<>();
        List<PK> pksList = pks.getPksList();

        for (int a = 0; a < pksList.size(); a++) {
            String databaseName = pksList.get(a).getDatabase();
            tableName = pksList.get(a).getTable();
            List<IntString> columnsNames = pksList.get(a).getColumns();
            // String column = row[a].getColumnName();
            StringBuilder sb = new StringBuilder(databaseName).append(".").append(tableName).append(" (");
            columnsNames.forEach(is -> sb.append(is.string).append(","));

            sb.deleteCharAt(sb.length() - 1);// TEST
            sb.append(")");
            list.add(sb.toString());
        }

        pksReferences = list.toArray(new String[list.size()]);
        tfsFKPs.forEach(e -> {
            e.addAllItems(pksReferences);
            // e.getLv().getItems().addAll(pksReferences);
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

    private void restartNodes(int index) {
        initNodes(index);
        restartIds();
        restartFirstUpdateAddButton();

        if (index != -1) {// ADD COULUMN STATE
            btnsAddColumn.get(index).setText("A");
            btnsAddColumn.get(index).setStyle(CSS.NEW_ROW_ADD_BUTTON);
            btnsAddColumn.get(index).setOnAction(btnsAddColumnUpdateActionListener);
            btnsAddColumn.get(index).setDisable(true);

            btnsRemoveColumn.get(index).setText("C");
            btnsRemoveColumn.get(index).setStyle(CSS.NEW_ROW_REMOVE_BUTTON);
            btnsRemoveColumn.get(index).setOnAction(btnsCancelAddColumnUpdateActionListener);
            btnsRenameColumn.get(index).setDisable(true);
            // DISABLE NODES------------------------
            rbsPK.get(index).setDisable(true);
            btnsSelectedFK.get(index).setDisable(true);
            rbsExtra.get(index).setDisable(true);

            hbTop.setDisable(true);
            btnUpdatePK.setDisable(true);
            btnUpdateFK.setDisable(true);
            btnUpdateExtra.setDisable(true);
            btnUpdateDist.setDisable(true);

            cksNull.get(index).setStyle(CSS.CKS_BG);
            cksDefault.get(index).setStyle(CSS.CKS_BG);
            // LISTENERS--------------------------------
            tfsColumn.get(index).setOnKeyReleased(tfsColumnsKeyReleasedListener);
            listColumns.removeListener(listColumnsChangeListener);
            listColumns.addListener(listColumnsChangeListener);

            int[] indexs = { 0 };
            btnsRemoveColumn.forEach(e -> {
                if (indexs[0]++ != index) {
                    e.setOnAction(btnsRemoveUpdateActionListener);
                }
            });
            indexs[0] = 0;
            btnsAddColumn.forEach(e -> {
                if (indexs[0]++ != index) {
                    e.setOnAction(btnsColumnSetVisibleActionListener);
                }
            });

            tfsType.get(index).textProperty().removeListener(tfasTypeTextPropertyListener);
            tfsType.get(index).textProperty().addListener(tfasTypeTextPropertyListener);
            tfsTypeLength.get(index).textProperty().removeListener(tfsTypeLengthTextPropertyListener);
            tfsTypeLength.get(index).textProperty().addListener(tfsTypeLengthTextPropertyListener);

            rbsPK.get(index).setOnAction(cksPKActionListener);
            tfsFK.get(index).textProperty().removeListener(tfasFKTextPropertyListener);
            tfsFK.get(index).textProperty().addListener(tfasFKTextPropertyListener);

            cksDefault.get(index).setOnAction(cksDefaultActionListener);
            tfsDefault.get(index).setOnKeyReleased(tfsDefaultKeyReleasedListener);
            rbsExtra.get(index).removeEventHandler(ActionEvent.ACTION, rbsExtraActionListener);
            rbsExtra.get(index).addEventHandler(ActionEvent.ACTION, rbsExtraActionListener);
            // CUSTOM-----------------------
            btnsDist.get(index).setStyle(CSS.NEW_ROW_DIST_BUTTONS);
            btnsDist.get(index).setOnAction(btnsDistActionListener);

            btnsTextArea.get(index).setStyle(CSS.NEW_ROW_DIST_BUTTONS);
            btnsTextArea.get(index).setOnAction(btnsTextAreaActionListener);
            // btnsImageC.get(index).addEventHandler(ActionEvent.ACTION,
            // this::btnsImageCAction);
            // NEW EMPTY ROW INDEX----------------------------------------
            addIndex = index;
            columnAdd = true;

            updateTable.getColumns().add(index, null);
            updateTable.getTypes().add(index, null);
            updateTable.getTypesLength().add(index, -1);
            updateTable.getNulls().add(index, null);
            updateTable.getPks().add(index, null);
            updateTable.getFks().add(index, null);
            // updateTable.getFksConstraint().add(index, null);
            updateTable.getFkFormed().add(index, null);
            updateTable.getDefaults().add(index, null);

            updateTable.getDists().add(index, null);
            updateTable.getImageCS().add(index, null);
        }
    }

    private void restartIds() {
        int forSize = !updateControl ? MSQL.MAX_COLUMNS : currentRowLength;
        for (int a = 0; a < forSize; a++) {
            // LEFT---------------------------------
            lbsN.get(a).setText("Column " + (a + 1));

            tfsColumn.get(a).setId(Integer.toString(a));
            btnsRemoveColumn.get(a).setId(Integer.toString(a));
            btnsAddColumn.get(a).setId(Integer.toString(a));
            btnsRenameColumn.get(a).setId(Integer.toString(a));

            tfsType.get(a).setId("TF-TYPE-" + a);
            tfsTypeLength.get(a).setId(Integer.toString(a));
            btnsChangeType.get(a).setId(Integer.toString(a));

            cksNull.get(a).setId(Integer.toString(a));
            btnsChangeNull.get(a).setId(Integer.toString(a));

            rbsPK.get(a).setId(Integer.toString(a));
            tfsFK.get(a).setId(Integer.toString(a));
            btnsSelectedFK.get(a).setId(Integer.toString(a));

            cksDefault.get(a).setId(Integer.toString(a));
            tfsDefault.get(a).setId(Integer.toString(a));
            btnsChangeDefault.get(a).setId(Integer.toString(a));

            rbsExtra.get(a).setId(Integer.toString(a));
            // CUSTOM----------------------------
            btnsDist.get(a).setText(Integer.toString(a + 1));
            btnsDist.get(a).setId(Integer.toString(a));

            btnsTextArea.get(a).setText(Integer.toString(a + 1));
            btnsTextArea.get(a).setId(Integer.toString(a));
        }
    }

    private void restartFirstUpdateAddButton() {
        btnsAddColumn.forEach(btn -> {
            btn.setTooltip(null);
            btn.setContextMenu(null);
        });
        btnsAddColumn.get(0).setTooltip(beforeAfterOptionTooltip);
        btnsAddColumn.get(0).setContextMenu(beforeAfterOptionMenu);
    }

    private void initNodesInit(int a) {
        // COLUMN NUMBER------------------------------
        lbsN.add(a, new Label("Column " + (a + 1)));
        hbsN.add(a, new HBox(lbsN.get(a)));
        // COLUMN NAMES---------------------------------------
        tfsColumn.add(a, new TextField());
        tfsColumnPopups.add(a, new PopupMessage(tfsColumn.get(a)));

        btnsRemoveColumn.add(a, new Button("X"));
        btnsAddColumn.add(a, new Button("+"));
        btnsRenameColumn.add(a, new Button("C"));

        hbsName.add(a,
                new HBox(tfsColumn.get(a), btnsRemoveColumn.get(a), btnsAddColumn.get(a), btnsRenameColumn.get(a)));
        hbsName.get(a).setPadding(new Insets(0, 2, 0, 2));
        // TYPES----------------------------------------------
        // tfasType.get(a) = new TextFieldAutoC(a, types.getTypeNames());
        tfsType.add(a, new TextField());
        tfsTypePs.add(a, new PopupAutoC(tfsType.get(a), types.getTypeNames()));
        tfsTypePopups.add(a, new PopupMessage(tfsType.get(a)));

        tfsTypeLength.add(a, new TextField());
        tfsTypeLengthPopups.add(a, new PopupMessage(tfsTypeLength.get(a)));

        btnsChangeType.add(a, new Button("C"));

        hbsType.add(a, new HBox(tfsType.get(a), tfsTypeLength.get(a), btnsChangeType.get(a)));
        hbsType.get(a).setPadding(new Insets(0, 2, 0, 2));
        // NULLS----------------------------------------------
        cksNull.add(a, new CheckBox());
        btnsChangeNull.add(a, new Button("C"));
        hbsNull.add(a, new HBox(cksNull.get(a), btnsChangeNull.get(a)));
        hbsNull.get(a).setPadding(new Insets(0, 2, 0, 2));
        // PKS----------------------------------------------
        rbsPK.add(a, new RadioButton());
        rbsPKPopups.add(a, new PopupMessage(rbsPK.get(a)));
        // btnsChangePK.get(a) = new Button("C");
        hbsPK.add(a, new HBox(rbsPK.get(a)));
        // FKS----------------------------------------------
        tfsFK.add(a, new TextField());
        tfsFKPs.add(a, new PopupAutoC(tfsFK.get(a)));
        tfsFKPopups.add(a, new PopupMessage(tfsFK.get(a)));

        btnsSelectedFK.add(a, new ToggleButton("ADD"));
        btnsSelectedFKPopups.add(a, new PopupMessage(btnsSelectedFK.get(a)));

        hbsFK.add(a, new HBox(tfsFK.get(a), btnsSelectedFK.get(a)));
        // DEFAULTS----------------------------------------------
        cksDefault.add(a, new CheckBox());
        cksDefaultPopups.add(a, new PopupMessage(cksDefault.get(a)));

        tfsDefault.add(a, new TextField());
        tfsDefaultPopups.add(a, new PopupMessage(tfsDefault.get(a)));

        btnsChangeDefault.add(a, new Button("C"));
        hbsDefault.add(a, new HBox(cksDefault.get(a), tfsDefault.get(a), btnsChangeDefault.get(a)));
        hbsDefault.get(a).setPadding(new Insets(0, 2, 0, 2));
        // EXTRA----------------------------------------------
        rbsExtra.add(a, new RadioButton());
        rbsExtraPopups.add(a, new PopupMessage(rbsExtra.get(a)));
        hbsExtra.add(a, new HBox(rbsExtra.get(a)/* , btnsChangeExtra.get(a) */));
        // DIST----------------------
        btnsDist.add(a, new ToggleButton("" + (a + 1)));
        btnsDistPopups.add(a, new PopupMessage(btnsDist.get(a)));
        // TEXT AREA--------------------------------------
        btnsTextArea.add(a, new ToggleButton("" + (a + 1)));
        btnsTextAreaPopups.add(new PopupMessage(btnsTextArea.get(a)));
    }

    private void initNodes(int index) {
        // ADDING NEW INDEXS OR
        // ALL---------------------------------------------------------
        int forSize = MSQL.MAX_COLUMNS;
        if (updateControl) {
            forSize = index == -1 ? currentTable.getColumns().size() : currentRowLength;
        }
        int forSize2 = forSize;
        if (index >= 0) {
            forSize = index + 1;
        }

        int indexu = index == -1 ? 0 : index;
        for (int a = indexu; a < forSize; a++) {
            initNodesInit(a);
            // PROMPT TEXT ----------------------------------------------
            tfsColumn.get(a).setPromptText("Column name required");
            tfsDefault.get(a).setPromptText("Value Required");
            tfsFK.get(a).setPromptText("NO FOREING KEY");
            // STYLE----------------------------------------------
            tfsType.get(a).setStyle(null);
            tfsType.get(a).setStyle(CSS.TFS_DIST_LOOK);
            btnsDist.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);
            btnsTextArea.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);

            hbsN.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsName.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsType.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);
            tfsTypeLength.get(a).setStyle(CSS.TEXT_FILL);
            hbsNull.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsPK.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsFK.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsDefault.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsExtra.get(a).setStyle(CSS.BORDER_GRID_BOTTOM);
            // TYPE DEFAULT SELECTION----------------------------
            tfsTypePs.get(a).getLv().getSelectionModel().select(presetTypeSelected.get(a).getTypeName());
            tfsTypeLength.get(a).setText(Integer.toString(presetTypeSelected.get(a).getTypeLength()));
            // WIDTH & HEIGHT -----------------------------------
            tfsColumn.get(a).setPrefWidth(-1);
            btnsRemoveColumn.get(a).setMinWidth(40);
            btnsRemoveColumn.get(a).setMaxWidth(40);
            btnsAddColumn.get(a).setMinWidth(40);
            btnsAddColumn.get(a).setMaxWidth(40);
            tfsType.get(a).setPrefWidth(140);
            tfsTypeLength.get(a).setMinWidth(40);
            tfsTypeLength.get(a).setMaxWidth(40);
            hbsFK.get(a).setPrefWidth(-1);
            tfsFK.get(a).setPrefWidth(-1);

            btnsDist.get(a).setMinWidth(40);
            btnsDist.get(a).setMaxWidth(40);
            btnsTextArea.get(a).setMinWidth(40);
            btnsTextArea.get(a).setMaxWidth(40);
            // VISIBLE PROPERTY ------------------------------------
            btnsRenameColumn.get(a).managedProperty().bind(btnsRenameColumn.get(a).visibleProperty());
            tfsTypeLength.get(a).managedProperty().bind(tfsTypeLength.get(a).visibleProperty());
            btnsChangeType.get(a).managedProperty().bind(btnsChangeType.get(a).visibleProperty());
            btnsChangeNull.get(a).managedProperty().bind(btnsChangeNull.get(a).visibleProperty());
            // btnsChangePK.get(a).managedProperty().bind(btnsChangePK.get(a).visibleProperty());
            tfsFK.get(a).managedProperty().bind(tfsFK.get(a).visibleProperty());
            btnsSelectedFK.get(a).managedProperty().bind(btnsSelectedFK.get(a).visibleProperty());
            tfsDefault.get(a).managedProperty().bind(tfsDefault.get(a).visibleProperty());
            btnsChangeDefault.get(a).managedProperty().bind(btnsChangeDefault.get(a).visibleProperty());

            btnsDist.get(a).managedProperty().bind(btnsDist.get(a).visibleProperty());
            btnsTextArea.get(a).managedProperty().bind(btnsTextArea.get(a).visibleProperty());
            // VISIBILITY----------------------------------
            tfsFK.get(a).setVisible(false);
            tfsDefault.get(a).setVisible(false);

            btnsRenameColumn.get(a).setDisable(true);
            btnsChangeType.get(a).setDisable(true);
            btnsChangeNull.get(a).setDisable(true);
            btnsChangeDefault.get(a).setDisable(true);

            if (!updateControl) {
                btnsRenameColumn.get(a).setVisible(false);
                btnsChangeType.get(a).setVisible(false);
                btnsChangeNull.get(a).setVisible(false);
                btnsChangeDefault.get(a).setVisible(false);
            }
            // MARGIN----------------------------------------------
            GridPane.setMargin(hbsN.get(a), INSETS);
            GridPane.setMargin(hbsName.get(a), INSETS);
            GridPane.setMargin(hbsType.get(a), INSETS);
            GridPane.setMargin(hbsNull.get(a), INSETS);
            GridPane.setMargin(hbsPK.get(a), INSETS);
            GridPane.setMargin(hbsFK.get(a), INSETS);
            GridPane.setMargin(hbsDefault.get(a), INSETS);
            GridPane.setMargin(hbsExtra.get(a), INSETS);

            GridPane.setMargin(btnsDist.get(a), INSETS);
            GridPane.setMargin(btnsTextArea.get(a), INSETS);
        }

        new ToggleGroupD<>(rbsExtra.toArray(new RadioButton[rbsExtra.size()]));
        if (index >= 0 && updateControl) {
            hbsN.get(index).setStyle(CSS.NEW_ROW);
            hbsExtra.get(index).setStyle(CSS.NEW_ROW);
            // DISABLE PREVIOUS ROW-----------------------------------
            for (int a = 0; a < forSize2; a++) {
                if (a != index) {
                    hbsN.get(a).setDisable(true);
                    hbsName.get(a).setDisable(true);
                    hbsType.get(a).setDisable(true);
                    hbsNull.get(a).setDisable(true);
                    hbsPK.get(a).setDisable(true);
                    hbsFK.get(a).setDisable(true);
                    hbsDefault.get(a).setDisable(true);
                    hbsExtra.get(a).setDisable(true);

                    btnsDist.get(a).setDisable(true);
                    btnsTextArea.get(a).setDisable(true);
                }
            }
        }

    }

    private void initListenerCreateUpdate() {
        tfsColumn.forEach(tf -> tf.setOnKeyReleased(this::tfsColumnsKeyReleased));
        tfsType.forEach(tf -> tf.textProperty().addListener(this::tfasTypeTextProperty));
        tfsTypeLength.forEach(tf -> tf.textProperty().addListener(this::tfsTypeLengthTextProperty));

        rbsPK.forEach(rb -> rb.setOnAction(this::cksPKAction));

        btnsSelectedFK.forEach(btn -> btn.setOnAction(this::btnsSelectedFK));
        tfsFK.forEach(tf -> tf.textProperty().addListener(this::tfasFKTextProperty));

        cksDefault.forEach(cks -> cks.setOnAction(this::cksDefaultAction));
        tfsDefault.forEach(tf -> tf.setOnKeyReleased(this::tfsDefaultKeyReleased));

        rbsExtra.forEach(rb -> rb.addEventHandler(ActionEvent.ACTION, this::rbsExtraAction));
        // CUSTOM----------------------
        btnsDist.forEach(btn -> btn.setOnAction(this::btnsDistAction));
        btnsTextArea.forEach(btn -> btn.setOnAction(this::btnsTextAreaAction));

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

    void createHelpPopupReset() {
        createHelpMap.put("Table Name", tableOK);
        createHelpMap.put("Columns Names", columnSNOK && columnBWOK);
        createHelpMap.put("Types", typeSelectionMatch && typeLengthOK);
        createHelpMap.put("Foreign Keys", fkSelectionMatch);
        createHelpMap.put("Default Values", defaultBW && defaultOK);
        createHelpMap.put("Extra Value", extraPKOK && extraFKOK && extraDefaultOK);
        createHelpMap.put("Dist", distExtraOK);
        createHelpMap.put("ImageC Path", imageCPathOk);
    }

    void createAddColumnHelpPopupReset() {
        updateAddColumnHelpMap.put("Columns", columnAddOk);
        updateAddColumnHelpMap.put("Type", typeAddOk);
        updateAddColumnHelpMap.put("Default", defaultAddOk);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // NODES------------------------
        presetSomeInit();
        restartNodes(-1);

        tfTable.setPromptText("Table name required");
        btnRenameTable.managedProperty().bind(btnRenameTable.visibleProperty());
        btnRenameTable.setDisable(true);

        scGridPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        lbUpdate.setDisable(true);

        lbStatus.setStyle(CSS.LB_STATUS);
        lbStatus.getBtnCloseStatus().setStyle(CSS.LB_STATUS_BUTTON);
        HBox.setHgrow(lbStatus, Priority.ALWAYS);
        hbStatus.getChildren().add(0, lbStatus);
        btnCreateUpdate.managedProperty().bind(btnCreateUpdate.visibleProperty());
        // LISTENERS-------------------------------------------
        initListenerCreateUpdate();
        listColumns.addListener(this::listColumnsChange);
        // FIRST BTN ADD------------------------
        beforeAfterOptionMenu.addAction(0, e -> updateAddVisible(-1));
        beforeAfterOptionMenu.addAction(1, e -> updateAddVisible(0));
        beforeAfterOptionTooltip.setShowDelay(Duration.millis(100));
        // TOP-----------------------------------------------
        tfTable.setOnKeyReleased(this::tfTableKeyReleased);
        // --------------------------------------------------
        fkReferencesInit();
        // BOTTOM --------------------------------------------
        btnCancel.setOnAction(this::btnCancelAction);
        btnCreateUpdate.setOnAction(this::btnCreateTableAction);
        btnCreateHelp.setOnAction(this::btnHelpAction);

        btnSelectImageC.setOnAction(this::btnSelectImageCAction);

    }

    // -------------------------------------------------------------
    public GridPane getGridPane() {
        return gridPane;
    }

    public void setGridPaneLeft(GridPane gridPane) {
        this.gridPane = gridPane;
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
        return tfsType;
    }

    public void setTfasType(List<TextField> tfasType) {
        this.tfsType = tfasType;
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

    public List<HBox> getHbsN() {
        return hbsN;
    }

    public void setHbsN(List<HBox> hbsN) {
        this.hbsN = hbsN;
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

    public PopupMenu getBeforeAfterOptionMenu() {
        return beforeAfterOptionMenu;
    }

    public Tooltip getBeforeAfterOptionTooltip() {
        return beforeAfterOptionTooltip;
    }

    public BorderPane getBpMain() {
        return bpMain;
    }

    public List<ToggleButton> getBtnsTextArea() {
        return btnsTextArea;
    }

    public void setBtnsTextArea(List<ToggleButton> btnsTextArea) {
        this.btnsTextArea = btnsTextArea;
    }

    public void setBpMain(BorderPane bpMain) {
        this.bpMain = bpMain;
    }

}
