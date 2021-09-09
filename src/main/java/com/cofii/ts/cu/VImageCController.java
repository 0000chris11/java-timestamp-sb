package com.cofii.ts.cu;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cofii.ts.cu.store.ImageCRow;
import com.cofii.ts.other.CSS;
import com.cofii.ts.sql.MSQL;
import com.cofii2.components.javafx.popup.PopupKV;
import com.cofii2.methods.MList;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class VImageCController implements Initializable {

    private static final String MATCH_A_ITEM = "mathc one of the items";
    // -------------------------------------------
    private Stage stage;

    @FXML
    private VBox vboxMain;

    @FXML
    private ComboBox<String> cbColumnSelect;
    @FXML
    private Label lbColumnSelect;

    @FXML
    private TextField tfNumberImageC;
    @FXML
    private Label lbNumberImageC;

    @FXML
    private ComboBox<String> cbDisplayOrder;
    @FXML
    private Label lbDisplayOrder;

    @FXML
    private ComboBox<String> cbType;
    @FXML
    private Label lbType;

    @FXML
    private ScrollPane scGridpane;
    @FXML
    private GridPane gridpane;
    private final List<ImageCRow> gridData = new ArrayList<>(MSQL.MAX_PATHS);
    private final ObservableList<TextField> tfsPath = FXCollections.observableArrayList();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    // BOTTOM---------------------------
    @FXML
    private Button btnReset;
    @FXML
    private Button btnSaveUpdate;
    @FXML
    private Button btnSaveUpdateHelp;
    private PopupKV btnSaveUpdateHelpKV;
    private ObservableMap<String, Boolean> btnSaveUpdateHelpMap;
    // CONTROL----------------------------------
    private boolean columnSelectOk = true;
    private boolean numberImageC = true;
    private boolean columnDisplayOrderOk = true;
    private boolean columnTypeOk = true;

    private boolean pathsSameOk = true;
    private boolean pathsExists = false;

    private void addImageCConfigControl() {
        restartAddImageCMap();

        lbColumnSelect.setVisible(!columnSelectOk);
        lbNumberImageC.setVisible(!numberImageC);
        lbDisplayOrder.setVisible(!columnDisplayOrderOk);
        lbType.setVisible(!columnTypeOk);

        boolean ok = columnSelectOk && numberImageC && columnDisplayOrderOk && columnTypeOk && pathsSameOk
                && pathsExists;
        btnSaveUpdate.setDisable(!ok);
    }

    private void restartAddImageCMap() {
        btnSaveUpdateHelpMap.put("Select Column", columnSelectOk);
        btnSaveUpdateHelpMap.put("Number of ImageCs", numberImageC);
        btnSaveUpdateHelpMap.put("Column Display Order", columnDisplayOrderOk);
        btnSaveUpdateHelpMap.put("Type", columnTypeOk);
        btnSaveUpdateHelpMap.put("Paths Unique", pathsSameOk);
        btnSaveUpdateHelpMap.put("Paths Exists", pathsExists);
    }

    // LISTENERS------------------------------------
    private void cbColumnSelectTextProperty(String newValue) {
        columnSelectOk = cbColumnSelect.getItems().stream().anyMatch(item -> item.equals(newValue));
        addImageCConfigControl();
    }

    private void tfNumberImageCOnKeyReleased(KeyEvent e) {
        String text = tfNumberImageC.getText();
        try {
            int length = Integer.parseInt(text);
            numberImageC = length <= MSQL.MAX_IMAGES_LENGTH;
        } catch (NumberFormatException ex) {
            numberImageC = false;
        } finally {
            addImageCConfigControl();
        }

    }

    private void cbDisplayOrderTextProperty(String newValue) {
        columnDisplayOrderOk = cbDisplayOrder.getItems().stream().anyMatch(item -> item.equals(newValue));
        addImageCConfigControl();
    }

    private void cbTypeTextProperty(String newValue) {
        columnTypeOk = cbType.getItems().stream().anyMatch(item -> item.equals(newValue));
        addImageCConfigControl();
    }

    // PATHS-------------
    private void tfsPathTextProperty(ObservableValue<? extends String> obs, String newValue) {
        tfsPathChangeListener(null);
        /*
         * StringProperty textProperty = (StringProperty) obs; TextField tf =
         * (TextField) textProperty.getBean();
         * 
         * int[] index = { -1 }; gridData.stream().anyMatch(data -> { index[0]++; return
         * data.getTfPath() == tf; });
         * 
         * tfsPath.set(index, element)
         */
    }

    private void tfsPathChangeListener(Change<? extends TextField> c) {
        boolean action = false;
        if (c != null) {
            while (c.next()) {
                action = true;
            }
        } else {
            action = true;
        }

        if (action) {
            pathsExists = true;
            List<String> list = tfsPath.stream().map(tf -> {
                File file = new File(tf.getText());
                if (file.exists()) {
                    if (file.isDirectory()) {
                        tf.setStyle(CSS.TEXT_FILL);
                    } else {
                        tf.setStyle(CSS.TEXT_FILL_ERROR);
                        pathsExists = false;
                    }
                } else {
                    tf.setStyle(CSS.TEXT_FILL_ERROR);
                    pathsExists = false;
                }

                return tf.getText();
            }).collect(Collectors.toList());

            pathsSameOk = !MList.areTheyDuplicatedElementsOnList(list);
        }

        addImageCConfigControl();
    }

    // BTNS--------------
    private void btnSelectPathAction(ActionEvent e) {
        int[] index = { -1 };
        gridData.stream().anyMatch(data -> {
            index[0]++;
            return data.getBtnSelectPath() == ((Button) e.getSource());
        });
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            tfsPath.get(index[0]).setText(file.getPath());
        }
    }

    private void btnAddAction(ActionEvent e) {
        int[] index = { -1 };
        gridData.stream().anyMatch(data -> {
            index[0]++;
            return data.getBtnAdd() == ((Button) e.getSource());
        });
        newRow(++index[0]);
    }

    private void btnRemoveAction(ActionEvent e) {
        int[] index = { -1 };
        gridData.stream().anyMatch(data -> {
            index[0]++;
            return data.getBtnRemove() == ((Button) e.getSource());
        });
        removeRow(index[0]);
    }

    private void btnResetAction(ActionEvent e) {
        VImageC.setInstance(null);
        stage.close();
    }

    void btnSaveCreateAction(ActionEvent e) {
        stage.close();
    }

    void btnSaveUpdateAction(ActionEvent e) {
        // ADD UPDATE QUERY !!!!!!!!!!!!!!
    }

    // INIT-----------------------------------------
    private void newRow(int index) {
        System.out.println("new Row (" + (index + 1) + ")");
        TextField tfPath = new TextField();
        tfsPath.add(index, tfPath);

        Button btnSelectPath = new Button();
        Button btnAdd = new Button("Add");
        Button btnRemove = new Button("Remove");

        btnSelectPath.setId(Integer.toString(index));
        btnAdd.setId(Integer.toString(index));
        btnRemove.setId(Integer.toString(index));

        tfPath.textProperty().addListener((obs, oldValue, newValue) -> tfsPathTextProperty(obs, newValue));
        btnSelectPath.setOnAction(this::btnSelectPathAction);
        btnAdd.setOnAction(this::btnAddAction);
        btnRemove.setOnAction(this::btnRemoveAction);

        gridData.add(index, new ImageCRow(tfPath, btnSelectPath, btnAdd, btnRemove));

        gridpane.getChildren().clear();
        int[] indexs = { 0 };
        gridData.forEach(data -> {
            gridpane.add(data.getTfPath(), 0, indexs[0]);
            gridpane.add(data.getBtnSelectPath(), 1, indexs[0]);
            gridpane.add(data.getBtnAdd(), 2, indexs[0]);
            gridpane.add(data.getBtnRemove(), 3, indexs[0]);

            indexs[0]++;
        });

        indexs[0] = 1;
        tfsPath.forEach(tf -> tf.setPromptText("Path " + (indexs[0]++)));

        if (gridData.size() == 1) {
            gridData.get(0).getBtnRemove().setDisable(true);
        } else {
            gridData.forEach(data -> data.getBtnRemove().setDisable(false));
        }
    }

    private void removeRow(int index) {
        tfsPath.remove(index);

        ImageCRow row = gridData.get(index);
        gridpane.getChildren().removeAll(row.getTfPath(), row.getBtnSelectPath(), row.getBtnAdd(), row.getBtnRemove());
        gridData.remove(index);

        int[] indexs = { 1 };
        tfsPath.forEach(tf -> tf.setPromptText("Path " + (indexs[0]++)));

        if (gridData.size() == 1) {
            gridData.get(0).getBtnRemove().setDisable(true);
        } else {
            gridData.forEach(data -> data.getBtnRemove().setDisable(false));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newRow(0);

        lbColumnSelect.setStyle(CSS.TEXT_FILL_ERROR);
        lbNumberImageC.setStyle(CSS.TEXT_FILL_ERROR);
        lbDisplayOrder.setStyle(CSS.TEXT_FILL_ERROR);
        lbType.setStyle(CSS.TEXT_FILL_ERROR);

        lbColumnSelect.setText(MATCH_A_ITEM);
        lbNumberImageC.setText("1 to 10 images allowed");
        lbDisplayOrder.setText(MATCH_A_ITEM);
        lbType.setText(MATCH_A_ITEM);

        btnSaveUpdateHelpMap = FXCollections.observableHashMap();
        btnSaveUpdateHelpKV = new PopupKV(btnSaveUpdateHelp, btnSaveUpdateHelpMap);
        restartAddImageCMap();

        // LISTENERS & BINDS----------------------------------
        cbColumnSelect.getEditor().textProperty()
                .addListener((obs, oldValue, newValue) -> cbColumnSelectTextProperty(newValue));
        tfNumberImageC.setOnKeyReleased(this::tfNumberImageCOnKeyReleased);
        cbDisplayOrder.getEditor().textProperty()
                .addListener((obs, oldValue, newValue) -> cbDisplayOrderTextProperty(newValue));
        cbType.getEditor().textProperty().addListener((obs, oldValue, newValue) -> cbTypeTextProperty(newValue));

        gridpane.minWidthProperty().bind(scGridpane.widthProperty());

        tfsPath.addListener(this::tfsPathChangeListener);

        btnReset.setOnAction(this::btnResetAction);
        // btnSaveUpdate.setOnAction(this::btnSaveCreateAction);
        btnSaveUpdateHelp.setOnAction(e -> btnSaveUpdateHelpKV.showPopup());
    }

    // GETTERS & SETTERS----------------------------
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ComboBox<String> getCbColumnSelect() {
        return cbColumnSelect;
    }

    public void setCbColumnSelect(ComboBox<String> cbColumnSelect) {
        this.cbColumnSelect = cbColumnSelect;
    }

    public ComboBox<String> getCbDisplayOrder() {
        return cbDisplayOrder;
    }

    public void setCbDisplayOrder(ComboBox<String> cbDisplayOrder) {
        this.cbDisplayOrder = cbDisplayOrder;
    }

    public ComboBox<String> getCbType() {
        return cbType;
    }

    public void setCbType(ComboBox<String> cbType) {
        this.cbType = cbType;
    }

    public TextField getTfNumberImageC() {
        return tfNumberImageC;
    }

    public void setTfNumberImageC(TextField tfNumberImageC) {
        this.tfNumberImageC = tfNumberImageC;
    }

    public Button getBtnSaveUpdate() {
        return btnSaveUpdate;
    }

    public void setBtnSaveUpdate(Button btnSaveUpdate) {
        this.btnSaveUpdate = btnSaveUpdate;
    }

    public ObservableList<TextField> getTfsPath() {
        return tfsPath;
    }

}
