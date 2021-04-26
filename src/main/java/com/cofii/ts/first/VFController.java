package com.cofii.ts.first;

import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.login.VLController;
import com.cofii.ts.other.ActionForEachNode;
import com.cofii.ts.other.GetNodesValuesImpl;
import com.cofii.ts.other.MultipleValuesSelectedImpl;
import com.cofii.ts.other.GetRowSelectedImpl;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectData;
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
    //----------------------
    private static final String SUCCESS = "\tsuccess";
    private static final String FAILED = "\tfailed";
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
        String tableName = MSQL.getTable().getName().replace(" ", "_");
        boolean returnValue = ms.deleteRow(tableName, rowData);
        if (returnValue) {
            ms.selectData(tableName, new SelectData(this, "Deleted Row on " + tableName));
            System.out.println(SUCCESS);
        }else{
            System.out.println(FAILED);
        }
    }

    @FXML
    private void btnUpdateAction() {
        System.out.println(CC.CYAN + "\nUPDATE ROW" + CC.RESET);
        String tableName = MSQL.getTable().getName().replace(" ", "_");
        int length = MSQL.getColumnsLength();
        Object[] newValues = new Object[length];

        GetNodesValuesImpl gn = new GetNodesValuesImpl(newValues);
        forEachAction(length, gn);

        boolean returnValue = ms.updateRow(tableName, selectedRow, gn.getValues());
        if (returnValue) {
            ms.selectData(tableName, new SelectData(this, "Updated on " + tableName));
            System.out.println(SUCCESS);
        }else{
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

        String tableName = MSQL.getTable().getName().replace(" ", "_");
        boolean update = ms.insert(tableName, gn.getValues());
        if (update) {
            ms.selectData(tableName, new SelectData(this, "Inserted on " + tableName));
            System.out.println(SUCCESS);
        }else{
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

}
