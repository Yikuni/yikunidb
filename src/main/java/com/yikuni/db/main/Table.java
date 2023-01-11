package com.yikuni.db.main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class Table<T> implements Iterable<T>{
    private String name;
    private List<T> data;
    private SerializeStrategy strategy;
    private Database db;
    private Thread saveThread;
    private Class<T> clazz;

    /**
     * 查找表中的对象
     * @param o 对象
     * @return  存放符合条件的list
     */
    public List<T> select(T o){
        List<T> resultList = new LinkedList<>();
        Method[] methods = o.getClass().getMethods();
        List<Method> compareMethod = new LinkedList<>();
        for (Method method: methods){
            if (method.getParameterCount() == 0 && method.getName().startsWith("get") && !method.getName().equals("getClass")){
                // 如果是get方法
                try {
                    Object invoke = method.invoke(o);
                    if (invoke != null){
                        // 暂时只支持5个数据类型的比较
                        // 忽略下面的几种情况
                        if (invoke instanceof Integer && (int)invoke == 0) continue;
                        if (invoke instanceof Long && (Long)invoke == 0) continue;
                        if (invoke instanceof Double && (double) invoke == 0.0) continue;
                        if (invoke instanceof Float && (float) invoke == 0.0) continue;
                        if (invoke instanceof List) continue;
                        // 如果这个字段不是空, 要比较
                        compareMethod.add(method);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        for (T element: data){
            boolean equal = true;
            for (Method method: compareMethod){
                try {
                    Object t1 = method.invoke(element);
                    Object t2 = method.invoke(o);

                    if (!t1.equals(t2)){
                        equal = false;
                        break;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            if (equal){
                resultList.add(element);
            }
        }
        return resultList;
    }

    /**
     * 插入
     * @param o 记录
     */
    public void add(T o){
        data.add(o);
    }
    public String getName() {
        return name;
    }

    /**
     * 根据条件删除
     * @param o 对象
     * @return  删除的个数
     */
    public Integer deleteSelective(T o){
        List<T> tList = select(o);
        for (T element: tList){
            data.remove(element);
        }
        return tList.size();
    }

    /**
     * 移除某个对象
     * @param o 对象
     * @return  成功, 返回true
     */
    public boolean remove(T o){
        return data.remove(o);
    }

    public boolean save(){
        if (strategy != null){
            strategy.save();
            return true;
        }else {
            return false;
        }
    }

    protected void loadData(){
        strategy.load();
    }

    protected void setName(String name) {
        this.name = name;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    protected SerializeStrategy getStrategy() {
        return strategy;
    }

    protected void setStrategy(SerializeStrategy strategy) {
        this.strategy = strategy;
        strategy.setTable(this);
    }

    protected Database getDb() {
        return db;
    }

    protected void setDb(Database db) {
        this.db = db;
    }

    protected Thread getSaveThread() {
        return saveThread;
    }

    protected void setSaveThread(Thread saveThread) {
        this.saveThread = saveThread;
    }

    protected Class<T> getClazz() {
        return clazz;
    }

    protected void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }
}
