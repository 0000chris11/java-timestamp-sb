package com.cofii.ts.first;

import java.net.URL;
import java.util.ResourceBundle;

import com.cofii.ts.login.VLController;
import com.cofii.ts.sql.CurrenConnection;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.WrongPassword;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowTableCurrentDB;
import com.cofii2.mysql.MSQLP;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
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
    private Menu menuOptions;
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
    private TableView<ObservableList<String>> table;

    // -----------------------------------------
    // NON-FXML
    private <T> void tableRowSelected(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        ObservableList<ObservableList<String>> list = table.getSelectionModel().getSelectedItems();
        System.out.println("\ntable length selection: " + list.size());
        System.out.println("table sub-list length selection: " + list.get(0).size());

        int c = 0;
        for (String element : list.get(0)) {
            if (!tfs[c].isNeedsLayout()) {
                System.out.println("\tadding to tfs " + (c + 1) + ": " + element);
                tfs[c].setText(element);
            } else if (!cbs[c].isNeedsLayout()) {
                System.out.println("\tadding to cbs " + (c + 1) + ": " + element);
                cbs[c].getEditor().setText(element);
            }else{
                System.out.println("\tERROR");
            }
            c++;
        }
    }

    @FXML
    private void btnDeleteAction(){
        //NOT IMPLEMENTED YET
    }

    @FXML
    private void btnUpdateAction(){
        //NOT IMPLEMENTED YET
    }

    @FXML
    private void btnFindAction(){
        //NOT IMPLEMENTED YET
    }

    @FXML
    private void btnAddAction(){
        //NOT IMPLEMENTED YET
    }
    // -------------------------------------------
    private void nonFXMLNodesInit() {
        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            lbs[a] = new Label("Column " + (a + 1));
            tfs[a] = new TextField();
            cbs[a] = new ComboBox<String>();
            btns[a] = new Button();

            cbs[a].setEditable(true);
            /*
             * lbs[a].setMinWidth(60); lbs[a].setPrefWidth(60); lbs[a].setMaxWidth(60);
             * tfs[a].setMaxWidth(Short.MAX_VALUE); cbs[a].setMaxWidth(Short.MAX_VALUE);
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
        System.out.println("\nVFController STARTS");

        nonFXMLNodesInit();
        // ----------------------------------
        table.getSelectionModel().selectedItemProperty().addListener(this::tableRowSelected);
    }
    // -------------------------------------------

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

    public Menu getMenuOptions() {
        return menuOptions;
    }

    public void setMenuOptions(Menu menuOptions) {
        this.menuOptions = menuOptions;
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

    public TableView<ObservableList<String>> getTable() {
        return table;
    }

    public void setTable(TableView<ObservableList<String>> table) {
        this.table = table;
    }

}
