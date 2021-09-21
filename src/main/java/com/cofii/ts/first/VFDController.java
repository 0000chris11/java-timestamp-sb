package com.cofii.ts.first;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.cofii.ts.other.CSS;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.main.Table;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.popup.PopupAutoC;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class VFDController implements Initializable {

    @FXML
    private TextField tfText;
    @FXML
    private TextField tfColumn;
    @FXML
    private Button btnFind;

    @FXML
    private HBox hbSearch;
    @FXML
    private Label lbSearch;
    @FXML
    private Label lbResult;
    // ------------------------------------------------------------
    private VFController vf;
    private Table currenTable = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable();
    // ------------------------------------------------------------
    private void resetResult(){
        lbSearch.setTextFill(NonCSS.TEXT_FILL);
        lbResult.setTextFill(NonCSS.TEXT_FILL);

        lbResult.setText("0");
    }
    // ------------------------------------------------------------
    private void btnFindAction(ActionEvent ev) {
        String table = currenTable.getName();
        String text = tfText.getText();
        String column = tfColumn.getText();

        vf.getTable().getSelectionModel().clearSelection();
        // Object[] data = vf.getMs().selectValues(table.replace(" ", "_"), column,
        // text);
        ObservableList<ObservableList<Object>> rows = vf.getTable().getItems();
        int colIndex = -1;
        if (!column.equals("Any")) {
            colIndex = currenTable.getColumnIndex(column);
        }
        int matchResult = 0;
        for (int a = 0; a < rows.size(); a++) {
            // TableColumn<ObservableList<Object>,?>>
            boolean rowMatch;
            if (!column.equals("Any")) {// SINGLE COLUMN SEARCH
                rowMatch = rows.get(a).get(colIndex).toString().equals(text);
            } else {// MULTIPLE COLUMNS SEARCH
                rowMatch = rows.get(a).stream().anyMatch(e -> e.toString().equals(text));
            }

            if (rowMatch) {
                vf.getTable().getSelectionModel().select(a);
                matchResult++;
            }
        }

        lbSearch.setTextFill(matchResult > 0 ? NonCSS.TEXT_FILL_OK : NonCSS.TEXT_FILL_ERROR);
        lbResult.setTextFill(matchResult > 0 ? NonCSS.TEXT_FILL_OK : NonCSS.TEXT_FILL_ERROR);
        lbResult.setText(Integer.toString(matchResult));
    }

    private void tfTextKeyReleased(KeyEvent e){
        resetResult();
    }

    private void tfColumnTextProperty(String newValue) {
        resetResult();

        if (currenTable.getColumns().stream().anyMatch(col -> newValue.equals(col.getName()))) {
            tfColumn.setStyle(null);
            btnFind.setDisable(false);
        } else {
            tfColumn.setStyle(CSS.NODE_BORDER_ERROR);
            btnFind.setDisable(true);
        }
    }

    // ------------------------------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfText.setStyle(null);
        tfColumn.setStyle(null);
        // ---------------------------------------
        tfText.setOnKeyReleased(this::tfTextKeyReleased);

        int columnSize = currenTable.getColumns().size();
        String[] columnsArray = new String[columnSize + 1];// .................
        for (int a = 0; a < columnSize; a++) {// COULN'T FIGURE OUT OF A SIMPLE WAY TO DO IT
            columnsArray[a] = currenTable.getColumns().get(a).getName();
        }
        columnsArray[columnSize] = "Any";
        new PopupAutoC(tfColumn, columnsArray);// .................................
        tfColumn.textProperty().addListener((obs, oldValue, newValue) -> tfColumnTextProperty(newValue));

        btnFind.setOnAction(this::btnFindAction);
    }

    // ------------------------------------------------------------
    public VFController getVf() {
        return vf;
    }

    public void setVf(VFController vf) {
        this.vf = vf;
    }

}
