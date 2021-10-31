package com.cofii.ts.cu.fk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.cofii.ts.cu.VCController;
import com.cofii.ts.cu.VCRow;
import com.cofii.ts.sql.MSQL;
import com.cofii2.components.javafx.drag.Draggable2;
import com.cofii2.components.javafx.drag.Target;
import com.cofii2.components.javafx.popup.PopupMessage;
import com.cofii2.components.javafx.popup.PopupMessageControl;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class VFK {

    private static Stage stage = new Stage();

    private static String[] columns;
    private static final List<Draggable2> draggableNodes = new ArrayList<>(MSQL.MAX_COLUMNS);

    private static final Alert exitAndResetConfigutation = new Alert(AlertType.CONFIRMATION,
            "The current configuration will be reset", new ButtonType("Cancel", ButtonData.CANCEL_CLOSE),
            new ButtonType("Ok", ButtonData.OK_DONE));

    private static VFKController vfkc;
    // INSTANCE =================================================================
    private static VFK instance;

    public static VFK getInstance(VCController vc, boolean create) {
        if (instance == null) {
            instance = new VFK(vc, create);
        } else {
            stage.show();
        }
        // ADDING COLUMNS-------------------------------
        if (create) {
            addColumns(vc);
        }
        return instance;
    }

    private static void addColumns(VCController vcc) {
        int columnsLength = vcc.getCurrentRowLength();
        columns = VCRow.getRows().stream().map(row -> row.getTfColumn().getText()).limit(columnsLength)
                .toArray(size -> new String[size]);

        Draggable2.getTargetNodes().clear();
        vfkc.getFpColumns().getChildren().clear();

        int[] indexs = { 0 };
        if (vfkc != null) {
            Arrays.asList(columns).forEach(s -> {
                Button btnColumn = new Button(s);

                Draggable2 draggable = new Draggable2(indexs[0], btnColumn, vfkc.getScFPColumns(),
                        vfkc.getPaneTopCenter());
                draggable.setDragAction2(vfkc::dragsAction);
                draggableNodes.add(indexs[0], draggable);

                vfkc.getFpColumns().getChildren().add(btnColumn);

                indexs[0]++;
            });
        }

        int[] targetIndexs = { 0 };
        List<Target> singleTargets = vfkc.getVbSingle().getChildren().stream()
                .map(single -> new Target("single-" + (targetIndexs[0]++), ((Row) single).getTargetPane(), null, 2))
                .collect(Collectors.toList());

        targetIndexs[0] = 0;
        List<Target> groupTargets = vfkc.getVbGroup().getChildren().stream()
                .map(group -> new Target("group-" + (targetIndexs[0]++), ((Row) group).getTargetPane(), null))
                .collect(Collectors.toList());

        Draggable2.getTargetNodes().addAll(singleTargets);
        Draggable2.getTargetNodes().addAll(groupTargets);
    }

    // CONSTRUCTOR ================================================
    {
        exitAndResetConfigutation.setTitle("Reset Foreign Key / Unique Index");

        exitAndResetConfigutation.setHeaderText(null);
        exitAndResetConfigutation.setGraphic(null);

        exitAndResetConfigutation.getDialogPane().getStylesheets()
                .add(VFK.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());
        exitAndResetConfigutation.getDialogPane().getStyleClass().add("dialogs");
    }

    private VFK(VCController vcc, boolean create) {
        try {
            FXMLLoader loader = new FXMLLoader(VFK.class.getResource("/com/cofii/ts/cu/fk/FKV.fxml"));

            Scene scene = new Scene(loader.load());

            vfkc = (VFKController) loader.getController();
            vfkc.setStage(stage);

            VFKController.setVcc(vcc);
            Row.setVfkc(vfkc);

            vfkc.setFkReferences(vcc.getPksReferences());
            vfkc.setDraggableNodes(draggableNodes);

            Row.setFkReferences(vcc.getPksReferences());
            addColumns(vcc);

            // vfkc.addEmptySingleRow(0);

            scene.getStylesheets().add(VFK.class.getResource("/com/cofii/ts/first/VF.css").toExternalForm());

            scene.setOnKeyReleased(e -> {
                if (e.getCode() == KeyCode.S) {
                    System.out.println("=========================================");
                }
            });

            stage.setOnCloseRequest(e -> {
                e.consume();
                Predicate<? super Node> emptyRows = node -> ((Row) node).isEmptyRow();
                boolean anyEmptyRow = vfkc.getVbSingle().getChildren().stream().allMatch(emptyRows)
                        && vfkc.getVbGroup().getChildren().stream().allMatch(emptyRows);

                if (anyEmptyRow) {
                    stage.close();
                } else {
                    exitAndResetConfigutation.getButtonTypes().get(0);
                    exitAndResetConfigutation.showAndWait()
                            .filter(res -> res.getButtonData().name().equals(ButtonData.OK_DONE.name()))
                            .ifPresent(res -> {
                                instance = null;
                                stage.close();
                            });
                }
            });
            stage.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue) {
                    if (!PopupMessageControl.getPopupMaster().getParentNode().equals(vfkc.getLbStatus())) {
                        PopupMessageControl.getPopupMaster().getPopup().hide();
                        PopupMessageControl.setPopupMaster(new PopupMessage("MASTER", vfkc.getLbStatus()));
                    }
                }
            });
            stage.setScene(scene);
            stage.show();

            // AFTER CONTROLLER INIT ---------------------------------
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GETTERS & SETTERS ======================================================
    public static VFKController getVfkc() {
        return vfkc;
    }

    public static void setVfkc(VFKController vfkc) {
        VFK.vfkc = vfkc;
    }

    public static List<Draggable2> getDraggableNodes() {
        return draggableNodes;
    }

    static VFK getInstance() {
        return instance;
    }

    public static void setInstance(VFK instance) {
        VFK.instance = instance;
    }
}
