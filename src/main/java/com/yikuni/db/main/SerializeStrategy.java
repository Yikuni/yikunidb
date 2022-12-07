package com.yikuni.db.main;

import java.io.File;
import java.io.IOException;

public abstract class SerializeStrategy implements Runnable{
    protected Table table;
    protected File file;

    protected abstract void load();
    public abstract void save();

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(table.getDb().getSaveGap());
                save();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
        file = new File(table.getDb().getSavePath(), table.getName() + getSuffix());
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    protected abstract String getSuffix();
}
