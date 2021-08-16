package com.cofii.ts.cu;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.cofii.ts.first.VFController;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.sql.querys.SelectDatabases;
import com.cofii.ts.store.main.Database;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.LabelStatus;
import com.cofii2.stores.CC;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class VCDController implements Initializable {

    private VFController vfc;
    // -----------------------------------
    @FXML
    private VBox vboxMain;
    @FXML
    private TextField tfDatabase;
    @FXML
    private Button btnCreateDatabase;
    private LabelStatus lbStatus = new LabelStatus("Waiting for action...", LabelStatus.LEFT);

    // LISTENERS-----------------------------------
    private void tfDatabaseTextPropertyChange(String newValue) {
        String database = newValue.trim().toLowerCase().replace(" ", "_");
        boolean patternMatch = Pattern.compile("[\\w_]+").matcher(database).matches();
        boolean noneMatch = Users.getInstance().getAllDatabasesList().stream()
                .noneMatch(d -> d.getName().equals(database));
        btnCreateDatabase.setDisable(!(noneMatch && patternMatch));
    }

    private void btnCreateDatabaseAction(ActionEvent e) {
        System.out.println(CC.CYAN + "CREATE DATABASE" + CC.RESET);
        String database = tfDatabase.getText().trim().toLowerCase().replace(" ", "_");
        boolean createDatabase = vfc.getMs().createDatabase(database);
        if (createDatabase) {
            int currentUserId = Users.getInstance().getCurrenUser().getId();
            boolean insert = vfc.getMs().insert(MSQL.TABLE_DATABASES, new Object[] { null, currentUserId, database });
            if (insert) {
                // SELECT ALL DATABASE
                vfc.getMs().selectData(MSQL.TABLE_DATABASES, new SelectDatabases(vfc));
                lbStatus.setText("Database '" + database + "' has been created!", Color.GREEN, Duration.seconds(3));
                btnCreateDatabase.setDisable(true);
                System.out.println("\tSUCCESS");
            } else {
                lbStatus.setText("FATAL: Database was created but not inserted", Color.RED);
                System.out.println("\tFATAL");
            }
        } else {
            lbStatus.setText("Database '" + database + "' fail to be created", Color.RED);
            System.out.println("\tFAIL");
        }
    }

    // INIT----------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vboxMain.getChildren().add(lbStatus);
        // LISTENERS-------------------
        tfDatabase.textProperty().addListener((obs, oldValue, newValue) -> tfDatabaseTextPropertyChange(newValue));
        btnCreateDatabase.setOnAction(this::btnCreateDatabaseAction);

    }
    // ------------------------------------------

    public VFController getVfc() {
        return vfc;
    }

    public void setVfc(VFController vfc) {
        this.vfc = vfc;
    }

}
