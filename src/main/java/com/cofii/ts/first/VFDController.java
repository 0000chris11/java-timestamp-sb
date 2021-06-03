package com.cofii.ts.first;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.cofii.ts.other.CSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.ColumnS;
import com.cofii2.components.javafx.PopupAutoC;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class VFDController implements Initializable {

    @FXML
    private TextField tfText;
    @FXML
    private TextField tfColumn;
    @FXML
    private Button btnFind;
    // ------------------------------------------------------------
    private VFController vf;
    private ColumnS columns = ColumnS.getInstance();

    // ------------------------------------------------------------
    private void btnFindAction(ActionEvent ev) {
        String table = MSQL.getCurrentTable().getName();
        String text = tfText.getText();
        String column = tfColumn.getText();

        Object[] data = vf.getMs().selectValues(table.replace(" ", "_"), column, text);
        ObservableList<ObservableList<Object>> rows = vf.getTable().getItems();
        for (int a = 0; a < rows.size(); a++) {
            boolean rowMatch = rows.get(a).stream().anyMatch(e -> Arrays.asList(data).stream().anyMatch(d -> d.equals(e)));
            if(rowMatch){
                vf.getTable().getSelectionModel().select(a);
            }
        }
    }

    private void tfColumnTextProperty(String newValue) {
        if (Arrays.asList(columns.getColumns()).stream().anyMatch(e -> newValue.equals(e))) {
            tfColumn.setStyle(CSS.TEXT_FILL);
            btnFind.setDisable(false);
        } else {
            tfColumn.setStyle(CSS.TEXT_FILL_ERROR);
            btnFind.setDisable(true);
        }
    }

    // ------------------------------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfText.setStyle(CSS.TEXT_FILL);
        tfColumn.setStyle(CSS.TEXT_FILL);
        // ---------------------------------------
        btnFind.setOnAction(this::btnFindAction);

        new PopupAutoC(tfColumn, columns.getColumns());
        tfColumn.textProperty().addListener((obs, oldValue, newValue) -> tfColumnTextProperty(newValue));
    }

    // ------------------------------------------------------------
    public VFController getVf() {
        return vf;
    }

    public void setVf(VFController vf) {
        this.vf = vf;
    }

}
