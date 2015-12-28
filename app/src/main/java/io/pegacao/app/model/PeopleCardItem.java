package io.pegacao.app.model;

import android.graphics.Bitmap;

/**
 * Created by ivanm on 11/4/15.
 */
public class PeopleCardItem {
    public Bitmap bitmap;
    public int imageResource;
    public String description;
    public int id;

    public PeopleCardItem(int id, int imageResource, String description){
        this(id, null, imageResource, description);
    }

    public PeopleCardItem(int id, Bitmap drawable, int imageResource, String description){
        this.bitmap = drawable;
        this.imageResource = imageResource;
        this.description = description;
    }

}
