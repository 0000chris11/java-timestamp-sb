package com.cofii.ts.first.nodes;

import com.cofii.ts.first.VFController;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GetRowSelectedImpl implements ActionForEachNode {

    private VFController vf;
    private Object[] values;

    public GetRowSelectedImpl(VFController vf, Object[] values) {
        this.vf = vf;
        this.values = values;
    }

    @Override
    public void forTFS(TextField tf, int c) {
        if (vf.getBtns()[c].isSelected()) {
            tf.setText(values[c].toString());
        }
        
    }

    @Override
    public void forTAS(TextArea tfa, int c) {
        if (vf.getBtns()[c].isSelected()) {
            tfa.setText(values[c].toString());
        }
    }

    @Override
    public void either(int c) {
        // NOT IMPL
    }

    @Override
    public void getLength(int length) {
        // TODO Auto-generated method stub

    }

}