package com.yikuni.db.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yikuni.db.Util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class JsonSerializeStrategy extends SerializeStrategy{
    public JsonSerializeStrategy() {
    }

    @Override
    public void load() {
        String json = FileUtil.readFileToString(file);
        if (Objects.equals(json, "")){
            table.setData(new LinkedList());
        }else {
            List<?> list = JSONObject.parseArray(json, table.getClazz());
            table.setData(list);
        }

    }

    @Override
    public void save() {
        List<?> data = table.getData();
        String json = JSON.toJSONString(data);
        FileUtil.writeJSONString(file, data);
    }

    @Override
    protected String getSuffix() {
        return ".json";
    }
}
