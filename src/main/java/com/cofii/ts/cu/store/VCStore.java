package com.cofii.ts.cu.store;

public class VCStore {

    public static final String ILLEGAL_CHARS = "Illegal Chars";
    public static final String SELECTION_UNMATCH = "Selection Unmatch";
    public static final String EMPTY_TEXT = "Column name field can't be empty";

    public static String getWrongLength(int maxLength) {
        return "Wrong length (1 to " + maxLength + ")";
    }

    public static String getWrongLength(String typeChar, boolean lengthVisible, int typeLength) {
        return ILLEGAL_CHARS + "- Match (" + typeChar + (lengthVisible ? ": must' be " + typeLength + " max)" : ")");
    }

    public static final String TYPE_AND_DEFAULT = "Select a correct \"Type\" and lenght (if needed)";

    public static final String AUTO_INCREMENT_AND_PK = "AUTO_INCREMENT should be a PRIMARY KEY";
    public static final String AUTO_INCREMENT_AND_FK = "There's no need to have a FOREIGN KEY column with AUTO_INCREMENT";
    public static final String AUTO_INCREMENT_AND_DEFAULT = "There's no need to have a DEFAULT value in a column with AUTO_INCREMENT";
    public static final String AUTO_INCREMENT_AND_DIST = "Dist shouldn't be AUTO_INCREMENT";

    // ----------------------------------------------------
    private VCStore() {

    }
}
