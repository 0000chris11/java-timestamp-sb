package com.cofii.ts.first;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.cofii.ts.first.nodes.ActionForEachNode;
import com.cofii.ts.first.nodes.ClearNodesDisplayed;
import com.cofii.ts.first.nodes.GetNodesValuesImpl;
import com.cofii.ts.first.nodes.GetRowSelectedImpl;
import com.cofii.ts.first.nodes.MultipleValuesSelectedImpl;
import com.cofii.ts.login.VL;
import com.cofii.ts.login.VLController;
import com.cofii.ts.other.CSS;
import com.cofii.ts.other.Dist;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectData;
import com.cofii.ts.sql.querys.SelectTableNames;
import com.cofii.ts.sql.querys.ShowColumns;
import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Option;
import com.cofii.ts.store.main.Options;
import com.cofii.ts.store.main.Path;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.User;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.LabelStatus;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.methods.MString;
import com.cofii2.mysql.MSQLP;
import com.cofii2.stores.CC;

import org.controlsfx.control.TaskProgressView;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.concurrent.Task;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VFController implements Initializable {

    private VLController vl;
    private VF vf;
    private Dist dist = Dist.getInstance(this);
    // ----------------------------------------
    private Stage stage;
    private Scene scene;

    // MENUBAR----------------------------------
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuOpen;
    @FXML
    private Menu menuOptions;
    // CENTER---------------------------------------
    @FXML
    private BorderPane bpMain;
    @FXML
    private SplitPane splitLeft;
    @FXML
    private ScrollPane spHBImages;
    @FXML
    private HBox hbImages;
    @FXML
    private Label lbDatabaseTable;
    @FXML
    private TextField tfDatabase;
    private PopupAutoC tfDatabaseAutoC;
    @FXML
    private TextField tfTable;
    private PopupAutoC tfTableAutoC;

    @FXML
    private GridPane gridPane;
    private final List<VFRow> rows = new ArrayList<>();
    // BOTTOM-----------------------------------
    @FXML
    private HBox hbStatus;

    private LabelStatus lbStatus = new LabelStatus();
    // private boolean autoClose = false;

    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnFind;
    @FXML
    private Button btnAdd;

    @FXML
    private TableView<ObservableList<Object>> tableView;
    private ObservableList<ObservableList<Object>> tableData;

    private Table currentTable;

    private ImageView[] ivImageC = new ImageView[MSQL.MAX_IMAGES_LENGTH];
    public static final String NO_IMAGE = "ImageC is set to NONE";
    private Label lbImageC = new Label(NO_IMAGE);

    // private ColumnS columns = ColumnS.getInstance();
    // private ColumnDS columnds = ColumnDS.getInstance();
    private Object[] rowData;// DELETE
    private Object[] selectedRow;
    private MSQLP ms;
    // MESSAGES----------------------------------------------
    private static final String SUCCESS = "\tsuccess";
    private static final String FAILED = "\tfailed";

    public static final String NO_DATABASES = "no databases found";
    public static final String NO_DATABASE_SELECTED = "no database selected";
    // --------------------------------------------
    private static final int CB_STARTS_WITH = 0;
    private int cbSearchOption = CB_STARTS_WITH;

    private ChangeListener<String> selectionForEachDatabaseListener = (obs, oldValue,
            newValue) -> selectionForEachDatabase(newValue);
    private ChangeListener<String> selectionForEachTableListener = (obs, oldValue,
            newValue) -> selectionForEachTable(newValue);
    // OTHER -------------------------------------------

    private void forEachAction(int length, ActionForEachNode en) {
        for (int a = 0; a < length; a++) {
            // MISING FOR PRIMARY KEY
            // en.forTFS(tfs[a], a);
            en.forTFS(rows.get(a).getTf(), a);
            // WHEN TEXTAREAS ARE IMPLEMENTED
            // en.forTAS(tfa, a);

            en.either(a);
        }
    }

    // NOT FINISH
    private void forEachAction2(ActionForEachNode en) {
        currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        for (int a = 0; a < currentTable.getColumns().size(); a++) {

            // en.forTFS(tfs[a], a);
            en.forTFS(rows.get(a).getTf(), a);

            // WHEN TEXTAREAS ARE IMPLEMENTED
            // en.forTAS(tfa, a);

            en.either(a);
        }
    }

    // CONTROL--------------------------------------
    void btnAddUpdateControl() {
        // ONLY NON ERROR STYLE (null)

        boolean ok = rows.stream().allMatch(row -> row.getTf().getStyle().equals(null));
        btnAdd.setDisable(!ok);
        btnUpdate.setDisable(!ok);
    }
    // TABLE------------------------------------
    public void selectionForEachDatabase(String newValue) {
        VL.LOG.info("CHANGE DATABASE");
        User user = Users.getInstance().getCurrenUser();
        boolean databaseMatch = user.getDatabases().stream().anyMatch(d -> d.getName().equals(newValue));
        if (databaseMatch) {
            ms.use(newValue);
            lbDatabaseTable.setText(newValue);
            // SELECT ALL TABLES
            vf.mainTablesCreation();
            Menus.getInstance(this).addTablesToTfTableReset(this);

            /*
             * if(user.getCurrentDatabase().getDefaultTable() != null){
             * user.getCurrentDatabase().setCurrentTable(user.getCurrentDatabase().
             * getDefaultTable()); }else{ user.getCurrentDatabase().setCurrentTable( }
             * 
             * ms.selectColumns(table, ac);
             */
        }
    }

    public void selectionForEachTable(String newValue) {
        Task<Void> task2 = new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    tfTableAutoC.hide();

                    VL.LOG.info("CHANGE TABLE [" + newValue + "]");
                    Database currentDatabase = Users.getInstance().getCurrenUser().getCurrentDatabase();
                    currentTable = currentDatabase.getCurrentTable();
                    boolean tableMatch = Database.getTables().stream().anyMatch(t -> t.getName().equals(newValue));

                    if (tableMatch) {
                        selectedRow = null;

                        btnFind.setDisable(false);
                        btnAdd.setDisable(false);

                        tfTableAutoC.getDisableItems().clear();

                        // ---------------------------------------
                        currentDatabase.setCurrentTable(currentDatabase.getTable(newValue));
                        currentTable = currentDatabase.getCurrentTable();

                        String databaseName = currentDatabase.getName();
                        String tableName = newValue;
                        System.out.println("\ttable: " + tableName);

                        lbDatabaseTable.setText(databaseName + "." + tableName);
                        lbDatabaseTable.setTooltip(new Tooltip(lbDatabaseTable.getText()));
                        lbDatabaseTable.getTooltip().setShowDelay(Duration.ZERO);

                        tfTableAutoC.getDisableItems().add(tableName);
                        // tfTableAutoC.getLv().getSelectionModel().clearSelection();
                        // SELECT -------------------------------------
                        String tableA = tableName.replace(" ", "_");
                        currentTable.getImageCPaths().clear();

                        ms.selectDataWhere(MSQL.TABLE_NAMES, "name", tableName, new SelectTableNames(true));
                        ms.selectColumns(tableA, new ShowColumns(VFController.this));
                        ms.selectDataWhere(MSQL.TABLE_CUSTOMS, "id_table", currentTable.getId(),
                                vf::selectCustomForCurrentTable);
                        ms.selectDataWhere(MSQL.TABLE_PATHS, "id_table", currentTable.getId(),
                                vf::selectTablePathsForEachTable);
                        ms.selectDataWhere(MSQL.TABLE_IMAGECS, "id_table", currentTable.getId(),
                                vf::selectImageCSForCurrentTable);  
                                 
                        Menus.getInstance(VFController.this).resetCurrentTableKeys();

                        dist.distStart();

                        ms.selectData(tableA,
                                new SelectData(VFController.this, SelectData.MESSGE_TABLE_CHANGE + tableName));

                        currentTable = currentDatabase.getCurrentTable();
                        System.out.println("\tMSQL's table: " + currentTable.getId() + " - " + currentTable.getName()
                                + " - " + currentTable.getDist());
                    }
                });
                return null;
            }
        };
        new Thread(task2).start();

    }

    private <T> void tableRowSelected(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        ObservableList<ObservableList<Object>> list = tableView.getSelectionModel().getSelectedItems();
        if (list.size() == 1) {// ONE ROW SELECTED
            rowData = new Object[list.get(0).size()];
            selectedRow = list.get(0).toArray();
            GetRowSelectedImpl nr = new GetRowSelectedImpl(this, selectedRow);
            forEachAction(rowData.length, nr);
            // ImageC----------------------------------------
            if (!currentTable.getImageCColumnName().equals("NONE")) {
                List<Path> imageCPaths = currentTable.getImageCPaths();
                if (imageCPaths.stream().allMatch(path -> new File(path.getPathName()).exists())) {

                    int imageCIndex = currentTable.getColumnIndex(currentTable.getImageCColumnName());
                    String itemSelected = list.get(0).get(imageCIndex).toString();
                    // System.out.println("itemSelected: " + itemSelected);
                    String formattedSelectedText = MString.getCustomFormattedString(itemSelected);

                    // RANDOM (DIRECTORY OPTION ONLY!) -----------------------------------
                    if (currentTable.getDisplayOrder().equals("Random")) {
                        Collections.shuffle(dist.getImageCFilesPath());
                    }

                    List<String> itemsMatch = dist.getImageCFilesPath().stream().filter(e -> {
                        String subFile = e.replaceAll("(.jpg|.png|.gif)$", "");
                        if (formattedSelectedText.contains("; ") && !currentTable.getImageType().equals("Folder")) {
                            String[] split = formattedSelectedText.split("; ");
                            List<String> spList = Arrays.asList(split);
                            // System.out.println("SPLITS");

                            return spList.stream().anyMatch(se -> subFile.endsWith(se));
                        } else {
                            // System.out.println("formattedSelectedText: [" + formattedSelectedText + "]");
                            return subFile.endsWith(formattedSelectedText) || subFile.equals(formattedSelectedText);
                        }
                    }).collect(Collectors.toList());
                    // System.out.println("SIZE: " + itemsMatch.size() + "
                    // ---------------------------------------------");
                    hbImages.getChildren().clear();
                    Arrays.asList(ivImageC).forEach(e -> e.setImage(null));

                    if (!itemsMatch.isEmpty()) {

                        int[] imageCounter = { 0 };
                        final boolean[] isDirectory = { false };
                        final boolean[] isFile = { false };
                        itemsMatch.forEach(e -> {
                            String filePath = itemsMatch.get(imageCounter[0]);
                            File toShow = new File(filePath);
                            if (toShow.isFile() && !isDirectory[0]) {
                                ivImageC[imageCounter[0]++].setImage(new Image(toShow.toURI().toString()));
                                isFile[0] = true;
                            } else if (toShow.isDirectory() && !isFile[0]) {// ALWAYS SHOUL BE ONE MATCH
                                imageCounter[0] = 0;
                                Arrays.asList(toShow.listFiles()).stream().limit(MSQL.DEFAULT_IMAGES_LENGTH)
                                        .forEach(file -> ivImageC[imageCounter[0]++]
                                                .setImage(new Image(file.toURI().toString())));
                                isDirectory[0] = true;
                            }
                        });
                        for (int a = 0; a < imageCounter[0]; a++) {
                            hbImages.getChildren().add(ivImageC[a]);
                        }
                    } else {
                        ivImageC[0].setImage(new Image(
                                VFController.class.getResource("/com/cofii/ts/images/Black.png").toExternalForm()));
                        hbImages.getChildren().add(ivImageC[0]);
                    }
                }
            }
            // ----------------------------------------------
            btnDelete.setDisable(false);
            btnUpdate.setDisable(false);
        } else if (list.size() > 1) {
            forEachAction(currentTable.getColumns().size(), new MultipleValuesSelectedImpl());
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
        currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();

        Object oldValue = t.getOldValue();
        Object newValue = t.getNewValue();
        if (!newValue.toString().equals(oldValue.toString())) {
            String tableName = currentTable.getName().replace(" ", "_");
            int colIndex = t.getTablePosition().getColumn();
            String columnName = currentTable.getColumns().get(colIndex).getName();

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

    // MAIN SQL FUNC----------------------------------
    @FXML
    private void btnDeleteAction() {
        currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        System.out.println(CC.CYAN + "\nDELETE ROW" + CC.RESET);
        String tableName = currentTable.getName().replace(" ", "_");
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
        currentTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
        System.out.println(CC.CYAN + "\nUPDATE ROW" + CC.RESET);
        String tableName = currentTable.getName().replace(" ", "_");
        int length = currentTable.getColumns().size();
        Object[] newValues = new Object[length];

        GetNodesValuesImpl gn = new GetNodesValuesImpl(newValues);
        forEachAction(length, gn);

        // UPDATE QUERY-------------------------------------
        boolean returnValue = ms.updateRow(tableName, selectedRow, gn.getValues());
        if (returnValue) {
            ms.selectData(tableName, new SelectData(this, SelectData.MESSAGE_UPDATED_ROW + tableName));
            dist.distAction();

            boolean updateClear = Options.getInstance().getOptionByName(Option.UPDATE_CLEAR).getValue().equals("true");
            if (updateClear) {
                ClearNodesDisplayed clearNodes = new ClearNodesDisplayed();
                forEachAction(length, clearNodes);
            }
            System.out.println(SUCCESS);
        } else {
            System.out.println(FAILED);
        }
    }

    @FXML
    private void btnFindAction() {
        new VFD(this);
    }

    @FXML
    private void btnAddAction() {
        User user = Users.getInstance().getCurrenUser();
        currentTable = user.getCurrentDatabase().getCurrentTable();

        System.out.println(CC.CYAN + "\nINSERT ROW" + CC.RESET);
        int length = currentTable.getColumns().size();
        Object[] values = new Object[length];
        GetNodesValuesImpl gn = new GetNodesValuesImpl(values);
        forEachAction(length, gn);

        String tableName = currentTable.getName().replace(" ", "_");

        // EXCEPTION---------------------------
        ms.setIDataToLong(e -> {
            System.out.println("\tData too long");
            lbStatus.setText(e.getMessage(), lbStatus.getTextFillError(), Duration.seconds(2));
        });
        // UPDATE QUERY-------------------------
        boolean update = ms.insert(tableName, gn.getValues());
        if (update) {
            ms.selectData(tableName, new SelectData(this, SelectData.MESSAGE_INSERT + tableName));
            dist.distAction();

            boolean insertClear = Options.getInstance().getOptionByName(Option.INSERT_CLEAR).getValue().equals("true");
            if (insertClear) {
                ClearNodesDisplayed clearNodes = new ClearNodesDisplayed();
                forEachAction(length, clearNodes);

            }
            System.out.println(SUCCESS);
        } else {
            System.out.println(FAILED);
        }
    }

    private void splitLeftPositionProperty(Number newValue) {
        hbImages.getChildren().forEach(e -> {
            if (e instanceof ImageView) {
                ((ImageView) e).setFitHeight(spHBImages.getHeight());
            }
        });
    }

    // RESET ----------------------------------------------
    public void clearCurrentTableView() {
        lbDatabaseTable.setText("No Table Selected");

        // Arrays.asList(lbs).forEach(e -> e.setVisible(false));
        // Arrays.asList(tfs).forEach(e -> e.setVisible(false));
        // Arrays.asList(btns).forEach(e -> e.setVisible(false));
        rows.forEach(e -> {
            e.getLb().setVisible(false);
            e.getVbCenter().setVisible(false);
            e.getHbBtns().setVisible(false);
        });

        splitLeft.setDividerPositions(1.0);

        tableView.getColumns().clear();

        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        btnFind.setDisable(true);
        btnAdd.setDisable(true);
    }

    // INIT METHODS -------------------------------------------
    private void nonFXMLNodesInit() {
        /*
         * for (int a = 0; a < MSQL.MAX_COLUMNS; a++) { Text text = new Text("Column " +
         * (a + 1)); text.setFill(NonCSS.TEXT_FILL); lbs[a] = new TextFlow(text); tfs[a]
         * = new TextField(); tfsAutoC.add(a, new PopupAutoC(tfs[a]));
         * tfsFKList.add(FXCollections.observableArrayList()); // FK MATCH /*
         * tfsAutoC.add(a, TextFields.bindAutoCompletion(tfs[a]));
         * tfsAutoC.get(a).setDelay(0);
         * tfsAutoC.get(a).setOnAutoCompleted(this::tfsSetOnAutoCompleted);
         * tfs[a].addEventHandler(KeyEvent.KEY_RELEASED, e -> { if
         * (!e.getCode().isNavigationKey()) { int index = Integer.parseInt(((TextField)
         * e.getSource()).getId()); String[] split = tfs[index].getText().split("; ");
         * if (split.length > 1) { tfsAutoC.get(index).setUserInput(split[split.length -
         * 1]); } } }); END
         * 
         * btns[a] = new ToggleButton();
         * 
         * // tfas[a].setStyle(CSS.TFAS_DEFAULT_LOOK);
         * tfs[a].setId(Integer.toString(a)); btns[a].setId(Integer.toString(a));
         * 
         * lbs[a].setVisible(false); tfs[a].setVisible(false);
         * btns[a].setVisible(false); btns[a].setSelected(true);
         * 
         * GridPane.setMargin(lbs[a], new Insets(2, 2, 2, 2));
         * GridPane.setMargin(tfs[a], new Insets(2, 2, 2, 2)); //
         * GridPane.setMargin(tfas[a], new Insets(2, 2, 2, 2));
         * GridPane.setMargin(btns[a], new Insets(2, 2, 2, 2));
         * 
         * gridPane.add(lbs[a], 0, a); gridPane.add(tfs[a], 1, a); gridPane.add(btns[a],
         * 2, a);
         * 
         * gridPane.getRowConstraints().get(a).setValignment(VPos.TOP);
         * gridPane.getRowConstraints().get(a).setVgrow(Priority.NEVER);
         * gridPane.getRowConstraints().get(a).setPrefHeight(-1);
         * gridPane.getRowConstraints().get(a).setMaxHeight(-1); }
         */
        // Arrays.fill(ivImageC, new ImageView());//DUPLICATED CHILDREN
        for (int a = 0; a < MSQL.MAX_IMAGES_LENGTH; a++) {
            ivImageC[a] = new ImageView();
            ivImageC[a].setPreserveRatio(true);
            // ivImageC[a].fitHeightProperty().bind(fpImages.heightProperty());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // NODES-----------------------------------
        nonFXMLNodesInit();
        // TOP ------------------------------------
        tfDatabaseAutoC = new PopupAutoC(tfDatabase);
        tfDatabaseAutoC.setShowOption(PopupAutoC.WHEN_FOCUS);
        tfDatabaseAutoC.getNoSearchableItems().add(NO_DATABASES);

        tfTableAutoC = new PopupAutoC(tfTable);
        tfTableAutoC.setShowOption(PopupAutoC.WHEN_FOCUS);
        tfTableAutoC.getNoSearchableItems().add(NO_DATABASE_SELECTED);

        // IMAGE-VIEW-------------------------------------
        // hbImages.setOrientation(Orientation.HORIZONTAL);
        hbImages.getChildren().add(lbImageC);
        spHBImages.setFitToHeight(true);
        spHBImages.setFitToWidth(true);

        hbImages.prefHeightProperty().bind(spHBImages.heightProperty());
        hbImages.maxHeightProperty().bind(spHBImages.heightProperty());

        Arrays.asList(ivImageC).forEach(iv -> iv.fitHeightProperty().bind(spHBImages.heightProperty()));
        // LISTENERS =====================================================
        // TOP --------------------------------
        tfDatabaseAutoC.getLv().getSelectionModel().selectedItemProperty()
                .addListener(selectionForEachDatabaseListener);
        tfTableAutoC.getLv().getSelectionModel().selectedItemProperty().addListener(selectionForEachTableListener);
        // CENTER ----------------------------
        gridPane.setVgap(4.0);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.getSelectionModel().selectedItemProperty().addListener(this::tableRowSelected);

        // STATUS
        lbStatus.getStyleClass().add("status");
        lbStatus.getBtnCloseStatus().setStyle(CSS.LB_STATUS_BUTTON);
        HBox.setHgrow(lbStatus, Priority.ALWAYS);
        hbStatus.getChildren().add(0, lbStatus);

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

    public Menu getMenuOptions() {
        return menuOptions;
    }

    public void setMenuOptions(Menu menuOptions) {
        this.menuOptions = menuOptions;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public Label getLbDatabaseTable() {
        return lbDatabaseTable;
    }

    public void setLbDatabaseTable(Label lbDatabaseTable) {
        this.lbDatabaseTable = lbDatabaseTable;
    }

    public TableView<ObservableList<Object>> getTable() {
        return tableView;
    }

    public void setTable(TableView<ObservableList<Object>> table) {
        this.tableView = table;
    }

    public MSQLP getMs() {
        return ms;
    }

    public void setMs(MSQLP ms) {
        this.ms = ms;
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

    public ImageView[] getIvImageC() {
        return ivImageC;
    }

    public void setIvImageC(ImageView[] ivImageC) {
        this.ivImageC = ivImageC;
    }

    public SplitPane getSplitLeft() {
        return splitLeft;
    }

    public void setSplitLeft(SplitPane splitLeft) {
        this.splitLeft = splitLeft;
    }

    public HBox getHbImages() {
        return hbImages;
    }

    public void setHbImages(HBox fpImages) {
        this.hbImages = fpImages;
    }

    public Button getBtnDelete() {
        return btnDelete;
    }

    public void setBtnDelete(Button btnDelete) {
        this.btnDelete = btnDelete;
    }

    public Button getBtnUpdate() {
        return btnUpdate;
    }

    public void setBtnUpdate(Button btnUpdate) {
        this.btnUpdate = btnUpdate;
    }

    public Button getBtnFind() {
        return btnFind;
    }

    public void setBtnFind(Button btnFind) {
        this.btnFind = btnFind;
    }

    public Button getBtnAdd() {
        return btnAdd;
    }

    public void setBtnAdd(Button btnAdd) {
        this.btnAdd = btnAdd;
    }

    public LabelStatus getLbStatus() {
        return lbStatus;
    }

    public void setLbStatus(LabelStatus lbStatus) {
        this.lbStatus = lbStatus;
    }

    public BorderPane getBpMain() {
        return bpMain;
    }

    public void setBpMain(BorderPane bpMain) {
        this.bpMain = bpMain;
    }

    public Object[] getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(Object[] selectedRow) {
        this.selectedRow = selectedRow;
    }

    public PopupAutoC getTfDatabaseAutoC() {
        return tfDatabaseAutoC;
    }

    public void setTfDatabaseAutoC(PopupAutoC tfDatabaseAutoC) {
        this.tfDatabaseAutoC = tfDatabaseAutoC;
    }

    public PopupAutoC getTfTableAutoC() {
        return tfTableAutoC;
    }

    public void setTfTableAutoC(PopupAutoC tfTableAutoC) {
        this.tfTableAutoC = tfTableAutoC;
    }

    public TextField getTfDatabase() {
        return tfDatabase;
    }

    public void setTfDatabase(TextField tfDatabase) {
        this.tfDatabase = tfDatabase;
    }

    public TextField getTfTable() {
        return tfTable;
    }

    public void setTfTable(TextField tfTable) {
        this.tfTable = tfTable;
    }

    public VF getVf() {
        return vf;
    }

    public void setVf(VF vf) {
        this.vf = vf;
    }

    public List<VFRow> getRows() {
        return rows;
    }

}
