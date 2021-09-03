package com.cofii.ts.cu;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import com.cofii.ts.cu.store.ImageCRow;
import com.cofii.ts.sql.MSQL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class VImageCController implements Initializable {

    private Stage stage;
    @FXML
    private VBox vboxMain;

    @FXML
    private TextField tfColumnSelect;
    @FXML
    private TextField tfNumberImageC;
    @FXML
    private TextField tfDisplayOrder;

    @FXML
    private ScrollPane scGridpane;
    @FXML
    private GridPane gridpane;
    private final List<ImageCRow> gridData = new ArrayList<>(MSQL.MAX_PATHS);
    private final List<TextField> tfsPath = new ArrayList<>(MSQL.MAX_PATHS);
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    // BOTTOM---------------------------
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnOk;

    // LISTENERS------------------------------------
    private void btnSelectPathAction(ActionEvent e) {
        int[] index = {-1};
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
        int[] index = {-1};
        gridData.stream().anyMatch(data -> {
            index[0]++;
            return data.getBtnAdd() == ((Button) e.getSource());
        });
        newRow(++index[0]);
    }

    private void btnRemoveAction(ActionEvent e) {
        int[] index = {-1};
        gridData.stream().anyMatch(data -> {
            index[0]++;
            return data.getBtnRemove() == ((Button) e.getSource());
        });
        removeRow(index[0]);
    }

    // INIT-----------------------------------------
    private void newRow(int index) {
        TextField tfPath = new TextField();
        tfsPath.add(index, tfPath);

        Button btnSelectPath = new Button();
        Button btnAdd = new Button("Add");
        Button btnRemove = new Button("Remove");

        btnSelectPath.setId(Integer.toString(index));
        btnAdd.setId(Integer.toString(index));
        btnRemove.setId(Integer.toString(index));

        btnSelectPath.setOnAction(this::btnSelectPathAction);
        btnAdd.setOnAction(this::btnAddAction);
        btnRemove.setOnAction(this::btnRemoveAction);

        gridData.add(index, new ImageCRow(tfPath, btnSelectPath, btnAdd, btnRemove));
        gridpane.add(tfPath, 0, index);
        gridpane.add(btnSelectPath, 1, index);
        gridpane.add(btnAdd, 2, index);
        gridpane.add(btnRemove, 3, index);

        int[] indexs = {1};
        tfsPath.forEach(tf -> tf.setPromptText("Path " + (indexs[0]++)));
    }

    private void removeRow(int index){
        tfsPath.remove(index);

        ImageCRow row = gridData.get(index);
        gridpane.getChildren().removeAll(row.getTfPath(), row.getBtnSelectPath(), row.getBtnAdd(), row.getBtnRemove());
        gridData.remove(index);

        int[] indexs = {1};
        tfsPath.forEach(tf -> tf.setPromptText("Path " + (indexs[0]++)));
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newRow(0);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
