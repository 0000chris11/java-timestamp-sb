package com.cofii.ts.cu.store;

public class VCStore {

    public static final String ILLEGAL_CHARS = "Illegal Chars";
    public static final String ILLEGAL_CHARS_COLUMN = "Illegal Chars%COLUMN";
    public static final String ILLEGAL_CHARS_TYPE = "Illegal Chars%TYPE";

    public static final String SELECTION_UNMATCH = "Selection Unmatch";
    public static final String SELECTION_UNMATCH_TYPE = "Selection Unmatch%TYPE";
    public static final String SELECTION_UNMATCH_FK = "Selection Unmatch%FK";

    public static final String EMPTY_TEXT = "Column name field can't be empty";

    public static final String WRONG_LENGTH = "Wrong length";
    public static String getWrongTypeLength(int maxLength) {
        return WRONG_LENGTH + " (1 to " + maxLength + ")";
    }

    public static String getWrongDefaultLength(String typeChar, boolean lengthVisible, int typeLength) {
        return ILLEGAL_CHARS + "- Match (" + typeChar + (lengthVisible ? ": must' be " + typeLength + " max)" : ")") + "%DEFAULT";
    }

    public static final String WRONG_LENGTH_2 = "Select a correct \"Type\" and lenght (if needed)";
    public static final String TYPE_AND_DEFAULT = "Select a correct \"Type\" and lenght (if needed)%DEFAULT";

    public static final String AUTO_INCREMENT_AND_PK = "AUTO_INCREMENT should be a PRIMARY KEY";
    public static final String AUTO_INCREMENT_AND_PK_O = "AUTO_INCREMENT should be a PRIMARY KEY%PK";
    public static final String AUTO_INCREMENT_AND_PK_EXTRA = "AUTO_INCREMENT should be a PRIMARY KEY%EXTRA";

    public static final String AUTO_INCREMENT_AND_FK = "There's no need to have a FOREIGN KEY column with AUTO_INCREMENT";
    public static final String AUTO_INCREMENT_AND_FK_O = "There's no need to have a FOREIGN KEY column with AUTO_INCREMENT%FK";
    public static final String AUTO_INCREMENT_AND_FK_EXTRA = "There's no need to have a FOREIGN KEY column with AUTO_INCREMENT%EXTRA";

    public static final String AUTO_INCREMENT_AND_DEFAULT = "There's no need to have a DEFAULT value in a column with AUTO_INCREMENT";
    public static final String AUTO_INCREMENT_AND_DEFAULT_O = "There's no need to have a DEFAULT value in a column with AUTO_INCREMENT%DEFAULT";
    public static final String AUTO_INCREMENT_AND_DEFAULT_EXTRA = "There's no need to have a DEFAULT value in a column with AUTO_INCREMENT%EXTRA";

    public static final String AUTO_INCREMENT_AND_DIST = "Dist shouldn't be AUTO_INCREMENT";
    public static final String AUTO_INCREMENT_AND_DIST_O = "Dist shouldn't be AUTO_INCREMENT%DIST";
    public static final String AUTO_INCREMENT_AND_DIST_EXTRA = "Dist shouldn't be AUTO_INCREMENT%EXTRA";

    // ----------------------------------------------------
    private VCStore() {

    }
}
