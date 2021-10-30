package com.cofii.ts.cu.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VCStore {

    public static final String TABLE_EXISTS = "This Table already exist";
    public static final String DUPLICATE_COLUMNS_NAMES = "Dupicated columns names";

    public static final String ILLEGAL_CHARS = "Illegal Chars";
    public static final String SELECTION_UNMATCH = "Selection Unmatch";
    public static final String EMPTY_TEXT = "Column name field can't be empty";
    public static final String WRONG_LENGTH = "Wrong Type length";

    public static String getWrongTypeLength(int maxLength) {
        return WRONG_LENGTH + " (1 to " + maxLength + ")";
    }

    public static String getWrongDefaultLength(String typeChar, boolean lengthVisible, int typeLength) {
        return ILLEGAL_CHARS + "- Match (" + typeChar + (lengthVisible ? ": must' be " + typeLength + " max)" : ")")
                + "%DEFAULT";
    }

    public static final String TYPE_AND_DEFAULT = "Type and or TypeLenght does not match with Default's value";
    public static final String AUTO_INCREMENT_AND_PK = "AUTO_INCREMENT should be a PRIMARY KEY";
    public static final String AUTO_INCREMENT_AND_FK = "There's no need to have a FOREIGN KEY column with AUTO_INCREMENT";
    public static final String AUTO_INCREMENT_AND_DEFAULT = "There's no need to have a DEFAULT value in a column with AUTO_INCREMENT";
    public static final String AUTO_INCREMENT_AND_DIST = "Dist shouldn't be AUTO_INCREMENT";

    public static final String TEXT_AREA_AND_EXTRA = "A TextArea column shouldn't be AUTO_INCREMENT";
    public static final String TEXT_AREA_AND_TYPE = "A TextArea column can only be a \"TEXT\" base type";

    static final List<String> ERRORS_LIST = new ArrayList<>(Arrays.asList(TABLE_EXISTS, DUPLICATE_COLUMNS_NAMES,
            ILLEGAL_CHARS, SELECTION_UNMATCH, EMPTY_TEXT, WRONG_LENGTH, TYPE_AND_DEFAULT, AUTO_INCREMENT_AND_PK,
            AUTO_INCREMENT_AND_FK, AUTO_INCREMENT_AND_DEFAULT, AUTO_INCREMENT_AND_DIST, TEXT_AREA_AND_EXTRA, TEXT_AREA_AND_TYPE));

    // ----------------------------------------------------
    private VCStore() {

    }
    // -------------------------------------------------------

    public static List<String> getErrorsList() {
        return ERRORS_LIST;
    }
    
}
