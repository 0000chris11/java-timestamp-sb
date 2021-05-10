package com.cofii.ts.sql;

import com.cofii.ts.store.Table;

public class MSQL {
    // DBS AND TABLES
    public static final String ROOT_DB = "RootConfig";
    public static final String TABLE_DEFAULT_USER = "defaultuser";
    public static final String TABLE_DEFAULT = "default_table";
    public static final String TABLE_CONFIG = "table_config";
    public static final String TABLE_NAMES = "table_names";

    // BAND DBS AND TABLES
    public static final String[] BAND_USERS = { "mysql.infoschema", "mysql.session", "mysql.sys" };
    public static final String[] BAND_DB = { "information_schema", "mysql", "performance_schema", "sys" };
    // TYPES
    public static final String[] TYPES = { "INT", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "FLOAT", "DOUBLE",
            "CHAR", "VARCHAR", "BOOLEAN/TINYINT(1)", "TIME", "DATE", "DATETIME", "TIMESTAMP", "BINARY", "VARBINARY" };

    // MAIN VARIABLES
    private static String user;
    private static String password;
    private static String[] databases;
    private static String database;
    private static Table currentTable = null;
    private static String[] columns;

    private static int columnsLength;
    public static final int MAX_COLUMNS = 10;

    // QUERYS
    public static final String CREATE_DB_ROOTCONFIG = "CREATE DATABASE ROOTCONFIG";

    public static final String CREATE_TABLE_DEFAULT_USER = "CREATE TABLE " + TABLE_DEFAULT_USER
            + "(User CHAR(100) NOT NULL, Password CHAR(50) NOT NULL, Database CHAR(100) NOT NULL)";
    public static final String CREATE_TABLE_NAMES = "CREATE TABLE " + TABLE_NAMES
            + "(id INT NOT NULL AUTO_INCREMENT, Name CHAR(100) NOT NULL, Dist CHAR(100) NOT NULL, PRIMARY KEY (id, name))";
    public static final String CREATE_TABLE_DEFAUT = "CREATE TABLE " + TABLE_DEFAULT
            + "(id INT, name CHAR(100), FOREIGN KEY(id, name) REFERENCES table_names(id, name))";
    public static final String CREATE_TABLE_CONFIG = "CREATE TABLE " + TABLE_CONFIG
            + "(id INT NOT NULL, Name CHAR(100) NOT NULL, Value BOOLEAN NOT NULL, PRIMARY KEY(id))";

    public static final String INSERT_TABLE_DEFAULT_USER = "INSERT INTO " + TABLE_DEFAULT_USER
            + " VALUES (\"NONE\", \"NONE\", \"NONE\")";

    public static final String SELECT_TABLE_ROW_DEFAULT_USER = "SELECT * FROM " + TABLE_DEFAULT_USER + " LIMIT 1";
    public static final String SELECT_TABLE_ROW_DEFAULT = "SELECT * FROM " + TABLE_DEFAULT + " LIMIT 1";
    public static final String SELECT_TABLE_NAMES = "SELECT * FROM " + TABLE_NAMES;

    public static final String UPDATE_TABLE_DEFAULT_USER = "UPDATE " + TABLE_DEFAULT_USER + " SET Name = \"" + user
            + "\", Passw = \"" + password + "\", Datab = \"" + database + "\" LIMIT = 1";

    // BOOLEANS
    private static boolean dbRootconfigExist = false;
    private static boolean tableDefaultUserExist = false;
    private static boolean tableDefaultExist = false;
    private static boolean tableConfigExist = false;
    private static boolean tableNamesExist = false;
    private static boolean tablesOnTableNames = false;

    private static boolean wrongPassword = false;

    // -------------------------------------------------
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

    public static boolean isTablesOnTableNames() {
        return tablesOnTableNames;
    }

    public static void setTablesOnTableNames(boolean tablesOnTableNames) {
        MSQL.tablesOnTableNames = tablesOnTableNames;
    }

    public static boolean isWrongPassword() {
        return wrongPassword;
    }

    public static void setWrongPassword(boolean wrongPassword) {
        MSQL.wrongPassword = wrongPassword;
    }

    public static Table getCurrentTable() {
        return currentTable;
    }

    public static void setCurrentTable(Table table) {
        MSQL.currentTable = table;
    }

    public static int getColumnsLength() {
        return columnsLength;
    }

    public static void setColumnsLength(int columnsLength) {
        MSQL.columnsLength = columnsLength;
    }

    public static String[] getColumns() {
        return columns;
    }

    public static void setColumns(String[] columns) {
        MSQL.columns = columns;
    }
    
    // -------------------------------------------------
    private MSQL() {

    }

    public static String[] getDatabases() {
        return databases;
    }

    public static void setDatabases(String[] databases) {
        MSQL.databases = databases;
    }
}
