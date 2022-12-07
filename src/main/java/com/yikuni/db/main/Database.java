package com.yikuni.db.main;

import com.yikuni.db.Util.PropertiesUtil;
import com.yikuni.db.exception.YikuniDBException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;

public class Database {
    private String savePath;
    private LinkedList<Table<?>> tables;
    private Long saveGap;
    private Properties properties;

    public Database(String savePath, Long saveGap) throws YikuniDBException {
        this.saveGap = saveGap;
        this.savePath = savePath;
        tables = new LinkedList<>();
        File file = new File(savePath);
        if (file.exists()){
            if (!file.isDirectory()) throw new YikuniDBException("Save Path Can Not Be a File !");
            File propertiesFile = new File(file, "database.properties");
            if (propertiesFile.exists()){
                // 如果不是新的数据库
                loadProperties(propertiesFile);
                loadTables();
            }else {
                init();
            }
        }else {
            file.mkdirs();
            init();
        }
    }

    private void loadProperties(File file){
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Table<?> getTable(String tableName){
        for(Table<?> table: tables){
            if (table.getName().equals(tableName)){
                return table;
            }
        }
        return null;
    }

    public <T> Table<T> getTable(String tableName, Class<T> clazz){
        for(Table<?> table: tables){
            if (table.getName().equals(tableName)){
                return (Table<T>) table;
            }
        }
        return null;
    }

    private void loadTables(){
        String tablesStr = properties.getProperty("tables");
        if (tablesStr == null){
            return;
        }
        String[] tableStrs = tablesStr.split(",");
        for (String tableName: tableStrs){
            loadTable(tableName);
        }
    }

    private <T> void loadTable(String tableName){
        String objClassStr = properties.getProperty(tableName + ".objClass");
        String strategyStr = properties.getProperty(tableName + ".strategy");
        try {
            Class<T> objClass = (Class<T>) Class.forName(objClassStr);
            Class<? extends SerializeStrategy> strategyClass = (Class<? extends SerializeStrategy>) Class.forName(strategyStr);
            Table<T> table = new Table<T>();
            table.setName(tableName);
            table.setDb(this);
            table.setClazz(objClass);
            table.setStrategy(strategyClass.newInstance());
            table.loadData();
            tables.add(table);
            Thread thread = new Thread(table.getStrategy());
            table.setSaveThread(thread);
            thread.start();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        try {
            File file = new File(savePath, "database.properties");
             file.createNewFile();
            properties = new Properties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> Table<T> createTable(String tableName, Class<T> elementClass) throws YikuniDBException {
        if (existTable(tableName)){
            throw new YikuniDBException("Table already Exists: " + tableName);
        }
        Table<T> table = new Table<T>();
        table.setClazz(elementClass);
        table.setName(tableName);
        table.setDb(this);
        table.setData(new LinkedList<>());
        tables.add(table);
        return table;
    }

    public <T> Table<T> createTable(String tableName, Class<T> elementClass, SerializeStrategy strategy) throws YikuniDBException {
        Table<T> table = createTable(tableName, elementClass);
        properties.setProperty(tableName + ".objClass", elementClass.getName());
        onTableChange();
        table.setStrategy(strategy);
        Thread thread = new Thread(strategy);
        table.setSaveThread(thread);
        thread.start();
        properties.setProperty(tableName + ".strategy", strategy.getClass().getName());
        updateProperties();
        return table;
    }

    public boolean existTable(String tableName){
        for (Table<?> table: tables){
            if (table.getName().equals(tableName)){
                return true;
            }
        }
        return false;
    }

    private void onTableChange(){
        LinkedList<String> tableNames = new LinkedList<>();
        tables.forEach(table -> {
            tableNames.add(table.getName());
        });
        properties.setProperty("tables", PropertiesUtil.getListString(tableNames));
        updateProperties();
    }

    private void updateProperties(){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(savePath, "database.properties"));
            properties.store(out, new SimpleDateFormat("yyyy-MM-dd mm:HH:ss").format(new Date()));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(){
        tables.forEach(table ->{
            table.save();
        });
    }

    public String getSavePath() {
        return savePath;
    }

    private void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    private LinkedList<Table<?>> getTables() {
        return tables;
    }

    private void setTables(LinkedList<Table<?>> tables) {
        this.tables = tables;
    }

    protected Long getSaveGap() {
        return saveGap;
    }

    private void setSaveGap(Long saveGap) {
        this.saveGap = saveGap;
    }

    private Properties getProperties() {
        return properties;
    }

    private void setProperties(Properties properties) {
        this.properties = properties;
    }
}
