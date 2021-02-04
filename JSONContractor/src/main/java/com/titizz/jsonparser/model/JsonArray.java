package com.titizz.jsonparser.model;

import com.titizz.jsonparser.BeautifyJsonUtils;
import com.titizz.jsonparser.exception.JsonTypeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: Gengda Li
 * @create_date: 2/4/2021 11:53 AM
 * @desc:
 * @modifier:
 * @modifier_date:
 * @desc:
 */

public class JsonArray implements Iterable {
    private List list = new ArrayList();

    public void add(Object obj) { list.add(obj); }

    public Object get(int index) { return list.get(index); }

    public int size() { return list.size(); }

    public JsonObject getJsonObject(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof JsonObject)) {
            throw new JsonTypeException("Type of value is not JsonObject");
        }

        return (JsonObject) obj;
    }

    public JsonArray getJsonArray(int index) {
        Object obj = list.get(index);
        if (!(obj instanceof JsonArray)) {
            throw new JsonTypeException("Type of value is not JsonArray");
        }

        return (JsonArray) obj;
    }

    // TODO
    // @Override

    public Iterator iterator() { return list.iterator(); }
}
