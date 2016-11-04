package com.github.xiaomi007.annotations;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.xiaomi007.annotations.models.Cat;
import com.github.xiaomi007.annotations.models.Dog;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyDB myDB = new MyDB(this);
        final Dog dog = new Dog();
        dog.firstName = "lilou";
        dog.lastName = "dallas multipass";
        dog.id = -1;
        myDB.insert(dog);

        final Cat cat = new Cat();
        cat.firstName = "korben";
        cat.id = 3;
        myDB.insert(cat);

        final ContentValues contentValues = new ContentValues();
        contentValues.put("first_name", "kit");
        myDB.update(cat, contentValues, "id = 2");

        myDB.queryAsync(Dog.class, new MyDB.Query<List<Dog>>() {
            @Override
            public void result(List<Dog> dogs) {
                Log.d(TAG, "result: " + Thread.currentThread().toString());
                Log.d(TAG, "result: " + dogs.size());
                for (Dog dog1 : dogs) {
                    Log.d(TAG, "result: " + dog1.toString());
                }
            }
        });

        myDB.queryAsync(Cat.class, new MyDB.Query<List<Cat>>() {
            @Override
            public void result(List<Cat> cats) {
                Log.d(TAG, "result: " + cats.size());
                for (Cat cat1 : cats) {
                    Log.d(TAG, "result: " + cat1.toString());
                }
            }
        });

    }
}
