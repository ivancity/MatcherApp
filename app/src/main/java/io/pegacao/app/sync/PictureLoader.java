package io.pegacao.app.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.content.AsyncTaskLoader;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

import io.pegacao.app.R;
import io.pegacao.app.data.Person;

/**
 * Created by ivanm on 11/20/15.
 */
public class PictureLoader extends AsyncTaskLoader<SparseArray<Person>> {

    /**
     * We are creating and interface and annotating to it variables of type Int restricted by
     * IntDef annotation and with the set int static values listed in this annotation. This way we
     * can restrict the use of methods and parameters. Just like Enum, but we are using annotations
     * because Enum wastes memory and it is inefficient.
     * Ex: @PeopleRequestStatus public int getPeopleStatus() //in here the methos will return ONLY integers defined
     *                                                       //define by interface PeopleRequestStatus.
     *Ex: public void setPeopleStatus(@PeopleRequestStatus int status) //the parameters will only receive
     *
     * Note: RetentionPolicy.Source means that we keep the annotations only in our source code and
     * no need to leave it in the class or in runtime. This annotations are specifically for the IDE to find
     * problems before actually creating the app. Thus no need to keep this code in the app
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({PEOPLE_STATUS_OK, PEOPLE_SERVER_DOWN, PEOPLE_STATUS_SERVER_INVALID,  PEOPLE_STATUS_UNKNOWN})
    public @interface PeopleRequestStatus {}

    public static final int PEOPLE_STATUS_OK = 0;
    public static final int PEOPLE_SERVER_DOWN = 1;
    public static final int PEOPLE_STATUS_SERVER_INVALID = 2;
    public static final int PEOPLE_STATUS_UNKNOWN = 3;

    public PictureLoader(Context context) {
        super(context);
    }


    @Override
    public SparseArray<Person> loadInBackground() {
        return generateSparseArray(10);
    }

    static private void setLocationStatus(Context c, @PictureLoader.PeopleRequestStatus int locationStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_people_server_status_key), locationStatus);
        spe.commit();
    }

    private int getRandomResource(int randomNumber){
        int imageResource;
        switch (randomNumber){
            case 2:
                imageResource = R.drawable.girl2;
                break;
            case 3:
                imageResource = R.drawable.girl3;
                break;
            case 4:
                imageResource = R.drawable.girl4;
                break;
            case 5:
                imageResource = R.drawable.girl5;
                break;
            default:
                imageResource = R.drawable.palvin3;
        }

        return imageResource;
    }

    private SparseArray<Person> generateSparseArray(int numItems){
        Random random = new Random();
        Person peopleCardItem;
        SparseArray<Person> peopleArray = new SparseArray<>();
        int randomNumber;
        for(int i=0;i<numItems;i++){
            randomNumber = random.nextInt(6 - 2) + 2;
            peopleCardItem = new Person("123", "Barbara", 25, null, "Some description to show in profile", "2", "1");
            peopleCardItem.resDrawableProfile = getRandomResource(randomNumber);
            peopleArray.put(i, peopleCardItem);
        }
        return peopleArray;
    }
}
