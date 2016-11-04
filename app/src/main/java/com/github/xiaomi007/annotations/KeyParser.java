package com.github.xiaomi007.annotations;

import android.content.ContentValues;
import android.util.Log;

import com.github.xiaomi007.annotations.annotations.Key;
import com.github.xiaomi007.annotations.annotations.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class KeyParser {
    private static final String TAG = "KeyParser";

    public static void parse(Object o) {
        Class<?> clazz = o.getClass();
        Annotation cAnnotation = clazz.getAnnotation(Table.class);
        Table table = (Table) cAnnotation;
        Log.d(TAG, "parse: " + table.table());
        Field[] fields = o.getClass().getFields();
        Log.d(TAG, "methods size: " + fields.length);
        for (Field field : fields) {
            Log.d(TAG, "method name: " + field.getName());
            Log.d(TAG, "method type: " + field.getType().getSimpleName());
            try {
                Log.d(TAG, "method field: " + field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Annotation annotation = field.getAnnotation(Key.class);
            Log.d(TAG, "parse: " + annotation);
            if (annotation != null) {
                Key key = (Key) annotation;
                Log.d(TAG, "field: " + key.field());
            }
        }
    }

    public static ContentValues getValues(Object o) {
        final ContentValues contentValues = new ContentValues();
        Field[] fields = o.getClass().getFields();
        for (Field field : fields) {
            final Key key = field.getAnnotation(Key.class);
            try {
                if (key.field().equals("id")) {
                    contentValues.put(key.field(), (Long) field.get(o));
                } else {
                    contentValues.put(key.field(), (String) field.get(o));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return contentValues;
    }
}
