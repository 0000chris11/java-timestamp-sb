package com.cofii.ts.sql;

public class MSQL {
        // MAIN DATABASE-------------------------------------------------
        public static final String ROOT_DB = "RootConfig";
        // MAIN TABLES--------------------------------------------------
        public static final String TABLE_USERS = "rootconfig.users";

        public static final String TABLE_DEFAULT_USER = "rootconfig.default_user";
        public static final String TABLE_DATABASES = "rootconfig.users_databases";

        public static final String TABLE_USER_DEFAULTS = "rootconfig.user_defaults";
        public static final String TABLE_USER_DEFAULTS_OPTIONS = "rootconfig.user_defaults_options";
        public static final String TABLE_USER_OPTIONS = "rootconfig.user_options";
        // FOR EACH DATABASE----------------------------------------------
        public static final String TABLE_DEFAULT = "default_table";// DELETE
        public static final String TABLE_CONFIG = "table_config";
        public static final String TABLE_NAMES = "table_names";
        public static final String TABLE_PATHS = "table_paths";

        // MAIN VARIABLES
        public static final int MAX_COLUMNS = 10;
        public static final int MAX_IMAGES = 4;

        // QUERYS ============================================
        // MAIN DATABASE-------------------------------------------------
        public static final String CREATE_DB_ROOTCONFIG = "CREATE DATABASE IF NOT EXISTS ROOTCONFIG";
        // MAIN TABLES---------------------------------------------------
        public static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS
                        + "(id INT NOT NULL AUTO_INCREMENT, " + "user_name VARCHAR(200) NOT NULL, "
                        + "user_password VARBINARY(200) NOT NULL, " + "PRIMARY KEY(id))";
        public static final String CREATE_TABLE_USERS_DEFAULTS = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_DEFAULTS
                        + "(user_id INT NOT NULL, " + "database_id INT NULL, " + "table_name CHAR(200) NULL, "
                        + "PRIMARY KEY(user_id), " + "FOREIGN KEY(database_id) REFERENCES " + TABLE_DATABASES + "(id))";
        public static final String CREATE_TABLE_DATABASES = "CREATE TABLE IF NOT EXISTS " + TABLE_DATABASES
                        + "(id INT NOT NULL AUTO_INCREMENT, " + "id_user INT, user_database CHAR(255) NOT NULL, "
                        + "PRIMARY KEY(id), " + "FOREIGN KEY(id_user) REFERENCES users(id))";
        public static final String CREATE_TABLE_DEFAULT_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_DEFAULT_USER
                        + "(id_user INT, " + "FOREIGN KEY(id_user) REFERENCES " + TABLE_USERS + "(id))";

        public static final String CREATE_TABLE_USER_OPTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_OPTIONS
                        + "(id INT NOT NULL AUTO_INCREMENT, " + "option_name CHAR(100) NOT NULL, "
                        + "default_value CHAR(50) NOT NULL, " + "PRIMARY KEY(id))";
        public static final String CREATE_TABLE_USER_DEFAULTS_OPTIONS = "CREATE TABLE IF NOT EXISTS "
                        + TABLE_USER_DEFAULTS_OPTIONS + "(id_user INT NOT NULL, " + "id_option INT NOT NULL, "
                        + "user_value CHAR(50) NOT NULL, " + "FOREIGN KEY(id_user) REFERENCES " + TABLE_USERS + "(id), "
                        + "FOREIGN KEY(id_option) REFERENCES " + TABLE_USER_OPTIONS + "(id))";
        // FOR EACH DATABASE----------------------------------------------
        public static final String CREATE_TABLE_NAMES = "CREATE TABLE IF NOT EXISTS " + TABLE_NAMES
                        + "(id INT NOT NULL AUTO_INCREMENT, " + "Name CHAR(100) NOT NULL, "
                        + "Dist CHAR(100) NOT NULL, " + "PRIMARY KEY (id, name))";
        public static final String CREATE_TABLE_DEFAUT = "CREATE TABLE IF NOT EXISTS " + TABLE_DEFAULT
                        + "(id INT, name CHAR(100), " + "FOREIGN KEY(id, name) REFERENCES table_names(id, name))";
        public static final String CREATE_TABLE_CONFIG = "CREATE TABLE IF NOT EXISTS " + TABLE_CONFIG
                        + "(id INT NOT NULL, " + "Name CHAR(100) NOT NULL, " + "Value BOOLEAN NOT NULL, "
                        + "PRIMARY KEY(id))";

        // -------------------------------------------------
        private MSQL() {

        }
}
