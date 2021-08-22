package com.cofii.ts.first.nodes;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public interface ActionForEachNode {
    
    /**
     * For all TextFields displayed
     * 
     * @param tf Each Texfield action
     * @param a Textfield id
     */
    public void forTFS(TextField tf, int a);
    /**
     * For all TextAreas displayed
     * 
     * @param tfa Each TextArea action
     * @param a TextArea id
     */
    public void forTAS(TextArea tfa, int a);
    //DELETE
    public void either(int a);
    //DELETE
    public void getLength(int length);
}
