package com.cofii.ts.first.nodes;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ClearNodesDisplayed implements ActionForEachNode{

    @Override
    public void forTFS(TextField tf, int a) {
        tf.setText("");
        
    }

    @Override
    public void forTAS(TextArea tfa, int a) {
        tfa.setText("");
        
    }

    @Override
    public void either(int a) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void getLength(int length) {
        // TODO Auto-generated method stub
        
    }
    
}
