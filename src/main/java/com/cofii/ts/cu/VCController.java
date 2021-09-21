package com.cofii.ts.cu;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.cofii.ts.cu.impl.PopupAction;
import com.cofii.ts.cu.store.VCStore;
import com.cofii.ts.first.VF;
import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
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
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.components.javafx.popup.PopupKV;
import com.cofii2.components.javafx.popup.PopupMenu;
import com.cofii2.components.javafx.popup.PopupMessage;
import com.cofii2.components.javafx.popup.config.PPosition;
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
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
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
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.util.Duration;

public class VCController implements Initializable {

    public static final String LBH_COLUMN_NAMES = "Name";
    public static final String LBH_COLUMN_NAMES_ERROR = "Duplicated names or empty columns";
    public static final Insets INSETS = new Insets(2, 2, 2, 2);

    // -------------------------------------------------
    private int presetRowsLenght = 2;
    private int currentRowLength = 0;
    private List<SQLType> presetTypeSelected = new ArrayList<>(MSQL.MAX_COLUMNS);

    private ObservableList<String> listColumns = FXCollections.observableArrayList();

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
    private HBox headerFKS;
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
    private Label lbhFK;
    @FXML
    private Label lbhExtra;
    @FXML
    private Label lbhDist;
    @FXML
    private Label lbhTextArea;

    @FXML
    private ToggleButton btnErrorDisplay;
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
    // CENTER NODES LISTS
    // =================================================================
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

    private final List<PopupAction> columnsPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // TYPE ------------------------------------------------
    private final List<HBox> hbsType = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<TextField> tfsType = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<PopupAutoC> tfsTypePAutoC = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<TextField> tfsTypeLength = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Button> btnsChangeType = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<PopupAction> typePopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // NULL ------------------------------------------------
    private final List<HBox> hbsNull = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<CheckBox> cksNull = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Button> btnsChangeNull = new ArrayList<>(MSQL.MAX_COLUMNS);
    // PK ---------------------------------------------------
    private final List<HBox> hbsPK = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<RadioButton> rbsPK = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<PopupAction> pkPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // FK --------------------------------------------------
    private final List<HBox> hbsFK = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<TextField> tfsFK = new ArrayList<>(MSQL.MAX_COLUMNS);
    private String[] pksReferences;
    private final List<PopupAutoC> tfsFKPAutoC = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<ToggleButton> btnsSelectedFK = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<PopupAction> fkPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // DEFAULT ---------------------------------------------
    private final List<HBox> hbsDefault = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<CheckBox> cksDefault = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<TextField> tfsDefault = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<Button> btnsChangeDefault = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<PopupAction> defaultPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // EXTRA -----------------------------------------------
    private final List<HBox> hbsExtra = new ArrayList<>(MSQL.MAX_COLUMNS);// -----------
    private final List<RadioButton> rbsExtra = new ArrayList<>(MSQL.MAX_COLUMNS);

    private final List<PopupAction> extraPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // DIST ------------------------------------------------
    private final List<ToggleButton> btnsDist = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<PopupAction> distPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // TEXTAREA --------------------------------------------
    private final List<ToggleButton> btnsTextArea = new ArrayList<>(MSQL.MAX_COLUMNS);
    private final List<PopupAction> textAreaPopupsControl = new ArrayList<>(MSQL.MAX_COLUMNS);
    // BOTTOM ----------------------------------------------
    private final ObservableMap<String, Boolean> createHelpMap = FXCollections.observableHashMap();
    private final PopupKV createHelpPopup = new PopupKV(createHelpMap);

    private final ObservableMap<String, Boolean> updateAddColumnHelpMap = FXCollections.observableHashMap();
    private final PopupKV updateAddColumnHelpPopup = new PopupKV(updateAddColumnHelpMap);
    // ---------------------------------------------
    private VFController vfc;
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
    private final EventHandler<KeyEvent> tfsColumnsKeyReleasedListener = this::tfsColumnsKeyReleased;
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
    // FK ---------------------------------------------
    private final ChangeListener<? super String> fksTextPropertyListener = this::fksTextProperty;
    private final EventHandler<ActionEvent> fksSelectedActionListener = this::fksSelectAction;
    // DEFAULT -----------------------------------------
    private final EventHandler<ActionEvent> defaultsActionListener = this::defaultsAction;
    private final EventHandler<KeyEvent> defaultsKeyReleasedListener = this::defaultsKeyReleased;
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
    // TABLE -----------------------------------------
    private boolean tableOK = false;
    // COLUMN ----------------------------------------
    private boolean columnSNOK = false;
    private boolean columnBWOK = false;
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
    // CONTROL =============================================================
    private void masterControl(boolean rest) {
        if (rest) {
            columnMatchControl(-1);
            typeControl(-1);
            typeLengthControl(-1);
            fkTFSControl(-1, true);
            defaultsControl(-1);

            imageCSelectControl();
        }

        if (!updateControl) {
            createControl();
        }
    }

    private void createControl() {
        createHelpPopupReset();
        boolean allOk = tableOK && columnSNOK && columnBWOK && typeSelectionMatch && typeLengthOK && fkSelectionMatch
                && defaultBW && defaultOK && extraPKOK && extraFKOK && extraDefaultOK && extraDistOK && imageCOK;

        btnCreateUpdate.setDisable(!allOk);
    }

    private void addColumnControl() {
        boolean disable = columnAddOk && typeAddOk && typeLenghtAddOk && defaultAddOk;
        createAddColumnHelpPopupReset();
        btnsAddColumn.get(addIndex).setDisable(!disable);
    }

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

                String wrongTypeLength = VCStore.getWrongLength(typeChar.toLowerCase(),
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

    private void imageCSelectControl() {
        boolean ok = columnSNOK && columnBWOK;
        btnSelectImageC.setDisable(!ok);
    }

    // MIX -----------------------------------------------------------
    /**
     * Shoul be at: extrasAction, pksAction and addRemoveColumns MESSAGE:
     * AUTO_INCREMENT (extra) has to be a Primary Key
     */
    private void extraAndPKControl(int index) {
        extraPKOK = true;
        int[] indexs = { 0 };
        rbsExtra.forEach(ex -> {
            if (ex.isSelected()) {
                if (!rbsPK.get(indexs[0]).isSelected()) {
                    extraPKOK = false;
                }
            }
            indexs[0]++;
        });
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
        int[] indexs = { 0 };
        rbsExtra.forEach(ex -> {
            if (ex.isSelected() && btnsSelectedFK.get(indexs[0]).isSelected()) {
                extraFKOK = false;
            }
            indexs[0]++;
        });
        // DISPLAY ------------------------------------------
        if (extraFKOK) {
            fkPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_FK);
            extraPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_FK);
        } else {
            fkPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_FK);
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
        int[] indexs = { 0 };
        rbsExtra.forEach(ex -> {
            if (ex.isSelected() && cksDefault.get(indexs[0]).isSelected()) {
                extraDefaultOK = false;
            }
            indexs[0]++;
        });
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
        int[] indexs = { 0 };
        rbsExtra.forEach(ex -> {
            if (ex.isSelected() && btnsDist.get(indexs[0]).isSelected()) {
                extraDistOK = false;
            }
            indexs[0]++;
        });
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
            masterControl(false);
        }

    }

    private void tfTableUpdate(String text) {
        if (updateControl) {
            if (tableOK) {
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
        return matcher.matches() && MSQL.BAND_COLUMNS_NAMES.stream().noneMatch(word -> textt.equals(word));
    }

    private void tfsColumnsKeyReleased(KeyEvent e) {
        setQOLVariables(e);

        String text = tfsColumn.get(index).getText().toLowerCase().trim().replace(" ", "_");
        listColumns.set(index, text);

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
        tfsColumnUpdate(index, text);
        // ---------------------------------------------------------
        masterControl(false);
        imageCSelectControl();
    }

    private void listColumnsChange(Change<? extends String> c) {
        while (c.next()) {
            columnSameControl();
        }
    }

    private void columnSameControl() {
        if (MList.areTheyDuplicatedElementsOnList(listColumns)) {
            lbhColumnNames.setText(LBH_COLUMN_NAMES_ERROR);
            headerColumns.setStyle(CSS.NODE_TEXTFILL_ERROR);
            columnSNOK = false;
        } else {
            lbhColumnNames.setText(LBH_COLUMN_NAMES);
            headerColumns.setStyle(null);
            columnSNOK = true;

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

    private void tfsColumnUpdate(int index, String text) {
        if (updateControl) {
            boolean ok = false;
            if (columnBWOK && columnSNOK) {
                if (!text.equals(column)) {
                    // tfsColumnPopups[index].hide();
                    tfsColumn.get(index).setStyle(null);
                    ok = true;
                } else {
                    // tfsColumnPopups[index].show(SAME_VALUE);
                    tfsColumn.get(index).setStyle(CSS.NODE_TEXTFILL_HINT);
                    ok = false;

                }
            } else {
                ok = false;

            }
            if (!columnAdd) {
                btnsRenameColumn.get(index).setDisable(!ok);
            } else {
                columnAddOk = ok;
                addColumnControl();
            }
        }
    }

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
            lbStatus.setText("Column '" + column + "' changed to '" + newColumn + "'", NonCSS.TEXT_FILL_OK,
                    Duration.seconds(2));
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to be renamed", NonCSS.TEXT_FILL_ERROR);
        }
    }

    /*
     * // COLUMNS POPUP------------------------------------------- private void
     * columnsPopupsChange(javafx.collections.SetChangeListener.Change<? extends
     * String> c) { // while (c.next()) {
     * System.out.println("\nTEST columnsPopupsChange"); String element =
     * c.getSet().stream().filter(item ->
     * item.contains("id-")).collect(Collectors.toList()).get(0); index =
     * Integer.parseInt(element.substring(3, element.length()));
     * System.out.println("index [" + index + "]");
     * 
     * if (!columnsPopupsControl.get(index).getVbox().getChildren().isEmpty()) {
     * columnPopups.get(index).getItemList().forEach(s -> System.out.println("\ti: "
     * + s)); hbsName.get(index).setStyle(CSS.NODE_BORDER_ERROR); } else {
     * System.out.println("\tempty"); hbsName.get(index).setStyle(null); } // } }
     */
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

    /**
     * Query update for Adding a column
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

            lbStatus.setText("Added column '" + column + "' to '" + currentTable.getName() + "'", NonCSS.TEXT_FILL_OK,
                    Duration.seconds(4));
            System.out.println("\tSUCCES");
        } else {
            lbStatus.setText("Couldn't add column '" + column + "'", NonCSS.TEXT_FILL_ERROR);
            System.out.println("\tFAILED");
        }

    }

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
                if (currentTable.getColumns().get(index).getDist()) {
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
        btnsSelectedFK.get(index).setDisable(true);
        rbsExtra.get(index).setDisable(true);
        // BOTTOM ----------------------------------
        btnUpdatePK.setDisable(true);
        btnUpdateFK.setDisable(true);
        btnUpdateExtra.setDisable(true);
        btnUpdateDist.setDisable(true);
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
                    hbsFK.get(a).setDisable(true);
                    hbsDefault.get(a).setDisable(true);
                    hbsExtra.get(a).setDisable(true);

                    btnsDist.get(a).setDisable(true);
                    btnsTextArea.get(a).setDisable(true);
                }
            }
        }
    }

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
        btnsSelectedFK.get(index).setDisable(false);
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
            hbsFK.get(a).setDisable(false);
            hbsDefault.get(a).setDisable(false);
            hbsExtra.get(a).setDisable(false);

            btnsDist.get(a).setDisable(false);
            btnsTextArea.get(a).setDisable(false);
        }
        // BOTTOM -----------------------------------------
        btnUpdatePK.setDisable(false);
        btnUpdateFK.setDisable(false);
        btnUpdateExtra.setDisable(false);
        btnUpdateDist.setDisable(false);
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
                    typePopupsControl.get(index).getItemList().add(VCStore.getWrongLength(typeMaxLength) + "%length");
                    typeLengthOK = false;
                }
            } else {
                typePopupsControl.get(index).getItemList().add(VCStore.getWrongLength(typeMaxLength) + "%length");
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

            lbStatus.setText("Column '" + column + "' has change it's type to '" + type + "'", NonCSS.TEXT_FILL_OK,
                    Duration.seconds(2));

            btnsChangeType.get(index).setDisable(true);
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to change it's type", NonCSS.TEXT_FILL_ERROR);
        }

    }

    // NULLS======================================================
    /**
     * Only on update mode
     */
    void nullsAction(ActionEvent e) {
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

            lbStatus.setText("Column '" + column + "' change to " + (nulll ? "NULL" : "NOT NULL"), NonCSS.TEXT_FILL_OK,
                    Duration.seconds(2));
        } else {
            System.out.println("\tFAILED");
            lbStatus.setText("Column '" + column + "' fail to be changed", NonCSS.TEXT_FILL_ERROR);
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
                    // if (!rb.isSelected() != updateTable.getPks().get(indexs[0]).equals("Yes")) {
                    if (!rb.isSelected() != currentTable.getColumns().get(indexs[0]).isPk()) {
                        // updateTable.getPks().set(indexs[0], "No");
                        currentTable.getColumns().get(indexs[0]).setPk(false);
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
    private void fksTextProperty(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        index = Integer.parseInt(tf.getId());

        if (tf.isVisible()) {
            if (MList.isOnThisList(tfsFKPAutoC.get(index).getLv().getItems(), newValue, false)) {
                fkPopupsControl.get(index).getItemList().remove(VCStore.SELECTION_UNMATCH);

                fkSelectionMatch = true;
                fkTFSControl(index, true);
            } else {
                fkPopupsControl.get(index).getItemList().add(VCStore.SELECTION_UNMATCH);

                fkSelectionMatch = false;
            }
        } else {
            fkPopupsControl.get(index).getItemList().remove(VCStore.SELECTION_UNMATCH);

            fkSelectionMatch = true;
            fkTFSControl(index, true);
        }

        // UPDATE----------------------------------------------
        // fkUpdateControl(index, false);
        fkControl();
        extraAndFKControl(index);
        // ----------------------------------------------
        masterControl(false);
    }

    private void fkTFSControl(int index, boolean all) {
        fkSelectionMatch = true;
        for (int a = 0; a < currentRowLength; a++) {
            if ((a != index && tfsFK.get(a).isVisible() && all) || (a == index && !all)) {
                String text = tfsFK.get(a).getText();
                if (!MList.isOnThisList(tfsFKPAutoC.get(a).getLv().getItems(), text, false)) {
                    fkSelectionMatch = false;
                    break;
                }

            }
        }
    }

    private void fkControl() {
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

    void fksSelectAction(ActionEvent e) {
        setQOLVariables(e);

        if (!btnsSelectedFK.get(index).getText().contains("REM")) {
            if (btnsSelectedFK.get(index).isSelected()) {
                tfsFK.get(index).setVisible(true);
            } else {
                tfsFK.get(index).setVisible(false);
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
                    // if (!updateControl ? true : updateTable.getFks().get(indexs[0]).equals("No")
                    // && !actionRem[0]) {
                    if (!updateControl ? true : currentTable.getColumns().get(indexs[0]).isFk() && !actionRem[0]) {
                        actionAdd[0] = true;
                        if (scount[0] == 1) {
                            btn.setText("ADD");
                        } else if (scount[0] > 1) {
                            // btn.setText("ADD (A)");
                            btnsSelectedFK.stream().filter(btnn -> btnn.isSelected())
                                    .forEach(btnn -> btnn.setText("ADD (A)"));
                        }
                    } else if (!updateControl ? false
                            : currentTable.getColumns().get(indexs[0]).isFk() && !actionAdd[0]) {
                        actionRem[0] = true;
                        if (!btn.getText().equals("REM (A)")) {
                            btn.setText("REM");
                        }
                    }
                } else {
                    if (!updateControl ? true : !currentTable.getColumns().get(indexs[0]).isFk()) {
                        btn.setText("ADD");
                    }
                }
            }
        });

        // ----------------------------------------
        // btnUpdateFK.setDisable(!(fkUpdate &&
        // Arrays.asList(btnsSelectedFK).stream().anyMatch(btn -> btn.isSelected())));
        fkTFSControl(-1, true);
        // UPDATE------------------------------------------------
        // fkUpdateControl(index, true);
        fkControl();
        extraAndFKControl(index);
        // ------------------------------------------------
        masterControl(false);
    }

    void updateFKS(ActionEvent e) {
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
        if (currentTable.getColumns().get(indexs[0]++).isFk()) {// DROP FOREIGN KEY
            String constraint = Users.getInstance().getCurrenUser().getConstraintName(currentDatabse.getName(),
                    currentTable.getName(), indexs[0]++);
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
                    // cols.add(updateTable.getColumns().get(indexs[0]).replace(" ", "_"));
                    cols.add(currentTable.getColumns().get(indexs[0]).getName());
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
                        // updateTable.getFks().set(i, "Yes");
                        currentTable.getColumns().get(i).setFk(true);
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
                    // updateTable.getFks().set(i, "No");
                    currentTable.getColumns().get(i).setFk(false);
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

    // DEFAULTS =================================================
    private void defaultsAction(ActionEvent e) {
        setQOLVariables(e);

        if (cksDefault.get(index).isSelected()) {
            tfsDefault.get(index).setVisible(true);
            extraDefaultOK = !rbsExtra.get(index).isSelected();
            if (!extraDefaultOK) {
                defaultPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_DEFAULT);
            } else {
                defaultPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_DEFAULT);
            }
        } else {
            tfsDefault.get(index).setVisible(false);
            extraDefaultOK = true;
            defaultPopupsControl.get(index).getItemList().remove(VCStore.AUTO_INCREMENT_AND_DEFAULT);
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

    private void defaultsKeyReleased(KeyEvent e) {
        setQOLVariables(e);

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
                    NonCSS.TEXT_FILL_OK, Duration.seconds(2));
        } else {
            lbStatus.setText("Failt to change Default Value of column '" + column + "'", NonCSS.TEXT_FILL_ERROR);
        }

    }

    // EXTRA =================================================
    void extrasAction(ActionEvent e) {
        setQOLVariables(e);
        // ---------------------------------------------
        // rbsExtraPopups.forEach(Popup::hide);
        // ---------------------------------------------
        if (rbsExtra.get(index).isSelected()) {
            // ---------------------------------------------
            if (cksDefault.get(index).isSelected()) {
                // lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
                extraPopupsControl.get(index).getItemList().add(VCStore.AUTO_INCREMENT_AND_DEFAULT);

                extraDefaultOK = false;
            } else {
                extraDefaultOK = true;
            }
            // ---------------------------------------------
        } else {
            extraDefaultOK = true;
        }

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

    private void extrasRestControl(int index){
        for(int a = 0;a < currentRowLength; a++){
            if(a != index){
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
        ms.setNullValue(extra ? currentTable.getColumns().get(indexs[0]).getNulll() : false);
        ms.setExtraValue(extra);
        boolean updateExtra = ms.changeType(tableName, column, type);
        if (updateExtra) {
            System.out.println(indexs[0]);
            // updateTable.setExtra(extra ? indexs[0] : -1);
            currentTable.setExtra(extra ? indexs[0] : -1);

            btnUpdateExtra.setDisable(true);

            lbStatus.setText("Changed Extra", NonCSS.TEXT_FILL_OK, Duration.seconds(2));
        } else {
            lbStatus.setText("Fail to change Extra", NonCSS.TEXT_FILL_ERROR);
        }
    }

    // BOTTOM ===================================================
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
                    boolean dist = currentTable.getColumns().get(a).getDist();

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

            lbStatus.setText("Dist has change to '" + dist + "'", NonCSS.TEXT_FILL_OK, Duration.seconds(2));
            System.out.println("\tSUCCESS");
        } else {
            lbStatus.setText("Dist fail to changed", NonCSS.TEXT_FILL_ERROR);
            System.out.println("\tFAILED");

        }
    }

    // TEXTAREA ----------------------------------------------
    private void textAreasAction(ActionEvent e) {
        // CONTROL ONLY TYPE TEXT-BASE
    }

    // IMAGEC -----------------------------------------------
    private void imageCSelectAction(ActionEvent e) {
        VImageC vImageC = VImageC.getInstance(this, !updateControl);
        vicc = vImageC.getVicc();
    }

    // MAIN CONFIG ==================================================
    // MAP HELPS----------------------------------------------------
    void createHelpPopupReset() {
        createHelpMap.put("Table Name", tableOK);
        createHelpMap.put("Columns Names", columnSNOK && columnBWOK);
        createHelpMap.put("Types", typeSelectionMatch && typeLengthOK);
        createHelpMap.put("Foreign Keys", fkSelectionMatch);
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
        columnsPopupsControl.add(index, new PopupAction(new PopupMessage(index, hbsName.get(index), false)));
        hbsName.get(index).setPadding(new Insets(0, 2, 0, 2));
        // TYPES----------------------------------------------
        // tfasType.get(a) = new TextFieldAutoC(a, types.getTypeNames());
        tfsType.add(index, new TextField());
        tfsTypePAutoC.add(index, new PopupAutoC(tfsType.get(index), types.getTypeNames()));

        tfsTypeLength.add(index, new TextField());

        btnsChangeType.add(index, new Button("C"));

        hbsType.add(index, new HBox(tfsType.get(index), tfsTypeLength.get(index), btnsChangeType.get(index)));
        // typePopups.add(index, new PopupMessage(index, hbsType.get(index)));
        typePopupsControl.add(index, new PopupAction(new PopupMessage(index, hbsType.get(index), false)));
        hbsType.get(index).setPadding(new Insets(0, 2, 0, 2));
        // NULLS----------------------------------------------
        cksNull.add(index, new CheckBox());
        btnsChangeNull.add(index, new Button("C"));
        hbsNull.add(index, new HBox(cksNull.get(index), btnsChangeNull.get(index)));
        hbsNull.get(index).setPadding(new Insets(0, 2, 0, 2));
        // PKS----------------------------------------------
        rbsPK.add(index, new RadioButton());
        // btnsChangePK.get(a) = new Button("C");
        hbsPK.add(index, new HBox(rbsPK.get(index)));
        pkPopupsControl.add(index, new PopupAction(new PopupMessage(index, hbsPK.get(index), false)));
        // FKS----------------------------------------------
        tfsFK.add(index, new TextField());
        tfsFKPAutoC.add(index, new PopupAutoC(tfsFK.get(index)));

        btnsSelectedFK.add(index, new ToggleButton("ADD"));
        // btnsSelectedFKPopups.add(index, new PopupMessage(index,
        // btnsSelectedFK.get(index)));

        hbsFK.add(index, new HBox(tfsFK.get(index), btnsSelectedFK.get(index)));
        fkPopupsControl.add(index, new PopupAction(new PopupMessage(index, hbsFK.get(index), false)));
        // DEFAULTS----------------------------------------------
        cksDefault.add(index, new CheckBox());
        tfsDefault.add(index, new TextField());
        btnsChangeDefault.add(index, new Button("C"));

        hbsDefault.add(index, new HBox(cksDefault.get(index), tfsDefault.get(index), btnsChangeDefault.get(index)));
        defaultPopupsControl.add(index, new PopupAction(new PopupMessage(index, hbsDefault.get(index), false)));
        hbsDefault.get(index).setPadding(new Insets(0, 2, 0, 2));
        // EXTRA----------------------------------------------
        rbsExtra.add(index, new RadioButton());
        hbsExtra.add(index, new HBox(rbsExtra.get(index)/* , btnsChangeExtra.get(a) */));
        extraPopupsControl.add(index, new PopupAction(new PopupMessage(index, hbsExtra.get(index), false)));
        // DIST----------------------
        btnsDist.add(index, new ToggleButton("" + (index + 1)));
        distPopupsControl.add(index, new PopupAction(new PopupMessage(index, btnsDist.get(index), false)));
        // TEXT AREA--------------------------------------
        btnsTextArea.add(index, new ToggleButton("" + (index + 1)));
        // btnsTextAreaPopups.add(new PopupMessage(index, btnsTextArea.get(index)));
        textAreaPopupsControl.add(new PopupAction(new PopupMessage(index, btnsTextArea.get(index), false)));
    }

    private void rowConfig(int index) {
        // ADDING NEW INDEXS OR
        // ALL---------------------------------------------------------
        int forSize = MSQL.MAX_COLUMNS;
        if (updateControl) {
            forSize = index == -1 ? currentTable.getColumns().size() : currentRowLength;
        }
        /*
         * int forSize2 = forSize; if (index >= 0) { forSize = index + 1; }
         * 
         * int indexu = index == -1 ? 0 : index;
         */
        rowInits(index);
        // PROMPT TEXT ----------------------------------------------
        tfsColumn.get(index).setPromptText("Column name required");
        tfsDefault.get(index).setPromptText("Value Required");
        tfsFK.get(index).setPromptText("NO FOREING KEY");
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
        // KEY -------------------------------
        hbsPK.get(index).getStyleClass().add("vc-row");
        hbsFK.get(index).getStyleClass().add("vc-row");
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
        hbsFK.get(index).setPrefWidth(-1);
        tfsFK.get(index).setPrefWidth(-1);

        btnsDist.get(index).setMinWidth(40);
        btnsDist.get(index).setMaxWidth(40);
        btnsTextArea.get(index).setMinWidth(40);
        btnsTextArea.get(index).setMaxWidth(40);

        // VISIBLE PROPERTY ------------------------------------
        btnsRenameColumn.get(index).managedProperty().bind(btnsRenameColumn.get(index).visibleProperty());
        tfsTypeLength.get(index).managedProperty().bind(tfsTypeLength.get(index).visibleProperty());
        btnsChangeType.get(index).managedProperty().bind(btnsChangeType.get(index).visibleProperty());
        btnsChangeNull.get(index).managedProperty().bind(btnsChangeNull.get(index).visibleProperty());
        // btnsChangePK.get(index).managedProperty().bind(btnsChangePK.get(index).visibleProperty());
        tfsFK.get(index).managedProperty().bind(tfsFK.get(index).visibleProperty());
        btnsSelectedFK.get(index).managedProperty().bind(btnsSelectedFK.get(index).visibleProperty());
        tfsDefault.get(index).managedProperty().bind(tfsDefault.get(index).visibleProperty());
        btnsChangeDefault.get(index).managedProperty().bind(btnsChangeDefault.get(index).visibleProperty());

        btnsDist.get(index).managedProperty().bind(btnsDist.get(index).visibleProperty());
        btnsTextArea.get(index).managedProperty().bind(btnsTextArea.get(index).visibleProperty());
        // VISIBILITY----------------------------------
        tfsFK.get(index).setVisible(false);
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
        // MARGIN----------------------------------------------
        GridPane.setMargin(hbsN.get(index), INSETS);
        GridPane.setMargin(hbsName.get(index), INSETS);
        GridPane.setMargin(hbsType.get(index), INSETS);
        GridPane.setMargin(hbsNull.get(index), INSETS);
        GridPane.setMargin(hbsPK.get(index), INSETS);
        GridPane.setMargin(hbsFK.get(index), INSETS);
        GridPane.setMargin(hbsDefault.get(index), INSETS);
        GridPane.setMargin(hbsExtra.get(index), INSETS);

        GridPane.setMargin(btnsDist.get(index), INSETS);
        GridPane.setMargin(btnsTextArea.get(index), INSETS);

        new ToggleGroupD<>(rbsExtra.toArray(new RadioButton[rbsExtra.size()]));

    }

    /**
     * Add the listener for a new row. Should happen one time for each row
     */
    private void newRowListeners(int index) {
        tfsColumn.get(index).setOnKeyReleased(tfsColumnsKeyReleasedListener);

        tfsType.get(index).textProperty().addListener(tfsTypeTextPropertyListener);
        tfsTypeLength.get(index).textProperty().addListener(tfsTypeLengthTextPropertyListener);

        rbsPK.get(index).setOnAction(pksActionListener);

        btnsSelectedFK.get(index).setOnAction(fksSelectedActionListener);
        tfsFK.get(index).textProperty().addListener(fksTextPropertyListener);

        cksDefault.get(index).setOnAction(defaultsActionListener);
        tfsDefault.get(index).setOnKeyReleased(defaultsKeyReleasedListener);

        rbsExtra.get(index).addEventHandler(ActionEvent.ACTION, extrasActionListener);
        // CUSTOM----------------------
        btnsDist.get(index).setOnAction(distsActionListener);
        btnsTextArea.get(index).setOnAction(textAreasActionListener);

    }

    void newRowCreateListeners(int index) {
        btnsRemoveColumn.get(index).setOnAction(this::removeColumnCreateAction);
        btnsAddColumn.get(index).setOnAction(this::addColumnCreateAction);

        btnsRenameColumn.get(index).setVisible(false);
        btnsChangeType.get(index).setVisible(false);
        btnsChangeNull.get(index).setVisible(false);
        btnsChangeDefault.get(index).setVisible(false);
    }

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

    private void addRowsToGridPane() {
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
            gridPane.add(hbsFK.get(a), 5, row);
            gridPane.add(hbsDefault.get(a), 6, row);
            gridPane.add(hbsExtra.get(a), 7, row);

            gridPane.add(btnsDist.get(a), 8, row);
            gridPane.add(btnsTextArea.get(a), 9, row);
        }
    }

    void addRow(int index, boolean create) {
        rowConfig(index);
        newRowListeners(index);
        // ------------------------------------------
        if (create) {
            /*
             * if (index > 0) { btnsAddColumn.get(index - 1).setVisible(false);
             * btnsRemoveColumn.get(index - 1).setVisible(false); }
             * btnsAddColumn.get(index).setVisible(true);
             * btnsRemoveColumn.get(index).setVisible(true);
             */
            newRowCreateListeners(index);
        } else {
            newRowUpdateListeners(index);
        }
        // -------------------------------------------
        // ------------------------------------------
        currentRowLength++;
        addRowsToGridPane();

        listColumns.add("");

        restartIds();
        restartFirstUpdateAddButton();
        btnsAddRemoveColumnReset();

        tfsFKPAutoC.get(index).addAllItems(pksReferences);
        tfsFKPAutoC.get(index).getLv().getSelectionModel().select(0);
        // ---------------------------------------------
        extraAndPKControl(index);
        extraAndFKControl(index);
        extraAndDefaultControl(index);
        extraAndDistControl(index);
        masterControl(true);
    }

    void removeRow(int index, boolean create) {
        if (create) {
            // btnsAddColumn.get(index - 1).setVisible(true);
            // btnsRemoveColumn.get(index - 1).setVisible(true);
        }
        // REMOVE ==============================================
        /*
         * gridPane.getChildren().removeAll(hbsN.get(index), hbsName.get(index),
         * hbsType.get(index), hbsNull.get(index), hbsPK.get(index), hbsFK.get(index),
         * hbsDefault.get(index), hbsExtra.get(index), btnsDist.get(index),
         * btnsTextArea.get(index));
         */
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
        // TYPES -------------------------------
        tfsType.remove(index);
        tfsTypePAutoC.remove(index);
        // typePopups.remove(index);
        tfsTypeLength.remove(index);
        btnsChangeType.remove(index);
        hbsType.remove(index);

        typePopupsControl.remove(index);
        // NULLS ----------------------------------------------
        cksNull.remove(index);
        btnsChangeNull.remove(index);
        hbsNull.remove(index);
        // PKS ----------------------------------------------
        rbsPK.remove(index);
        hbsPK.remove(index);

        pkPopupsControl.remove(index);
        // FKS ----------------------------------------------
        tfsFK.remove(index);
        tfsFKPAutoC.remove(index);
        btnsSelectedFK.remove(index);
        hbsFK.remove(index);

        fkPopupsControl.remove(index);
        // DEFAULTS ----------------------------------------------
        cksDefault.remove(index);
        tfsDefault.remove(index);
        btnsChangeDefault.remove(index);
        hbsDefault.remove(index);

        defaultPopupsControl.remove(index);
        // EXTRA ----------------------------------------------
        rbsExtra.remove(index);
        hbsExtra.remove(index);

        extraPopupsControl.remove(index);
        // CUSTOM -----------------------------------------------
        btnsDist.remove(index);
        distPopupsControl.remove(index);

        btnsTextArea.remove(index);
        textAreaPopupsControl.remove(index);
        // LIST -----------------------------------------------
        listColumns.remove(index);
        // ===========================================================
        currentRowLength--;
        addRowsToGridPane();

        restartIds();
        restartFirstUpdateAddButton();
        btnsAddRemoveColumnReset();
        // CONTROL ============================================================
        masterControl(true);

    }

    // RESTART------------------------------------------------------
    private void restartIds() {
        for (int a = 0; a < currentRowLength; a++) {
            // INDEX -----------------------------------------------
            lbsN.get(a).setText("Column " + (a + 1));
            // COLUMNS ---------------------------------------------
            tfsColumn.get(a).setId(Integer.toString(a));
            listColumns.set(a, tfsColumn.get(a).getText());

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
            // PKS & FKS -------------------------------------------
            rbsPK.get(a).setId(Integer.toString(a));
            tfsFK.get(a).setId(Integer.toString(a));
            btnsSelectedFK.get(a).setId(Integer.toString(a));
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

    private void restartFirstUpdateAddButton() {
        btnsAddColumn.forEach(btn -> {
            btn.setTooltip(null);
            btn.setContextMenu(null);
        });
        btnsAddColumn.get(0).setTooltip(beforeAfterOptionTooltip);
        btnsAddColumn.get(0).setContextMenu(beforeAfterOptionMenu);
    }

    // --------------------------------------------------------------
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

    /*
     * void pesetListInit(int rowLength) { for (int a = 0; a < rowLength; a++) {
     * listColumns.add(tfsColumn.get(a).getText().trim()); listImageC.add(false); }
     * }
     */

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
        // NODES------------------------
        presetSomeInit();
        fkReferencesInit();
        // IntStream.range(0, currentRowLength).forEach(this::addRow);
        // HEADERS STYLING -------------------------------------
        headerId.getStyleClass().add("vi-header");
        headerColumns.getStyleClass().add("vi-header");
        headerTypes.getStyleClass().add("vi-header");
        headerNulls.getStyleClass().add("vi-header");
        headerPKS.getStyleClass().add("vi-header");
        headerFKS.getStyleClass().add("vi-header");
        headerDefaults.getStyleClass().add("vi-header");
        headerExtras.getStyleClass().add("vi-header");
        headerDists.getStyleClass().add("vi-header");
        headerTextAreas.getStyleClass().add("vi-header");
        // TOP-----------------------------
        tfTable.setPromptText("Table name required");
        btnRenameTable.managedProperty().bind(btnRenameTable.visibleProperty());
        btnRenameTable.setDisable(true);
        // CENTER---------------------------
        scGridPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        gridPane.minWidthProperty().bind(scGridPane.widthProperty());

        PopupMessage popupMaster = new PopupMessage(-1, btnErrorDisplay);
        popupMaster.setPopupPosition(PPosition.TOP);
        btnErrorDisplay.selectedProperty().bind(popupMaster.showingProperty());
        btnErrorDisplay.setOnAction(e -> {
            if(btnErrorDisplay.isSelected()){
                //popupMaster.hide();
            }else{
                //popupMaster.showPopup();
            }
        });
        PopupAction.setPopupMaster(popupMaster);
        // BOTTOM--------------------------
        lbUpdate.setDisable(true);
        lbStatus.setStyle(CSS.LB_STATUS);
        lbStatus.getBtnCloseStatus().setStyle(CSS.LB_STATUS_BUTTON);
        HBox.setHgrow(lbStatus, Priority.ALWAYS);
        hbStatus.getChildren().add(0, lbStatus);
        // LISTENERS ===================================================
        // TOP-----------------------------------------------
        tfTable.setOnKeyReleased(this::tfTableKeyReleased);
        // CENTER---------------------------
        listColumns.addListener(listColumnsChangeListener);
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

        btnSelectImageC.setOnAction(this::imageCSelectAction);

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

    public List<HBox> getHbsFK() {
        return hbsFK;
    }

    public List<TextField> getTfasFK() {
        return tfsFK;
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

    public List<ToggleButton> getBtnsSelectedFK() {
        return btnsSelectedFK;
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

    public void setBpMain(BorderPane bpMain) {
        this.bpMain = bpMain;
    }

}
