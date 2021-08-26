package com.cofii.ts.cu;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class VImageCController implements Initializable{

    @FXML
    private VBox vboxMain;

    @FXML
    private TextField tfColumnSelect;
    @FXML
    private ToggleButton btnChangeColumnSelect;

    @FXML
    private TextField tfNumberImageC;

    @FXML
    private ScrollPane scGridpane;
    @FXML
    private GridPane gridpane;
    //BOTTOM---------------------------
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnOk;
    //INIT-----------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        
    }
    
}
