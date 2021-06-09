package com.cofii.ts.cu;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cofii.ts.first.Menus;
import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.other.Timers;
import com.cofii.ts.sql.CurrenConnection;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.Key;
import com.cofii.ts.store.Keys;
import com.cofii.ts.store.SQLType;
import com.cofii.ts.store.SQLTypes;
import com.cofii.ts.store.TableS;
import com.cofii.ts.store.UpdateTable;
import com.cofii2.components.javafx.PopupAutoC;
import com.cofii2.components.javafx.PopupKV;
import com.cofii2.components.javafx.PopupMessage;
import com.cofii2.components.javafx.ToggleGroupD;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
    private CheckBox[] cksPK = new CheckBox[MSQL.MAX_COLUMNS];
    private PopupMessage[] cksPKPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private Button[] btnsChangePK = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsFK = new HBox[MSQL.MAX_COLUMNS];// -----------
    private CheckBox[] cksFK = new CheckBox[MSQL.MAX_COLUMNS];
    // private TextFieldAutoC[] tfasFK = new TextFieldAutoC[MSQL.MAX_COLUMNS];
    private PopupMessage[] cksFKPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private TextField[] tfasFK = new TextField[MSQL.MAX_COLUMNS];
    private PopupAutoC[] tfsFKPs = new PopupAutoC[MSQL.MAX_COLUMNS];
    private PopupMessage[] tfsFKPopups = new PopupMessage[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeFK = new Button[MSQL.MAX_COLUMNS];
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
    private Keys keys = Keys.getInstance();
    private Timers timers = Timers.getInstance(vf);
    private UpdateTable updateTable;

    private Pattern patternBWTC = Pattern.compile("[A-Za-z]\\w*");
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

    private void tfasFKControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {
                if (tfasFK[a].isVisible()) {
                    String text = tfasFK[a].getText();
                    if (!MList.isOnThisList(tfsFKPs[a].getLv().getItems(), text, false)) {
                        fkSelectionMatch = false;
                        break;
                    }
                }
            }
        }
    }

    private boolean tfsDefaultControl(int index) {
        boolean single = true;
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {
                if (tfsDefault[a].isVisible()) {
                    String text = tfsDefault[a].getText();
                    matcher = patternBWTC.matcher(text);
                    if (!matcher.matches()) {
                        defaultBW = false;
                        break;
                    }
                }

            } else if (updateControl) {
                if (tfsDefault[a].isVisible()) {
                    String text = tfsDefault[a].getText();
                    matcher = patternBWTC.matcher(text);
                    if (!matcher.matches()) {
                        single = false;
                        break;
                    }
                }
            }
        }

        return single;
    }

    private void btnsDistControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {
                ToggleButton btn = btnsDist[a];
                if (btn.isSelected()) {
                    if (rbsExtra[a].isSelected()) {
                        distExtraOK = false;
                        break;
                    }
                }
            }
        }
    }

    private void fkUpdateControl(int index) {
        if (updateControl) {
            if (fkSelectionMatch) {
                boolean update = false;
                // DEPENDS ON TFS-FK TO
                for (int c = 0; c < currentRowLength; c++) {
                    boolean fkSelected = updateTable.getFks()[c] != null;
                    String fkText = updateTable.getFkFormed()[c];

                    if (cksFK[c].isSelected() != fkSelected
                            || (cksFK[c].isSelected() && !tfasFK[c].getText().equals(fkText))) {
                        update = true;
                        break;
                    }
                }
                if (update) {
                    btnUpdateFK.setDisable(false);
                    cksFKPopups[index].hide();
                } else {
                    btnUpdateFK.setDisable(true);
                    cksFKPopups[index].show(SAME_VALUE);
                }
            } else {
                btnUpdateFK.setDisable(true);
            }
        }
    }

    private void defaultUpdateControl(int index, boolean update) {
        if (updateControl) {
            if (update) {
                boolean defaultSelected = updateTable.getDefaults()[index] != null;
                String defaultValue = updateTable.getDefaults()[index];
                if (cksDefault[index].isSelected() != defaultSelected
                        || (cksDefault[index].isSelected() && !tfsDefault[index].getText().equals(defaultValue))) {
                    btnsChangeDefault[index].setDisable(false);
                } else {
                    cksDefaultPopups[index].show(SAME_VALUE);
                    btnsChangeDefault[index].setDisable(true);
                }
            } else {
                btnsChangeDefault[index].setDisable(true);
            }
        }
    }

    // LISTENERS---------------------------------------------
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
            if (updateControl && tableOK) {
                String tableO = updateTable.getTable().toLowerCase().trim().replace(" ", "_");
                if (!text.equals(tableO)) {
                    lbTable.setText("Table Name");
                    lbTable.setTextFill(NonCSS.TEXT_FILL);

                    btnRenameTable.setDisable(true);
                    // tableOK = true;
                } else {
                    lbTable.setText("Same Value");
                    lbTable.setTextFill(NonCSS.TEXT_FILL_HINT);

                    btnRenameTable.setDisable(false);
                    // tableOK = false;
                }
            }

            masterControl();
        }

    }

    private void tfsColumnsKeyReleased(KeyEvent e) {
        TextField tf = (TextField) e.getSource();
        int index = Integer.parseInt(tf.getId());

        boolean update = false;
        String text = tf.getText().toLowerCase().trim().replace(" ", "_");
        listColumns.set(index, text);

        if (!text.trim().isEmpty()) {
            matcher = patternBWTC.matcher(text);
            if (matcher.matches()) {
                tf.setStyle(CSS.TEXT_FILL);
                // popupHide(tf);
                tfsColumnPopups[index].hide();

                columnBWOK = true;// REST CONTROL
                update = true;
                tfsColumnControl(index);
            } else {
                tf.setStyle(CSS.TEXT_FILL_ERROR);
                // popupShow(tf, ILLEGAL_CHARS);
                tfsColumnPopups[index].show(ILLEGAL_CHARS);
                columnBWOK = false;
            }
        } else {
            tfsColumnPopups[index].show(EMPTY_TEXT);
            columnBWOK = false;
        }
        // UPDATE---------------------------------------------------
        if (updateControl) {
            if (update) {
                String column = updateTable.getColumns()[index].toLowerCase().trim().replace(" ", "_");
                if (!text.equals(column)) {
                    tfsColumnPopups[index].show(SAME_VALUE);
                    btnsRenameColumn[index].setDisable(false);
                } else {
                    tfsColumnPopups[index].hide();
                    btnsRenameColumn[index].setDisable(true);
                }
            } else {
                btnsRenameColumn[index].setDisable(true);
            }
        }
        masterControl();
    }

    // CONTROL
    private void listColumnsChange(Change<? extends String> c) {
        System.out.println("\tlistColumnsChange");

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

    // ----
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

    void btnsAddUpdateAction(ActionEvent e) {

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

    void btnsRemoveUpdateAction(ActionEvent e) {

    }

    // ----
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
        if (updateControl) {
            if (update) {
                String type = updateTable.getTypes()[index];
                if (!newValue.equals(type)) {
                    tfsTypePopups[index].hide();
                    btnsChangeType[index].setDisable(false);
                } else {
                    btnsChangeType[index].setDisable(true);
                    tfsTypePopups[index].show(SAME_VALUE);
                }
            } else {
                btnsChangeType[index].setDisable(true);
            }
        }
        // ----------------------------------------------------------
        masterControl();
    }

    private void tfsTypeLengthTextProperty(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
        System.out.println("\ttfsTypeLengthTextProperty");

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
            if (updateControl) {
                if (update) {
                    String typeLength = Integer.toString(updateTable.getTypesLength()[index]).toLowerCase().trim();
                    if (!text.equals(typeLength)) {
                        tfsTypeLengthPopups[index].hide();
                        btnsChangeType[index].setDisable(false);
                    } else {
                        tfsTypeLengthPopups[index].show(SAME_VALUE);
                        btnsChangeType[index].setDisable(true);
                    }
                } else {
                    btnsChangeType[index].setDisable(true);
                }
            }
        }
        // -----------------------------------------------------
        masterControl();
    }

    void cksPKAction(ActionEvent e) {
        // ONLY FOR UPDATE
        int index = Integer.parseInt(((CheckBox) e.getSource()).getId());
        boolean update = false;

        for (int c = 0; c < currentRowLength; c++) {
            boolean pkSelected = updateTable.getPks()[c].equals("Yes");
            if (cksPK[c].isSelected() != pkSelected) {
                update = true;
                break;
            }
        }
        if (update) {
            btnUpdatePK.setDisable(false);
            cksPKPopups[index].hide();
        } else {
            btnUpdatePK.setDisable(true);
            cksPKPopups[index].show(SAME_VALUE);
        }
    }

    private void cksFKAction(ActionEvent e) {
        CheckBox ck = (CheckBox) e.getSource();
        int index = Integer.parseInt(ck.getId());
        if (ck.isSelected()) {
            tfasFK[index].setVisible(true);
        } else {
            tfasFK[index].setVisible(false);
        }

        tfasFKControl(-1);
        // UPDATE------------------------------------------------
        fkUpdateControl(index);
        // ------------------------------------------------
        masterControl();
    }

    private void tfasFKTextProperty(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        int index = Integer.parseInt(tf.getId());
        System.out.println("\ttfasFKTextProperty FK_" + (index + 1));

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
        fkUpdateControl(index);
        // ----------------------------------------------
        masterControl();
    }

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
        defaultUpdateControl(index, update);
        // -------------------------------------------
        masterControl();
    }

    private void tfsDefaultTypeControl(int index) {
        System.out.println("\ttfsDefaultTypeControl");

        TextField tf = tfsDefault[index];
        String text = tf.getText();
        boolean update = false;

        if (tf.isVisible()) {
            if (tfasType[index].getStyle().contains(CSS.TEXT_FILL)
                    && (tfsTypeLength[index].getStyle().contains(CSS.TEXT_FILL) || !tfsTypeLength[index].isVisible())) {
                String type = tfasType[index].getText();
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
            defaultUpdateControl(index, update);
            // -------------------------------------------
        } else {
            tfsDefaultPopups[index].hide();
            defaultBW = true;
            tfsDefaultControl(index);
        }
    }

    private void tfsDefaultKeyReleased(KeyEvent e) {
        TextField tf = (TextField) e.getSource();
        int index = Integer.parseInt(tf.getId());

        tfsDefaultTypeControl(index);// ++++++++++++++++
        masterControl();
    }

    private void rbsExtraAction(ActionEvent e) {
        RadioButton btn = (RadioButton) e.getSource();
        int index = Integer.parseInt(btn.getId());
        int errorCount = 0;
        // ---------------------------------------------
        if (btn.isSelected()) {
            if (cksPK[index].isSelected()) {
                rbsExtraPopups[index].hide();
                extraPKOK = true;
            } else {
                lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
                rbsExtraPopups[index].show("An AUTO_INCREMENT column has to be a PRIMARY KEY");

                errorCount++;

                extraPKOK = false;
            }
            // ---------------------------------------------
            if (cksFK[index].isSelected()) {
                lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
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
                lbhExtra.setTextFill(NonCSS.TEXT_FILL_ERROR);
                if (errorCount == 0) {
                    rbsExtraPopups[index]
                            .show("There's no need to have a DEFAULT value in a column with AUTO_INCREMENT");

                } else {
                    rbsExtraPopups[index].show(EXTRA_GENERAL_ERROR);

                }
                extraDefaultOK = false;
            }
            // ---------------------------------------------
        } else {
            lbhExtra.setTextFill(NonCSS.TEXT_FILL);
            rbsExtraPopups[index].hide();

            extraPKOK = true;
            extraFKOK = true;
            extraDefaultOK = true;
        }
        //UPDATE------------------------------------------------
        if (updateControl) {
            if (extraDefaultOK && extraPKOK && extraFKOK) {// NOT SURE IF NECESSARY
                int extraO = updateTable.getExtra();
                int extra = btn.isSelected() ? index : -1;
                if (extra != extraO) {
                    rbsExtraPopups[index].hide();
                    btnUpdateExtra.setDisable(false);
                } else {
                    rbsExtraPopups[index].show(SAME_VALUE);
                    btnUpdateExtra.setDisable(true);
                }
            } else {
                btnUpdateExtra.setDisable(true);
            }
        }
        //------------------------------------------------
        masterControl();
    }

    // BOTTOM ----------------------------------------
    private void btnCancelAction(ActionEvent e) {
        vf.getStage().setScene(vf.getScene());
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
        StringBuilder dist = new StringBuilder("NONE");
        boolean distPresent = false;
        int distCount = 0;
        StringBuilder imageC = new StringBuilder("NONE");
        StringBuilder imageCPath = new StringBuilder("NONE");
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

            if (cksPK[a].isSelected()) {
                pks.add(columnsNames[a]);
            }
            if (cksFK[a].isSelected()) {
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
            if (btnsDist[a].isSelected()) {// OLD WAY
                if (!distPresent) {
                    dist.delete(0, dist.length());
                    dist.append("X0: ");
                    distPresent = true;
                }
                dist.deleteCharAt(1);
                dist.insert(1, Integer.toString(++distCount));
                dist.append(Integer.toString(a + 1) + "_");
            }
            if (btnsImageC[a].isSelected()) {
                imageC.delete(0, imageC.length());
                imageC.append("C" + (a + 1));

                imageCPath.delete(0, imageCPath.length());
                imageCPath.append(tfImageCPath.getText());
            }
        }
        if (distCount > 0) {// DELETE LAST '_'
            dist.deleteCharAt(dist.length() - 1);
        }
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
            Object[] values = new Object[] { null, tableName.replace("_", " "), dist.toString(), imageC.toString(),
                    imageCPath.toString() };
            boolean insert = ms.insert(MSQL.TABLE_NAMES, values);
            if (insert) {
                /*
                 * try {
                 * 
                 * ResultSet rs = ms.selectRow(MSQL.TABLE_NAMES, "Name", tableName.replace("_",
                 * " ")); while (rs.next()) { int id = rs.getInt(1); String name =
                 * rs.getString(2); String distName = rs.getString(3); String imageCName =
                 * rs.getString(4); String imageCPathName = rs.getString(5);
                 * 
                 * System.out.println("\tid: " + id); System.out.println("\tname: " + name);
                 * System.out.println("\tdistName: " + distName);
                 * System.out.println("\timageCName: " + imageCName);
                 * System.out.println("\timageCPathName: " + imageCPathName);
                 * 
                 * //tables.addTable(new Table(id, name, distName, imageCName, imageCPathName));
                 * 
                 * 
                 * }
                 * 
                 * // vf.getMenuTable().getItems().add(new MenuItem(name));//NOT TESTED
                 * 
                 * } catch (SQLException e1) { e1.printStackTrace(); }
                 */
                // NOT TESTED
                Menus.getInstance(vf).addMenuItemsReset();// NOT TESTED

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
    private void btnsDistAction(ActionEvent e) {
        //MAY HAVE TO ADD DIST BUTTONS TO AN OBSERVABLE LIST (ADD OR REMOVE COLUMN)
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

        //UPDATE--------------------------------------------------------------
        if(updateControl){
            if(distExtraOK){
                boolean update = false;
                for(int a = 0;a < currentRowLength; a++){
                    boolean dist = updateTable.getDist()[a].equals("Yes");
                    if(btnsDist[index].isSelected() != dist){
                        update = true;
                        break;
                    }
                }
                if(update){
                    btnsDistPopups[index].hide();
                    btnUpdateDist.setDisable(false);
                }else{
                    btnsDistPopups[index].show(SAME_VALUE);
                    btnUpdateDist.setDisable(true);
                }
            }else{
                btnUpdateDist.setDisable(true);
            }
        }
        //--------------------------------------------------------------
        masterControl();
    }

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
    }

    private void listImageCChange(Change<? extends Boolean> c) {
        System.out.println("\tlistImageCChange");

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
        System.out.println("\ttfImageCPathTextProperty");

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

    // INIT ---------------------------------------------
    private void fkReferencesInit() {
        Key[] row = keys.getRowPrimaryKeys();
        List<String> list = new ArrayList<>();

        for (int a = 0; a < row.length; a++) {
            String database = row[a].getDatabase();
            String table = row[a].getTableName();
            String column = row[a].getColumnName();
            list.add(database + "." + table + "." + column);
        }

        String[] elements = list.toArray(new String[list.size()]);
        Arrays.asList(tfsFKPs).forEach(e -> {
            e.setLvOriginalItems(elements);
            e.getLv().getItems().addAll(elements);
            e.getLv().getSelectionModel().select(0);// UNTESTED
        });
    }

    private void nonFXMLLeftNodesInit() {
        // ADD SPACING ON EACH HB, SPECIFY SOURCE OF EACH PROPERTY TRIGGER
        spGridPaneLeft.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            lbsN[a] = new Label("Column " + (a + 1));
            hbsN[a] = new HBox(lbsN[a]);
            // COLUMN NAMES---------------------------------------
            tfsColumn[a] = new TextField();
            tfsColumnPopups[a] = new PopupMessage(tfsColumn[a]);

            btnsRemoveColumn[a] = new Button("X");
            btnsAddColumn[a] = new Button("+");
            btnsRenameColumn[a] = new Button("C");

            hbsName[a] = new HBox(tfsColumn[a], btnsRemoveColumn[a], btnsAddColumn[a], btnsRenameColumn[a]);
            hbsName[a].setPadding(new Insets(0, 2, 0, 2));
            // TYPES----------------------------------------------
            // tfasType[a] = new TextFieldAutoC(a, types.getTypeNames());
            tfasType[a] = new TextField();
            tfsTypePs[a] = new PopupAutoC(tfasType[a], types.getTypeNames());
            tfsTypePopups[a] = new PopupMessage(tfasType[a]);

            tfsTypeLength[a] = new TextField("1");
            tfsTypeLengthPopups[a] = new PopupMessage(tfsTypeLength[a]);

            btnsChangeType[a] = new Button("C");

            hbsType[a] = new HBox(tfasType[a], tfsTypeLength[a], btnsChangeType[a]);
            hbsType[a].setPadding(new Insets(0, 2, 0, 2));
            // NULLS----------------------------------------------
            cksNull[a] = new CheckBox();
            btnsChangeNull[a] = new Button("C");
            hbsNull[a] = new HBox(cksNull[a], btnsChangeNull[a]);
            hbsNull[a].setPadding(new Insets(0, 2, 0, 2));
            // PKS----------------------------------------------
            cksPK[a] = new CheckBox();
            cksPKPopups[a] = new PopupMessage(cksPK[a]);
            btnsChangePK[a] = new Button("C");
            hbsPK[a] = new HBox(cksPK[a], btnsChangePK[a]);
            // FKS----------------------------------------------
            cksFK[a] = new CheckBox();
            cksFKPopups[a] = new PopupMessage(cksFK[a]);

            tfasFK[a] = new TextField();
            tfsFKPs[a] = new PopupAutoC(tfasFK[a]);
            tfsFKPopups[a] = new PopupMessage(tfasFK[a]);

            btnsChangeFK[a] = new Button("C");
            hbsFK[a] = new HBox(cksFK[a], tfasFK[a], btnsChangeFK[a]);
            // DEFAULTS----------------------------------------------
            cksDefault[a] = new CheckBox();
            cksDefaultPopups[a] = new PopupMessage(cksDefault[a]);

            tfsDefault[a] = new TextField();
            tfsDefaultPopups[a] = new PopupMessage(tfsDefault[a]);

            btnsChangeDefault[a] = new Button("C");
            hbsDefault[a] = new HBox(cksDefault[a], tfsDefault[a], btnsChangeDefault[a]);
            hbsDefault[a].setPadding(new Insets(0, 2, 0, 2));
            // EXTRA----------------------------------------------
            rbsExtra[a] = new RadioButton();
            rbsExtraPopups[a] = new PopupMessage(rbsExtra[a]);
            // rbsExtra[a].setToggleGroup(rbsExtraGroup);
            // btnsChangeExtra[a] = new Button("C");
            hbsExtra[a] = new HBox(rbsExtra[a]/* , btnsChangeExtra[a] */);
            // -----------------------------------------
            // OTHERS ---------------------------------
            tfsColumn[a].setId(Integer.toString(a));
            btnsRemoveColumn[a].setId(Integer.toString(a));
            btnsAddColumn[a].setId(Integer.toString(a));
            tfasType[a].setId("TF-TYPE-" + a);
            tfsTypeLength[a].setId(Integer.toString(a));
            cksFK[a].setId(Integer.toString(a));
            tfasFK[a].setId(Integer.toString(a));
            cksDefault[a].setId(Integer.toString(a));
            tfsDefault[a].setId(Integer.toString(a));
            rbsExtra[a].setId(Integer.toString(a));
            // ----------------------------------------------
            tfsColumn[a].setPromptText("Column name required");
            tfsDefault[a].setPromptText("Value Required");
            // ----------------------------------------------
            tfasType[a].setStyle(CSS.TFAS_DEFAULT_LOOK);
            // TYPE DEFAULT SELECTION----------------------------
            tfsTypePs[a].getLv().getSelectionModel().select(presetTypeSelected.get(a).getTypeName());
            tfsTypeLength[a].setText(Integer.toString(presetTypeSelected.get(a).getTypeLength()));
            // ----------------------------------------------

            tfsColumn[a].setPrefWidth(-1);
            btnsRemoveColumn[a].setMinWidth(40);
            btnsRemoveColumn[a].setMaxWidth(40);
            btnsAddColumn[a].setMinWidth(40);
            btnsAddColumn[a].setMaxWidth(40);
            tfasType[a].setPrefWidth(140);
            tfsTypeLength[a].setMinWidth(40);
            tfsTypeLength[a].setMaxWidth(40);
            // tfasFK[a].setPrefWidth(-1);
            // tfasFK[a].setMaxHeight(30);
            hbsFK[a].setPrefWidth(-1);
            tfasFK[a].setPrefWidth(-1);
            // SOME PROPERTIES AND LISTENERS---------------------------
            btnsRenameColumn[a].managedProperty().bind(btnsRenameColumn[a].visibleProperty());
            tfsTypeLength[a].managedProperty().bind(tfsTypeLength[a].visibleProperty());
            btnsChangeType[a].managedProperty().bind(btnsChangeType[a].visibleProperty());
            btnsChangeNull[a].managedProperty().bind(btnsChangeNull[a].visibleProperty());
            btnsChangePK[a].managedProperty().bind(btnsChangePK[a].visibleProperty());
            tfasFK[a].managedProperty().bind(tfasFK[a].visibleProperty());
            btnsChangeFK[a].managedProperty().bind(btnsChangeFK[a].visibleProperty());
            tfsDefault[a].managedProperty().bind(tfsDefault[a].visibleProperty());
            btnsChangeDefault[a].managedProperty().bind(btnsChangeDefault[a].visibleProperty());
            // btnsChangeExtra[a].managedProperty().bind(btnsChangeExtra[a].visibleProperty());
            /*
             * btnsRenameColumn[a].disableProperty().bind(btnsRenameColumn[a].
             * visibleProperty().not());
             * btnsChangeType[a].disableProperty().bind(btnsChangeType[a].visibleProperty().
             * not());
             * btnsChangeNull[a].disableProperty().bind(btnsChangeNull[a].visibleProperty().
             * not()); btnsChangeDefault[a].disableProperty().bind(btnsChangeDefault[a].
             * visibleProperty().not());
             */
            // ----------------------------------------------------------
            tfasFK[a].setVisible(false);
            tfsDefault[a].setVisible(false);

            btnsRenameColumn[a].setDisable(true);
            btnsChangeType[a].setDisable(true);
            btnsChangeNull[a].setDisable(true);
            btnsChangeDefault[a].setDisable(true);
            //STYLE-------------------------------------------------------
            tfasType[a].getStyleClass().clear();
            tfasType[a].setStyle(null);

            hbsN[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsName[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsType[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            tfsTypeLength[a].setStyle(CSS.TEXT_FILL);
            hbsNull[a].setStyle(CSS.BORDER_GRID_BOTTOM);

            hbsPK[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsFK[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsDefault[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsExtra[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            //-------------------------------------------------------
            GridPane.setMargin(hbsN[a], INSETS);
            GridPane.setMargin(hbsName[a], INSETS);
            GridPane.setMargin(hbsType[a], INSETS);
            GridPane.setMargin(hbsNull[a], INSETS);
            GridPane.setMargin(hbsPK[a], INSETS);
            GridPane.setMargin(hbsFK[a], INSETS);
            GridPane.setMargin(hbsDefault[a], INSETS);
            GridPane.setMargin(hbsExtra[a], INSETS);
        }
        new ToggleGroupD<>(rbsExtra);
        // SUB-----------------------------------
    }

    private void nonFXMLRightNodesInit() {
        spGridPaneRight.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            btnsDist[a] = new ToggleButton("" + (a + 1));
            btnsDistPopups[a] = new PopupMessage(btnsDist[a]);
            btnsImageC[a] = new ToggleButton("" + (a + 1));

            btnsDist[a].setId(Integer.toString(a));
            btnsImageC[a].setId(Integer.toString(a));

            btnsDist[a].setMinWidth(40);
            btnsDist[a].setMaxWidth(40);
            btnsImageC[a].setMinWidth(40);
            btnsImageC[a].setMaxWidth(40);

            btnsDist[a].managedProperty().bind(btnsDist[a].visibleProperty());
            btnsImageC[a].managedProperty().bind(btnsImageC[a].visibleProperty());

            btnsDist[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            btnsImageC[a].setStyle(CSS.BORDER_GRID_BOTTOM);

            GridPane.setMargin(btnsDist[a], INSETS);
            GridPane.setMargin(btnsImageC[a], INSETS);
        }
        new ToggleGroupD<>(btnsImageC);
        tfImageCPathPopup = new PopupMessage(tfImageCPath);
        // SUB-------------------------------------
        lbUpdateLeft.setDisable(true);
        directoryChooser.setTitle("Select Image for a column");

        // gridPaneRightSub.setGridLinesVisible(true);
    }

    private void btnAddRemoveColumnInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (a < presetRowsLenght - 1) {
                btnsAddColumn[a].setVisible(false);
                btnsRemoveColumn[a].setVisible(false);
            }
        }
        btnsRemoveColumn[0].setDisable(true);
        btnsAddColumn[MSQL.MAX_COLUMNS - 1].setDisable(true);
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
        nonFXMLLeftNodesInit();
        nonFXMLRightNodesInit();
        // TOP-----------------------------------------------
        tfTable.setPromptText("Table name required");
        btnRenameTable.managedProperty().bind(btnRenameTable.visibleProperty());
        btnRenameTable.setDisable(true);
        // --------------------------------------------------
        fkReferencesInit();
        btnAddRemoveColumnInit();
        createHelpPopupReset();

        btnCreateUpdate.managedProperty().bind(btnCreateUpdate.visibleProperty());
        for (int a = 0; a < presetRowsLenght; a++) {
            listColumns.add(tfsColumn[a].getText().trim());
            listImageC.add(false);
        }
        // gridPaneLeft.setGridLinesVisible(true);
        tfTable.setOnKeyReleased(this::tfTableKeyReleased);
        // LEFT --------------------------------------
        Arrays.asList(tfsColumn).forEach(e -> {
            e.setOnKeyReleased(this::tfsColumnsKeyReleased);
        });
        listColumns.addListener(this::listColumnsChange);

        Arrays.asList(tfasType).forEach(e -> e.textProperty().addListener(this::tfasTypeTextProperty));
        Arrays.asList(tfsTypeLength).forEach(e -> e.textProperty().addListener(this::tfsTypeLengthTextProperty));
        Arrays.asList(cksFK).forEach(e -> e.setOnAction(this::cksFKAction));
        Arrays.asList(tfasFK).forEach(e -> e.textProperty().addListener(this::tfasFKTextProperty));
        Arrays.asList(cksDefault).forEach(e -> e.setOnAction(this::cksDefaultAction));
        Arrays.asList(tfsDefault).forEach(e -> e.setOnKeyReleased(this::tfsDefaultKeyReleased));
        Arrays.asList(rbsExtra).forEach(e -> e.addEventHandler(ActionEvent.ACTION, this::rbsExtraAction));
        // RIGHT --------------------------------------
        Arrays.asList(btnsDist).forEach(e -> e.setOnAction(this::btnsDistAction));
        Arrays.asList(btnsImageC).forEach(e -> e.addEventHandler(ActionEvent.ACTION, this::btnsImageCAction));
        btnSelectImageC.setOnAction(this::btnSelectImageCAction);
        listImageC.addListener(this::listImageCChange);
        tfImageCPath.textProperty().addListener(this::tfImageCPathTextProperty);
        // Arrays.asList(btnsImageC).forEach(e -> e.addEventHandler(ActionEvent.ACTION,
        // this::btnsImageCAction));
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

    public CheckBox[] getCksPK() {
        return cksPK;
    }

    public void setCksPK(CheckBox[] cksPK) {
        this.cksPK = cksPK;
    }

    public Button[] getBtnsChangePK() {
        return btnsChangePK;
    }

    public void setBtnsChangePK(Button[] btnsChangePK) {
        this.btnsChangePK = btnsChangePK;
    }

    public HBox[] getHbsFK() {
        return hbsFK;
    }

    public void setHbsFK(HBox[] hbsFK) {
        this.hbsFK = hbsFK;
    }

    public CheckBox[] getCksFK() {
        return cksFK;
    }

    public void setCksFK(CheckBox[] cksFK) {
        this.cksFK = cksFK;
    }

    public TextField[] getTfasFK() {
        return tfasFK;
    }

    public void setTfasFK(TextField[] tfasFK) {
        this.tfasFK = tfasFK;
    }

    public Button[] getBtnsChangeFK() {
        return btnsChangeFK;
    }

    public void setBtnsChangeFK(Button[] btnsChangeFK) {
        this.btnsChangeFK = btnsChangeFK;
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

}
