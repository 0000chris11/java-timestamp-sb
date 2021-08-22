package com.cofii.ts.first.nodes;

import java.util.List;

import com.cofii.ts.store.main.Column;
import com.cofii.ts.store.main.Users;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GetNodesValuesImpl implements ActionForEachNode {

    private Object[] values;

    public GetNodesValuesImpl(Object[] values) {
        this.values = values;
    }

    @Override
    public void forTFS(TextField tf, int c) {
        List<Column> columns = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable().getColumns();

        if (columns.get(c).getType().contains("CHAR")) {
            if (!tf.getText().trim().isEmpty()) {
                values[c] = tf.getText().trim();
            } else if (columns.get(c).getDefaultt() != null) {
                // DEFAULT
                values[c] = columns.get(c).getDefaultt();
            }else if(columns.get(c).getExtra()){
                //UNNECESARY??
            }
        } else if (columns.get(c).getType().contains("INT")) {
            try {
                if (!tf.getText().trim().isEmpty()) {
                    values[c] = Integer.parseInt(tf.getText().trim());
                } else if (columns.get(c).getDefaultt() != null) {
                    // DEFAULT
                    values[c] = Integer.parseInt(columns.get(c).getDefaultt());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("C0FII: Only integer are accepted in this column");
            }
        }

    }

    @Override
    public void forTAS(TextArea tfa, int c) {
        List<Column> columns = Users.getInstance().getCurrenUser().getCurrentDatabase().getCurrentTable().getColumns();

        if (columns.get(c).getType().contains("CHAR")) {
            values[c] = tfa.getText().trim();
        } else if (columns.get(c).getType().contains("INT")) {
            try {
                values[c] = Integer.parseInt(tfa.getText().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("C0FII: Only integer are accepted in this column");
            }

        }
    }

    @Override
    public void either(int c) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getLength(int length) {
        // TODO Auto-generated method stub

    }

    // ---------------------------------------
    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

}
