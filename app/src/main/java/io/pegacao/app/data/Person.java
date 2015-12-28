package io.pegacao.app.data;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;

import java.util.UUID;

/**
 * Created by ivanm on 12/3/15.
 * Single object to represent a Person. It has its basic data as id, name, urls for pics, and
 * a profile message
 */
public class Person {
    public String id;
    @NonNull
    public String facebookId;
    @NonNull
    public String name;
    public String[] imageUrls;
    public String profileMessage;
    public String picCounter;
    public String distance;
    public String lastTimeSeen;

    public int age;

    public boolean liked;

    public Bitmap bitmap;
    public int resDrawableProfile;

    public Person(@NonNull String facebookId, @NonNull String name) {
        this(facebookId, name, 0, null, null, null, null);
    }

    public Person(@NonNull String facebookId, @NonNull String name,
                  int age, String[] imageUrls, String profileMessage, String distance, String lastTimeSeen) {
        this.id = UUID.randomUUID().toString();
        this.facebookId = facebookId;
        this.name = name;
        this.imageUrls = imageUrls;
        this.profileMessage = profileMessage;
        this.age = age;
        this.distance = distance;
        this.lastTimeSeen = lastTimeSeen;
        //TODO uncomment line for final build
        //this.picCounter = String.valueOf(imageUrls.length);
        this.liked = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equal(id, person.id) &&
                Objects.equal(facebookId, person.facebookId) &&
                Objects.equal(name, person.name) &&
                Objects.equal(profileMessage, person.profileMessage) &&
                Objects.equal(liked, person.liked) &&
                Objects.equal(age, person.age) &&
                Objects.equal(imageUrls, person.imageUrls);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, facebookId, name, imageUrls, profileMessage, liked, age);
    }

}