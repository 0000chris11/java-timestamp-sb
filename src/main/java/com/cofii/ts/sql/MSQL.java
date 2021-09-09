package com.cofii.ts.sql;

import com.cofii.ts.store.main.Users;

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
        public static final String TABLE_NAMES = "table_names";
        public static final String PATHS = "paths";
        public static final String TABLE_PATHS = "table_paths";
        public static final String TABLE_IMAGECS = "table_imagecs";

        // MAIN VARIABLES
        public static final int MAX_COLUMNS = 10;

        public static final int DEFAULT_IMAGES_LENGTH = 4;
        public static final int MAX_IMAGES_LENGTH = 10;

        public static final int MAX_PATHS = 6;

        // QUERYS ============================================
        // MAIN DATABASE-------------------------------------------------
        public static final String CREATE_DB_ROOTCONFIG = "CREATE DATABASE IF NOT EXISTS ROOTCONFIG";
        // MAIN TABLES---------------------------------------------------
        public static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS
                        + "(id INT NOT NULL AUTO_INCREMENT, " + "user_name VARCHAR(200) NOT NULL, "
                        + "user_password VARBINARY(200) NOT NULL, " + "PRIMARY KEY(id), "
                        + "UNIQUE INDEX idx_user_name (user_name))";
        public static final String CREATE_TABLE_USERS_DEFAULTS = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_DEFAULTS
                        + "(user_id INT NOT NULL, " + "database_id INT NULL, " + "table_name CHAR(200) NULL, "
                        + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id), "
                        + "FOREIGN KEY(database_id) REFERENCES " + TABLE_DATABASES + "(id), "
                        + "UNIQUE INDEX idx_user_id (user_id))";
        public static final String CREATE_TABLE_DATABASES = "CREATE TABLE IF NOT EXISTS " + TABLE_DATABASES
                        + "(id INT NOT NULL AUTO_INCREMENT, " + "id_user INT, " + "user_database CHAR(255) NOT NULL, "
                        + "PRIMARY KEY(id), " + "FOREIGN KEY(id_user) REFERENCES users(id), "
                        + "UNIQUE INDEX idx_user_database (user_database))";
        public static final String CREATE_TABLE_DEFAULT_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_DEFAULT_USER
                        + "(id_user INT, " + "FOREIGN KEY(id_user) REFERENCES " + TABLE_USERS + "(id))";

        public static final String CREATE_TABLE_USER_OPTIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_OPTIONS
                        + "(id INT NOT NULL AUTO_INCREMENT, " + "option_name CHAR(100) NOT NULL, "
                        + "default_value CHAR(50) NOT NULL, " + "PRIMARY KEY(id), "
                        + "UNIQUE INDEX idx_option_name (option_name))";
        public static final String CREATE_TABLE_USER_DEFAULTS_OPTIONS = "CREATE TABLE IF NOT EXISTS "
                        + TABLE_USER_DEFAULTS_OPTIONS + "(id_user INT NOT NULL, " + "id_option INT NOT NULL, "
                        + "user_value CHAR(50) NOT NULL, " + "FOREIGN KEY(id_user) REFERENCES " + TABLE_USERS + "(id), "
                        + "FOREIGN KEY(id_option) REFERENCES " + TABLE_USER_OPTIONS + "(id))";
        // FOR EACH DATABASE----------------------------------------------
        public static final String CREATE_TABLE_NAMES = "CREATE TABLE IF NOT EXISTS " + TABLE_NAMES
                        + "(id INT NOT NULL AUTO_INCREMENT, " + "Name CHAR(100) NOT NULL, "
                        + "Dist1 CHAR(100) NOT NULL, " + "ImageC VARCHAR(200) NOT NULL, "
                        + "ImageC_Path VARCHAR(500) NOT NULL, " + "PRIMARY KEY (id, name), "
                        + "UNIQUE INDEX idx_name (name))";
        public static final String CREATE_PATHS = "CREATE TABLE IF NOT EXISTS " + PATHS
                        + "(id INT NOT NULL AUTO_INCREMENT, " + "path_name VARCHAR(300) NOT NULL," + "PRIMARY KEY(id), "
                        + "UNIQUE INDEX idx_path_name (path_name))";
        public static final String CREATE_TABLE_PATHS = "CREATE TABLE IF NOT EXISTS " + TABLE_PATHS
                        + "(id_table INT NOT NULL, " + "id_path INT NOT NULL, " + "FOREIGN KEY(id_table) REFERENCES "
                        + Users.getInstance().getCurrenUser().getCurrentDatabase().getName() + ".table_names(id), "
                        + "FOREIGN KEY(id_path) REFERENCES "
                        + Users.getInstance().getCurrenUser().getCurrentDatabase().getName() + ".paths(id))";

        public static final String CREATE_TABLE_IMAGECS = "CREATE TABLE IF NOT EXISTS " + TABLE_IMAGECS
                        + "(id_table INT NOT NULL, " + "columns_names CHAR(255) NOT NULL, "
                        + "images_length INT NOT NULL, " + "display_order CHAR(50) NOT NULL, "
                        + "image_type CHAR(50) NOT NULL, " + "FOREIGN KEY(id_table) REFERENCES "
                        + Users.getInstance().getCurrenUser().getCurrentDatabase().getName()
                        + ".table_names(id), UNIQUE INDEX idx_id_table (id_table))";

        // -------------------------------------------------
        private MSQL() {

        }
}
