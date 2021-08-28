package com.cofii.ts.store.main;

import java.util.ArrayList;
import java.util.List;

public class Options {

    private List<Option> optionList = new ArrayList<>();
    // --------------------------------------------------
    public Option getOptionById(int id){
        return optionList.stream().filter(op -> op.getId() == id).toArray(size -> new Option[size])[0];
    }
    // --------------------------------------------------
    private static Options instance;
    public static Options getInstance() {
        if (instance == null) {
            instance = new Options();
        }
        return instance;
    }
    // --------------------------------------------------

    public List<Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<Option> optionList) {
        this.optionList = optionList;
    }

    
}
