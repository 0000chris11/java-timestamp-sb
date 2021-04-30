package com.cofii.ts.first;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.cofii.ts.login.VLController;
import com.cofii.ts.other.ActionForEachNode;
import com.cofii.ts.other.GetNodesValuesImpl;
import com.cofii.ts.other.MultipleValuesSelectedImpl;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.other.Timers;
import com.cofii.ts.other.GetRowSelectedImpl;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.sql.querys.SelectDistinct;
import com.cofii.ts.store.ColumnS;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class VFController implements Initializable {

    private VLController vl;
    private Stage stage;

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuOpen;
    @FXML
    private Menu menuSelection;
    @FXML
    private Menu menuTable;

    @FXML
    private Label lbTable;

    @FXML
    private GridPane gridPane;
    private Label[] lbs = new Label[MSQL.MAX_COLUMNS];
    private TextField[] tfs = new TextField[MSQL.MAX_COLUMNS];
    private ComboBox[] cbs = new ComboBox[MSQL.MAX_COLUMNS];
    private List<List<String>> cbElements = new ArrayList<>(MSQL.MAX_COLUMNS);
    private Button[] btns = new Button[MSQL.MAX_COLUMNS];

    @FXML
    private Label lbStatus;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;

    @FXML
    private TableView<ObservableList<Object>> table;
    private ColumnS columns = ColumnS.getInstance();
    private Object[] rowData;
    private Object[] selectedRow;
    private MSQLP ms;
    // ----------------------
    private static final String SUCCESS = "\tsuccess";
    private static final String FAILED = "\tfailed";

    private static final int CB_STARTS_WITH = 0;
    private int cbSearchOption = CB_STARTS_WITH;

    // OTHER -------------------------------------------
    private void forEachAction(int length, ActionForEachNode en) {
        for (int a = 0; a < length; a++) {
            // MISING FOR PRIMARY KEY
            if (!tfs[a].isNeedsLayout()) {
                en.forTFS(tfs[a], a);

            } else if (!cbs[a].isNeedsLayout()) {
                en.forCBS(cbs[a], a);

            }

            en.either(a);
        }
    }

    // LISTENER -----------------------------------------
    // NON-FXML
    private void cbsMouseClicked(MouseEvent e) {
        for (int a = 0; a < cbs.length; a++) {
            TextField tf = (TextField) e.getSource();
            if (tf == cbs[a].getEditor()) {
                cbs[a].show();
            }
        }
    }

    // LISTENER CBS-----------------------------------
    private void searchFunction(KeyEvent e) {
        for (int a = 0; a < cbs.length; a++) {
            TextField tf = (TextField) e.getSource();
            if (tf == cbs[a].getEditor()) {
                // System.out.println("\nCB " + (a + 1));
                cbs[a].getItems().clear();
                cbs[a].getItems().addAll(cbElements.get(a));
                if (!cbs[a].getItems().get(0).equals(SelectDistinct.NO_DISTINCT_ELEMENTS)) {
                    String text = tf.getText().toUpperCase();
                    // System.out.println("\ttext: " + text);
                    // -----------------------------------------
                    int originalLength = cbElements.get(a).size();
                    // System.out.println("\toriginal length: " + originalLength);
                    for (int b = 0; b < originalLength; b++) {
                        String element = cbElements.get(a).get(b);
                        if (cbSearchOption == CB_STARTS_WITH) {
                            if (!element.toUpperCase().startsWith(text)) {
                                cbs[a].getItems().remove(element);
                            }
                        }
                    }
                    // System.out.println("\tcurrent length: " + cbs[a].getItems().size());
                    if (!cbs[a].isShowing()) {
                        cbs[a].show();
                    }
                }
            }
        }
    }

    private void cbsKeyReleased(KeyEvent e) {
        if (!e.getCode().isArrowKey() && !e.getCode().isFunctionKey()) {
            searchFunction(e);
        }else if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.DOWN){
            
        }
    }

    private <T> void tableRowSelected(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        ObservableList<ObservableList<Object>> list = table.getSelectionModel().getSelectedItems();
        System.out.println("\ntable length selection: " + list.size());
        if (list.size() == 1) {
            System.out.println("table sub-list length selection: " + list.get(0).size());
            rowData = new Object[list.get(0).size()];

            selectedRow = list.get(0).toArray();
            GetRowSelectedImpl nr = new GetRowSelectedImpl(selectedRow);
            forEachAction(rowData.length, nr);

            btnDelete.setDisable(false);
            btnUpdate.setDisable(false);
        } else if (list.size() > 1) {
            forEachAction(columns.size(), new MultipleValuesSelectedImpl());
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);

        } else if (list.isEmpty()) {
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);
        }

    }

    @FXML
    private void btnDeleteAction() {
        System.out.println(CC.CYAN + "\nDELETE ROW" + CC.RESET);
        String tableName = MSQL.getCurrentTable().getName().replace(" ", "_");
        System.out.println("\ttableName: " + tableName + " - rowData length" + rowData.length);
        boolean returnValue = ms.deleteRow(tableName, rowData);
        if (returnValue) {
            ms.selectData(tableName, new SelectData(this, SelectData.MESSAGE_DELETE_ROW + tableName));
            System.out.println(SUCCESS);
        } else {
            System.out.println(FAILED);
        }
    }

    @FXML
    private void btnUpdateAction() {
        System.out.println(CC.CYAN + "\nUPDATE ROW" + CC.RESET);
        String tableName = MSQL.getCurrentTable().getName().replace(" ", "_");
        int length = MSQL.getColumnsLength();
        Object[] newValues = new Object[length];

        GetNodesValuesImpl gn = new GetNodesValuesImpl(newValues);
        forEachAction(length, gn);

        boolean returnValue = ms.updateRow(tableName, selectedRow, gn.getValues());
        if (returnValue) {
            ms.selectData(tableName, new SelectData(this, SelectData.MESSAGE_UPDATED_ROW + tableName));
            System.out.println(SUCCESS);
        } else {
            System.out.println(FAILED);
        }
    }

    @FXML
    private void btnFindAction() {
        // CHANGE
    }

    @FXML
    private void btnAddAction() {
        System.out.println(CC.CYAN + "\nINSERT ROW" + CC.RESET);
        int length = MSQL.getColumns().length;
        Object[] values = new Object[length];
        GetNodesValuesImpl gn = new GetNodesValuesImpl(values);
        forEachAction(length, gn);

        String tableName = MSQL.getCurrentTable().getName().replace(" ", "_");

        ms.setIDataToLong(e -> {
            System.out.println("\tData too long");
            lbStatus.setText(e.getMessage());
            lbStatus.setTextFill(NonCSS.TEXT_FILL_ERROR);
            Timers.getInstance(this).playLbStatusReset();
        });
        boolean update = ms.insert(tableName, gn.getValues());
        if (update) {
            ms.selectData(tableName, new SelectData(this, SelectData.MESSAGE_INSERT + tableName));
            System.out.println(SUCCESS);
        } else {
            System.out.println(FAILED);
        }
    }

    // INIT METHODS -------------------------------------------
    private void nonFXMLNodesInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            lbs[a] = new Label("Column " + (a + 1));
            tfs[a] = new TextField();
            cbs[a] = new ComboBox<String>();
            btns[a] = new Button();

            cbs[a].setEditable(true);
            cbs[a].setMaxWidth(Short.MAX_VALUE);
            /*
             * lbs[a].setMinWidth(60); lbs[a].setPrefWidth(60); lbs[a].setMaxWidth(60);
             * 
             * btns[a].setMinWidth(20); btns[a].setMaxWidth(20);
             */

            lbs[a].setVisible(false);
            tfs[a].setVisible(false);
            btns[a].setVisible(false);

            gridPane.add(lbs[a], 0, a);
            gridPane.add(tfs[a], 1, a);
            gridPane.add(btns[a], 2, a);

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nonFXMLNodesInit();
        // CB ELEMENTS
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            cbElements.add(new ArrayList<>());
        }
        // LISTENERS
        for (ComboBox<String> cb : cbs) {
            cb.getEditor().setOnKeyReleased(this::cbsKeyReleased);
            cb.getEditor().setOnMouseClicked(this::cbsMouseClicked);
        }

        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().selectedItemProperty().addListener(this::tableRowSelected);
    }
    // GET AND SET -------------------------------------------

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public VLController getVl() {
        return vl;
    }

    public void setVl(VLController vl) {
        this.vl = vl;
    }

    public Menu getMenuOpen() {
        return menuOpen;
    }

    public void setMenuOpen(Menu menuOptions) {
        this.menuOpen = menuOptions;
    }

    public Menu getMenuSelection() {
        return menuSelection;
    }

    public void setMenuSelection(Menu menuSelection) {
        this.menuSelection = menuSelection;
    }

    public Menu getMenuTable() {
        return menuTable;
    }

    public void setMenuTable(Menu menuTable) {
        this.menuTable = menuTable;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public Label[] getLbs() {
        return lbs;
    }

    public void setLbs(Label[] lbs) {
        this.lbs = lbs;
    }

    public TextField[] getTfs() {
        return tfs;
    }

    public void setTfs(TextField[] tfs) {
        this.tfs = tfs;
    }

    public Button[] getBtns() {
        return btns;
    }

    public void setBtns(Button[] btns) {
        this.btns = btns;
    }

    public Label getLbTable() {
        return lbTable;
    }

    public void setLbTable(Label lbTable) {
        this.lbTable = lbTable;
    }

    public ComboBox[] getCbs() {
        return cbs;
    }

    public void setCbs(ComboBox[] cbs) {
        this.cbs = cbs;
    }

    public TableView<ObservableList<Object>> getTable() {
        return table;
    }

    public void setTable(TableView<ObservableList<Object>> table) {
        this.table = table;
    }

    public MSQLP getMs() {
        return ms;
    }

    public void setMs(MSQLP ms) {
        this.ms = ms;
    }

    public Label getLbStatus() {
        return lbStatus;
    }

    public void setLbStatus(Label lbStatus) {
        this.lbStatus = lbStatus;
    }

    public List<List<String>> getCbElements() {
        return cbElements;
    }

    public void setCbElements(List<List<String>> cbElements) {
        this.cbElements = cbElements;
    }

}
