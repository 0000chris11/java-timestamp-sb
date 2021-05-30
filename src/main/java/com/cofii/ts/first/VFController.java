package com.cofii.ts.first;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.cofii.ts.login.VLController;
import com.cofii.ts.other.ActionForEachNode;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.Dist;
import com.cofii.ts.other.GetNodesValuesImpl;
import com.cofii.ts.other.GetRowSelectedImpl;
import com.cofii.ts.other.MultipleValuesSelectedImpl;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.other.Timers;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.store.ColumnDS;
import com.cofii.ts.store.ColumnS;
import com.cofii2.components.javafx.TextFieldAutoC;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class VFController implements Initializable {

    private VLController vl;
    private Dist dist = Dist.getInstance(this);
    // ----------------------------------------
    private Stage stage;
    private Scene scene;

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
    private TextFlow[] lbs = new TextFlow[MSQL.MAX_COLUMNS];
    private TextField[] tfs = new TextField[MSQL.MAX_COLUMNS];
    // private ComboBox[] cbs = new ComboBox[MSQL.MAX_COLUMNS];
    private TextFieldAutoC[] tfas = new TextFieldAutoC[MSQL.MAX_COLUMNS];
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
    private ObservableList<ObservableList<Object>> tableData;

    private ColumnS columns = ColumnS.getInstance();
    private ColumnDS columnds = ColumnDS.getInstance();
    private Object[] rowData;
    private Object[] selectedRow;
    private MSQLP ms;
    // ----------------------------------------------
    private static final String SUCCESS = "\tsuccess";
    private static final String FAILED = "\tfailed";

    private static final int CB_STARTS_WITH = 0;
    private int cbSearchOption = CB_STARTS_WITH;

    // OTHER -------------------------------------------
    private void forEachAction(int length, ActionForEachNode en) {
        for (int a = 0; a < length; a++) {
            // MISING FOR PRIMARY KEY
            System.out.println("a: " + (a + 1));
            if (columnds.getDist(a).equals("No")) {
                en.forTFS(tfs[a], a);
            } else {
                en.forTFAS(tfas[a], a);

            }

            en.either(a);
        }
    }

    private void comboBoxConfig() {
        /*
         * for (int a = 0; a < cbs.length; a++) { ComboBoxListViewSkin<String>
         * comboBoxListViewSkin = new ComboBoxListViewSkin<>(cbs[a]);
         * comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, event ->
         * { if (event.getCode() == KeyCode.SPACE) { event.consume(); } });
         * cbs[a].setSkin(comboBoxListViewSkin); }
         */
    }

    // LISTENER -----------------------------------------
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

    public void tableCellChanged(Change<? extends ObservableList<Object>> c) {
        System.out.println("\nTABLE CHANGED");
        while (c.next()) {
            if (c.wasUpdated()) {
                System.out.println("\tRow original state: " + Arrays.toString(selectedRow));
                tableData.subList(c.getFrom(), c.getTo()).forEach(System.out::println);
            }
        }

    }

    public void tableCellEdit(CellEditEvent<ObservableList<Object>, Object> t) {
        System.out.println("OLD Value: " + t.getOldValue().toString());
        System.out.println("NEW Value: " + t.getNewValue().toString());

        Object oldValue = t.getOldValue();
        Object newValue = t.getNewValue();
        if (!newValue.toString().equals(oldValue.toString())) {
            String tableName = MSQL.getCurrentTable().getName().replace(" ", "_");
            int colIndex = t.getTablePosition().getColumn();
            String columnName = columns.getColumn(colIndex);

            boolean returnValue = ms.updateRow(tableName, selectedRow, columnName, newValue);
            if (returnValue) {
                ms.selectData(tableName, new SelectData(this, SelectData.MESSAGE_UPDATED_ROW + tableName));
                dist.distAction();
                System.out.println(SUCCESS);
            } else {
                System.out.println(FAILED);
            }
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
            dist.distAction();
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
            dist.distAction();
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
            Timers.getInstance(this).playLbStatusReset(lbStatus);
        });
        boolean update = ms.insert(tableName, gn.getValues());
        if (update) {
            ms.selectData(tableName, new SelectData(this, SelectData.MESSAGE_INSERT + tableName));
            System.out.println(SUCCESS);
            dist.distAction();
        } else {
            System.out.println(FAILED);
        }
    }
    // RESET ----------------------------------------------
    public void clearCurrentTableView(){
        lbTable.setText("No Table Selected");
        
        Arrays.asList(lbs).forEach(e -> e.setVisible(false));
        Arrays.asList(tfs).forEach(e -> e.setVisible(false));
        Arrays.asList(tfas).forEach(e -> e.setVisible(false));
        Arrays.asList(btns).forEach(e -> e.setVisible(false));

        table.getColumns().clear();
    }
    // INIT METHODS -------------------------------------------
    private void nonFXMLNodesInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            Text text = new Text("Column " + (a + 1));
            text.setFill(NonCSS.TEXT_FILL);
            lbs[a] = new TextFlow(text);
            tfs[a] = new TextField();
            tfas[a] = new TextFieldAutoC(a);
            btns[a] = new Button();

            tfas[a].getTf().setStyle(CSS.TFAS_DEFAULT_LOOK);

            lbs[a].setVisible(false);
            tfs[a].setVisible(false);
            btns[a].setVisible(false);

            GridPane.setMargin(lbs[a], new Insets(2, 2, 2, 2));
            GridPane.setMargin(tfs[a], new Insets(2, 2, 2, 2));
            GridPane.setMargin(tfas[a], new Insets(2, 2, 2, 2));
            GridPane.setMargin(btns[a], new Insets(2, 2, 2, 2));

            gridPane.add(lbs[a], 0, a);
            gridPane.add(tfs[a], 1, a);
            gridPane.add(btns[a], 2, a);

            gridPane.getRowConstraints().get(a).setValignment(VPos.TOP);
            // gridPane.getRowConstraints().get(a).setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().get(a).setPrefHeight(-1);
            gridPane.getRowConstraints().get(a).setMaxHeight(-1);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nonFXMLNodesInit();

        comboBoxConfig();
        // CB ELEMENTS
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            cbElements.add(new ArrayList<>());
        }
        // LISTENERS
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

    public TextFlow[] getLbs() {
        return lbs;
    }

    public void setLbs(TextFlow[] lbs) {
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

    public TextFieldAutoC[] getTfas() {
        return tfas;
    }

    public void setTfas(TextFieldAutoC[] tfas) {
        this.tfas = tfas;
    }

    public ObservableList<ObservableList<Object>> getTableData() {
        return tableData;
    }

    // + LISTENER-----------------
    public void setTableData(ObservableList<ObservableList<Object>> tableData) {
        this.tableData = tableData;
        // tableData.addListener(this::tableCellChanged);
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

}
