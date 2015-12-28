package io.pegacao.app.data;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

import io.pegacao.app.Utility;

/**
 * Here we have some fake data stored in PEOPLE_SERVICE_DATA. This is the API used by
 * the repository to reach data in the ArrayMap.
 * Created by ivanm on 12/3/15.
 */
public class FakePeopleDataApiImpl implements PeopleServiceApi{

    private static final String TAG = "FakePeopleDataApiImpl";
    private static ArrayMap<String, Person> PEOPLE_SERVICE_DATA = new ArrayMap<>();

    private static ArrayMap<String, Boolean> LIKED_QUEUE = new ArrayMap<>();


    @Override
    public void getAllPeople(PeopleServiceCallback<SparseArray<Person>> callback) {
        callback.onLoaded(Utility.arrayMapToSparseArray(PEOPLE_SERVICE_DATA));
    }

    @Override
    public void getPerson(String personId, PeopleServiceCallback<Person> callback) {
        Person person = PEOPLE_SERVICE_DATA.get(personId);
        callback.onLoaded(person);
    }

    @Override
    public void savePerson(Person person) {
        //TODO maybe change to facebookId instead of using the app generated id?
        PEOPLE_SERVICE_DATA.put(person.id, person);
    }

    @Override
    public void deletePerson(String id) {
        //TODO maybe change to facebookId instead of using the app generated id if saving uses facebookId?
        PEOPLE_SERVICE_DATA.remove(id);
    }

    @Override
    public void setLikeInPerson(String id, boolean liked) {
        Person person = PEOPLE_SERVICE_DATA.get(id);
        person.liked = liked;
        //TODO make sure we have put here person.facebook id? Maybe?
        PEOPLE_SERVICE_DATA.put(person.id, person);

        //If we have a positive liked save it in LIKED_QUEUE to update the server later.
        if(LIKED_QUEUE.containsKey(id)) {
            //If the id exists in the queue then make sure the incoming liked is positive. If it
            //is not positive then remove the existing id from the queue.
            if (!liked) {
                LIKED_QUEUE.remove(id);
            }
        } else if(liked) {
            //we know that the id is not in the queue but make sure that we have positive values in the queue.
            LIKED_QUEUE.put(id, liked);
        }
    }

    @Override
    public void savePeopleArray(@NonNull SparseArray<Person> personSparseArray,
                                @NonNull SaveSparseArrayCallback callback) {
        Person person;
        int counter = 0;
        for (int i = 0; i < personSparseArray.size(); i++) {
            person = personSparseArray.get(i);
            //TODO change this later for facebookID, maybe it is better?
            PEOPLE_SERVICE_DATA.put(person.id, person);
            counter++;
        }

        if(counter == personSparseArray.size()){
            callback.savedSparseArray(true);
        } else{
            callback.savedSparseArray(false);
        }

    }


}
