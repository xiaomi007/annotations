package com.github.xiaomi007.annotations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.github.xiaomi007.annotations.annotations.Key;
import com.github.xiaomi007.annotations.annotations.Table;
import com.github.xiaomi007.annotations.models.Cat;
import com.github.xiaomi007.annotations.models.Dog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * //TODO: Javadoc on com.github.xiaomi007.annotations.MyDB
 *
 * @author Damien
 * @version //TODO version
 * @since 2016-11-01
 */

public class MyDB extends SQLiteOpenHelper {

    private Handler handler;
    private Handler mainHandler;
    private Class<?>[] classes = {
            Dog.class,
            Cat.class
    };

    public MyDB(Context context) {
        super(context, "MyDB", null, 5);
        final HandlerThread handlerThread = new HandlerThread("queries");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(context.getMainLooper());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: " + Thread.currentThread().getName());
        for (Class<?> aClass : classes) {
            db.execSQL(createTable(aClass));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d(TAG, "onUpgrade() called with: i = [" + i + "], i1 = [" + i1 + "]");
        for (Class<?> aClass : classes) {
            Table table = aClass.getAnnotation(Table.class);
            db.execSQL("DROP TABLE IF EXISTS " + table.table() + ";");
        }
        onCreate(db);
    }

    public static String createTable(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        StringBuilder unique = new StringBuilder();

        sb.append("CREATE TABLE ");
        Table table = clazz.getAnnotation(Table.class);
        sb.append(table.table()).append(" (");
        final Field[] fields = clazz.getFields();
        boolean isFirst = true;
        boolean isFirstUnique = true;
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++) {
            Field field = fields[i];
            Key key = field.getAnnotation(Key.class);
            if (key.isPrimary()) {
                if (!isFirstUnique) {
                    unique.append(", ");
                } else {
                    unique.append(", UNIQUE (");
                    isFirstUnique = false;
                }
                unique.append(key.field());
            }
            if (!isFirst) {
                sb.append(", ");
            } else {
                isFirst = false;
            }
            sb.append(key.field()).append(" ").append("TEXT");

        }

        if (!isFirstUnique) {
            sb.append(unique.toString());
            sb.append(") ON CONFLICT REPLACE");
        }
        sb.append(");");
        Log.d(TAG, "createTable: " + sb.toString());
        return sb.toString();
    }


    public void insert(final Object o) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Table table = o.getClass().getAnnotation(Table.class);
                SQLiteDatabase database = getWritableDatabase();
                database.insert(table.table(), null, KeyParser.getValues(o));
                database.close();
            }
        });
    }

    public void update(final Object o, final ContentValues values, final String where) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Table table = o.getClass().getAnnotation(Table.class);
                SQLiteDatabase database = getWritableDatabase();
                int i = database.update(table.table(), values, where, null);
                if (i == 0) {
                    database.insert(table.table(), null, values);
                }
                database.close();
            }
        });

    }

    public <T> void queryAsync(Class<T> t, Query<List<T>> query) {
        final MyRunnable<T> runnable = new MyRunnable<>(t, query);
        handler.post(runnable);
    }

    public interface Query<T> {
        void result(T t);
    }

    public class MyRunnable<T> implements Runnable {

        private static final String TAG = "MyRunnable";

        private Class<T> t;
        private Query<List<T>> query;

        public MyRunnable(Class<T> t, Query<List<T>> query) {
            this.t = t;
            this.query = query;
        }

        @Override
        public void run() {
            Table table = t.getAnnotation(Table.class);
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().query(table.table(), null, null, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final List<T> list = new ArrayList<>();
                    do {
                        Field[] fields = t.getDeclaredFields();
                        try {
                            T o = t.newInstance();
                            list.add(o);
                            for (Field field : fields) {
                                Key key = field.getAnnotation(Key.class);
                                if (key.field().equals("id")) {
                                    Log.d(TAG, "run: " + cursor.getLong(cursor.getColumnIndexOrThrow(key.field())));
                                    field.set(o, cursor.getLong(cursor.getColumnIndexOrThrow(key.field())));
                                } else {
                                    Log.d(TAG, "run: " + cursor.getString(cursor.getColumnIndexOrThrow(key.field())));
                                    field.set(o, cursor.getString(cursor.getColumnIndexOrThrow(key.field())));
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "run: ", e);
                        }
                    } while (cursor.moveToNext());

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            query.result(list);
                        }
                    });
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

}
