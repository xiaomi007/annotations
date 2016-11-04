package com.github.xiaomi007.annotations.models;

import com.github.xiaomi007.annotations.annotations.Key;
import com.github.xiaomi007.annotations.annotations.Table;


@Table(table = "table_cat")
public class Cat {

    @Key(field = "id", isPrimary = true)
    public long id;

    @Key(field = "first_name")
    public String firstName;

    @Override
    public String toString() {
        return "Cat{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                '}';
    }
}
