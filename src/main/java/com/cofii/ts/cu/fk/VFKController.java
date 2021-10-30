package com.cofii.ts.cu.fk;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import com.cofii.ts.cu.VCController;
import com.cofii.ts.store.main.Users;
import com.cofii2.components.javafx.LabelStatus;
import com.cofii2.components.javafx.drag.DragEnum;
import com.cofii2.components.javafx.drag.Draggable2;
import com.cofii2.components.javafx.drag.Target;
import com.cofii2.components.javafx.popup.PopupMessageControl;
import com.cofii2.mysql.CustomConnection;
import com.cofii2.mysql.MSQLCreate;
import com.cofii2.stores.CC;
import com.cofii2.stores.ReplaceList;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VFKController implements Initializable {

    private Stage stage;

    private static VCController vcc;
    // CENTER ==============================================
    @FXML
    private VBox vbCenter;
    @FXML
    private Pane paneTopCenter;

    @FXML
    private ScrollPane scFPColumns;
    @FXML
    private FlowPane fpColumns;
    private List<Draggable2> draggableNodes;

    @FXML
    private ScrollPane scVBSingle;
    @FXML
    private VBox vbSingle;

    @FXML
    private ScrollPane scVBGroup;
    @FXML
    private VBox vbGroup;

    private static final String ROW_HOVERED = "-fx-border-style: solid solid solid solid; -fx-border-width: 2; -fx-border-color: rgb(0, 68, 255);";
    private static final String ROW_BLOCKED = "-fx-border-style: solid solid solid solid; -fx-border-width: 2; -fx-border-color: red;";

    private String[] fkReferences;
    private ReplaceList<Integer> lastIndexesSuccesses = new ReplaceList<>(2);
    // BOTTOM ============================================
    @FXML
    private HBox hbStatus;
    private LabelStatus lbStatus = new LabelStatus();
    @FXML
    private Button btnReset;
    @FXML
    private Button btnAddFK;

    // CONTROL ======================================================
    void groupsControl() {
        Predicate<? super Node> emptyRowFilter = child -> ((Row) child).isEmptyRow();
        if (!fpColumns.getChildren().isEmpty()) {
            groupControlWich("Single", vbSingle, emptyRowFilter);
            groupControlWich("Group", vbGroup, emptyRowFilter);
        } else {
            vbSingle.getChildren().removeIf(emptyRowFilter);
            vbGroup.getChildren().removeIf(emptyRowFilter);
        }

        Row.allOkControl();

    }
    // LISTENERS ====================================================
    void dragsAction(Draggable2 draggable, DragEnum dragEnum) {
        draggable.backAndForwardTarget(dragEnum);
        // ENTER OR EXIT --------------------------------------
        if (DragEnum.MOUSE_ENTERED == dragEnum) {
            draggable.getNode().setCursor(Cursor.OPEN_HAND);
        }
        if (DragEnum.MOUSE_EXITED == dragEnum) {
            draggable.getNode().setCursor(Cursor.DEFAULT);
        }
        // DRAG OVER -------------------------------------------
        Target scrollStore = draggable.getLastTargetNodeHovered();
        if (DragEnum.DRAG_OVER_DROP_TARGET_SUCCESS == dragEnum) {
            if (scrollStore.getScrollPaneTargetNode() != null) {
                scrollStore.getScrollPaneTargetNode().setStyle(ROW_HOVERED);
            } else {
                scrollStore.getTargetNode().setStyle(ROW_HOVERED);
            }
        }
        if (DragEnum.DRAG_OVER_DROP_TARGET_BLOCK == dragEnum) {
            if (scrollStore.getScrollPaneTargetNode() != null) {
                scrollStore.getScrollPaneTargetNode().setStyle(ROW_BLOCKED);
            } else {
                scrollStore.getTargetNode().setStyle(ROW_BLOCKED);
            }
        }
        // DROP ----------------------------------------------------
        if (DragEnum.DROP_ON_TARGET_SUCCESS == dragEnum || DragEnum.DROP_ON_TARGET_FAILED == dragEnum
                || DragEnum.DRAG_OVER_DROP_TARGET_FAIL == dragEnum) {
            Draggable2.getTargetNodes().forEach(targ -> {
                if (targ.getScrollPaneTargetNode() != null) {
                    targ.getScrollPaneTargetNode().setStyle(null);
                } else {
                    targ.getTargetNode().setStyle(null);
                }
            });
        }

    }

    private void fpColumnsChildrenChange(Change<? extends Node> c) {
        while (c.next()) {
            long delay = 500;
            Row.getExecutor().schedule(() -> {
                Platform.runLater(() -> {
                    groupsControl();
                });
            }, delay, TimeUnit.MILLISECONDS);
        }
    }
    
    // INIT ====================================================
    private void resetIds(){
        int[] singleIds = {0};
        int[] groupIds = {0};
        vbSingle.getChildren().forEach(child -> {
            Row row = (Row) child;
            if(row.getRowType().equals("Single")){
                row.setId("S" + singleIds[0]++);
            }else{
                row.setId("G" + groupIds[0]++);
            }
        });
    }

    private void groupControlWich(String rowType, VBox group, Predicate<? super Node> emptyRowFilter){
        int newIndex = group.getChildren().size();
        if (group.getChildren().isEmpty()) {
            group.getChildren().add(newIndex,  new Row(rowType, newIndex));
        } else {
            if (group.getChildren().stream().noneMatch(emptyRowFilter)) {
                group.getChildren().add(newIndex,  new Row(rowType, newIndex));
            } else {
                // DELETE MULTIMPLE EMPTY ROWS -----------------
                for (int a = 0; a < group.getChildren().size(); a++) {
                    if (group.getChildren().stream().filter(emptyRowFilter).count() > 1) {
                        if (((Row) group.getChildren().get(a)).isEmptyRow()) {
                            Row.getReferencemessages().remove(a);
                            Row.getConstraintmessages().remove(a);

                            group.getChildren().remove(a);
                            PopupMessageControl.removeAllErrors("FKREF:" + a);
                            PopupMessageControl.removeAllErrors("FKCONS:" + a);
                        }
                    }
                }
            }
        }
    }
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // NODES PRESET ===========================================
        // PROPERTY ===============================================
        fpColumns.getStyleClass().add("sub-pane");
        vbSingle.getStyleClass().add("sub-pane");
        vbGroup.getStyleClass().add("sub-pane");

        lbStatus.getStyleClass().add("status");

        fpColumns.getChildren().addListener(this::fpColumnsChildrenChange);
        // CENTER --------------------------------------
        fpColumns.minWidthProperty().bind(scFPColumns.widthProperty().subtract(4));
        scFPColumns.setHbarPolicy(ScrollBarPolicy.NEVER);

        vbSingle.minWidthProperty().bind(scVBSingle.widthProperty().subtract(4));
        vbGroup.minWidthProperty().bind(scVBGroup.widthProperty().subtract(4));

        //vbSingle.getChildren().addListener(this::vbSingleChildrenChange);
        // BOTTOM --------------------------------------
        
        hbStatus.getChildren().add(0, lbStatus);

        btnReset.setOnAction(e -> {
            VFK.setInstance(null);
            stage.close();
        });
        btnAddFK.setOnAction(e -> stage.close());
    }

    // GETTERS & SETTERS =========================================
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public FlowPane getFpColumns() {
        return fpColumns;
    }

    public void setFpColumns(FlowPane fpColumns) {
        this.fpColumns = fpColumns;
    }

    public VBox getVbCenter() {
        return vbCenter;
    }

    public void setVbCenter(VBox vbCenter) {
        this.vbCenter = vbCenter;
    }

    public Pane getPaneTopCenter() {
        return paneTopCenter;
    }

    public void setPaneTopCenter(Pane paneTopCenter) {
        this.paneTopCenter = paneTopCenter;
    }

    public String[] getFkReferences() {
        return fkReferences;
    }

    public void setFkReferences(String[] fkReferences) {
        this.fkReferences = fkReferences;
    }

    public ScrollPane getScFPColumns() {
        return scFPColumns;
    }

    public void setScFPColumns(ScrollPane scFPColumns) {
        this.scFPColumns = scFPColumns;
    }

    public VBox getVbSingle() {
        return vbSingle;
    }

    public void setVbSingle(VBox vbSingle) {
        this.vbSingle = vbSingle;
    }

    public VBox getVbGroup() {
        return vbGroup;
    }

    public void setVbGroup(VBox vbGroup) {
        this.vbGroup = vbGroup;
    }

    public List<Draggable2> getDraggableNodes() {
        return draggableNodes;
    }

    void setDraggableNodes(List<Draggable2> draggableNodes) {
        this.draggableNodes = draggableNodes;
    }

    public VCController getVcc() {
        return vcc;
    }

    public static void setVcc(VCController vcc) {
        VFKController.vcc = vcc;
    }

    public ScrollPane getScVBSingle() {
        return scVBSingle;
    }

    public void setScVBSingle(ScrollPane scVBSingle) {
        this.scVBSingle = scVBSingle;
    }

    public LabelStatus getLbStatus() {
        return lbStatus;
    }

    public void setLbStatus(LabelStatus lbStatus) {
        this.lbStatus = lbStatus;
    }
    public Button getBtnAddFK() {
        return btnAddFK;
    }
    public void setBtnAddFK(Button btnAddFK) {
        this.btnAddFK = btnAddFK;
    }
    
}
