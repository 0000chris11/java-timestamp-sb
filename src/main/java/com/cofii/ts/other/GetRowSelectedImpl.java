package com.cofii.ts.other;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class GetRowSelectedImpl implements ActionForEachNode {

    private Object[] values;
    public GetRowSelectedImpl(Object[] values){

        this.values = values;
    }

    @Override
    public void forTFS(TextField tf, int c) {
        tf.setText(values[c].toString());

    }

    @Override
    public void forCBS(ComboBox<String> cb, int c) {
        cb.getEditor().setText(values[c].toString());
    }

    @Override
    public void either(int c) {
        //NOT IMPL
    }

    @Override
    public void getLength(int length) {
        // TODO Auto-generated method stub
        
    }

    
}
