package com.cofii.ts.cu;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.basic.BasicComboBoxUI.PropertyChangeHandler;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.Key;
import com.cofii.ts.store.Keys;
import com.cofii.ts.store.SQLType;
import com.cofii.ts.store.SQLTypes;
import com.cofii.ts.store.TableS;
import com.cofii2.components.javafx.TextFieldAutoC;
import com.cofii2.methods.MList;
import com.mysql.cj.MysqlType;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class VCController implements Initializable {

    public static final String LBH_COLUMN_NAMES = "Name";
    public static final String LBH_COLUMN_NAMES_ERROR = "Columns have the same name";
    public static final Insets INSETS = new Insets(2, 2, 2, 2);

    private static final String ILLEGAL_CHARS = "Illegal Chars";
    private static final String SELECTION_UNMATCH = "Selection Unmatch";

    // -------------------------------------------------
    private int presetRowsLenght = 2;
    private int currentRowLength = presetRowsLenght;
    private List<SQLType> presetTypeSelected = new ArrayList<>(MSQL.MAX_COLUMNS);

    private ObservableList<String> listColumns = FXCollections.observableArrayList();
    // -------------------------------------------------
    @FXML
    private Label lbhColumnNames;
    @FXML
    private Label lbhTypes;
    @FXML
    private Label lbhFK;

    @FXML
    private Label lbTable;
    @FXML
    private TextField tfTable;
    @FXML
    private GridPane gridPaneLeft;
    @FXML
    private GridPane gridPaneRight;
    @FXML
    private Label lbStatus;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;

    private HBox[] hbsN = new HBox[MSQL.MAX_COLUMNS];// -----------
    private Label[] lbsN = new Label[MSQL.MAX_COLUMNS];
    private HBox[] hbsName = new HBox[MSQL.MAX_COLUMNS];// -----------
    private TextField[] tfsColumn = new TextField[MSQL.MAX_COLUMNS];
    private Tooltip[] tfsColumnTT = new Tooltip[MSQL.MAX_COLUMNS];
    private Button[] btnsRemoveColumn = new Button[MSQL.MAX_COLUMNS];
    private Button[] btnsAddColumn = new Button[MSQL.MAX_COLUMNS];
    private Button[] btnsRenameColumn = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsType = new HBox[MSQL.MAX_COLUMNS];// -----------
    private TextFieldAutoC[] tfasType = new TextFieldAutoC[MSQL.MAX_COLUMNS];
    private TextField[] tfsTypeLength = new TextField[MSQL.MAX_COLUMNS];
    private Tooltip[] tfsTypeLengthTT = new Tooltip[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeType = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsNull = new HBox[MSQL.MAX_COLUMNS];// -----------
    private CheckBox[] cksNull = new CheckBox[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeNull = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsPK = new HBox[MSQL.MAX_COLUMNS];// -----------
    private CheckBox[] cksPK = new CheckBox[MSQL.MAX_COLUMNS];
    private Button[] btnsChangePK = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsFK = new HBox[MSQL.MAX_COLUMNS];// -----------
    private CheckBox[] cksFK = new CheckBox[MSQL.MAX_COLUMNS];
    private TextFieldAutoC[] tfasFK = new TextFieldAutoC[MSQL.MAX_COLUMNS];
    private Tooltip[] tfasFKTT = new Tooltip[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeFK = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsDefault = new HBox[MSQL.MAX_COLUMNS];// -----------
    private CheckBox[] cksDefault = new CheckBox[MSQL.MAX_COLUMNS];
    private TextField[] tfsDefault = new TextField[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeDefault = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsExtra = new HBox[MSQL.MAX_COLUMNS];// -----------
    private RadioButton[] rbsExtra = new RadioButton[MSQL.MAX_COLUMNS];
    private ToggleGroup rbsExtraGroup = new ToggleGroup();
    private Button[] btnsChangeExtra = new Button[MSQL.MAX_COLUMNS];

    private ToggleButton[] btnsDist = new ToggleButton[MSQL.MAX_COLUMNS];
    // ---------------------------------------------
    private VFController vf;
    private TableS tables = TableS.getInstance();
    private SQLTypes types = SQLTypes.getInstance();
    private Keys keys = Keys.getInstance();

    private Pattern patternBW = Pattern.compile("[A-Za-z]\\w*");
    private Pattern patternTypeLength = Pattern.compile("\\d{1,5}");
    private Matcher matcher;

    private boolean tfsColumnBWOK = false;
    private boolean typeSelectionMatch = false;
    private boolean typeLengthOK = false;
    private boolean fkSelectionMatch = false;
    private boolean defaultBW = false;

    // CONTROL---------------------------------------------
    private void btnCreateControl() {
        // TABLE: EXIST AND MATCHES---------------------------
        boolean tableDiferrentMatch = lbTable.getTextFill().equals(NonCSS.TEXT_FILL);
        // COLUMN NAMES: SAME AND MATCHES---------------------
        boolean columSameNames = lbhColumnNames.getTextFill().equals(NonCSS.TEXT_FILL);
        // TYPE: SELECTION MATCH ~ TYPE LENGTH: CORRECT LENGTH
        // FK: SELECTION MATCH
        // DEFAULT: MATCHES

        if (tableDiferrentMatch && columSameNames && tfsColumnBWOK && typeSelectionMatch && typeLengthOK
                && fkSelectionMatch && defaultBW) {
            btnCreate.setDisable(false);
        } else {
            btnCreate.setDisable(true);
        }
    }

    private void tfsColumnControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {
                if (!matcher.matches()) {
                    tfsColumnBWOK = false;
                    break;
                }
            }
        }
    }

    private void tfsTypeControl() {
        if (typeSelectionMatch && typeLengthOK) {
            lbhTypes.setText("Type");
            lbhTypes.setTextFill(NonCSS.TEXT_FILL);
        } else {
            lbhTypes.setText("Type - Wrong type or lenght");
            lbhTypes.setTextFill(NonCSS.TEXT_FILL_ERROR);
        }
    }

    private void tfasTypeAllControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (index != a) {
                String text = tfasType[a].getTf().getText();
                matcher = patternBW.matcher(text);
                if (matcher.matches()) {
                    if (!MList.isOnThisList(tfasType[a].getLv().getItems(), text, false)) {
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

    private void tfsTypeLengthAllControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (index != a) {
                String text = tfsTypeLength[a].getText();
                int typeMaxLength = types.getTypeMaxLength(tfasType[a].getTf().getText());

                matcher = patternBW.matcher(text);
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
                String text = tfasFK[a].getTf().getText();
                if (!MList.isOnThisList(tfasFK[index].getLv().getItems(), text, false)) {
                    fkSelectionMatch = false;
                    break;
                }
            }
        }
    }

    private void tfsDefaultControl(int index) {
        for (int a = 0; a < currentRowLength; a++) {
            if (a != index) {

            }
        }
    }

    // LISTENERS---------------------------------------------
    private void tfTableKeyReleased(KeyEvent e) {
        System.out.println("\ntfTableKeyReleased");
        String[] tableList = tables.getTables();
        String text = tfTable.getText().toLowerCase().trim().replace(" ", "_");

        matcher = patternBW.matcher(text);
        if (matcher.matches()) {
            if (MList.isOnThisList(tableList, text, true)) {
                System.out.println("\tTABLE EXIST");
                lbTable.setText("This table already exist");
                lbTable.setTextFill(NonCSS.TEXT_FILL_ERROR);
            } else {
                System.out.println("\tOK");
                lbTable.setText("Table Name");
                lbTable.setTextFill(NonCSS.TEXT_FILL);
            }
        } else {
            lbTable.setText("Not Accepted Characters");
            lbTable.setTextFill(NonCSS.TEXT_FILL_ERROR);
        }

        btnCreateControl();
    }

    private void tfsColumnsKeyReleased(KeyEvent e) {
        TextField tf = (TextField) e.getSource();
        int index = Integer.parseInt(tf.getId());
        System.out.println("TF " + (index + 1));

        String text = tf.getText().toLowerCase().trim().replace(" ", "_");
        listColumns.set(index, text);

        //ASK IF TOOLTIP IS NULL

        matcher = patternBW.matcher(text);
        if (matcher.matches()) {
            tf.setStyle(CSS.TEXT_FILL);

            //tf.setTooltip(null);

            tfsColumnBWOK = true;// REST CONTROL
            tfsColumnControl(index);
        } else {
            tf.setStyle(CSS.TEXT_FILL_ERROR);

            tooltipDefaultAction(tf);

            tfsColumnBWOK = false;
        }

        btnCreateControl();
    }

    private void listColumnsChange(Change<? extends String> c) {
        System.out.println("LIST CHANGE");
        while (c.next()) {
            if (c.wasReplaced() || c.wasAdded() || c.wasRemoved()) {
                System.out.println("\tupdate");

                if (MList.areTheyDuplicatedElementsOnList(listColumns)) {
                    lbhColumnNames.setText(LBH_COLUMN_NAMES_ERROR);
                    lbhColumnNames.setStyle(CSS.TEXT_FILL_ERROR);
                } else {
                    lbhColumnNames.setText(LBH_COLUMN_NAMES);
                    lbhColumnNames.setStyle(CSS.TEXT_FILL);
                }
            }
        }
    }

    // ----
    private void btnsAddAction(ActionEvent e) {
        Button btn = (Button) e.getSource();
        int index = Integer.parseInt(btn.getId()) + 1;
        int row = index + 1;

        btnsAddColumn[index - 1].setVisible(false);
        btnsRemoveColumn[index - 1].setVisible(false);
        btnsAddColumn[index].setVisible(true);
        btnsRemoveColumn[index].setVisible(true);

        gridPaneLeft.add(hbsN[index], 0, row);
        gridPaneLeft.add(hbsName[index], 1, row);
        gridPaneLeft.add(hbsType[index], 2, row);
        gridPaneLeft.add(hbsNull[index], 3, row);
        gridPaneLeft.add(hbsPK[index], 4, row);
        gridPaneLeft.add(hbsFK[index], 5, row);
        gridPaneLeft.add(hbsDefault[index], 6, row);
        gridPaneLeft.add(hbsExtra[index], 7, row);

        listColumns.add("");
        currentRowLength++;
    }

    private void btnsRemoveAction(ActionEvent e) {
        Button btn = (Button) e.getSource();
        int index = Integer.parseInt(btn.getId());

        btnsAddColumn[index - 1].setVisible(true);
        btnsRemoveColumn[index - 1].setVisible(true);

        gridPaneLeft.getChildren().removeAll(hbsN[index], hbsName[index], hbsType[index], hbsNull[index], hbsPK[index],
                hbsFK[index], hbsDefault[index], hbsExtra[index]);

        listColumns.remove(index);
        currentRowLength--;
    }

    // ----
    private void tfasTypeTextProperty(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        int index = Integer.parseInt(tf.getId());

        newValue = newValue.trim();
        matcher = patternBW.matcher(newValue);
        if (matcher.matches()) {
            if (MList.isOnThisList(tfasType[index].getLv().getItems(), newValue, false)) {
                int typeLength = types.getTypeLength(newValue);
                if (typeLength > 0) {// TF-TYPE-LENGTH-POPUP
                    tfsTypeLength[index].setVisible(true);
                    tfsTypeLength[index].setText(Integer.toString(typeLength));
                } else {
                    tfsTypeLength[index].setVisible(false);
                    tfsTypeLength[index].setText("1");
                    typeLengthOK = true;
                }

                //tf.setTooltip(null);

                typeSelectionMatch = true;
                tfasTypeAllControl(index);
            } else {
                tfsTypeLength[index].setVisible(false);
                tfsTypeLength[index].setText("1");
                
                tooltipDefaultAction(tf);

                typeSelectionMatch = false;
            }
        } else {
            typeSelectionMatch = false;
        }

        btnCreateControl();
    }

    private void tfsTypeLengthTextProperty(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        if (tf.isVisible()) {
            boolean wrong = false;
            int index = Integer.parseInt(tf.getId());

            String text = tfsTypeLength[index].getText().toLowerCase().trim();
            int typeMaxLength = types.getTypeMaxLength(tfasType[index].getTf().getText());

            matcher = patternTypeLength.matcher(text);
            if (matcher.matches()) {
                int length = Integer.parseInt(text);
                if (length <= typeMaxLength) {
                    typeLengthOK = true;
                    tfsTypeLengthAllControl(index);

                    tf.setStyle(CSS.TEXT_FILL);
                    // tfsTypeLengthTT[index].hide();
                    //tf.setTooltip(null);

                } else {
                    wrong = true;
                }
            } else {
                wrong = true;
            }

            if (wrong) {
                typeLengthOK = false;

                if (typeMaxLength > 0) {
                    tfsTypeLengthTT[index].setText("Wrong length (1 to " + typeMaxLength + ")");
                }

                tf.setStyle(CSS.TEXT_FILL_ERROR);

                tooltipDefaultAction(tf);
            }

            // tfsTypeControl();
        }

        btnCreateControl();
    }

    private void cksFKAction(ActionEvent e) {
        CheckBox ck = (CheckBox) e.getSource();
        int index = Integer.parseInt(ck.getId());
        if (ck.isSelected()) {
            tfasFK[index].setVisible(true);
        } else {
            tfasFK[index].setVisible(false);
        }

        btnCreateControl();
    }

    private void tfasFKTextProperty(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        System.out.println("tfasFKTextProperty observable class: " + observable.getClass().getName());
        StringProperty textProperty = (StringProperty) observable;
        TextField tf = (TextField) textProperty.getBean();
        int index = Integer.parseInt(tf.getId());

        if (MList.isOnThisList(tfasFK[index].getLv().getItems(), newValue, false)) {
            tf.setStyle(CSS.TEXT_FILL);
            // tfasFKTT[index].hide();
            //tf.setTooltip(null);

            fkSelectionMatch = true;
            tfasFKControl(index);
        } else {
            tf.setStyle(CSS.TEXT_FILL_ERROR);

            tooltipDefaultAction(tf);

            fkSelectionMatch = false;
        }

        btnCreateControl();
    }

    private void cksDefaultAction(ActionEvent e) {
        CheckBox ck = (CheckBox) e.getSource();
        int index = Integer.parseInt(ck.getId());
        if (ck.isSelected()) {
            tfsDefault[index].setVisible(true);
        } else {
            tfsDefault[index].setVisible(false);
        }

        btnCreateControl();
    }

    private void tfsDefaultKeyReleased(KeyEvent e) {
        TextField tf = (TextField) e.getSource();
        int index = Integer.parseInt(tf.getId());

        String text = tf.getText();
        matcher = patternBW.matcher(text);
        if (matcher.matches()) {
            tf.setStyle(CSS.TEXT_FILL);
            
            //tf.setTooltip(null);

            defaultBW = true;
            tfsDefaultControl(index);
        } else {
            tf.setStyle(CSS.TEXT_FILL_ERROR);

            tooltipDefaultAction(tf);

            defaultBW = false;
        }

        btnCreateControl();
    }

    private void btnCancelAction(ActionEvent e) {
        vf.getStage().setScene(vf.getScene());
    }

    // ----
    private void tooltipMove(WindowEvent value) {
        Tooltip tt = (Tooltip) value.getSource();
        TextField tf = (TextField) tt.getOwnerNode();

        Bounds bounds = tf.localToScreen(tf.getBoundsInLocal());
        tf.getTooltip().setX(bounds.getMaxX());
        tf.getTooltip().setY(bounds.getMinY());
    }

    private void tooltipDefaultAction(TextField tf) {
        if (tf.getTooltip() == null) {
            tf.setTooltip(new Tooltip());
            tf.getTooltip().setShowDuration(Duration.seconds(1));
        }
        tf.getTooltip().setText(ILLEGAL_CHARS);
        Bounds sb = tf.localToScreen(tf.getBoundsInLocal());
        tf.getTooltip().show(tf, sb.getMaxX(), sb.getMinY());
    }

    // INIT ---------------------------------------------
    private void fkReferencesInit() {
        Key[] row = keys.getRowPrimaryKeys();
        List<String> list = new ArrayList<>();

        for (int a = 0; a < row.length; a++) {
            String database = row[a].getDatabase();
            String table = row[a].getTableName();

            list.add(database + "." + table);
        }

        Arrays.asList(tfasFK).forEach(e -> {
            String[] elements = list.toArray(new String[list.size()]);
            e.setLvOriginalElements(elements);
            e.getLv().getItems().addAll(elements);
            e.getLv().getSelectionModel().select(0);// UNTESTED
        });
    }

    private void nonFXMLNodesInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {

            lbsN[a] = new Label("Column " + (a + 1));
            hbsN[a] = new HBox(lbsN[a]);

            tfsColumn[a] = new TextField();
            btnsRemoveColumn[a] = new Button("X");
            btnsAddColumn[a] = new Button("+");
            btnsRenameColumn[a] = new Button("C");
            hbsName[a] = new HBox(tfsColumn[a], btnsRemoveColumn[a], btnsAddColumn[a], btnsRenameColumn[a]);

            tfasType[a] = new TextFieldAutoC(a, types.getTypeNames());
            tfsTypeLength[a] = new TextField("1");
            btnsChangeType[a] = new Button("C");
            hbsType[a] = new HBox(tfasType[a], tfsTypeLength[a], btnsChangeType[a]);

            cksNull[a] = new CheckBox();
            btnsChangeNull[a] = new Button("C");
            hbsNull[a] = new HBox(cksNull[a], btnsChangeNull[a]);

            cksPK[a] = new CheckBox();
            btnsChangePK[a] = new Button("C");
            hbsPK[a] = new HBox(cksPK[a], btnsChangePK[a]);

            cksFK[a] = new CheckBox();
            tfasFK[a] = new TextFieldAutoC(a);
            btnsChangeFK[a] = new Button("C");
            hbsFK[a] = new HBox(cksFK[a], tfasFK[a], btnsChangeFK[a]);

            cksDefault[a] = new CheckBox();
            tfsDefault[a] = new TextField();
            btnsChangeDefault[a] = new Button("C");
            hbsDefault[a] = new HBox(cksDefault[a], tfsDefault[a], btnsChangeDefault[a]);

            rbsExtra[a] = new RadioButton();
            rbsExtra[a].setToggleGroup(rbsExtraGroup);
            btnsChangeExtra[a] = new Button("C");
            hbsExtra[a] = new HBox(rbsExtra[a], btnsChangeExtra[a]);
            // OTHERS ---------------------------------
            tfsColumn[a].setId(Integer.toString(a));
            btnsRemoveColumn[a].setId(Integer.toString(a));
            btnsAddColumn[a].setId(Integer.toString(a));
            tfasType[a].getTf().setId(Integer.toString(a));
            tfsTypeLength[a].setId(Integer.toString(a));
            cksFK[a].setId(Integer.toString(a));
            tfasFK[a].getTf().setId(Integer.toString(a));
            cksDefault[a].setId(Integer.toString(a));
            tfsDefault[a].setId(Integer.toString(a));

            // TYPE DEFAULT SELECTION----------------------------
            tfasType[a].getLv().getSelectionModel().select(presetTypeSelected.get(a).getTypeName());
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

            btnsRenameColumn[a].managedProperty().bind(btnsRenameColumn[a].visibleProperty());
            tfsTypeLength[a].managedProperty().bind(tfsTypeLength[a].visibleProperty());
            btnsChangeType[a].managedProperty().bind(btnsChangeType[a].visibleProperty());
            btnsChangeNull[a].managedProperty().bind(btnsChangeNull[a].visibleProperty());
            btnsChangePK[a].managedProperty().bind(btnsChangePK[a].visibleProperty());
            tfasFK[a].managedProperty().bind(tfasFK[a].visibleProperty());
            btnsChangeFK[a].managedProperty().bind(btnsChangeFK[a].visibleProperty());
            tfsDefault[a].managedProperty().bind(tfsDefault[a].visibleProperty());
            btnsChangeDefault[a].managedProperty().bind(btnsChangeDefault[a].visibleProperty());
            btnsChangeExtra[a].managedProperty().bind(btnsChangeExtra[a].visibleProperty());

            btnsRenameColumn[a].setVisible(false);
            // tfsTypeLength[a].setVisible(false);
            btnsChangeType[a].setVisible(false);
            btnsChangeNull[a].setVisible(false);
            btnsChangePK[a].setVisible(false);
            tfasFK[a].setVisible(false);
            btnsChangeFK[a].setVisible(false);
            tfsDefault[a].setVisible(false);
            btnsChangeDefault[a].setVisible(false);
            btnsChangeExtra[a].setVisible(false);

            hbsN[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsName[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsType[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsNull[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsPK[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsFK[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsDefault[a].setStyle(CSS.BORDER_GRID_BOTTOM);
            hbsExtra[a].setStyle(CSS.BORDER_GRID_BOTTOM);

            GridPane.setMargin(hbsN[a], INSETS);
            GridPane.setMargin(hbsName[a], INSETS);
            GridPane.setMargin(hbsType[a], INSETS);
            GridPane.setMargin(hbsNull[a], INSETS);
            GridPane.setMargin(hbsPK[a], INSETS);
            GridPane.setMargin(hbsFK[a], INSETS);
            GridPane.setMargin(hbsDefault[a], INSETS);
            GridPane.setMargin(hbsExtra[a], INSETS);
            // --------------------------------------
            btnsDist[a] = new ToggleButton("" + (a + 1));
        }
    }

    private void tooltipNodesInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            TextField tf = tfsTypeLength[a];

            Tooltip t = new Tooltip();
            Tooltip.install(tf, t);
            tfsTypeLengthTT[a] = t;
            // tfsTypeLengthTT[a].setText("Wrong type length");
            tfsTypeLengthTT[a].setShowDuration(Duration.seconds(2));
        }
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            TextField tf = tfsColumn[a];

            Tooltip t = new Tooltip();
            Tooltip.install(tf, t);
            tfsColumnTT[a] = t;
            // tfsColumnTT[a].setText("Illegal Chars");
            tfsColumnTT[a].setShowDuration(Duration.seconds(2));
            // tfsColumnTT[a].onShownProperty().addListener(this::tooltipOnShowListener);

            // DOES NOT WORK
            // tfsColumnTT[a].onShowingProperty().addListener((this::tooltipOnShowListener));
        }
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            TextField tf = tfasFK[a].getTf();

            Tooltip t = new Tooltip();
            Tooltip.install(tf, t);
            tfasFKTT[a] = t;
            tfasFKTT[a].setShowDuration(Duration.seconds(2));
        }

    }

    private void btnAddRemoveColumnConfig() {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presetSomeInit();
        nonFXMLNodesInit();
        // tooltipNodesInit();
        fkReferencesInit();
        btnAddRemoveColumnConfig();
        for (int a = 0; a < presetRowsLenght; a++) {
            listColumns.add(tfsColumn[a].getText().trim());
        }
        // gridPaneLeft.setGridLinesVisible(true);
        tfTable.setOnKeyReleased(this::tfTableKeyReleased);
        Arrays.asList(tfsColumn).forEach(e -> {
            e.setOnKeyReleased(this::tfsColumnsKeyReleased);
        });
        listColumns.addListener(this::listColumnsChange);
        Arrays.asList(btnsAddColumn).forEach(e -> e.setOnAction(this::btnsAddAction));
        Arrays.asList(btnsRemoveColumn).forEach(e -> e.setOnAction(this::btnsRemoveAction));
        Arrays.asList(tfasType).forEach(e -> e.getTf().textProperty().addListener(this::tfasTypeTextProperty));
        Arrays.asList(tfsTypeLength).forEach(e -> e.textProperty().addListener(this::tfsTypeLengthTextProperty));
        Arrays.asList(cksFK).forEach(e -> e.setOnAction(this::cksFKAction));
        Arrays.asList(tfasFK).forEach(e -> e.getTf().textProperty().addListener(this::tfasFKTextProperty));
        Arrays.asList(cksDefault).forEach(e -> e.setOnAction(this::cksDefaultAction));
        Arrays.asList(tfsDefault).forEach(e -> e.setOnKeyReleased(this::tfsDefaultKeyReleased));

        btnCancel.setOnAction(this::btnCancelAction);
        /*
         * TextField textField = new TextField();
         * textField.textProperty().addListener((observable, oldValue, newValue) -> {
         * System.out.println("textfield changed from " + oldValue + " to " + newValue);
         * });
         */

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

    public TextFieldAutoC[] getTfasType() {
        return tfasType;
    }

    public void setTfasType(TextFieldAutoC[] tfasType) {
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

    public TextFieldAutoC[] getTfasFK() {
        return tfasFK;
    }

    public void setTfasFK(TextFieldAutoC[] tfasFK) {
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

    public Button[] getBtnsChangeExtra() {
        return btnsChangeExtra;
    }

    public void setBtnsChangeExtra(Button[] btnsChangeExtra) {
        this.btnsChangeExtra = btnsChangeExtra;
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

}
