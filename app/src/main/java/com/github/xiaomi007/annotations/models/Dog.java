package com.github.xiaomi007.annotations.models;

import com.github.xiaomi007.annotations.annotations.Key;
import com.github.xiaomi007.annotations.annotations.Table;


@Table(table = "table_dog")
public class Dog {

    @Key(field = "id", isPrimary = true)
    public long id;

    @Key(field = "first_name")
    public String firstName;

    @Key(field = "last_name")
    public String lastName;

    @Override
    public String toString() {
        return "Dog{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
