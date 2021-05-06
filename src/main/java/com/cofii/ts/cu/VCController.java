package com.cofii.ts.cu;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.cofii.ts.other.CSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.SQLTypes;
import com.cofii2.components.javafx.TextFieldAutoC;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class VCController implements Initializable {

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

    private HBox[] hbsN = new HBox[MSQL.MAX_COLUMNS];//-----------
    private Label[] lbsN = new Label[MSQL.MAX_COLUMNS];
    private HBox[] hbsName = new HBox[MSQL.MAX_COLUMNS];//-----------
    private TextField[] tfsColumn = new TextField[MSQL.MAX_COLUMNS];
    private Button[] btnsRemoveColumn = new Button[MSQL.MAX_COLUMNS];
    private Button[] btnsAddColumn = new Button[MSQL.MAX_COLUMNS];
    private Button[] btnsRenameColumn = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsType = new HBox[MSQL.MAX_COLUMNS];//-----------
    private TextFieldAutoC[] tfasType = new TextFieldAutoC[MSQL.MAX_COLUMNS];
    private TextField[] tfsTypeLength = new TextField[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeType = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsNull = new HBox[MSQL.MAX_COLUMNS];//-----------
    private CheckBox[] cksNull = new CheckBox[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeNull = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsPK = new HBox[MSQL.MAX_COLUMNS];//-----------
    private CheckBox[] cksPK = new CheckBox[MSQL.MAX_COLUMNS];
    private Button[] btnsChangePK = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsFK = new HBox[MSQL.MAX_COLUMNS];//-----------
    private CheckBox[] cksFK = new CheckBox[MSQL.MAX_COLUMNS];
    private TextFieldAutoC[] tfasFK = new TextFieldAutoC[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeFK = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsDefault = new HBox[MSQL.MAX_COLUMNS];//-----------
    private CheckBox[] cksDefault = new CheckBox[MSQL.MAX_COLUMNS];
    private TextField[] tfsDefault = new TextField[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeDefault = new Button[MSQL.MAX_COLUMNS];
    private HBox[] hbsExtra = new HBox[MSQL.MAX_COLUMNS];//-----------
    private RadioButton[] rbsExtra = new RadioButton[MSQL.MAX_COLUMNS];
    private Button[] btnsChangeExtra = new Button[MSQL.MAX_COLUMNS];

    private ToggleButton[] btnsDist = new ToggleButton[MSQL.MAX_COLUMNS];
    //---------------------------------------------
    private SQLTypes types = SQLTypes.getInstance();
    //---------------------------------------------
    private void btnsAddAction(ActionEvent e){
        Button btn = (Button) e.getSource();
        int index = Integer.parseInt(btn.getId()) + 1;
        int row = index + 1;
        gridPaneLeft.add(hbsN[index], 0, row );
        gridPaneLeft.add(hbsName[index], 1, row);
        gridPaneLeft.add(hbsType[index], 2, row);
        gridPaneLeft.add(hbsNull[index], 3, row);
        gridPaneLeft.add(hbsPK[index], 4, row);
        gridPaneLeft.add(hbsFK[index], 5, row);
        gridPaneLeft.add(hbsDefault[index], 6, row);
        gridPaneLeft.add(hbsExtra[index], 7, row);
    }
    private void cksFKAction(ActionEvent e){
        CheckBox ck = (CheckBox) e.getSource();
        int index = Integer.parseInt(ck.getId());
        tfasFK[index].setVisible(true);
    }
    //---------------------------------------------
    private void nonFXMLNodesInit(){
        for(int a = 0;a < MSQL.MAX_COLUMNS; a++){

            lbsN[a] = new Label("Column " + (a + 1));
            hbsN[a] = new HBox(lbsN[a]);
            hbsN[a].setStyle(CSS.BORDER_GRID_BOTLEFT);

            tfsColumn[a] = new TextField();
            tfsColumn[a].setPrefWidth(-1);
            btnsRemoveColumn[a] = new Button("X");
            btnsRemoveColumn[a].setMinWidth(40);
            btnsAddColumn[a] = new Button("+");
            btnsAddColumn[a].setMinWidth(40);
            btnsAddColumn[a].setId(Integer.toString(a));
            btnsRenameColumn[a] = new Button("C");
            btnsRenameColumn[a].setVisible(false);
            hbsName[a] = new HBox(tfsColumn[a], btnsRemoveColumn[a], btnsAddColumn[a], btnsRenameColumn[a]);
            hbsName[a].setStyle(CSS.BORDER_GRID_BOTLEFT);

            tfasType[a] = new TextFieldAutoC(a, types.getTypeNames());
            tfasType[a].setPrefWidth(-1);
            tfasType[a].setMaxHeight(30);
            tfsTypeLength[a] = new TextField("0");
            btnsChangeType[a] = new Button("C");
            btnsChangeType[a].setVisible(false);
            hbsType[a] = new HBox(tfasType[a], tfsTypeLength[a], btnsChangeType[a]);
            hbsType[a].setStyle(CSS.BORDER_GRID_BOTLEFT);

            cksNull[a] = new CheckBox();
            btnsChangeNull[a] = new Button("C");
            btnsChangeNull[a].setVisible(false);
            hbsNull[a] = new HBox(cksNull[a], btnsChangeNull[a]);
            hbsNull[a].setStyle(CSS.BORDER_GRID_BOTLEFT);

            cksPK[a] = new CheckBox();
            btnsChangePK[a] = new Button("C");
            btnsChangePK[a].setVisible(false);
            hbsPK[a] = new HBox(cksPK[a], btnsChangePK[a]);
            hbsPK[a].setStyle(CSS.BORDER_GRID_BOTLEFT);

            cksFK[a] = new CheckBox();
            cksFK[a].setId(Integer.toString(a));
            tfasFK[a] = new TextFieldAutoC(a);
            tfasFK[a].setPrefWidth(-1);
            tfasFK[a].setMaxHeight(30);
            tfasFK[a].setVisible(false);
            btnsChangeFK[a] = new Button("C");
            btnsChangeFK[a].setVisible(false);
            hbsFK[a] = new HBox(cksFK[a], tfasFK[a], btnsChangeFK[a]);
            hbsFK[a].setStyle(CSS.BORDER_GRID_BOTLEFT);

            cksDefault[a] = new CheckBox();
            tfsDefault[a] = new TextField();
            tfsDefault[a].setVisible(false);
            btnsChangeDefault[a] = new Button("C");
            btnsChangeDefault[a].setVisible(false);
            hbsDefault[a] = new HBox(cksDefault[a], tfsDefault[a], btnsChangeDefault[a]);
            hbsDefault[a].setStyle(CSS.BORDER_GRID_BOTLEFT);

            rbsExtra[a] = new RadioButton();
            btnsChangeExtra[a] = new Button("C");
            btnsChangeExtra[a].setVisible(false);
            hbsExtra[a] = new HBox(rbsExtra[a], btnsChangeExtra[a]);
            hbsExtra[a].setStyle(CSS.BORDER_GRID_BOTLEFTRIGHT);
            //--------------------------------------
            btnsDist[a] = new ToggleButton("" + (a + 1));
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nonFXMLNodesInit();
        //gridPaneLeft.setGridLinesVisible(true);
        Arrays.asList(btnsAddColumn).forEach(e -> e.setOnAction(this::btnsAddAction));
        Arrays.asList(cksFK).forEach(e -> e.setOnAction(this::cksFKAction));
    }
    //-------------------------------------------------------------
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
    
}
