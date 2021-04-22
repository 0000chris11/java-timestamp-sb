package com.cofii.ts.sql;

public class MSQL {
    //DBS AND TABLES
    public static final String ROOT_DB = "RootConfig";
    public static final String TABLE_DEFAULT_USER = "defaultuser";
    public static final String TABLE_DEFAULT = "default_table";
    public static final String TABLE_CONFIG = "table_config";
    public static final String TABLE_NAMES = "table_names";

    //BAND DBS AND TABLES
    public static final String[] BAND_USERS = {"mysql.infoschema", "mysql.session", "mysql.sys"};
    public static final String[] BAND_DB = {"information_schema", "mysql", "performance_schema", "sys"};
    
    //MAIN VARIABLES
    private static String user;
    private static String password;
    private static String database;

    //QUERYS
    public static final String CREATE_DB_ROOTCONFIG = "CREATE DATABASE ROOTCONFIG";
    public static final String CREATE_TABLE_DEFAULT_USER = "CREATE TABLE " + TABLE_DEFAULT_USER + "(User CHAR(100) NOT NULL, Password CHAR(50) NOT NULL, Database CHAR(100) NOT NULL)";
    public static final String INSERT_TABLE_DEFAULT_USER = "INSERT INTO " + TABLE_DEFAULT_USER + "(\"NONE\", \"NONE\", \"NONE\")";
    public static final String SELECT_TABLE_ROW_DEFAULT_USER = "SELECT * FROM " + TABLE_DEFAULT_USER + " LIMIT 1";
    public static final String UPDATE_TABLE_DEFAULT_USER = "UPDATE " + TABLE_DEFAULT_USER + " SET Name = \"" + user + "\", Passw = \"" + password + "\", Datab = \"" + database + "\" LIMIT = 1";

    private static boolean dbRootconfigExist = false;
    private static boolean tableDefaultUserExist = false;
    private static boolean tableDefaultExist = false;
    private static boolean tableConfigExist = false;
    private static boolean tableNamesExist = false;
    //-------------------------------------------------
    public static boolean isDbRootconfigExist() {
        return dbRootconfigExist;
    }

    public static void setDbRootconfigExist(boolean dbRootconfigExist) {
        MSQL.dbRootconfigExist = dbRootconfigExist;
    }
    public static boolean isTableDefaultUserExist() {
        return tableDefaultUserExist;
    }

    public static void setTableDefaultUserExist(boolean tableDefaultUserExist) {
        MSQL.tableDefaultUserExist = tableDefaultUserExist;
    }
    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        MSQL.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        MSQL.password = password;
    }

    public static String getDatabase() {
        return database;
    }

    public static void setDatabase(String database) {
        MSQL.database = database;
    }
    
    public static boolean isTableDefaultExist() {
        return tableDefaultExist;
    }

    public static void setTableDefaultExist(boolean tableDefaultExist) {
        MSQL.tableDefaultExist = tableDefaultExist;
    }

    public static boolean isTableConfigExist() {
        return tableConfigExist;
    }

    public static void setTableConfigExist(boolean tableConfigExist) {
        MSQL.tableConfigExist = tableConfigExist;
    }

    public static boolean isTableNamesExist() {
        return tableNamesExist;
    }

    public static void setTableNamesExist(boolean tableNamesExist) {
        MSQL.tableNamesExist = tableNamesExist;
    }

    //-------------------------------------------------
    private MSQL(){

    }
}
