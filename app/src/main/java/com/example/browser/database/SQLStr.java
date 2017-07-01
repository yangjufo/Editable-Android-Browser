package com.example.browser.database;

/**
 * Created by Young on 2016/11/29.
 */

public class SQLStr {
    //定义sql语句
    public static final String CREATE_DATABASE =
            "create database com_webBrowser_data";
    public static final String CREATE_TABLE_FAVORITES =
            "create table favorite (id integer primary key, name varchar not null, url varchar not null)";
    public static final String CREATE_TABLE_HISTORY =
            "create table history (id integer primary key, name varchar not null, url varchar not null, date integer not null)";
}
