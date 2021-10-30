package com.cofii.ts.cu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cofii.ts.cu.store.VCStore;
import com.cofii.ts.login.VL;
import com.cofii.ts.other.CSS;
import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.SQLType;
import com.cofii.ts.store.SQLTypes;
import com.cofii2.components.javafx.TrueFalseWindow;
import com.cofii2.components.javafx.popup.Message;
import com.cofii2.components.javafx.popup.PopupAutoC;
import com.cofii2.components.javafx.popup.PopupMenu;
import com.cofii2.methods.MList;
import com.cofii2.mysql.MSQLP;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

public class VCRow {

    // ===============================================================================
    private static final Insets COLUMN_CONTAINER_PADDING = new Insets(0, 2, 0, 2);
    private static final String COLUMN_NAME_REQUIRED = "Column name required";
    private static final String VALUE_REQUIRED = "Value Required";

    private static final String ROW_CLASS = "vc-row";

    private static VCController vcc;
    private static boolean createRows = true;
    private static final ObservableList<VCRow> rows = FXCollections.observableArrayList();
    private static final ObservableList<String> sameColumnsList = FXCollections.observableArrayList();

    private static final List<SQLType> PRESET_TYPES = new ArrayList<>(MSQL.MAX_COLUMNS);

    private static final Pattern patternBWTC = Pattern.compile("[A-Za-z](\\w| )*");
    private static final Pattern patternTypeLength = Pattern.compile("\\d{1,5}");

    private static final SQLTypes types = SQLTypes.getInstance();
    // COLUMN NUMBER ---------------------------------------
    private final HBox hbN = new HBox();
    private final Label lbN = new Label();
    // COLUMN NAME ------------------------------------------
    private final HBox hbColumn = new HBox();
    private final TextField tfColumn = new TextField();
    private final Button btnRemoveColumn = new Button("x");
    private final Button btnAddColumn = new Button("+");
    private final Button btnChangeColumn = new Button("C");
    // INDEX 0 ONLY ! --------
    private final PopupMenu popupBeforeAfter = new PopupMenu("Before", "After");

    private Message messageColumn;
    // TYPE --------------------------------------------------
    private final HBox hbType = new HBox();
    private final TextField tfType = new TextField();
    private PopupAutoC tfTypeAutoC;
    private final TextField tfTypeLength = new TextField();
    private final Button btnChangeType = new Button("C");

    private Message messageType;
    // NULL --------------------------------------------------
    private final HBox hbNull = new HBox();
    private final CheckBox ckNull = new CheckBox();
    private final Button btnChangeNull = new Button("C");
    // PK -----------------------------------------------------
    private final HBox hbPK = new HBox();
    private final RadioButton rbPK = new RadioButton();

    private Message messagePK;
    // DEFAULT ------------------------------------------------
    private final HBox hbDefault = new HBox();
    private final CheckBox ckDefault = new CheckBox();
    private final TextField tfDefault = new TextField();
    private final Button btnChangeDefault = new Button("C");

    private Message messageDefault;
    // EXTRA --------------------------------------------------
    private final HBox hbExtra = new HBox();
    private final RadioButton rbExtra = new RadioButton();

    private Message messageExtra;
    // CUSTOM -------------------------------------------------
    private final ToggleButton btnDist = new ToggleButton();
    private final ToggleButton btnTextArea = new ToggleButton();

    private Message messageDist;
    private Message messageTextArea;
    // LISTENERS =======================================================
    private final EventHandler<ActionEvent> addColumnUpdateActionListener = this::addColumnUpdateAction;
    private final EventHandler<ActionEvent> cancelAddColumnUpdateActionListener = this::addRowCancelAction;
    // BOOLEANS ========================================================
    // COLUMNS ---------------------------------------
    private boolean columnBWOK = !createRows;
    private static boolean columnsSameOk = true;
    // COLUMNS UPDATE --------------------------------
    private static boolean columnAddState = false;
    private static int columnAddIndex = -1;

    private boolean columnAddOk = false;
    // TYPE ------------------------------------------
    private boolean typeSelectionMatch = true;
    private boolean typeLengthOK = true;
    // TYPE UPDATE -----------------------------------
    private boolean typeAddOk = false;
    private boolean typeLenghtAddOk = false;
    // DEFAULT ---------------------------------------
    private boolean defaultOK = true;
    // DEFAULT UPDATE --------------------------------
    private boolean defaultAddOk = false;
    // MIX -------------------------------------------
    private boolean typeDefaultOk = true;

    private boolean extraPKOK = true;
    private boolean extraFKOK = true;
    private boolean extraDefaultOK = true;
    private boolean extraDistOK = true;

    private boolean textAreaExtraOK = true;
    private boolean textAreaTypeOK = true;

    // IMPORTANT STATICS ========================================================
    private static void importants() {
        /*
         * GridPane.setMargin(hbsN.get(index), INSETS);
         * GridPane.setMargin(hbsName.get(index), INSETS);
         * GridPane.setMargin(hbsType.get(index), INSETS);
         * GridPane.setMargin(hbsNull.get(index), INSETS);
         * GridPane.setMargin(hbsPK.get(index), INSETS);
         * GridPane.setMargin(hbsDefault.get(index), INSETS);
         * GridPane.setMargin(hbsExtra.get(index), INSETS);
         * 
         * GridPane.setMargin(btnsDist.get(index), INSETS);
         * GridPane.setMargin(btnsTextArea.get(index), INSETS);
         * 
         * ToggleGroupD staticGroup new ToggleGroupD<>(rbsExtra.toArray(new
         * RadioButton[rbsExtra.size()]));
         */
    }

    // OTHERS ===================================================================
    private int getThisIndex() {
        return rows.indexOf(this);
    }

    private boolean getColumnMatch(String text) {
        final String textt = text.toUpperCase();
        Matcher matcher = patternBWTC.matcher(text);
        return matcher.matches() && MSQL.BAND_COLUMNS_NAMES.stream().noneMatch(word -> textt.equals(word));
    }
    // BOOLEANS -----------------------------------------
    static boolean isAllColumnBWOK() {
        return rows.stream().allMatch(VCRow::isColumnBWOK);
    }
    
    static boolean isAllTypeSelectionMatch(){
        return rows.stream().allMatch(VCRow::isTypeSelectionMatchOk);
    }

    static boolean isAllTypeLengthOK(){
        return rows.stream().allMatch(VCRow::isTypeLengthOK);
    }
    // --------------------------------------------------
    /**
     * to determine if currentRowLength is under the size of the current table
     * 
     * @return if under the limit
     */
    private boolean isUnderTheColumnsLimit() {
        return vcc.getCurrentRowLength() <= vcc.getCurrentTable().getColumns().size();
    }

    static void removeAllMessageFromThisRow(int index) {
        ObservableList<Message> messages = vcc.getPopupMessageControl().getMessages();

        VCRow row = rows.get(index);
        messages.removeAll(row.getMessageColumn(), row.getMessageType(), row.getMessagePK(), row.getMessageDefault(),
                row.getMessageExtra(), row.getMessageDist(), row.getMessageTextArea());
    }

    private static void resetIndexes() {
        for (int a = 0; a < rows.size(); a++) {
            VCRow row = rows.get(a);

            row.getLbN().setText("Column " + (a + 1));

            row.getMessageColumn().setId("COLUMN:" + a);
            row.getMessageType().setId("TYPE:" + a);
            row.getMessagePK().setId("PK:" + a);
            row.getMessageDefault().setId("DEFAULT:" + a);
            row.getMessageExtra().setId("EXTRA:" + a);

            row.getBtnDist().setText(Integer.toString(a + 1));
            row.getBtnTextArea().setText(Integer.toString(a + 1));

            row.getMessageDist().setId("DIST:" + a);
            row.getMessageTextArea().setId("TEXTAREA:" + a);

            if (a == 0) {
                PopupMenu beforeAfterOptionMenu = new PopupMenu("Before", "After");
                Tooltip beforeAfterOptionTooltip = new Tooltip("Right click to add...");

                row.getBtnAddColumn().setContextMenu(beforeAfterOptionMenu);
                row.getBtnAddColumn().setTooltip(beforeAfterOptionTooltip);
            } else {
                row.getBtnAddColumn().setContextMenu(null);
                row.getBtnAddColumn().setTooltip(null);
            }
        }

        btnsAddRemoveColumnReset();
    }

    private static void btnsAddRemoveColumnReset() {
        rows.get(0).getBtnRemoveColumn().setDisable(false);
        if (vcc.getCurrentRowLength() < 2) {
            rows.get(0).getBtnRemoveColumn().setDisable(true);
        }

        rows.forEach(row -> row.getBtnAddColumn().setDisable(vcc.getCurrentRowLength() >= MSQL.MAX_COLUMNS));
    }

    /**
     * 
     * @param type           In Custom: Dist or TextArea
     * @param newColumnIndex new column added index
     * @param newColumnName  new column added name
     * @return a custom String to insert on Table_Customs
     */
    private String getCustomStringFromUpdateTable(String type, int newColumnIndex, String newColumnName) {
        StringBuilder sb = new StringBuilder();
        int[] indexs = { 0 };

        List<Boolean> list = null;
        if (type.equals("dist")) {
            // list = updateTable.getDists();
            list = vcc.getCurrentTable().getDistList();
        } else if (type.equals("textArea")) {
            list = vcc.getCurrentTable().getTextAreaList();
        }
        list.forEach(s -> {
            boolean dist = s == null ? false : s;
            if (dist) {
                if (indexs[0] != newColumnIndex) {
                    sb.append(vcc.getCurrentTable().getColumns().get(indexs[0]).getName().replace(" ", "_"))
                            .append(",");
                } else {
                    sb.append(newColumnName.replace(" ", "_")).append(",");
                }
            }
            indexs[0]++;
        });

        return sb.length() == 0 ? "NONE" : sb.deleteCharAt(sb.length() - 1).toString();
    }

    // CONTROL ==================================================================
    private void addColumnControl() {
        boolean disable = columnAddOk && typeAddOk && typeLenghtAddOk && defaultAddOk;
        btnAddColumn.setDisable(!disable);
    }

    private void extraPKControl() {
        extraPKOK = true;
        if (rbExtra.isSelected() && !rbPK.isSelected()) {
            extraPKOK = false;
        }

        // DISPLAY ------------------------------------------
        if (extraPKOK) {
            messagePK.getItemList().remove(VCStore.AUTO_INCREMENT_AND_PK);
            messageExtra.getItemList().remove(VCStore.AUTO_INCREMENT_AND_PK);
        } else {
            messagePK.getItemList().add(VCStore.AUTO_INCREMENT_AND_PK);
            messageExtra.getItemList().add(VCStore.AUTO_INCREMENT_AND_PK);
        }
    }

    /**
     * May not use bc of the diferent use of windows
     */
    private void extraFKControl() {
        extraFKOK = true;
        if (rbExtra.isSelected() && vcc.getVfkc() != null
                ? vcc.getVfkc().getDraggableNodes().get(getThisIndex()).isNodeOnItsOriginalParent()
                : false) {
            extraFKOK = false;
        }

        // DISPLAY ------------------------------------------
        if (extraFKOK) {
            // message.getItemList().remove(VCStore.AUTO_INCREMENT_AND_FK);
            messageExtra.getItemList().remove(VCStore.AUTO_INCREMENT_AND_FK);
        } else {
            // fkPopupsControl.getItemList().add(VCStore.AUTO_INCREMENT_AND_FK);
            messageExtra.getItemList().add(VCStore.AUTO_INCREMENT_AND_FK);
        }
    }

    private void extraDefaultControl() {
        extraDefaultOK = true;
        if (rbExtra.isSelected() && ckDefault.isSelected()) {
            extraDefaultOK = false;
        }
        // DISPLAY ------------------------------------------
        if (extraDefaultOK) {
            messageDefault.getItemList().remove(VCStore.AUTO_INCREMENT_AND_DEFAULT);
            messageExtra.getItemList().remove(VCStore.AUTO_INCREMENT_AND_DEFAULT);
        } else {
            messageDefault.getItemList().add(VCStore.AUTO_INCREMENT_AND_DEFAULT);
            messageExtra.getItemList().add(VCStore.AUTO_INCREMENT_AND_DEFAULT);
        }
    }

    private void extraDistControl() {
        extraDistOK = true;
        if (rbExtra.isSelected() && btnDist.isSelected()) {
            extraDistOK = false;
        }
        // DISPLAY ------------------------------------------
        if (extraDistOK) {
            messageDist.getItemList().remove(VCStore.AUTO_INCREMENT_AND_DIST);
            messageExtra.getItemList().remove(VCStore.AUTO_INCREMENT_AND_DIST);
        } else {
            messageDist.getItemList().add(VCStore.AUTO_INCREMENT_AND_DIST);
            messageExtra.getItemList().add(VCStore.AUTO_INCREMENT_AND_DIST);
        }
    }

    private void textAreaExtraControl() {
        textAreaExtraOK = true;
        if (rbExtra.isSelected() && btnTextArea.isSelected()) {
            textAreaExtraOK = false;
        }

        // DISPLAY ------------------------------------------
        if (textAreaExtraOK) {
            messageTextArea.getItemList().remove(VCStore.TEXT_AREA_AND_EXTRA);
            messageExtra.getItemList().remove(VCStore.TEXT_AREA_AND_EXTRA);
        } else {
            messageTextArea.getItemList().add(VCStore.TEXT_AREA_AND_EXTRA);
            messageExtra.getItemList().add(VCStore.TEXT_AREA_AND_EXTRA);
        }
    }

    private void textAreaTypeControl() {
        textAreaTypeOK = false;
        if (typeSelectionMatch && typeLengthOK) {
            String typeChar = types.getTypeChar(tfType.getText());
            if (typeChar.equals("STRING") && btnTextArea.isSelected()) {
                textAreaTypeOK = true;
            }
        }

        // DISPLAY -----------------------------------------
        if (textAreaTypeOK) {
            messageType.getItemList().remove(VCStore.TEXT_AREA_AND_TYPE);
            messageTextArea.getItemList().remove(VCStore.TEXT_AREA_AND_TYPE);
            ;
        } else {
            messageType.getItemList().add(VCStore.TEXT_AREA_AND_TYPE);
            messageTextArea.getItemList().add(VCStore.TEXT_AREA_AND_TYPE);
        }
    }

    // LISTENERS ================================================================
    // ROWS LISTENERS ===========================================================
    private static void rowsChange(Change<? extends VCRow> c) {
        while (c.next()) {
            if (c.wasAdded()) {
                sameColumnsList.add(c.getFrom(), "");
            } else if (c.wasRemoved()) {
                // UNTESTED !!!!!!!!!!!!!!!!!!!!
                sameColumnsList.remove(c.getFrom());
            }

            if (c.wasAdded() || c.wasRemoved()) {
                vcc.resetGridPaneRowsTest();
                resetIndexes();
                vcc.masterControlTest();
            }
        }
    }

    private static void sameColumnsChange(Change<? extends String> c) {
        while (c.next()) {
            if (MList.areTheyDuplicatedElementsOnList(sameColumnsList)) {
                columnsSameOk = false;
                vcc.getMessageSameColumns().getItemList().add(VCStore.DUPLICATE_COLUMNS_NAMES);
            } else {
                columnsSameOk = true;
                vcc.getMessageSameColumns().getItemList().remove(VCStore.DUPLICATE_COLUMNS_NAMES);
            }
        }
    }

    // ADD STATE -------------------------------------------
    private void startAddColumnState() {
        int index = getThisIndex();

        btnAddColumn.setText("A");// ADD
        btnAddColumn.setStyle(CSS.NEW_ROW_ADD_BUTTON);
        btnAddColumn.setOnAction(addColumnUpdateActionListener);
        btnAddColumn.setDisable(true);// DISABLE FOR CONTROL

        btnRemoveColumn.setText("C");// CANCEL
        btnRemoveColumn.setStyle(CSS.NEW_ROW_REMOVE_BUTTON);
        btnRemoveColumn.setOnAction(cancelAddColumnUpdateActionListener);
        btnChangeColumn.setDisable(true);
        // DISABLE NODES ================================================
        // TOP -------------------------------------
        vcc.getHbTop().setDisable(true);
        // CENTER ----------------------------------
        hbPK.setDisable(true);
        hbExtra.setDisable(true);
        // BOTTOM ----------------------------------
        vcc.getBtnUpdatePK().setDisable(true);
        vcc.getBtnUpdateExtra().setDisable(true);
        vcc.getBtnUpdateDist().setDisable(true);
        vcc.getBtnUpdateTextArea().setDisable(true);

        vcc.getBtnSelectUI().setDisable(true);
        vcc.getBtnSelectFK().setDisable(true);
        vcc.getBtnSelectIC().setDisable(true);
        // OFF UPDATE FOREGROUND COLOR ---------
        ckNull.setStyle(CSS.CKS_BG);
        ckDefault.setStyle(CSS.CKS_BG);
        // ==============================================================
        btnDist.setStyle(CSS.NEW_ROW_DIST_BUTTONS);
        btnTextArea.setStyle(CSS.NEW_ROW_DIST_BUTTONS);
        // NEW EMPTY ROW INDEX ==========================================
        columnAddIndex = index;
        columnAddState = true;

        disableRowsAtAddColumnState(index);
        vcc.getCurrentTable().getColumns().add(index, null);
    }

    private void exitAddColumnState(int index) {
        columnAddState = false;
        columnAddOk = false;
        typeAddOk = true;
        typeLenghtAddOk = true;
        defaultAddOk = true;

        vcc.getHbTop().setDisable(false);
        // RESET NEW ROW ==========================================
        hbN.setStyle(CSS.ROW);
        tfColumn.setStyle(CSS.NODE_TEXTFILL_HINT);
        tfType.setStyle(CSS.NODE_TEXTFILL_HINT);
        tfTypeLength.setStyle(CSS.NODE_TEXTFILL_HINT);
        ckNull.setStyle(CSS.CKS_BG_HINT);
        rbPK.setDisable(false);
        ckDefault.setStyle(CSS.CKS_BG_HINT);
        tfDefault.setStyle(CSS.NODE_TEXTFILL_HINT);
        rbExtra.setDisable(false);
        hbExtra.setStyle(CSS.ROW);

        btnRemoveColumn.setText("X");// REMOVE
        btnRemoveColumn.setStyle(CSS.ROW_REMOVE_BUTTON);
        btnRemoveColumn.setOnAction(e -> removeRowTest(getThisIndex()));

        btnAddColumn.setText("+");// ADD
        btnAddColumn.setStyle(CSS.ROW_ADD_BUTTON);
        btnAddColumn.setTooltip(null);
        btnAddColumn.setOnAction(e -> addRowTest(getThisIndex()));

        btnDist.setStyle(CSS.ROW_DIST_BUTTONS);
        btnTextArea.setStyle(CSS.ROW_DIST_BUTTONS);
        // RE-ENABLED NODES ===============================================
        // CENTER --------------------------------------------------
        for (int a = 0; a < vcc.getCurrentRowLength(); a++) {
            hbN.setDisable(false);
            hbColumn.setDisable(false);
            hbType.setDisable(false);
            hbNull.setDisable(false);
            hbPK.setDisable(false);
            hbDefault.setDisable(false);
            hbExtra.setDisable(false);

            btnDist.setDisable(false);
            btnTextArea.setDisable(false);
        }
        // BOTTOM -----------------------------------------
        vcc.getBtnUpdatePK().setDisable(false);
        vcc.getBtnUpdateExtra().setDisable(false);
        vcc.getBtnUpdateDist().setDisable(false);
        vcc.getBtnUpdateTextArea().setDisable(false);

        vcc.getBtnSelectUI().setDisable(false);
        vcc.getBtnSelectFK().setDisable(false);
        vcc.getBtnSelectIC().setDisable(false);
    }

    private void disableRowsAtAddColumnState(int index) {
        if (index >= 0 && !createRows) {
            hbN.setStyle(CSS.NEW_ROW);
            hbExtra.setStyle(CSS.NEW_ROW);
            // DISABLE ALL ROW BUT NEW ONE-----------------------------------
            for (int a = 0; a < vcc.getCurrentRowLength(); a++) {
                if (a != index) {
                    hbN.setDisable(true);
                    hbColumn.setDisable(true);
                    hbType.setDisable(true);
                    hbNull.setDisable(true);
                    hbPK.setDisable(true);
                    hbDefault.setDisable(true);
                    hbExtra.setDisable(true);

                    btnDist.setDisable(true);
                    btnTextArea.setDisable(true);
                }
            }
        }
    }

    /**
     * Query update for Adding a column
     * 
     * @param e action event
     */
    private void addColumnUpdateAction(ActionEvent e) {
        VL.LOG.info("ADD COLUMN (START)");
        // VARIABLES SET ================================================
        int index = getThisIndex();
        String tableName = vcc.getCurrentTable().getName();
        String column = tfColumn.getText().toLowerCase().trim().replace(" ", "_");
        String type = tfType.getText() + (tfTypeLength.isVisible() ? "(" + tfTypeLength.getText() + ")" : "");
        String afterBeforeColumn;
        boolean addColumn;

        vcc.getMs().setNullValue(ckNull.isSelected());

        String defaultText = tfDefault.getText();//
        String typeChar = types.getTypeChar(tfType.getText());
        // WILL FAIL WITH DECIMALS !!!!!!!!!!!!!!!!!!!
        Object defaultValue = !defaultText.isEmpty() && (typeChar.equals("NUMBER") || typeChar.equals("DECIMAL"))
                ? Integer.parseInt(defaultText)
                : defaultText;
        defaultValue = ckDefault.isSelected() ? defaultValue : null;
        vcc.getMs().setDefaultValue(defaultValue);

        // ADD COLUMN ====================================================
        try {// ATTEMPTING TO GRAB THE PREVIOUS COLUMN
            afterBeforeColumn = vcc.getCurrentTable().getColumns().get(index - 1).getName().toLowerCase().trim()
                    .replace(" ", "_");

            vcc.getMs().setAfterOrBeforeColumn(MSQLP.AFTER);
            addColumn = vcc.getMs().addColumn(tableName, column, type, afterBeforeColumn);
        } catch (ArrayIndexOutOfBoundsException ex) {
            afterBeforeColumn = vcc.getCurrentTable().getColumns().get(0).getName().toLowerCase().trim().replace(" ",
                    "_");
            ;

            vcc.getMs().setAfterOrBeforeColumn(MSQLP.BEFORE);
            addColumn = vcc.getMs().addColumn(tableName, column, type, afterBeforeColumn);
        }
        // ADD CUSTOM =====================================
        // NOT TESTED
        boolean addDist = true;
        if (btnDist.isSelected()) {
            vcc.getCurrentTable().getColumns().get(index).setDist(true);
            String dist = getCustomStringFromUpdateTable("dist", index, column);
            addDist = vcc.getMs().updateRow(MSQL.TABLE_CUSTOMS, "id_table", vcc.getCurrentTable().getId(), "dist",
                    dist);
        }
        // NOT TESTED
        boolean addTextArea = true;
        if (btnTextArea.isSelected()) {
            vcc.getCurrentTable().getColumns().get(index).setTextArea(true);
            String textArea = getCustomStringFromUpdateTable("textArea", index, column);
            addTextArea = vcc.getMs().updateRow(MSQL.TABLE_CUSTOMS, "id_table", vcc.getCurrentTable().getId(),
                    "textArea", textArea);
        }
        // TO FINISH =================================================
        if (addColumn && addDist && addTextArea) {
            // ADDING VALUES TO EMPTY ROW (PREVIOUSLY ADDED)
            vcc.getCurrentTable().getColumns().get(index).setName(column);
            vcc.getCurrentTable().getColumns().get(index).setType(tfType.getText());
            vcc.getCurrentTable().getColumns().get(index).setTypeLength(Integer.parseInt(tfTypeLength.getText()));

            vcc.getCurrentTable().getColumns().get(index).setNulll(ckNull.isSelected());
            vcc.getCurrentTable().getColumns().get(index)
                    .setDefaultt(ckDefault.isSelected() ? tfDefault.getText() : null);
            // CUSTOM ----------------------
            vcc.getCurrentTable().getColumns().get(index).setDist(btnDist.isSelected());
            vcc.getCurrentTable().getColumns().get(index).setTextArea(btnTextArea.isSelected());
            // -----------------------------
            exitAddColumnState(index);

            vcc.getLbStatus().setText("Added column '" + column + "' to '" + tableName + "'",
                    vcc.getLbStatus().getTextFillOk(), Duration.seconds(4));
            System.out.println("\tSUCCES");
        } else {
            vcc.getLbStatus().setText("Couldn't add column '" + column + "'", vcc.getLbStatus().getTextFillError());
            System.out.println("\tFAILED");
        }

    }

    private void addRowCancelAction(ActionEvent e) {
        VL.LOG.info("REMOVE ROW (START)");
        int index = getThisIndex();
        // -------------------------------------
        removeRowTest(index);
        // -------------------------------------
        vcc.getCurrentTable().getColumns().remove(index); // Added At AddColumnState !

        exitAddColumnState(index);
    }
    // ADD AND REMOVE ---------------------------------------
    static void removeRowTest(int index) {
        if(!createRows){
            rows.get(index).dropColumn();
        }
        vcc.setCurrentRowLength(vcc.getCurrentRowLength() - 1);

        removeAllMessageFromThisRow(index);
        getRows().remove(index);
        // resetGridPaneRowsTest();
    }

    /**
     * Query update for Drop a Column
     * 
     * @param e the removeColumn Button (update only)
     */
    void dropColumn() {
        final int index = getThisIndex();
        String column = tfColumn.getText().trim();
        // ARE YOU SURE WINDOW ====================================================
        TrueFalseWindow w = new TrueFalseWindow("Delete Column '" + column + "'?");
        w.getBtnFalse().setOnAction(ef -> w.hide());
        w.getBtnTrue().setOnAction(et -> {
            VL.LOG.info("REMOVE COLUMN");
            // DROP COLUMN ========================================================
            String tableName = vcc.getCurrentTable().getName();
            boolean dropColumn = vcc.getMs().dropColumn(tableName, column);
            if (dropColumn) {
                // REMOVE CUSTOM ----------------------------------
                // NOT TESTED
                if (vcc.getCurrentTable().getColumns().get(index).isDist()) {
                    // String dist = updateTable.getDistHole();
                    String dist = vcc.getCurrentTable().getDist();
                    dist = dist.replace(column, "").replaceAll("(^,|,$)", "");// removing ',' at the beggining an end
                    dist = dist.isEmpty() ? "NONE" : dist;

                    boolean removeDistTest = vcc.getMs().updateRow(MSQL.TABLE_CUSTOMS, "id_table", vcc.getCurrentTable().getId(), "dist",
                            dist);
                    System.out.println("TEST removeDistTest: " + removeDistTest);
                }
                // NOT TESTED
                if (vcc.getCurrentTable().getColumns().get(index).isTextArea()) {
                    String textArea = vcc.getCurrentTable().getTextArea();
                    textArea = textArea.replace(column, "").replaceAll("(^,|,$)", "");
                    textArea = textArea.isEmpty() ? "NONE" : textArea;

                    boolean removeTextArea = vcc.getMs().updateRow(MSQL.TABLE_CUSTOMS, "id_table", vcc.getCurrentTable().getId(),
                            "textArea", textArea);
                    System.out.println("TEST removeTextArea: " + removeTextArea);
                }
                // ===================================================
                vcc.getCurrentTable().getColumns().remove(index);
                // MESSAGES --------------------------------------------------
                vcc.getLbStatus().setText("Column '" + column.replace("_", " ") + "' has been removed", vcc.getLbStatus().getTextFillOk(),
                        Duration.seconds(3));
                System.out.println("\tSUCCESS");
            } else {
                vcc.getLbStatus().setText("Column '" + column.replace("_", " ") + "' fail to be removed",
                        vcc.getLbStatus().getTextFillError());
                System.out.println("\tFAILED");
            }
            // --------------------------------------
            w.hide();
        });
        w.show();

        // --------------------------------
        vcc.masterControlTest();
    }

    static void addRowTest(int index) {
        VCRow vcRow = new VCRow(index);
        // resetGridPaneRowsTest();
        vcc.setCurrentRowLength(vcc.getCurrentRowLength() + 1);
        rows.add(index, vcRow);

        if (!createRows) {
            rows.get(index).startAddColumnState();
        }
    }

    // COLUMN --------------------------------------------
    private void columnsTextProperty() {
        String text = tfColumn.getText().toLowerCase().trim().replace(" ", "_");
        sameColumnsList.set(getThisIndex(), text);

        if (!text.trim().isEmpty()) {
            if (getColumnMatch(text)) {
                messageColumn.getItemList().removeIf(s -> !s.contains("id-"));
                // columnMatchControl(index); NECESSARY ?????
            } else {
                messageColumn.getItemList().add(VCStore.ILLEGAL_CHARS);
            }
        } else {
            messageColumn.getItemList().add(VCStore.EMPTY_TEXT);
        }

        columnBWOK = messageColumn.getItemList().stream()
                .noneMatch(s -> s.contains(VCStore.ILLEGAL_CHARS) || s.contains(VCStore.EMPTY_TEXT));

        // UPDATE---------------------------------------------------
        columnUpdateControl(text, columnBWOK);
        // ---------------------------------------------------------
        vcc.masterControlTest();
        vcc.selectsControl();
    }

    private void columnUpdateControl(String text, boolean update) {
        if (!createRows) {
            columnAddOk = false;
            if (update) {
                if (columnBWOK && columnsSameOk) {
                    boolean under = isUnderTheColumnsLimit();
                    String currentColumn = "";
                    if (under) {
                        currentColumn = vcc.getCurrentTable().getColumns().get(getThisIndex()).getName();
                    }

                    if (!text.equals(currentColumn)) {
                        tfColumn.setStyle(null);
                        columnAddOk = true;
                    } else {
                        tfColumn.setStyle(CSS.NODE_TEXTFILL_HINT);
                    }
                }
            }
            if (!columnAddState) {
                btnChangeColumn.setDisable(!columnAddOk);
            } else {
                addColumnControl();
            }
        }
    }

    void updateColumn() {
        VL.LOG.info("Rename Column (Start)");
        int index = getThisIndex();
        String tableName = vcc.getCurrentTable().getName();
        String columnName = vcc.getCurrentTable().getColumns().get(index).getName();
        String newColumnName = tfColumn.getText().toLowerCase().trim().replace(" ", "_");

        boolean renameColumn = vcc.getMs().renameColumn(tableName, columnName, newColumnName);
        if (renameColumn) {
            VL.LOG.log(Level.FINE, "SUCCESS");            
            // RENAME THE COLUMN AND THEN TEST THE CANCEL BUTTON TO RESET !!!!!!!!
            vcc.getCurrentTable().getColumns().get(index).setName(newColumnName);

            btnChangeColumn.setDisable(true);
            vcc.getLbStatus().setText("Column '" + columnName + "' changed to '" + newColumnName + "'", vcc.getLbStatus().getTextFillOk(),
                    Duration.seconds(2));
        } else {
            VL.LOG.log(Level.SEVERE, "FAILED");            
            vcc.getLbStatus().setText("Column '" + columnName + "' fail to be renamed", vcc.getLbStatus().getTextFillError());
        }
    }
    // TYPE ----------------------------------------------
    private void typeTextProperty() {
        boolean updateType = false;

        String text = tfType.getText().trim();
        Matcher matcher = patternBWTC.matcher(text);
        if (matcher.matches()) {
            messageType.getItemList().remove(VCStore.ILLEGAL_CHARS);

            SQLTypes sqlTypes = SQLTypes.getInstance();
            final String newValuee = text;

            if (Arrays.asList(sqlTypes.getTypeNames()).stream().anyMatch(item -> item.equals(newValuee))) {
                int typeLength = sqlTypes.getTypeLength(text);
                if (typeLength > 0) {// TF-TYPE-LENGTH-POPUP
                    tfTypeLength.setVisible(true);
                    tfTypeLength.setText(Integer.toString(typeLength));
                } else {
                    tfTypeLength.setVisible(false);
                    tfTypeLength.setText("1");

                    typeLengthOK = true;
                }
                messageType.getItemList()
                        .removeIf(s -> s.contains(VCStore.ILLEGAL_CHARS) || s.contains(VCStore.SELECTION_UNMATCH));

                typeSelectionMatch = true;
                updateType = true;
                // typeControl(index); NECESSARY ????????
            } else {
                tfTypeLength.setVisible(false);
                tfTypeLength.setText("1");

                messageType.getItemList().add(VCStore.SELECTION_UNMATCH);

                typeSelectionMatch = false;
            }
        } else {
            messageType.getItemList().add(VCStore.ILLEGAL_CHARS);
            typeSelectionMatch = false;
        }

        // defaultAndTypesControl(index);
        // UPDATE----------------------------------------------------------
        typeUpdateControl(text, updateType);
        // ----------------------------------------------------------
        typeAndDefaultControl();
        textAreaTypeControl();
        vcc.masterControlTest();
    }

    private void typeLengthTextProperty() {
        if (tfTypeLength.isVisible()) {
            String text = tfTypeLength.getText().toLowerCase().trim();
            // Max lenght of the current tfType's text
            int typeMaxLength = types.getTypeMaxLength(tfType.getText());

            boolean updateType = false;
            // Max digit control (1, 5)
            Matcher matcher = patternTypeLength.matcher(text);
            if (matcher.matches()) {
                int length = Integer.parseInt(text);
                // Under the type max limit ~ OK
                if (length <= typeMaxLength) {
                    messageType.getItemList().removeIf(s -> s.contains("%length"));
                    typeLengthOK = true;
                    updateType = true;
                    // typeLengthControl(index); Unec
                } else {
                    messageType.getItemList().add(VCStore.getWrongTypeLength(typeMaxLength) + "%length");
                    typeLengthOK = false;
                }
            } else {
                messageType.getItemList().add(VCStore.getWrongTypeLength(typeMaxLength) + "%length");
                typeLengthOK = false;

            }

            // defaultAndTypesControl(index);
            // UPDATE-----------------------------------------------------
            typeLengthUpdateControl(text, updateType);
        }
        // -----------------------------------------------------
        typeAndDefaultControl();
        vcc.masterControlTest();
    }

    private void typeUpdateControl(String type, boolean update) {
        if (!createRows) {
            typeAddOk = false;
            if (update) {
                boolean under = isUnderTheColumnsLimit();
                String typeO = "";
                if (under) {
                    typeO = vcc.getCurrentTable().getColumns().get(getThisIndex()).getType();
                }

                if (!type.equals(typeO)) {
                    tfType.setStyle(null);
                    typeAddOk = true;
                } else {
                    tfType.setStyle(CSS.NODE_TEXTFILL_HINT);
                }
            }

            if (!columnAddState) {
                btnChangeType.setDisable(!typeAddOk);
            } else {
                addColumnControl();
            }
        }
    }

    private void typeLengthUpdateControl(String typeLength, boolean update) {
        if (!createRows) {
            typeLenghtAddOk = false;
            if (update) {
                boolean under = isUnderTheColumnsLimit();

                String typeLengthO = "";
                if (under) {
                    typeLengthO = Integer
                            .toString(vcc.getCurrentTable().getColumns().get(getThisIndex()).getTypeLength());
                }
                if (!typeLength.equals(typeLengthO)) {
                    tfTypeLength.setStyle(null);
                    typeLenghtAddOk = true;
                } else {
                    tfTypeLength.setStyle(CSS.NODE_TEXTFILL_HINT);
                }
            }

            if (!columnAddState) {
                btnChangeType.setDisable(!typeLenghtAddOk);
            } else {
                addColumnControl();
            }
        }
    }

    void updateType() {
        VL.LOG.info("Change Type");

        int index = getThisIndex();
        String tableName = vcc.getCurrentTable().getName();
        String columnName = vcc.getCurrentTable().getColumns().get(index).getName();
        String type = vcc.getCurrentTable().getColumns().get(index).getFullType();

        boolean changeType = vcc.getMs().changeType(tableName, columnName, type);
        if (changeType) {
            VL.LOG.log(Level.FINE, "SUCCESS");
            vcc.getCurrentTable().getColumns().get(index).setType(tfType.getText());
            vcc.getCurrentTable().getColumns().get(index).setTypeLength(
                    tfTypeLength.isVisible() ? Integer.parseInt(tfTypeLength.getText()) : 0);

            vcc.getLbStatus().setText("Column '" + columnName + "' has change it's type to '" + type + "'", vcc.getLbStatus().getTextFillOk(),
                    Duration.seconds(2));

            btnChangeType.setDisable(true);
        } else {
            VL.LOG.log(Level.SEVERE, "FAILED");
            vcc.getLbStatus().setText("Column '" + columnName + "' fail to change it's type", vcc.getLbStatus().getTextFillError());
        }

    }
    // NULL ----------------------------------------------
    private void nullUpdateControl() {
        // !createRows - unecessary bc of init listeners
        int index = getThisIndex();

        boolean nulllO = vcc.getCurrentTable().getColumns().get(index).isNulll();
        boolean nulll = ckNull.isSelected();

        if (nulllO != nulll) {
            ckNull.setStyle(CSS.CKS_BG);
            btnChangeNull.setDisable(false);
        } else {
            ckNull.setStyle(CSS.CKS_BG_HINT);
            btnChangeNull.setDisable(true);
        }
    }

    private void updateNull() {
        VL.LOG.info("Change Null (Starts)");
        
        int index = getThisIndex();
        String tableName = vcc.getCurrentTable().getName();
        String columnName = vcc.getCurrentTable().getColumns().get(index).getName();
        String type = vcc.getCurrentTable().getColumns().get(index).getFullType();
        boolean nulll = ckNull.isSelected();
        // ----------------------------------------------
        vcc.getMs().setNullValue(nulll);
        vcc.getMs().setExtraValue(index == vcc.getCurrentTable().getExtra());
        boolean changeNull = vcc.getMs().changeType(tableName, columnName, type);
        if (changeNull) {
            VL.LOG.log(Level.FINE, "SUCCESS");
            // updateTable.getNulls().set(index, nulll);
            vcc.getCurrentTable().getColumns().get(index).setNulll(nulll);

            ckNull.setStyle(CSS.CKS_BG_HINT);
            btnChangeNull.setDisable(true);

            vcc.getLbStatus().setText("Column '" + columnName + "' change to " + (nulll ? "NULL" : "NOT NULL"),
                    vcc.getLbStatus().getTextFillOk(), Duration.seconds(2));
        } else {
            VL.LOG.log(Level.SEVERE, "FAILED");
            vcc.getLbStatus().setText("Column '" + columnName + "' fail to be changed", vcc.getLbStatus().getTextFillError());
        }
    }
    // PK ------------------------------------------------
    void pkUpdateControl() {
        // ONLY FOR UPDATE
        boolean updatePK = false;

        extraPKOK = !(rbExtra.isSelected() && !rbPK.isSelected());
        // UPDATE--------------------------------
        if (!createRows) {
            for (int c = 0; c < vcc.getCurrentRowLength(); c++) {
                if (c < vcc.getCurrentTable().getColumns().size()) {
                    boolean pkSelected = vcc.getCurrentTable().getColumns().get(c).isPk();
                    if (rows.get(c).getRbPK().isSelected() != pkSelected) {
                        updatePK = true;
                        break;
                    }
                } else {
                    if (rows.get(c).getRbPK().isSelected()) {
                        updatePK = true;
                        break;
                    }
                }
            }

            vcc.getBtnUpdatePK().setDisable(!updatePK);
        }

        // extraAndPKControl(index);
        vcc.masterControlTest();
    }

    // DEFAULT -------------------------------------------
    private void defaultsAction() {
        if (ckDefault.isSelected()) {
            tfDefault.setVisible(true);
        } else {
            tfDefault.setVisible(false);
            defaultOK = true;
        }

        // extraAndDefaultControl(index);
        // -------------------------------------------
        typeAndDefaultControl();
        vcc.masterControlTest();
    }

    private void defaultsTextProperty() {
        String text = tfDefault.getText();
        if (!text.isEmpty()) {
            messageDefault.getItemList().remove(VCStore.EMPTY_TEXT);
        } else {
            messageDefault.getItemList().add(VCStore.EMPTY_TEXT);
        }

        typeAndDefaultControl();
        // extraAndDefaultControl(index);
        vcc.masterControlTest();
    }

    private void typeAndDefaultControl() {
        typeDefaultOk = false;

        if (ckDefault.isSelected()) {
            if (messageType.getItemList().stream().noneMatch(
                    mess -> mess.contains(VCStore.ILLEGAL_CHARS) || mess.contains(VCStore.SELECTION_UNMATCH))) {
                String typeText = tfType.getText();
                int typeLength = Integer.parseInt(tfTypeLength.getText());
                String typeChar = types.getTypeChar(typeText);

                String defaultText = tfDefault.getText();

                Pattern pattern = null;
                if (tfTypeLength.isVisible()) {
                    if (typeChar.equals("NUMBER")) {
                        pattern = Pattern.compile("\\d{1," + typeLength + "}");
                    } else if (typeChar.equals("STRING")) {
                        if (typeLength == 1) {
                            pattern = Pattern.compile(".{1}");
                        } else {
                            pattern = Pattern.compile(".{1," + typeLength + "}");
                        }
                    }
                }

                typeDefaultOk = pattern != null ? pattern.matcher(defaultText).matches() : true;
            }
        } else {
            typeDefaultOk = true;
        }

        if (typeDefaultOk) {
            messageType.getItemList().remove(VCStore.TYPE_AND_DEFAULT);
            messageDefault.getItemList().remove(VCStore.TYPE_AND_DEFAULT);
        } else {
            messageType.getItemList().add(VCStore.TYPE_AND_DEFAULT);
            messageDefault.getItemList().add(VCStore.TYPE_AND_DEFAULT);
        }

        defaultUpdateControl(typeDefaultOk);
    }

    private void defaultUpdateControl(boolean update) {
        if (!createRows) {
            defaultAddOk = false;
            if (update) {
                boolean under = isUnderTheColumnsLimit();
                Boolean defaultSelectedO = null;
                String defaultValueO = null;
                if (under) {
                    defaultSelectedO = vcc.getCurrentTable().getColumns().get(getThisIndex()).getDefaultt() != null;
                    defaultValueO = defaultSelectedO
                            ? vcc.getCurrentTable().getColumns().get(getThisIndex()).getDefaultt().toString()
                            : "";
                }

                String defaultValue = tfDefault.getText();
                if (under) {
                    if (ckDefault.isSelected() != Boolean.TRUE.equals(defaultSelectedO)) {
                        if (ckDefault.isSelected()) {
                            if (!defaultValueO.equals(defaultValue)) {
                                defaultAddOk = true;
                            }
                        } else {
                            defaultAddOk = true;
                        }
                    }
                } else {
                    defaultAddOk = true;
                }
            }

            if (defaultAddOk) {
                ckDefault.setStyle(null);
                tfDefault.setStyle(null);
            } else {
                ckDefault.setStyle(CSS.BG_COLOR_HINT);
                tfDefault.setStyle(CSS.NODE_TEXTFILL_HINT);
            }

            if (!columnAddState) {
                btnChangeDefault.setDisable(!defaultAddOk);
            } else {
                addColumnControl();
            }
        }
    }

    void updateDefault() {
        VL.LOG.info("Change Default (Starts)");

        int index = getThisIndex();
        String tableName = vcc.getCurrentTable().getName();
        String columnName = vcc.getCurrentTable().getColumns().get(index).getName();
        String type = vcc.getCurrentTable().getColumns().get(index).getType();
        String defaultt = tfDefault.getText();

        Object defaultValue = null;
        if (ckDefault.isSelected()) {
            if (types.getTypeChar(type).equals("STRING")) {
                defaultValue = defaultt;
            } else if (types.getTypeChar(type).equals("NUMBER")) {
                defaultValue = Integer.parseInt(defaultt);
            } else if (types.getTypeChar(type).equals("NUMBER") && type.startsWith("BIGINT")) {
                // NOT TESTED
                defaultValue = Long.parseLong(defaultt);
            } else if (types.getTypeChar(type).equals("DECIMAL") && type.startsWith("FLOAT")) {
                // NOT TESTED
                defaultValue = Float.parseFloat(defaultt);
            } else if (types.getTypeChar(type).equals("DECIMAL") && type.startsWith("DOUBLE")) {
                // NOT TESTED
                defaultValue = Double.parseDouble(defaultt);
            } else if (types.getTypeChar(type).equals("BOOLEAN")) {
                // NOT TESTED
                defaultValue = defaultt.equals("true");
            }
        }

        boolean setDefaultValue = vcc.getMs().addDefaultValue(tableName, columnName, defaultValue);
        if (setDefaultValue) {
            VL.LOG.log(Level.FINE, "SUCCESS");
            vcc.getCurrentTable().getColumns().get(index).setDefaultt(defaultValue);

            tfDefault.setStyle(CSS.NODE_TEXTFILL_HINT); // ???
            ckDefault.setStyle(CSS.CKS_BG_HINT);
            btnChangeDefault.setDisable(true);

            vcc.getLbStatus().setText(
                    "Default value for column '" + columnName + "' has change to "
                            + (defaultValue != null ? defaultValue.toString() : "NULL"),
                    vcc.getLbStatus().getTextFillOk(), Duration.seconds(2));
        } else {
            VL.LOG.log(Level.SEVERE, "FAILED");
            vcc.getLbStatus().setText("Failt to change Default Value of column '" + columnName + "'", vcc.getLbStatus().getTextFillError());
        }

    }
    // EXTRA ---------------------------------------------
    private void extraSelectedProperty() {
        extraPKControl();
        // extraFKControl();
        extraDefaultControl();
        extraDistControl();
        textAreaExtraControl();
        // extrasRestControl(index); UNCE
        // UPDATE------------------------------------------------
        extraUpdateControl();
        // ------------------------------------------------
        vcc.masterControlTest();
    }

    private void extraUpdateControl() {
        if (!createRows) {
            if (extraDefaultOK && extraPKOK && extraFKOK && extraDistOK) {
                // int extraO = updateTable.getExtra();
                int extraO = vcc.getCurrentTable().getExtra();
                int extra = rbExtra.isSelected() ? getThisIndex() : -1;

                if (extra != extraO) {
                    rbExtra.setStyle(CSS.BG_COLOR);
                    vcc.getBtnUpdateExtra().setDisable(false);
                } else {
                    rbExtra.setStyle(CSS.BG_COLOR_HINT);
                    vcc.getBtnUpdateExtra().setDisable(true);
                }
            } else {
                vcc.getBtnUpdateExtra().setDisable(true);
            }
        }
    }

    // DIST ----------------------------------------------
    private void distSelectedProperty() {
        // MAY HAVE TO ADD DIST BUTTONS TO AN OBSERVABLE LIST (ADD OR REMOVE COLUMN) ??
        extraDistControl();
        // UPDATE--------------------------------------------------------------
        distUpdateControl();
        // --------------------------------------------------------------
        vcc.masterControlTest();
    }

    private void distUpdateControl() {
        if (!createRows) {
            if (extraDistOK) {
                boolean update = false;
                for (int a = 0; a < vcc.getCurrentRowLength(); a++) {
                    if (a < vcc.getCurrentTable().getColumns().size()) {
                        boolean distO = vcc.getCurrentTable().getColumns().get(a).isDist();

                        if (rows.get(a).getBtnDist().isSelected() != distO || (columnAddState)) {
                            update = true;
                            break;
                        }
                    } else {
                        if (rows.get(a).getBtnDist().isSelected()) {
                            update = true;
                            break;
                        }
                    }

                }

                vcc.getBtnUpdateDist().setDisable(!update);
            } else {
                vcc.getBtnUpdateDist().setDisable(true);
            }
        }
    }

    // TEXT AREA -----------------------------------------
    private void textAreaSelectedProperty() {
        textAreaExtraControl();
        textAreaTypeControl();

        // UPDATE --------------------------------
        textAreaUpdateControl();
    }

    private void textAreaUpdateControl() {
        if (!createRows) {
            if (textAreaExtraOK && textAreaTypeOK) {
                boolean update = false;
                for (int a = 0; a < vcc.getCurrentRowLength(); a++) {
                    if (a < vcc.getCurrentTable().getColumns().size()) {
                        boolean textAreaO = vcc.getCurrentTable().getColumns().get(getThisIndex()).isTextArea();
                        if (rows.get(a).getBtnTextArea().isSelected() != textAreaO || (columnAddState)) {
                            update = true;
                            break;
                        }
                    } else {
                        if (rows.get(a).getBtnTextArea().isSelected()) {
                            update = true;
                            break;
                        }
                    }
                }

                vcc.getBtnUpdateDist().setDisable(!update);
            } else {
                vcc.getBtnUpdateTextArea().setDisable(true);
            }
        }
    }

    // CONSTRUCTOR / INIT ==============================================================
    {
        // ROWS -----------------------------------------
        rows.addListener(VCRow::rowsChange);
        sameColumnsList.addListener(VCRow::sameColumnsChange);
        // SQL TYPES ------------------------------------
        final SQLTypes types = SQLTypes.getInstance();

        for (int a = 0; a < MSQL.MAX_COLUMNS; a++) {
            if (a == 0) {
                PRESET_TYPES.add(types.getType("INT"));
            } else {
                PRESET_TYPES.add(types.getType("CHAR"));
            }
        }
    }

    private void initListeners(){
        // COLUMN ---------------------------------------------
        tfColumn.textProperty().addListener((obs, oldValue, newValue) -> columnsTextProperty());

        btnRemoveColumn.setOnAction(e -> removeRowTest(getThisIndex()));
        btnAddColumn.setOnAction(e -> addRowTest(getThisIndex()));

        if(!createRows){
            btnChangeColumn.setOnAction(e -> updateColumn());
        }
        // TYPE ------------------------------------------------
        tfType.textProperty().addListener((obs, oldValue, newValue) -> typeTextProperty());
        tfTypeLength.textProperty().addListener((obs, oldValue, newValue) -> typeLengthTextProperty());

        if(!createRows){
            btnChangeType.setOnAction(e -> updateType());
        }
        // NULL ------------------------------------------------
        if(!createRows){
            ckNull.setOnAction(e -> nullUpdateControl());
            btnChangeNull.setOnAction(e -> updateNull());
        }
        // PK --------------------------------------------------
        rbPK.setOnAction(e -> pkUpdateControl());
        // DEFAULT ---------------------------------------------
        ckDefault.setOnAction(e -> defaultsAction());
        tfDefault.textProperty().addListener((obs, oldValue, newValue) -> defaultsTextProperty());

        if(!createRows){
            btnChangeDefault.setOnAction(e -> updateDefault());
        }
        // EXTRA -----------------------------------------------
        rbExtra.selectedProperty().addListener((obs, oldValue, newValue) -> extraSelectedProperty());
        // CUSTOM ----------------------------------------------
        btnDist.selectedProperty().addListener((obs, oldValue, newValue) -> distSelectedProperty());
        btnTextArea.selectedProperty().addListener((obs, oldValue, newValue) -> textAreaSelectedProperty());
    }

    public VCRow(int index) {
        // COLUMN NUMBER ===============================================
        lbN.setText("Column " + (index + 1));

        hbN.getChildren().add(lbN);
        hbN.getStyleClass().add(ROW_CLASS);
        // COLUMN NAME =================================================
        tfColumn.setPromptText(COLUMN_NAME_REQUIRED);
        HBox.setHgrow(tfColumn, Priority.ALWAYS);

        btnRemoveColumn.setPrefWidth(40);
        HBox.setHgrow(btnRemoveColumn, Priority.NEVER);

        btnAddColumn.setPrefWidth(40);
        HBox.setHgrow(btnAddColumn, Priority.NEVER);

        btnChangeColumn.managedProperty().bind(btnChangeColumn.visibleProperty());
        btnChangeColumn.setVisible(!createRows);
        btnChangeColumn.setDisable(true);

        hbColumn.getChildren().addAll(tfColumn, btnRemoveColumn, btnAddColumn, btnChangeColumn);
        hbColumn.setPadding(COLUMN_CONTAINER_PADDING);
        hbColumn.getStyleClass().add(ROW_CLASS);

        messageColumn = new Message("COLUMN:" + index, hbColumn);
        vcc.getPopupMessageControl().getMessages().add(messageColumn);

        if (createRows) {
            messageColumn.getItemList().add(VCStore.EMPTY_TEXT);
        }
        // TYPE ===========================================================
        tfType.setPrefWidth(140);
        HBox.setHgrow(tfType, Priority.ALWAYS);
        tfTypeAutoC = new PopupAutoC(tfType, SQLTypes.getInstance().getTypeNames());
        if (createRows) {
            tfTypeAutoC.getLv().getSelectionModel().select(PRESET_TYPES.get(index).getTypeName());

            tfTypeLength.setText(Integer.toString(PRESET_TYPES.get(index).getTypeLength()));
        }
        tfTypeLength.setPrefWidth(40);
        HBox.setHgrow(tfTypeLength, Priority.NEVER);
        tfTypeLength.managedProperty().bind(tfTypeLength.visibleProperty());

        btnChangeType.managedProperty().bind(btnChangeType.visibleProperty());
        btnChangeType.setVisible(!createRows);
        btnChangeType.setDisable(true);

        hbType.getChildren().addAll(tfType, tfTypeLength, btnChangeType);
        hbType.setPadding(COLUMN_CONTAINER_PADDING);
        hbType.getStyleClass().add(ROW_CLASS);

        messageType = new Message("TYPE:" + index, hbType);
        vcc.getPopupMessageControl().getMessages().add(messageType);
        // NULL =============================================================
        btnChangeNull.managedProperty().bind(btnChangeNull.visibleProperty());
        btnChangeNull.setVisible(!createRows);
        btnChangeNull.setDisable(true);

        hbNull.getChildren().addAll(ckNull, btnChangeNull);
        hbNull.setPadding(COLUMN_CONTAINER_PADDING);
        hbNull.getStyleClass().add(ROW_CLASS);
        // PK ============================================================
        hbPK.getChildren().addAll(rbPK);
        hbPK.getStyleClass().add(ROW_CLASS);

        messagePK = new Message("PK:" + index, hbPK);
        vcc.getPopupMessageControl().getMessages().add(messagePK);
        // DEFAULT =======================================================
        tfDefault.setPromptText(VALUE_REQUIRED);
        tfDefault.managedProperty().bind(tfDefault.visibleProperty());
        tfDefault.setVisible(false);

        btnChangeDefault.managedProperty().bind(btnChangeDefault.visibleProperty());
        btnChangeDefault.setVisible(!createRows);
        btnChangeDefault.setDisable(true);

        hbDefault.getChildren().addAll(ckDefault, tfDefault, btnChangeDefault);
        hbDefault.setPadding(COLUMN_CONTAINER_PADDING);
        hbDefault.getStyleClass().add(ROW_CLASS);

        messageDefault = new Message("DEFAULT:" + index, hbDefault);
        vcc.getPopupMessageControl().getMessages().add(messageDefault);
        // EXTRA ===========================================================
        hbExtra.getChildren().addAll(rbExtra);
        hbExtra.getStyleClass().add(ROW_CLASS);

        messageExtra = new Message("EXTRA:" + index, hbExtra);
        vcc.getPopupMessageControl().getMessages().add(messageExtra);
        // CUSTOM ==========================================================
        btnDist.setText(Integer.toString(index + 1));
        btnTextArea.setText(Integer.toString(index + 1));

        btnDist.setPrefWidth(40);
        btnTextArea.setPrefWidth(40);

        btnDist.getStyleClass().add(ROW_CLASS);
        btnTextArea.getStyleClass().add(ROW_CLASS);

        messageDist = new Message("DIST:" + index, btnDist);
        messageTextArea = new Message("TEXTAREA:" + index, btnTextArea);

        vcc.getPopupMessageControl().getMessages().add(messageDist);
        vcc.getPopupMessageControl().getMessages().add(messageTextArea);

        initListeners();
        /**
         * GridPane.setMargin(hbsN.get(index), INSETS);
         * GridPane.setMargin(hbsName.get(index), INSETS);
         * GridPane.setMargin(hbsType.get(index), INSETS);
         * GridPane.setMargin(hbsNull.get(index), INSETS);
         * GridPane.setMargin(hbsPK.get(index), INSETS);
         * GridPane.setMargin(hbsDefault.get(index), INSETS);
         * GridPane.setMargin(hbsExtra.get(index), INSETS);
         * 
         * GridPane.setMargin(btnsDist.get(index), INSETS);
         * GridPane.setMargin(btnsTextArea.get(index), INSETS);
         */
    }

    // GETTERS & SETTERS =======================================================
    public static boolean isCreateRows() {
        return createRows;
    }

    public static void setCreateRows(boolean createRows) {
        VCRow.createRows = createRows;
    }

    public static ObservableList<VCRow> getRows() {
        return rows;
    }

    public static ObservableList<String> getSamecolumnslist() {
        return sameColumnsList;
    }

    public HBox getHbN() {
        return hbN;
    }

    public Label getLbN() {
        return lbN;
    }

    public HBox getHbColumn() {
        return hbColumn;
    }

    public TextField getTfColumn() {
        return tfColumn;
    }

    public Button getBtnRemoveColumn() {
        return btnRemoveColumn;
    }

    public Button getBtnAddColumn() {
        return btnAddColumn;
    }

    public Button getBtnChangeColumn() {
        return btnChangeColumn;
    }

    public Message getMessageColumn() {
        return messageColumn;
    }

    public void setMessageColumn(Message messageColumn) {
        this.messageColumn = messageColumn;
    }

    public HBox getHbType() {
        return hbType;
    }

    public TextField getTfType() {
        return tfType;
    }

    public PopupAutoC getTfTypeAutoC() {
        return tfTypeAutoC;
    }

    public void setTfTypeAutoC(PopupAutoC tfTypeAutoC) {
        this.tfTypeAutoC = tfTypeAutoC;
    }

    public TextField getTfTypeLength() {
        return tfTypeLength;
    }

    public Button getBtnChangeType() {
        return btnChangeType;
    }

    public Message getMessageType() {
        return messageType;
    }

    public void setMessageType(Message messageType) {
        this.messageType = messageType;
    }

    public HBox getHbNull() {
        return hbNull;
    }

    public CheckBox getCkNull() {
        return ckNull;
    }

    public Button getBtnChangeNull() {
        return btnChangeNull;
    }

    public HBox getHbPK() {
        return hbPK;
    }

    public RadioButton getRbPK() {
        return rbPK;
    }

    public Message getMessagePK() {
        return messagePK;
    }

    public void setMessagePK(Message messagePK) {
        this.messagePK = messagePK;
    }

    public HBox getHbDefault() {
        return hbDefault;
    }

    public CheckBox getCkDefault() {
        return ckDefault;
    }

    public TextField getTfDefault() {
        return tfDefault;
    }

    public Button getBtnChangeDefault() {
        return btnChangeDefault;
    }

    public Message getMessageDefault() {
        return messageDefault;
    }

    public void setMessageDefault(Message messageDefault) {
        this.messageDefault = messageDefault;
    }

    public HBox getHbExtra() {
        return hbExtra;
    }

    public RadioButton getRbExtra() {
        return rbExtra;
    }

    public Message getMessageExtra() {
        return messageExtra;
    }

    public void setMessageExtra(Message messageExtra) {
        this.messageExtra = messageExtra;
    }

    public ToggleButton getBtnDist() {
        return btnDist;
    }

    public ToggleButton getBtnTextArea() {
        return btnTextArea;
    }

    public Message getMessageDist() {
        return messageDist;
    }

    public void setMessageDist(Message messageDist) {
        this.messageDist = messageDist;
    }

    public Message getMessageTextArea() {
        return messageTextArea;
    }

    public void setMessageTextArea(Message messageTextArea) {
        this.messageTextArea = messageTextArea;
    }

    public static VCController getVcc() {
        return vcc;
    }

    public static void setVcc(VCController vcc) {
        VCRow.vcc = vcc;
    }

    public boolean isTypeSelectionMatchOk() {
        return typeSelectionMatch;
    }

    public void setTypeSelectionMatch(boolean typeSelectionMatch) {
        this.typeSelectionMatch = typeSelectionMatch;
    }

    public boolean isColumnBWOK() {
        return columnBWOK;
    }

    public void setColumnBWOK(boolean columnBWOK) {
        this.columnBWOK = columnBWOK;
    }

    public static boolean isColumnsSameOk() {
        return columnsSameOk;
    }

    public static void setColumnsSameOk(boolean columnsSameOk) {
        VCRow.columnsSameOk = columnsSameOk;
    }

    public boolean isTypeLengthOK() {
        return typeLengthOK;
    }

    public void setTypeLengthOK(boolean typeLengthOK) {
        this.typeLengthOK = typeLengthOK;
    }

}
