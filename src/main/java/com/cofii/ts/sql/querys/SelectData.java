package com.cofii.ts.sql.querys;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cofii.ts.first.VFController;
import com.cofii.ts.other.NonCSS;
import com.cofii.ts.other.Timers;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.main.Table;
import com.cofii2.myInterfaces.IActions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Selec the data from the current Table
 */
public class SelectData implements IActions {
    public static final String MESSAGE_INSERT = "Inserted on ";
    public static final String MESSGE_TABLE_CHANGE = "Change table to ";
    public static final String MESSAGE_DELETE_ROW = "Row deleted in ";
    public static final String MESSAGE_UPDATED_ROW = "Row updated in ";
    // --------------------------------------------------
    private VFController vf;
    private Table table = MSQL.getCurrentTable();
    private String message;

    private int columnCount;
    private ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();

    public SelectData(VFController vf, String message) {
        this.vf = vf;
        this.message = message;
    }

    @Override
    public void beforeQuery() {
        vf.getTable().getItems().clear();
        columnCount = MSQL.getColumns().length;
    }

    @Override
    public void setData(ResultSet rs, int rowN) throws SQLException {
        ObservableList<Object> row = FXCollections.observableArrayList();
        for (int a = 0; a < columnCount; a++) {
            if (table.getColumns().get(a).getType().contains("CHAR")) {
                row.add(rs.getString((a + 1)));
            } else if (table.getColumns().get(a).getType().contains("INT")) {
                row.add(rs.getInt((a + 1)));
            }

        }
        data.add(row);

    }

    @Override
    public void afterQuery(String query, boolean rsValue) {
        if (rsValue) {
            vf.getTable().setItems(data);
            if (message != null) {
                vf.getLbStatus().setText(message, NonCSS.TEXT_FILL_OK, Duration.seconds(2));

                Media media;
                MediaPlayer mediaPlayer;
                if (message.contains(MESSAGE_INSERT)) {
                    media = new Media(SelectData.class.getResource("/com/cofii/ts/sounds/pbobble-010.wav").toExternalForm());      
                }else if(message.contains(MESSAGE_UPDATED_ROW)){
                    media = new Media(SelectData.class.getResource("/com/cofii/ts/sounds/pbobble-007.wav").toExternalForm());
                }else if(message.contains(MESSAGE_DELETE_ROW)){
                    media = new Media(SelectData.class.getResource("/com/cofii/ts/sounds/pbobble-006.wav").toExternalForm());
                }else{
                    /*
                    if(vf.getTableData() != null){
                        vf.getTableData().removeListener(vf::tableCellChanged);
                    }
                    vf.setTableData(vf.getTable().getItems());
                    */
                    media = new Media(SelectData.class.getResource("/com/cofii/ts/sounds/pbobble-032.wav").toExternalForm());
                }
                
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
            }
        }
    }

}
