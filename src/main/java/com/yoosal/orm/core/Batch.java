package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.exception.DatabaseOperationException;

import java.util.ArrayList;
import java.util.List;

public class Batch {
    private Object[] columns;
    private List<ModelObject> objects;
    private Class objectClass;

    public Batch(List<ModelObject> objects, Object... columns) {
        if (!this.isConsistent(objects)) {
            throwException();
        }
        this.columns = columns;
        this.objects = objects;
    }

    public Object[] getColumns() {
        return columns;
    }

    public void setColumns(Object... columns) {
        this.columns = columns;
    }

    public List<ModelObject> getObjects() {
        return objects;
    }

    public void setObjects(List<ModelObject> objects) {
        if (this.isConsistent(objects)) {
            this.objects = objects;
        } else {
            throwException();
        }
    }

    public Class getObjectClass() {
        return objectClass;
    }

    public void addBatch(ModelObject object) {
        if (objectClass == null && object != null) {
            objectClass = object.getObjectClass();
        }
        if (!objectClass.equals(object.getObjectClass())) {
            throwException();
        }
        if (objects == null) {
            objects = new ArrayList<ModelObject>();
        }
        objects.add(object);
    }

    private boolean isConsistent(List<ModelObject> objects) {
        if (objectClass == null && objects.size() > 0) {
            objectClass = objects.get(0).getObjectClass();
        }
        for (ModelObject object : objects) {
            if (!object.getObjectClass().equals(objectClass)) {
                return false;
            }
        }
        return true;
    }

    private void throwException() {
        throw new DatabaseOperationException("batch objects has different table");
    }

}
