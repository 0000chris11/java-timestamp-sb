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
    private Button btnCreateHelp;

    @FXML
    private HBox hbStatus;
    private LabelStatus lbStatus = new LabelStatus();
    // CENTER NODES LISTS
    // =================================================================
    private PopupMessageControl2 popupMessageControl;

    private Message tablePopupControl;
    private Message messageSameColumns;
    // COLUMN ----------------------------------------------
    private final List<HBox> hbsN = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<Label> lbsN = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<HBox> hbsName = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<TextField> tfsColumn = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Button> btnsRemoveColumn = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Button> btnsAddColumn = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final PopupMenu beforeAfterOptionMenu = new PopupMenu("Before", "After");
    private final Tooltip beforeAfterOptionTooltip = new Tooltip("Right click to add...");
    private final List<Button> btnsRenameColumn = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<Message> columnsPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // TYPE ------------------------------------------------
    private final List<HBox> hbsType = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<TextField> tfsType = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<PopupAutoC> tfsTypePAutoC = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<TextField> tfsTypeLength = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Button> btnsChangeType = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<Message> typePopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // NULL ------------------------------------------------
    private final List<HBox> hbsNull = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<CheckBox> cksNull = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Button> btnsChangeNull = new ArrayList<>(MSQL.MAX_COLUMNS);
    // PK ---------------------------------------------------
    private final List<HBox> hbsPK = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<RadioButton> rbsPK = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<Message> pkPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // FK --------------------------------------------------
    private String[] pksReferences;
    private Message fkPopupsControl;
    // DEFAULT ---------------------------------------------
    private final List<HBox> hbsDefault = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<CheckBox> cksDefault = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<TextField> tfsDefault = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Button> btnsChangeDefault = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<Message> defaultPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // EXTRA -----------------------------------------------
    private final List<HBox> hbsExtra = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<RadioButton> rbsExtra = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<Message> extraPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // DIST ------------------------------------------------
    private final List<ToggleButton> btnsDist = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Message> distPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // TEXTAREA --------------------------------------------
    private final List<ToggleButton> btnsTextArea = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Message> textAreaPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // BOTTOM ----------------------------------------------
    private final ObservableMap<String, Boolean> createHelpMap = FXCollections.observableHashMap();
    private final PopupKV createHelpPopup = new PopupKV(createHelpMap);

    private final ObservableMap<String, Boolean> updateAddColumnHelpMap = FXCollections.observableHashMap();
    private final PopupKV updateAddColumnHelpPopup = new PopupKV(updateAddColumnHelpMap);
    // ---------------------------------------------
    private VFController vfc;

    private VFKController vfkc;
    private VImageCController vicc;

    private MSQLP ms;

    private Database currentDatabse = Users.getInstance().getCurrenUser().getCurrentDatabase();
    private Table currentTable = currentDatabse.getCurrentTable();
    private SQLTypes types = SQLTypes.getInstance();

    private Pattern patternBWTC = Pattern.compile("[A-Za-z](\\w| )*");
    private Pattern patternTypeLength = Pattern.compile("\\d{1,5}");
    private Matcher matcher;
    // LISTENERS =========================================================
    // COLUMNS ---------------------------------------
    private final ChangeListener<? super String> columnsTextPropertyListener = (obs, oldValue,
            newValue) -> columnsTextProperty(obs);
    private final ListChangeListener<? super String> listColumnsChangeListener = this::listColumnsChange;
    // COLUMNS UPDATE ----------------------------------
    private final EventHandler<ActionEvent> addColumnUpdateVisibleActionListener = this::addColumnUpdateVisibleAction;
    private final EventHandler<ActionEvent> addColumnUpdateActionListener = this::addColumnUpdateAction;

    private final EventHandler<ActionEvent> removeColumnUpdateActionListener = this::dropColumnAction;
    private final EventHandler<ActionEvent> cancelAddColumnUpdateActionListener = this::addRowCancelAction;

    // TYPE -------------------------------------------
    private final ChangeListener<? super String> tfsTypeTextPropertyListener = this::typeTextProperty;
    private final ChangeListener<? super String> tfsTypeLengthTextPropertyListener = this::typeLengthTextProperty;
    // PK ----------------------------------------------
    private final EventHandler<ActionEvent> pksActionListener = this::pksAction;
    // DEFAULT -----------------------------------------
    private final EventHandler<ActionEvent> defaultsActionListener = this::defaultsAction;
    private final ChangeListener<? super String> defaultsTextPropertyListener = (obs, oldValue,
            newValue) -> defaultsTextProperty(obs);
    // EXTRA --------------------------------------------
    private final EventHandler<ActionEvent> extrasActionListener = this::extrasAction;
    // CUSTOM---------------------
    private final EventHandler<ActionEvent> distsActionListener = this::distsAction;
    private final EventHandler<ActionEvent> textAreasActionListener = this::textAreasAction;
    // =============================================================================
    private int addIndex = -1;
    private boolean columnAdd = false;
    private boolean updateControl = false;
    // BOOLEAN CONTROL =============================================================
    // TABLE ----------------------------------------
    private boolean tableOk = false;
    // COLUMN ----------------------------------------
    private boolean columnBWOK = false;
    private boolean columnsSameOk = true;

    private boolean columnAddOk = false;
    // TYPE ------------------------------------------
    private boolean typeSelectionMatch = true;
    private boolean typeLengthOK = true;
    private boolean typeAddOk = true;
    private boolean typeLenghtAddOk = true;
    // FK --------------------------------------------
    private boolean fkSelectionMatch = true;
    // DEFAULT ---------------------------------------
    private boolean defaultBW = true;
    private boolean defaultOK = true;
    private boolean defaultAddOk = true;
    // CUSTOM -----------------------------------------
    private boolean imageCOK = true;
    // MIX ---------------------------------------------
    private boolean extraPKOK = true;
    private boolean extraFKOK = true;
    private boolean extraDefaultOK = true;

    private boolean extraDistOK = true;
    // QOL =========================================================
    private int index;
    private String tableName;
    private String column;
    private String type;

    // DELETE METHOD
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
                sb.append(tfsColumn.get(indexs[0]).getText().replace(" ", "_")).append(",");
            }
            indexs[0]++;
        });

        return sb.length() == 0 ? "NONE" : sb.deleteCharAt(sb.length() - 1).toString();
    }

    // DELETE METHOD
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

    // CONTROL =============================================================
    // DELETE METHOD
    private void masterControl(boolean rest) {
        if (rest) {
            columnMatchControl(-1); // Unec for columnTextProperty
            typeControl(-1); // Unec for columnTextProperty
            typeLengthControl(-1); // Unec for columnTextProperty
            defaultsControl(-1);

            selectsControl();
        }

        if (!updateControl) {
            boolean allOk = popupMessageControl.getPopupMaster() != null
                    ? popupMessageControl.getPopupMaster().getVbox().getChildren().isEmpty()
                    : false;
            btnCreateUpdate.setDisable(!allOk);
        }
    }
    
    void masterControlTest(){
        if (!updateControl) {
            boolean allOk = popupMessageControl.getPopupMaster() != null
                    ? popupMessageControl.getPopupMaster().getVbox().getChildren().isEmpty()
                    : false;
            btnCreateUpdate.setDisable(!allOk);

        }
    }

    // DELETE METHOD
    private void addColumnControl() {
        boolean disable = columnAddOk && typeAddOk && typeLenghtAddOk && defaultAddOk;
        //createAddColumnHelpPopupReset();
        btnsAddColumn.get(addIndex).setDisable(!disable);
    }

    // DELETE METHOD
    private void defaultAndTypesControl(int index) {
        TextField tf = tfsDefault.get(index);
        String text = tf.getText();
        boolean update = false;

        if (tf.isVisible()) {
            if (!hbsType.get(index).getStyle().contains(CSS.NODE_BORDER_ERROR)) {
                defaultPopupsControl.get(index).getItemList().remove(VCStore.TYPE_AND_DEFAULT);

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

                String wrongTypeLength = VCStore.getWrongDefaultLength(typeChar.toLowerCase(),
                        tfsTypeLength.get(index).isVisible(), typeLength);
                if (pattern != null ? pattern.matcher(text).matches() : true) {
                    defaultPopupsControl.get(index).getItemList().remove(wrongTypeLength);

                    defaultBW = true;
                    if (!updateControl) {
                        defaultsControl(index);
                    }
                } else {
                    defaultPopupsControl.get(index).getItemList().add(wrongTypeLength);
                    defaultBW = false;
                }

            } else {
                defaultPopupsControl.get(index).getItemList().add(VCStore.TYPE_AND_DEFAULT);
            }

        } else {
            defaultPopupsControl.get(index).getItemList().removeIf(s -> !s.contains("id-"));
            defaultBW = true;
            defaultsControl(index);
        }
        // UPDATE-------------------------------------------
        defaultUpdateControl(index, defaultBW, false);
    }

    void selectsControl() {
        boolean ok = tableOk && VCRow.isAllColumnBWOK() && VCRow.isColumnsSameOk();

        btnSelectUI.setDisable(!ok);
        btnSelectIC.setDisable(!ok);
        btnSelectFK.setDisable(!ok);
    }

    // MIX -----------------------------------------------------------
    /**
     * Shoul be at: extrasAction, pksAction and addRemoveColumns MESSAGE:
     * AUTO_INCREMENT (extra) has to be a Primary Key
     */
    private void extraAndPKControl(int index) {
        extraPKOK = true;
        if (rbsExtra.get(index).isSelected() && !rbsPK.get(index).isSelected()) {
            extraPKOK = false;
        }

        // DISPLAY ------------------------------------------
        if (extraPKOK) {
            pkPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_PK);
            extraPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_PK);
        } else {
            pkPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_PK);
            extraPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_PK);
        }
    }

    /**
     * Should be at: extrasAction, fksSelectedAction, fksTextProperty and
     * addRemoveColumns MESSAGE: There's no need to have a FOREIGN KEY column with
     * AUTO_INCREMENT
     */
    private void extraAndFKControl(int index) {
        extraFKOK = true;
        if (rbsExtra.get(index).isSelected() && vfkc != null
                ? vfkc.getDraggableNodes().get(index).isNodeOnItsOriginalParent()
                : false) {
            extraFKOK = false;
        }

        // DISPLAY ------------------------------------------
        if (extraFKOK) {
            fkPopupsControl.getItemList().remove(VCStore.AUTO_INCREMENT_AND_FK);
            extraPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_FK);
        } else {
            fkPopupsControl.getItemList().add(VCStore.AUTO_INCREMENT_AND_FK);
            extraPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_FK);
        }
    }

    /**
     * Should be at: extrasAction, (defaultsAction and defaultsTextProperty) and
     * addRemoveColumns MESSAGE: There's no need to have a DEFAULT value in a column
     * with AUTO_INCREMENT
     */
    private void extraAndDefaultControl(int index) {
        extraDefaultOK = true;
        if (rbsExtra.get(index).isSelected() && cksDefault.get(index).isSelected()) {
            extraDefaultOK = false;
        }
        // DISPLAY ------------------------------------------
        if (extraDefaultOK) {
            defaultPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_DEFAULT);
            extraPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_DEFAULT);
        } else {
            defaultPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_DEFAULT);
            extraPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_DEFAULT);
        }
    }

    /**
     * 
     * MESSAGE: Dist shouldn't be selected when extra (AUTO_INCREMENT) is on
     */
    private void extraAndDistControl(int index) {
        extraDistOK = true;
        if (rbsExtra.get(index).isSelected() && btnsDist.get(index).isSelected()) {
            extraDistOK = false;
        }
        // DISPLAY ------------------------------------------
        if (extraDistOK) {
            distPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_DIST);
            extraPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_DIST);
        } else {
            distPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_DIST);
            extraPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_DIST);
        }
    }

    // LISTENERS ============================================================
    // TABLE-------------------------------------
    private void tableTextProperty(String newValue) {
        String[] tableList = currentDatabse.getTablesNames();
        String text = tfTable.getText().toLowerCase().replace(" ", "_").trim().replaceAll("(^_|_$)", "");

        matcher = patternBWTC.matcher(text);
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
        masterControl(false);
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

    // COLUMNS=====================================================
    private boolean getColumnMatch(String text) {
        final String textt = text.toUpperCase();
        matcher = patternBWTC.matcher(text);
        return matcher.matches() && MSQL.BAND_COLUMNS_NAMES.stream().noneMatch(word -> textt.equals(word));
    }

    private void columnsTextProperty(ObservableValue<? extends String> obs) {
        index = Integer.parseInt(((TextField) ((StringProperty) obs).getBean()).getId());

        String text = tfsColumn.get(index).getText().toLowerCase().trim().replace(" ", "_");
        listSameColumns.set(index, text);

        if (!text.trim().isEmpty()) {
            matcher = patternBWTC.matcher(text);
            if (getColumnMatch(text)) {
                columnsPopupsControl.get(index).getItemList().removeIf(s -> !s.contains("id-"));

                columnBWOK = true;// REST CONTROL
                columnMatchControl(index);
            } else {
                // popupShow(tf, ILLEGAL_CHARS);
                columnsPopupsControl.get(index).getItemList().add(VCStore.ILLEGAL_CHARS);
                columnBWOK = false;
            }
        } else {
            columnsPopupsControl.get(index).getItemList().add(VCStore.EMPTY_TEXT);
            columnBWOK = false;
        }
        // UPDATE---------------------------------------------------
        // tfsColumnUpdate(index, text);
        // ---------------------------------------------------------
        masterControl(false);
        selectsControl();
    }

    private void listColumnsChange(Change<? extends String> c) {
        while (c.next()) {
            columnSameControl();
        }
    }

    private void columnSameControl() {
        if (MList.areTheyDuplicatedElementsOnList(listSameColumns)) {
            columnsSameOk = false;
            messageSameColumns.getItemList().add(VCStore.DUPLICATE_COLUMNS_NAMES);
        } else {
            columnsSameOk = true;
            messageSameColumns.getItemList().remove(VCStore.DUPLICATE_COLUMNS_NAMES);
        }
    }

    private void columnMatchControl(int index) {
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

    /**
     * DELETE METHOD
     */
    void updateColumn(ActionEvent e) {
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
            lbStatus.setText("Column '" + column + "' changed to '" + newColumn + "'", lbStatus.getTextFillOk(),
                    Duration.seconds(2));
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to be renamed", lbStatus.getTextFillError());
        }
    }

    // ADD OR REMOVE COLUMNS ======================================================
    // CREATE --------------------------------------------------
    void addColumnCreateAction(ActionEvent e) {
        setQOLVariables(e);
        addRow(index + 1, true);
    }

    void removeColumnCreateAction(ActionEvent e) {
        setQOLVariables(e);
        removeRow(index, true);
    }

    // UPDATE --------------------------------------------------
    void addColumnUpdateVisibleAction(ActionEvent e) {
        setQOLVariables(e);
        addRow(index + 1, false);
        startAddColumnState(index);
    }


    // DELETE METHOD
    /**
     * Query update for Adding a column
     * 
     * 
     * @param e action event
     */
    private void addColumnUpdateAction(ActionEvent e) {
        System.out.println(CC.CYAN + "\nADD COLUMN" + CC.RESET);
        // VARIABLES SET ================================================
        setQOLVariables(e);
        column = tfsColumn.get(index).getText().toLowerCase().trim().replace(" ", "_");
        type = tfsType.get(index).getText()
                + (tfsTypeLength.get(index).isVisible() ? "(" + tfsTypeLength.get(index).getText() + ")" : "");
        String afterBeforeColumn;
        boolean addColumn;

        ms.setNullValue(cksNull.get(index).isSelected());

        String defaultText = tfsDefault.get(index).getText();//
        String typeChar = types.getTypeChar(tfsType.get(index).getText());
        // WILL FAIL WITH DECIMALS !!!!!!!!!!!!!!!!!!!
        Object defaultValue = !defaultText.isEmpty() && (typeChar.equals("NUMBER") || typeChar.equals("DECIMAL"))
                ? Integer.parseInt(defaultText)
                : defaultText;
        defaultValue = cksDefault.get(index).isSelected() ? defaultValue : null;
        ms.setDefaultValue(defaultValue);

        // ADD COLUMN ====================================================
        try {// ATTEMPTING TO GRAB THE PREVIOUS COLUMN
            afterBeforeColumn = currentTable.getColumns().get(index - 1).getName().toLowerCase().trim().replace(" ",
                    "_");

            ms.setAfterOrBeforeColumn(MSQLP.AFTER);
            addColumn = ms.addColumn(tableName, column, type, afterBeforeColumn);
        } catch (ArrayIndexOutOfBoundsException ex) {
            afterBeforeColumn = currentTable.getColumns().get(0).getName().toLowerCase().trim().replace(" ", "_");
            ;

            ms.setAfterOrBeforeColumn(MSQLP.BEFORE);
            addColumn = ms.addColumn(tableName, column, type, afterBeforeColumn);
        }
        // ADD CUSTOM =====================================
        // NOT TESTED
        boolean addDist = true;
        if (btnsDist.get(index).isSelected()) {
            currentTable.getColumns().get(index).setDist(true);
            String dist = getCustomStringFromUpdateTable("dist", index, column);
            addDist = ms.updateRow(MSQL.TABLE_CUSTOMS, "id_table", currentTable.getId(), "dist", dist);
        }
        // NOT TESTED
        boolean addTextArea = true;
        if (btnsTextArea.get(index).isSelected()) {
            currentTable.getColumns().get(index).setTextArea(true);
            String textArea = getCustomStringFromUpdateTable("textArea", index, column);
            addTextArea = ms.updateRow(MSQL.TABLE_CUSTOMS, "id_table", currentTable.getId(), "textArea", textArea);
        }
        // TO FINISH =================================================
        if (addColumn && addDist && addTextArea) {
            // ADDING VALUES TO EMPTY ROW (PREVIOUSLY ADDED)
            currentTable.getColumns().get(index).setName(column);
            currentTable.getColumns().get(index).setType(tfsType.get(index).getText());
            currentTable.getColumns().get(index).setTypeLength(Integer.parseInt(tfsTypeLength.get(index).getText()));

            currentTable.getColumns().get(index).setNulll(cksNull.get(index).isSelected());
            currentTable.getColumns().get(index)
                    .setDefaultt(cksDefault.get(index).isSelected() ? tfsDefault.get(index).getText() : null);
            // CUSTOM ----------------------
            currentTable.getColumns().get(index).setDist(btnsDist.get(index).isSelected());
            currentTable.getColumns().get(index).setTextArea(btnsTextArea.get(index).isSelected());
            // -----------------------------
            exitAddColumnState(index);

            lbStatus.setText("Added column '" + column + "' to '" + currentTable.getName() + "'",
                    lbStatus.getTextFillOk(), Duration.seconds(4));
            System.out.println("\tSUCCES");
        } else {
            lbStatus.setText("Couldn't add column '" + column + "'", lbStatus.getTextFillError());
            System.out.println("\tFAILED");
        }

    }

    /**
     * DELETE METHOD
     */
    private void addRowCancelAction(ActionEvent e) {
        System.out.println(CC.GREEN + "CANCEL ADD COLUMN" + CC.RESET);
        setQOLVariables(e);
        // -------------------------------------
        removeRow(index, false);
        // removeRowUpdateBACKUP(index);
        // -------------------------------------
        currentTable.getColumns().remove(index); // Added At AddColumnState !

        exitAddColumnState(index);
    }

    /**
     * Query update for Drop a Column
     * 
     * DELETE METHOD
     * 
     * @param e the removeColumn Button (update only)
     */
    void dropColumnAction(ActionEvent e) {
        column = tfsColumn.get(index).getText().trim();
        // ARE YOU SURE WINDOW ====================================================
        TrueFalseWindow w = new TrueFalseWindow("Delete Column '" + column + "'?");
        w.getBtnFalse().setOnAction(ef -> w.hide());
        w.getBtnTrue().setOnAction(et -> {
            System.out.println(CC.GREEN + "REMOVE COLUMN" + CC.RESET);
            setQOLVariables(e);
            // DROP COLUMN ========================================================
            boolean dropColumn = ms.dropColumn(tableName, column);
            if (dropColumn) {
                // REMOVE CUSTOM ----------------------------------
                // NOT TESTED
                if (currentTable.getColumns().get(index).isDist()) {
                    // String dist = updateTable.getDistHole();
                    String dist = currentTable.getDist();
                    dist = dist.replace(column, "").replaceAll("(^,|,$)", "");// removing ',' at the beggining an end
                    dist = dist.isEmpty() ? "NONE" : dist;

                    boolean removeDistTest = ms.updateRow(MSQL.TABLE_CUSTOMS, "id_table", currentTable.getId(), "dist",
                            dist);
                    System.out.println("TEST removeDistTest: " + removeDistTest);
                }
                // NOT TESTED
                if (currentTable.getColumns().get(index).isTextArea()) {
                    String textArea = currentTable.getTextArea();
                    textArea = textArea.replace(column, "").replaceAll("(^,|,$)", "");
                    textArea = textArea.isEmpty() ? "NONE" : textArea;

                    boolean removeTextArea = ms.updateRow(MSQL.TABLE_CUSTOMS, "id_table", currentTable.getId(),
                            "textArea", textArea);
                    System.out.println("TEST removeTextArea: " + removeTextArea);
                }
                // ===================================================
                // removeRowUpdateBACKUP(index);
                removeRow(index, false);
                currentTable.getColumns().remove(index);
                // MESSAGES --------------------------------------------------
                lbStatus.setText("Column '" + column.replace("_", " ") + "' has been removed", lbStatus.getTextFillOk(),
                        Duration.seconds(3));
                System.out.println("\tSUCCESS");
            } else {
                lbStatus.setText("Column '" + column.replace("_", " ") + "' fail to be removed",
                        lbStatus.getTextFillError());
                System.out.println("\tFAILED");
            }
            // --------------------------------------
            w.hide();
        });
        w.show();

        // --------------------------------
        masterControl(true);
    }

    // ADD COLUMN STATE ===================================================
    private void startAddColumnState(int index) {
        btnsAddColumn.get(index).setText("A");// ADD
        btnsAddColumn.get(index).setStyle(CSS.NEW_ROW_ADD_BUTTON);
        btnsAddColumn.get(index).setOnAction(addColumnUpdateActionListener);
        btnsAddColumn.get(index).setDisable(true);// DISABLE FOR CONTROL

        btnsRemoveColumn.get(index).setText("C");// CANCEL
        btnsRemoveColumn.get(index).setStyle(CSS.NEW_ROW_REMOVE_BUTTON);
        btnsRemoveColumn.get(index).setOnAction(cancelAddColumnUpdateActionListener);
        btnsRenameColumn.get(index).setDisable(true);
        // DISABLE NODES ================================================
        // TOP -------------------------------------
        hbTop.setDisable(true);
        // CENTER ----------------------------------
        rbsPK.get(index).setDisable(true);
        rbsExtra.get(index).setDisable(true);
        // BOTTOM ----------------------------------
        btnUpdatePK.setDisable(true);
        btnUpdateExtra.setDisable(true);
        btnUpdateDist.setDisable(true);

        btnSelectUI.setDisable(true);
        btnSelectFK.setDisable(true);
        btnSelectIC.setDisable(true);
        // ???? =========================================================
        cksNull.get(index).setStyle(CSS.CKS_BG);
        cksDefault.get(index).setStyle(CSS.CKS_BG);

        btnsDist.get(index).setStyle(CSS.NEW_ROW_DIST_BUTTONS);
        btnsTextArea.get(index).setStyle(CSS.NEW_ROW_DIST_BUTTONS);
        // NEW EMPTY ROW INDEX ==========================================
        addIndex = index;
        columnAdd = true;

        disableRowsAtAddColumnState(index);
        currentTable.getColumns().add(index, null);
    }

    /**
     * DELETE METHOD
     */
    private void disableRowsAtAddColumnState(int index) {
        if (index >= 0 && updateControl) {
            hbsN.get(index).setStyle(CSS.NEW_ROW);
            hbsExtra.get(index).setStyle(CSS.NEW_ROW);
            // DISABLE PREVIOUS ROW-----------------------------------
            for (int a = 0; a < currentRowLength; a++) {
                if (a != index) {
                    hbsN.get(a).setDisable(true);
                    hbsName.get(a).setDisable(true);
                    hbsType.get(a).setDisable(true);
                    hbsNull.get(a).setDisable(true);
                    hbsPK.get(a).setDisable(true);
                    hbsDefault.get(a).setDisable(true);
                    hbsExtra.get(a).setDisable(true);

                    btnsDist.get(a).setDisable(true);
                    btnsTextArea.get(a).setDisable(true);
                }
            }
        }
    }

    /**
     * DELETE METHOD
     */
    private void exitAddColumnState(int index) {
        columnAdd = false;
        columnAddOk = false;
        typeAddOk = true;
        typeLenghtAddOk = true;
        defaultAddOk = true;

        hbTop.setDisable(false);
        // RESET NEW ROW ==========================================
        hbsN.get(index).setStyle(CSS.ROW);
        tfsColumn.get(index).setStyle(CSS.NODE_TEXTFILL_HINT);
        tfsType.get(index).setStyle(CSS.NODE_TEXTFILL_HINT);
        tfsTypeLength.get(index).setStyle(CSS.NODE_TEXTFILL_HINT);
        cksNull.get(index).setStyle(CSS.CKS_BG_HINT);
        rbsPK.get(index).setDisable(false);
        cksDefault.get(index).setStyle(CSS.CKS_BG_HINT);
        tfsDefault.get(index).setStyle(CSS.NODE_TEXTFILL_HINT);
        rbsExtra.get(index).setDisable(false);
        hbsExtra.get(index).setStyle(CSS.ROW);

        btnsRemoveColumn.get(index).setText("X");// REMVE
        btnsRemoveColumn.get(index).setStyle(CSS.ROW_REMOVE_BUTTON);

        btnsAddColumn.get(index).setText("+");// ADD
        btnsAddColumn.get(index).setStyle(CSS.ROW_ADD_BUTTON);
        btnsAddColumn.get(index).setTooltip(null);

        btnsDist.get(index).setStyle(CSS.ROW_DIST_BUTTONS);
        btnsTextArea.get(index).setStyle(CSS.ROW_DIST_BUTTONS);
        // RE-ENABLED NODES ===============================================
        // CENTER --------------------------------------------------
        for (int a = 0; a < currentRowLength; a++) {
            hbsN.get(a).setDisable(false);
            hbsName.get(a).setDisable(false);
            hbsType.get(a).setDisable(false);
            hbsNull.get(a).setDisable(false);
            hbsPK.get(a).setDisable(false);
            hbsDefault.get(a).setDisable(false);
            hbsExtra.get(a).setDisable(false);

            btnsDist.get(a).setDisable(false);
            btnsTextArea.get(a).setDisable(false);
        }
        // BOTTOM -----------------------------------------
        btnUpdatePK.setDisable(false);
        btnUpdateExtra.setDisable(false);
        btnUpdateDist.setDisable(false);

        btnSelectUI.setDisable(false);
        btnSelectFK.setDisable(false);
        btnSelectIC.setDisable(false);
    }

    // TYPES=====================================================
    private void typeTextProperty(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        index = Character.getNumericValue(tf.getId().charAt(tf.getId().length() - 1));
        boolean update = false;

        newValue = newValue.trim();
        matcher = patternBWTC.matcher(newValue);
        if (matcher.matches()) {
            typePopupsControl.get(index).getItemList().remove(VCStore.ILLEGAL_CHARS);

            final String newValuee = newValue;
            if (Arrays.asList(types.getTypeNames()).stream().anyMatch(item -> {
                return item.equals(newValuee);
            })) {
                int typeLength = types.getTypeLength(newValue);
                if (typeLength > 0) {// TF-TYPE-LENGTH-POPUP
                    tfsTypeLength.get(index).setVisible(true);
                    tfsTypeLength.get(index).setText(Integer.toString(typeLength));
                } else {
                    tfsTypeLength.get(index).setVisible(false);
                    tfsTypeLength.get(index).setText("1");
                    typeLengthOK = true;
                }
                typePopupsControl.get(index).getItemList().removeIf(s -> !s.contains("id-") && !s.contains("%length"));

                typeSelectionMatch = true;
                update = true;
                typeControl(index);
            } else {
                tfsTypeLength.get(index).setVisible(false);
                tfsTypeLength.get(index).setText("1");

                typePopupsControl.get(index).getItemList().add(VCStore.SELECTION_UNMATCH);

                typeSelectionMatch = false;
            }
        } else {
            typePopupsControl.get(index).getItemList().add(VCStore.ILLEGAL_CHARS);
            typeSelectionMatch = false;
        }

        defaultAndTypesControl(index);
        // UPDATE----------------------------------------------------------
        typeUpdateControl(index, newValue, update);
        // ----------------------------------------------------------
        masterControl(false);
    }

    private void typeLengthTextProperty(ObservableValue<? extends String> observable, String oldValue,
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

                    typePopupsControl.get(index).getItemList().removeIf(s -> s.contains("%length"));
                    typeLengthOK = true;
                    update = true;
                    typeLengthControl(index);
                } else {
                    typePopupsControl.get(index).getItemList()
                            .add(VCStore.getWrongTypeLength(typeMaxLength) + "%length");
                    typeLengthOK = false;
                }
            } else {
                typePopupsControl.get(index).getItemList().add(VCStore.getWrongTypeLength(typeMaxLength) + "%length");
                typeLengthOK = false;

            }

            defaultAndTypesControl(index);
            // UPDATE-----------------------------------------------------
            typeLengthUpdateControl(index, text, update);
        }
        // -----------------------------------------------------
        masterControl(false);
    }

    private void typeControl(int index) {
        typeSelectionMatch = true;
        for (int a = 0; a < currentRowLength; a++) {
            if (index != a) {
                // String text = tfasType[a].getTf().getText();
                String text = tfsType.get(a).getText();
                matcher = patternBWTC.matcher(text);
                if (matcher.matches()) {
                    if (!MList.isOnThisList(
                            /* tfasType[a].getLv().getItems() */ tfsTypePAutoC.get(a).getLv().getItems(), text,
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

    private void typeLengthControl(int index) {
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
                    tfsType.get(index).setStyle(null);
                    ok = true;
                } else {
                    tfsType.get(index).setStyle(CSS.NODE_TEXTFILL_HINT);
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
                addColumnControl();
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
                    tfsTypeLength.get(index).setStyle(null);
                    ok = true;
                } else {
                    // tfsTypeLengthPopups[index].show(SAME_VALUE);
                    tfsTypeLength.get(index).setStyle(CSS.NODE_TEXTFILL_HINT);
                    ok = false;
                }
            } else {
                ok = false;
            }

            if (!columnAdd) {
                btnsChangeType.get(index).setDisable(!ok);
            } else {
                typeLenghtAddOk = ok;
                addColumnControl();
            }
        }
    }

    /**
     * DELETE METHOD
     */
    void updateType(ActionEvent e) {
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

            lbStatus.setText("Column '" + column + "' has change it's type to '" + type + "'", lbStatus.getTextFillOk(),
                    Duration.seconds(2));

            btnsChangeType.get(index).setDisable(true);
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to change it's type", lbStatus.getTextFillError());
        }

    }

    // NULLS======================================================
    /**
     * Only on update mode
     * 
     * DELETE METHOD
     */
    void nullsAction(ActionEvent e) {
        setQOLVariables(e);
        // boolean nulllO = updateTable.getNulls().get(index);
        boolean nulllO = currentTable.getColumns().get(index).isNulll();
        boolean nulll = cksNull.get(index).isSelected();

        if (nulllO != nulll) {
            cksNull.get(index).setStyle(CSS.CKS_BG);
            btnsChangeNull.get(index).setDisable(false);
        } else {
            cksNull.get(index).setStyle(CSS.CKS_BG_HINT);
            btnsChangeNull.get(index).setDisable(true);
        }
    }

    // DELETE METHOD
    void updateNull(ActionEvent e) {
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
        // ms.setExtraValue(index == updateTable.getExtra());
        ms.setExtraValue(index == currentTable.getExtra());
        boolean changeNull = ms.changeType(tableName, column, type);
        if (changeNull) {
            System.out.println("\tSUCCESS");
            // updateTable.getNulls().set(index, nulll);
            currentTable.getColumns().get(index).setNulll(nulll);

            cksNull.get(index).setStyle(CSS.CKS_BG_HINT);
            btnsChangeNull.get(index).setDisable(true);

            lbStatus.setText("Column '" + column + "' change to " + (nulll ? "NULL" : "NOT NULL"),
                    lbStatus.getTextFillOk(), Duration.seconds(2));
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to be changed", lbStatus.getTextFillError());
        }
    }

    // PKS/FKS======================================================
    void pksAction(ActionEvent e) {
        // ONLY FOR UPDATE
        setQOLVariables(e);
        boolean update = false;

        extraPKOK = !(rbsExtra.get(index).isSelected() && !rbsPK.get(index).isSelected());
        // UPDATE--------------------------------
        if (updateControl) {
            for (int c = 0; c < currentRowLength; c++) {
                // boolean pkSelected = updateTable.getPks().get(c).equals("Yes");
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

        extraAndPKControl(index);
        masterControl(false);
    }

    void updatePK(ActionEvent e) {
        System.out.println(CC.CYAN + "Update PK" + CC.RESET);
        setQOLVariables(e);

        String[] errorMessage = { null };
        ms.setSQLException((ex, s) -> errorMessage[0] = ex.getMessage());
        boolean dropPK = true;
        // if (updateTable.getPks().stream().anyMatch(p -> p.equals("Yes"))) {
        if (!currentTable.getPKS().isEmpty()) {
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

    // DEFAULTS =================================================
    private void defaultsAction(ActionEvent e) {
        setQOLVariables(e);

        if (cksDefault.get(index).isSelected()) {
            tfsDefault.get(index).setVisible(true);
        } else {
            tfsDefault.get(index).setVisible(false);
            defaultOK = true;
        }

        defaultsControl(!updateControl ? -1 : index);// UNECESSARY
        // UPDATE-------------------------------------------
        defaultAndTypesControl(index);
        // defaultUpdate(index, update, true);
        extraAndDefaultControl(index);
        // -------------------------------------------
        masterControl(false);
    }

    private void defaultsTextProperty(ObservableValue<? extends String> obs) {
        index = Integer.parseInt(((TextField) ((StringProperty) obs).getBean()).getId());

        defaultAndTypesControl(index);// ++++++++++++++++
        extraAndDefaultControl(index);
        masterControl(false);
    }

    private void defaultsControl(int index) {
        defaultBW = true;
        for (int a = 0; a < currentRowLength; a++) {
            if (!updateControl && (a != index)) {
                if (tfsDefault.get(a).isVisible()) {
                    if (!tfsType.get(a).getStyle().contains(CSS.NODE_BORDER_ERROR)
                            && (!tfsTypeLength.get(a).getStyle().contains(CSS.NODE_BORDER_ERROR)
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

    private void defaultUpdateControl(int index, boolean update, boolean cks) {
        if (updateControl) {
            boolean ok = false;
            if (update) {
                // boolean defaultSelected = updateTable.getDefaults().get(index) != null;
                boolean defaultSelected = currentTable.getColumns().get(index).getDefaultt() != null;
                // String defaultValue = updateTable.getDefaults().get(index) != null
                String defaultValue = defaultSelected ? currentTable.getColumns().get(index).getDefaultt().toString()
                        : "";
                if (cksDefault.get(index).isSelected() != defaultSelected
                        || (cksDefault.get(index).isSelected() && !tfsDefault.get(index).getText().equals(defaultValue))
                        || columnAdd) {
                    // cksDefaultPopups[index].hide();
                    if (cks) {
                        cksDefault.get(index).setStyle(CSS.BG_COLOR);
                    } else {
                        tfsDefault.get(index).setStyle(null);
                    }
                    ok = true;
                } else {
                    // cksDefaultPopups[index].show(SAME_VALUE);
                    if (cks) {
                        cksDefault.get(index).setStyle(CSS.BG_COLOR_HINT);
                    } else {
                        tfsDefault.get(index).setStyle(CSS.NODE_TEXTFILL_HINT);
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
                addColumnControl();
            }
        }
    }

    //DELETE METHOD
    void updateDefault(ActionEvent e) {
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
            // updateTable.getDefaults().set(index, defaultValue);
            currentTable.getColumns().get(index).setDefaultt(defaultValue);

            tfsDefault.get(index).setStyle(CSS.NODE_TEXTFILL_HINT);
            cksDefault.get(index).setStyle(CSS.CKS_BG_HINT);
            btnsChangeDefault.get(index).setDisable(true);

            lbStatus.setText(
                    "Default value for column '" + column + "' has change to "
                            + (defaultValue != null ? defaultValue.toString() : "NULL"),
                    lbStatus.getTextFillOk(), Duration.seconds(2));
        } else {
            lbStatus.setText("Failt to change Default Value of column '" + column + "'", lbStatus.getTextFillError());
        }

    }

    // EXTRA =================================================
    void extrasAction(ActionEvent e) {
        setQOLVariables(e);

        extraAndPKControl(index);
        extraAndFKControl(index);
        extraAndDefaultControl(index);
        extraAndDistControl(index);

        extrasRestControl(index);
        // UPDATE------------------------------------------------
        extraUpdateControl(index);
        // ------------------------------------------------
        masterControl(false);
    }

    private void extrasRestControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {
                extraAndPKControl(a);
                extraAndFKControl(a);
                extraAndDefaultControl(a);
                extraAndDistControl(a);
            }
        }
    }

    private void extraUpdateControl(int index) {
        if (updateControl) {
            if (extraDefaultOK && extraPKOK && extraFKOK) {
                // int extraO = updateTable.getExtra();
                int extraO = currentTable.getExtra();
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

    void updateExtra(ActionEvent e) {
        System.out.println(CC.CYAN + "Update Extra" + CC.RESET);
        setQOLVariables(e);

        int[] indexs = { 0 };
        boolean extra = rbsExtra.stream().anyMatch(rb -> {
            boolean selected = rb.isSelected();
            indexs[0]++;// MAY WORK -> TEST MORE
            return selected;
        });
        // ms.setNullValue(extra ? updateTable.getNulls().get(indexs[0]) : false);
        ms.setNullValue(extra ? currentTable.getColumns().get(indexs[0]).isNulll() : false);
        ms.setExtraValue(extra);
        boolean updateExtra = ms.changeType(tableName, column, type);
        if (updateExtra) {
            System.out.println(indexs[0]);
            // updateTable.setExtra(extra ? indexs[0] : -1);
            currentTable.setExtra(extra ? indexs[0] : -1);

            btnUpdateExtra.setDisable(true);

            lbStatus.setText("Changed Extra", lbStatus.getTextFillOk(), Duration.seconds(2));
        } else {
            lbStatus.setText("Fail to change Extra", lbStatus.getTextFillError());
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
            // DEFAULT --------------------------------
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

    private void helpAction(ActionEvent e) {
        Bounds sb = btnCreateHelp.localToScreen(btnCreateHelp.getBoundsInLocal());
        if (!updateControl) {
            createHelpPopup.show(btnCreateHelp, sb.getMinX(), sb.getMinY());
        } else if (columnAdd) {
            updateAddColumnHelpPopup.show(btnCreateHelp, sb.getMinX(), sb.getMinY());
        }
    }

    // CUSTOM =====================================================
    // DIST --------------------------------------------------
    private void distsAction(ActionEvent e) {
        // MAY HAVE TO ADD DIST BUTTONS TO AN OBSERVABLE LIST (ADD OR REMOVE COLUMN)
        setQOLVariables(e);

        extraAndDistControl(index);
        // UPDATE--------------------------------------------------------------
        distUpdateControl();
        // --------------------------------------------------------------
        masterControl(false);
    }

    private void distsRestControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {
                ToggleButton btn = btnsDist.get(a);
                if (btn.isSelected() && rbsExtra.get(a).isSelected()) {
                    extraDistOK = false;
                    break;
                }
            }
        }
    }

    private void distUpdateControl() {
        if (updateControl) {
            if (extraDistOK) {
                boolean update = false;
                for (int a = 0; a < currentRowLength; a++) {
                    // boolean dist = false;
                    // if (updateTable.getDists().get(a) != null) {
                    boolean dist = currentTable.getColumns().get(a).isDist();

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

    void updateDist(ActionEvent e) {
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
                    // updateTable.getDists().set(indexs[0], btnsDist.get(indexs[0]).isSelected());
                    currentTable.getColumns().get(indexs[0]).setDist(btnsDist.get(indexs[0]).isSelected());
                }
                indexs[0]++;
            });
            btnUpdateDist.setDisable(true);

            lbStatus.setText("Dist has change to '" + dist + "'", lbStatus.getTextFillOk(), Duration.seconds(2));
            System.out.println("\tSUCCESS");
        } else {
            lbStatus.setText("Dist fail to changed", lbStatus.getTextFillError());
            System.out.println("\tFAILED");

        }
    }

    // TEXTAREA ----------------------------------------------
    private void textAreasAction(ActionEvent e) {
        // CONTROL ONLY TYPE TEXT-BASE
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

    // DELETE METHOD
    // ROW------------------------------------------------
    private void rowInits(int index) {
        // COLUMN NUMBER------------------------------
        lbsN.add(index, new Label("Column " + (index + 1)));
        hbsN.add(index, new HBox(lbsN.get(index)));
        // COLUMN NAMES---------------------------------------
        tfsColumn.add(index, new TextField());

        btnsRemoveColumn.add(index, new Button("X"));
        btnsAddColumn.add(index, new Button("+"));
        btnsRenameColumn.add(index, new Button("C"));

        hbsName.add(index, new HBox(tfsColumn.get(index), btnsRemoveColumn.get(index), btnsAddColumn.get(index),
                btnsRenameColumn.get(index)));
        hbsName.get(index).setPadding(new Insets(0, 2, 0, 2));

        // columnsPopupsControl.add(index, new PopupAction(new PopupMessage("COLUMN:" +
        // index, hbsName.get(index), false)));
        Message columnMessage = new Message("COLUMN:" + index, hbsName.get(index));
        popupMessageControl.getMessages().add(columnMessage);
        columnMessage.getItemList().add(VCStore.EMPTY_TEXT);
        columnsPopupsControl.add(index, columnMessage);
        // TYPES----------------------------------------------
        // tfasType.get(a) = new TextFieldAutoC(a, types.getTypeNames());
        tfsType.add(index, new TextField());
        tfsTypePAutoC.add(index, new PopupAutoC(tfsType.get(index), types.getTypeNames()));

        tfsTypeLength.add(index, new TextField());

        btnsChangeType.add(index, new Button("C"));

        hbsType.add(index, new HBox(tfsType.get(index), tfsTypeLength.get(index), btnsChangeType.get(index)));
        hbsType.get(index).setPadding(new Insets(0, 2, 0, 2));

        Message typeMessage = new Message("TYPE:" + index, hbsType.get(index));
        popupMessageControl.getMessages().add(typeMessage);
        typePopupsControl.add(index, typeMessage);
        // NULLS----------------------------------------------
        cksNull.add(index, new CheckBox());
        btnsChangeNull.add(index, new Button("C"));
        hbsNull.add(index, new HBox(cksNull.get(index), btnsChangeNull.get(index)));
        hbsNull.get(index).setPadding(new Insets(0, 2, 0, 2));
        // PKS----------------------------------------------
        rbsPK.add(index, new RadioButton());
        // btnsChangePK.get(a) = new Button("C");
        hbsPK.add(index, new HBox(rbsPK.get(index)));

        Message pkMessage = new Message("PK:" + index, hbsPK.get(index));
        popupMessageControl.getMessages().add(pkMessage);
        pkPopupsControl.add(index, pkMessage);
        // DEFAULTS----------------------------------------------
        cksDefault.add(index, new CheckBox());
        tfsDefault.add(index, new TextField());
        btnsChangeDefault.add(index, new Button("C"));

        hbsDefault.add(index, new HBox(cksDefault.get(index), tfsDefault.get(index), btnsChangeDefault.get(index)));
        hbsDefault.get(index).setPadding(new Insets(0, 2, 0, 2));

        Message defaultMessage = new Message("DEFAULT:" + index, hbsDefault.get(index));
        popupMessageControl.getMessages().add(defaultMessage);
        defaultPopupsControl.add(index, defaultMessage);
        // EXTRA----------------------------------------------
        rbsExtra.add(index, new RadioButton());
        hbsExtra.add(index, new HBox(rbsExtra.get(index)/* , btnsChangeExtra.get(a) */));

        Message extraMessage = new Message("EXTRA:" + index, hbsExtra.get(index));
        popupMessageControl.getMessages().add(extraMessage);
        extraPopupsControl.add(index, extraMessage);
        // DIST----------------------
        btnsDist.add(index, new ToggleButton("" + (index + 1)));

        Message distMessage = new Message("DIST:" + index, btnsDist.get(index));
        popupMessageControl.getMessages().add(distMessage);
        distPopupsControl.add(index, distMessage);
        // TEXT AREA--------------------------------------
        btnsTextArea.add(index, new ToggleButton("" + (index + 1)));
        // btnsTextAreaPopups.add(new PopupMessage(index, btnsTextArea.get(index)));
        Message textAreaMessage = new Message("TEXTAREA:" + index, btnsTextArea.get(index));
        popupMessageControl.getMessages().add(textAreaMessage);
        textAreaPopupsControl.add(index, textAreaMessage);
    }

    // DELETE METHOD
    private void rowConfig(int index) {
        // ADDING NEW INDEXS OR
        // ALL---------------------------------------------------------
        rowInits(index);
        // PROMPT TEXT ----------------------------------------------
        tfsColumn.get(index).setPromptText("Column name required");
        tfsDefault.get(index).setPromptText("Value Required");
        // STYLE ======================================================
        // COLUMN ----------------------------
        hbsN.get(index).getStyleClass().add("vc-row");
        hbsName.get(index).getStyleClass().add("vc-row");
        // TYPE ------------------------------
        hbsType.get(index).getStyleClass().add("vc-row");
        // tfsType.get(index).setStyle(null); ???????
        tfsType.get(index).getStyleClass().add("tf-dist");
        tfsTypeLength.get(index).setStyle(null);
        // NULL ------------------------------
        hbsNull.get(index).getStyleClass().add("vc-row");
        // PK -------------------------------
        hbsPK.get(index).getStyleClass().add("vc-row");
        // DEFAULT ---------------------------
        hbsDefault.get(index).getStyleClass().add("vc-row");
        // EXTRA -----------------------------
        hbsExtra.get(index).getStyleClass().add("vc-row");
        // CUSTOM ----------------------------
        btnsDist.get(index).getStyleClass().add("vc-row");
        btnsTextArea.get(index).getStyleClass().add("vc-row");
        // TYPE DEFAULT SELECTION ==================================================
        tfsTypePAutoC.get(index).getLv().getSelectionModel().select(presetTypeSelected.get(index).getTypeName());
        tfsTypeLength.get(index).setText(Integer.toString(presetTypeSelected.get(index).getTypeLength()));
        // WIDTH & HEIGHT -----------------------------------
        tfsColumn.get(index).setPrefWidth(-1);
        HBox.setHgrow(tfsColumn.get(index), Priority.ALWAYS);

        btnsRemoveColumn.get(index).setMinWidth(40);
        btnsRemoveColumn.get(index).setMaxWidth(40);
        HBox.setHgrow(btnsRemoveColumn.get(index), Priority.NEVER);

        btnsAddColumn.get(index).setMinWidth(40);
        btnsAddColumn.get(index).setMaxWidth(40);
        HBox.setHgrow(btnsAddColumn.get(index), Priority.NEVER);

        tfsType.get(index).setPrefWidth(140);
        tfsTypeLength.get(index).setMinWidth(40);
        tfsTypeLength.get(index).setMaxWidth(40);

        btnsDist.get(index).setMinWidth(40);
        btnsDist.get(index).setMaxWidth(40);
        btnsTextArea.get(index).setMinWidth(40);
        btnsTextArea.get(index).setMaxWidth(40);

        // VISIBLE PROPERTY ------------------------------------
        btnsRenameColumn.get(index).managedProperty().bind(btnsRenameColumn.get(index).visibleProperty());
        tfsTypeLength.get(index).managedProperty().bind(tfsTypeLength.get(index).visibleProperty());
        btnsChangeType.get(index).managedProperty().bind(btnsChangeType.get(index).visibleProperty());
        btnsChangeNull.get(index).managedProperty().bind(btnsChangeNull.get(index).visibleProperty());
        tfsDefault.get(index).managedProperty().bind(tfsDefault.get(index).visibleProperty());
        btnsChangeDefault.get(index).managedProperty().bind(btnsChangeDefault.get(index).visibleProperty());

        btnsDist.get(index).managedProperty().bind(btnsDist.get(index).visibleProperty());
        btnsTextArea.get(index).managedProperty().bind(btnsTextArea.get(index).visibleProperty());
        // VISIBILITY----------------------------------
        tfsDefault.get(index).setVisible(false);

        btnsRenameColumn.get(index).setDisable(true);
        btnsChangeType.get(index).setDisable(true);
        btnsChangeNull.get(index).setDisable(true);
        btnsChangeDefault.get(index).setDisable(true);

        if (!updateControl) {
            btnsRenameColumn.get(index).setVisible(false);
            btnsChangeType.get(index).setVisible(false);
            btnsChangeNull.get(index).setVisible(false);
            btnsChangeDefault.get(index).setVisible(false);
        }

        new ToggleGroupD<>(rbsExtra.toArray(new RadioButton[rbsExtra.size()]));

    }

    // DELETE METHOD
    /**
     * Add the listener for a new row. Should happen one time for each row
     */
    private void newRowListeners(int index) {
        tfsColumn.get(index).textProperty().addListener(columnsTextPropertyListener);

        tfsType.get(index).textProperty().addListener(tfsTypeTextPropertyListener);
        tfsTypeLength.get(index).textProperty().addListener(tfsTypeLengthTextPropertyListener);

        rbsPK.get(index).setOnAction(pksActionListener);

        cksDefault.get(index).setOnAction(defaultsActionListener);
        tfsDefault.get(index).textProperty().addListener(defaultsTextPropertyListener);

        rbsExtra.get(index).addEventHandler(ActionEvent.ACTION, extrasActionListener);
        // CUSTOM----------------------
        btnsDist.get(index).setOnAction(distsActionListener);
        btnsTextArea.get(index).setOnAction(textAreasActionListener);

    }

    // DELETE METHOD
    void newRowCreateListeners(int index) {
        btnsRemoveColumn.get(index).setOnAction(this::removeColumnCreateAction);
        btnsAddColumn.get(index).setOnAction(this::addColumnCreateAction);

        btnsRenameColumn.get(index).setVisible(false);
        btnsChangeType.get(index).setVisible(false);
        btnsChangeNull.get(index).setVisible(false);
        btnsChangeDefault.get(index).setVisible(false);
    }

    // DELETE METHOD
    void newRowUpdateListeners(int index) {
        btnsAddColumn.get(0).setContextMenu(null);
        btnsAddColumn.get(0).setTextFill(null);
        if (index == 0) {
            btnsAddColumn.get(0).setContextMenu(beforeAfterOptionMenu);
            btnsAddColumn.get(0).setTooltip(beforeAfterOptionTooltip);
        }
        // VISIBILITY ===========================================================
        btnsRenameColumn.get(index).setVisible(true);
        btnsChangeType.get(index).setVisible(true);
        btnsChangeNull.get(index).setVisible(true);
        btnsChangeDefault.get(index).setVisible(true);
        // LISTENERS ============================================================
        // COLUMN ---------------------------------------
        btnsAddColumn.get(index).setOnAction(addColumnUpdateVisibleActionListener);
        btnsRemoveColumn.get(index).setOnAction(removeColumnUpdateActionListener);
        // This Listeners don't havev a reserved listener variable yet!
        btnsRenameColumn.get(index).setOnAction(this::updateColumn);
        // TYPE -----------------------------------------
        btnsChangeType.get(index).setOnAction(this::updateType);
        // NULL ----------------------------------------
        cksNull.get(index).setOnAction(this::nullsAction);
        btnsChangeNull.get(index).setOnAction(this::updateNull);
        // DEFAULT -------------------------------------
        btnsChangeDefault.get(index).setOnAction(this::updateDefault);

    }

    // DELETE METHOD
    private void resetGridPaneRows() {
        // REMOVING ======================================================
        gridPane.getChildren().removeIf(node -> !node.getStyleClass().contains("vi-header"));
        // RE-ADDING ===========================================================
        for (int a = 0; a < currentRowLength; a++) {
            int row = a + 1;// + HEADER
            gridPane.getRowConstraints().add(new RowConstraints(32, 32, 32, Priority.ALWAYS, VPos.TOP, true));
            gridPane.add(hbsN.get(a), 0, row);
            gridPane.add(hbsName.get(a), 1, row);
            gridPane.add(hbsType.get(a), 2, row);
            gridPane.add(hbsNull.get(a), 3, row);
            gridPane.add(hbsPK.get(a), 4, row);
            gridPane.add(hbsDefault.get(a), 5, row);
            gridPane.add(hbsExtra.get(a), 6, row);

            gridPane.add(btnsDist.get(a), 7, row);
            gridPane.add(btnsTextArea.get(a), 8, row);
        }
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

    // DELETE METHOD
    void addRow(int index, boolean create) {
        rowConfig(index);
        newRowListeners(index);
        // ------------------------------------------
        if (create) {
            newRowCreateListeners(index);
        } else {
            newRowUpdateListeners(index);
        }
        // -------------------------------------------
        // ------------------------------------------
        currentRowLength++;
        resetGridPaneRows();

        listSameColumns.add("");

        restartIds();
        restartFirstUpdateAddButton();
        btnsAddRemoveColumnReset();
        // ---------------------------------------------
        extraAndPKControl(index);
        extraAndFKControl(index);
        extraAndDefaultControl(index);
        extraAndDistControl(index);
        masterControl(true);
    }

    /**
     * For all instance (Create: remove and | Update: cancel and drop)
     * 
     * DELETE METHOD
     * 
     * @param index  row index
     * @param create instance Create
     */
    void removeRow(int index, boolean create) {
        // INDEX ---------------------------
        hbsN.remove(index);
        lbsN.remove(index);
        // COLUMN NAMES ---------------------------------------
        tfsColumn.remove(index);
        btnsRemoveColumn.remove(index);
        btnsAddColumn.remove(index);
        btnsRenameColumn.remove(index);
        hbsName.remove(index);

        columnsPopupsControl.remove(index);
        popupMessageControl.removeAllMessages("COLUMN:" + index);
        // TYPES -------------------------------
        tfsType.remove(index);
        tfsTypePAutoC.remove(index);
        // typePopups.remove(index);
        tfsTypeLength.remove(index);
        btnsChangeType.remove(index);
        hbsType.remove(index);

        typePopupsControl.remove(index);
        popupMessageControl.removeAllMessages("TYPE:" + index);
        // NULLS ----------------------------------------------
        cksNull.remove(index);
        btnsChangeNull.remove(index);
        hbsNull.remove(index);
        // PKS ----------------------------------------------
        rbsPK.remove(index);
        hbsPK.remove(index);

        pkPopupsControl.remove(index);
        popupMessageControl.removeAllMessages("PK:" + index);
        // DEFAULTS ----------------------------------------------
        cksDefault.remove(index);
        tfsDefault.remove(index);
        btnsChangeDefault.remove(index);
        hbsDefault.remove(index);

        defaultPopupsControl.remove(index);
        popupMessageControl.removeAllMessages("DEFAULT:" + index);
        // EXTRA ----------------------------------------------
        rbsExtra.remove(index);
        hbsExtra.remove(index);

        extraPopupsControl.remove(index);
        popupMessageControl.removeAllMessages("EXTRA:" + index);
        // CUSTOM -----------------------------------------------
        btnsDist.remove(index);
        distPopupsControl.remove(index);
        popupMessageControl.removeAllMessages("DIST:" + index);

        btnsTextArea.remove(index);
        textAreaPopupsControl.remove(index);
        popupMessageControl.removeAllMessages("TEXTAREA:" + index);
        // LIST -----------------------------------------------
        listSameColumns.remove(index);
        // ===========================================================
        currentRowLength--;
        resetGridPaneRows();

        restartIds();
        restartFirstUpdateAddButton();
        btnsAddRemoveColumnReset();
        // CONTROL ============================================================
        masterControl(true);

    }

    void removeRowTest(int index, boolean create){
        currentRowLength--;

        VCRow.removeAllMessageFromThisRow(index);
        VCRow.getRows().remove(index);
        //resetGridPaneRowsTest();
    }
    // RESTART------------------------------------------------------
    // DELETE METHOD
    private void restartIds() {
        for (int a = 0; a < currentRowLength; a++) {
            // INDEX -----------------------------------------------
            lbsN.get(a).setText("Column " + (a + 1));
            // COLUMNS ---------------------------------------------
            tfsColumn.get(a).setId(Integer.toString(a));
            listSameColumns.set(a, tfsColumn.get(a).getText());

            btnsRemoveColumn.get(a).setId(Integer.toString(a));
            btnsAddColumn.get(a).setId(Integer.toString(a));
            btnsRenameColumn.get(a).setId(Integer.toString(a));
            // TYPES -----------------------------------------------
            tfsType.get(a).setId("TF-TYPE-" + a);
            tfsTypeLength.get(a).setId(Integer.toString(a));
            btnsChangeType.get(a).setId(Integer.toString(a));
            // NULLS -----------------------------------------------
            cksNull.get(a).setId(Integer.toString(a));
            btnsChangeNull.get(a).setId(Integer.toString(a));
            // PKS -------------------------------------------
            rbsPK.get(a).setId(Integer.toString(a));
            // DEFAULTS --------------------------------------------
            cksDefault.get(a).setId(Integer.toString(a));
            tfsDefault.get(a).setId(Integer.toString(a));
            btnsChangeDefault.get(a).setId(Integer.toString(a));
            // EXTRAS ----------------------------------------------
            rbsExtra.get(a).setId(Integer.toString(a));
            // CUSTOM----------------------------
            btnsDist.get(a).setText(Integer.toString(a + 1));
            btnsDist.get(a).setId(Integer.toString(a));

            btnsTextArea.get(a).setText(Integer.toString(a + 1));
            btnsTextArea.get(a).setId(Integer.toString(a));
        }
    }

    // DELETE METHOD
    private void restartFirstUpdateAddButton() {
        btnsAddColumn.forEach(btn -> {
            btn.setTooltip(null);
            btn.setContextMenu(null);
        });
        btnsAddColumn.get(0).setTooltip(beforeAfterOptionTooltip);
        btnsAddColumn.get(0).setContextMenu(beforeAfterOptionMenu);
    }

    // --------------------------------------------------------------
    // DELETE METHOD
    private void btnsAddRemoveColumnReset() {
        /*
         * for (int a = 0; a < MSQL.MAX_COLUMNS; a++) { if (a < presetRowsLenght - 1) {
         * btnsAddColumn.get(a).setVisible(false);
         * btnsRemoveColumn.get(a).setVisible(false); } }
         */
        btnsRemoveColumn.get(0).setDisable(false);
        if (currentRowLength < 2) {
            btnsRemoveColumn.get(0).setDisable(true);
        }

        btnsAddColumn.forEach(btn -> btn.setDisable(currentRowLength >= MSQL.MAX_COLUMNS));
    }

    // DELETE METHOD
    private void presetSomeInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (a == 0) {
                presetTypeSelected.add(types.getType("INT"));
            } else {
                presetTypeSelected.add(types.getType("CHAR"));
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        popupMessageControl = new PopupMessageControl2(new PopupMessage("MASTER", btnErrorDisplay));
        popupMessageControl.getMessagesList().addAll(VCStore.getErrorsList());
        popupMessageControl.setNodeStyleClass("node-border-error");
        // NODES ====================================================
        // CENTER -------------------------------
        presetSomeInit();
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
        // CENTER---------------------------
        listSameColumns.addListener(listColumnsChangeListener);
        // beforeAfterOptionMenu.addAction(0, e -> updateAddVisible(-1));
        beforeAfterOptionMenu.addAction(0, e -> addRow(0, false));
        // beforeAfterOptionMenu.addAction(1, e -> updateAddVisible(0));
        beforeAfterOptionMenu.addAction(1, e -> addRow(1, false));
        beforeAfterOptionTooltip.setShowDelay(Duration.millis(100));
        // BOTTOM --------------------------------------------
        btnCreateUpdate.managedProperty().bind(btnCreateUpdate.visibleProperty());
        btnCancel.setOnAction(this::btnCancelAction);
        btnCreateUpdate.setOnAction(this::createTableAction);
        btnCreateHelp.setOnAction(this::helpAction);

        btnSelectFK.setOnAction(this::fkSelectAction);
        btnSelectUI.setOnAction(this::uiSelectAction);
        btnSelectIC.setOnAction(this::icSelectAction);

    }

    // GETTERS &
    // SETTERS-------------------------------------------------------------
    public GridPane getGridPane() {
        return gridPane;
    }

    public void setGridPaneLeft(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public List<Label> getLbsN() {
        return lbsN;
    }

    public List<HBox> getHbsName() {
        return hbsName;
    }

    public List<TextField> getTfsColumn() {
        return tfsColumn;
    }

    public List<Button> getBtnsRemoveColumn() {
        return btnsRemoveColumn;
    }

    public List<Button> getBtnsAddColumn() {
        return btnsAddColumn;
    }

    public List<Button> getBtnsRenameColumn() {
        return btnsRenameColumn;
    }

    public List<HBox> getHbsType() {
        return hbsType;
    }

    public List<TextField> getTfasType() {
        return tfsType;
    }

    public List<TextField> getTfsTypeLength() {
        return tfsTypeLength;
    }

    public List<Button> getBtnsChangeType() {
        return btnsChangeType;
    }

    public List<HBox> getHbsNull() {
        return hbsNull;
    }

    public List<CheckBox> getCksNull() {
        return cksNull;
    }

    public List<Button> getBtnsChangeNull() {
        return btnsChangeNull;
    }

    public List<HBox> getHbsPK() {
        return hbsPK;
    }

    public List<RadioButton> getRbsPK() {
        return rbsPK;
    }

    public List<HBox> getHbsDefault() {
        return hbsDefault;
    }

    public List<CheckBox> getCksDefault() {
        return cksDefault;
    }

    public List<TextField> getTfsDefault() {
        return tfsDefault;
    }

    public List<Button> getBtnsChangeDefault() {
        return btnsChangeDefault;
    }

    public List<HBox> getHbsExtra() {
        return hbsExtra;
    }

    public List<RadioButton> getRbsExtra() {
        return rbsExtra;
    }

    public List<ToggleButton> getBtnsDist() {
        return btnsDist;
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

    public List<HBox> getHbsN() {
        return hbsN;
    }

    public Button getBtnSelectIC() {
        return btnSelectIC;
    }

    public void setBtnSelectIC(Button btnSelectImageC) {
        this.btnSelectIC = btnSelectImageC;
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

    public void setBpMain(BorderPane bpMain) {
        this.bpMain = bpMain;
    }

    public String[] getPksReferences() {
        return pksReferences;
    }

    public void setPksReferences(String[] pksReferences) {
        this.pksReferences = pksReferences;
    }

    public boolean isFkSelectionMatch() {
        return fkSelectionMatch;
    }

    public void setFkSelectionMatch(boolean fkSelectionMatch) {
        this.fkSelectionMatch = fkSelectionMatch;
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
