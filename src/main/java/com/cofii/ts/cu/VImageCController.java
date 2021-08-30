package com.cofii.ts.cu;

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
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class VImageCController implements Initializable {

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
    // BOTTOM---------------------------
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnOk;

    //LISTENERS------------------------------------
    private void btnSelectPathAction(ActionEvent e){

    }   
    private void btnAddAction(ActionEvent e){

    }
    private void btnRemoveAction(ActionEvent e){

    }
    // INIT-----------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        IntStream.range(0, 1).forEach(i -> {
            TextField tfPath = new TextField();
            tfPath.setPromptText("Path " + (i + 1));
            tfsPath.add(tfPath);

            Button btnSelectPath = new Button();
            btnSelectPath.setOnAction(this::btnSelectPathAction);

            Button btnAdd = new Button("Add");
            btnAdd.setOnAction(this::btnAddAction);
            Button btnRemove = new Button("Add");
            btnRemove.setOnAction(this::btnRemoveAction);

            gridData.add(new ImageCRow(tfPath, btnSelectPath, btnAdd, btnRemove));
            gridpane.add(tfPath, 0, i);
            gridpane.add(btnSelectPath, 1, i);
            gridpane.add(btnAdd, 2, i);
            gridpane.add(btnRemove, 3, i);
        });
    }

}
