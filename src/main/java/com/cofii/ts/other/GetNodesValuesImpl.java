package com.cofii.ts.other;

import com.cofii.ts.sql.MSQL;
import com.cofii.ts.store.ColumnS;
import com.cofii2.components.javafx.TextFieldAutoC;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class GetNodesValuesImpl implements ActionForEachNode {

    private Object[] values;
    private ColumnS columns = ColumnS.getInstance();

    public GetNodesValuesImpl(Object[] values) {
        this.values = values;
    }

    @Override
    public void forTFS(TextField tf, int c) {
        if (columns.getType(c).contains("CHAR")) {
            if (!tf.getText().trim().isEmpty()) {
                values[c] = tf.getText().trim();
            }
        } else if (columns.getType(c).contains("INT")) {
            try {
                if (!tf.getText().trim().isEmpty()) {
                    values[c] = Integer.parseInt(tf.getText().trim());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("C0FII: Only integer are accepted in this column");
            }
        }

    }

    @Override
    public void forTFAS(TextFieldAutoC tfa, int c) {
        if (columns.getType(c).contains("CHAR")) {
            values[c] = tfa.getTf().getText().trim();
        } else if (columns.getType(c).contains("INT")) {
            try {
                values[c] = Integer.parseInt(tfa.getTf().getText().trim());
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
