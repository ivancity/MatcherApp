package io.pegacao.app.data;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

import io.pegacao.app.Utility;

/**
 * Created by ivanm on 12/11/15.
 * Service API to read and write Person objects using data coming from a Service API
 * endpoint (PeopleServiceApiEndpoint)
 */
public class PeopleServiceApiImpl implements PeopleServiceApi {


    private static final int SERVICE_LATENCY_IN_MILLIS = 2000;
    private static final ArrayMap<String, Person> PERSON_SERVICE_DATA =
            PeopleServiceApiEndpoint.loadPersistedNotes();

    //cache Like action to later send to server through the network. caching in case we are offline.
    private static ArrayMap<String, Boolean> LIKED_QUEUE = new ArrayMap<>();


    @Override
    public void getAllPeople(PeopleServiceCallback<SparseArray<Person>> callback) {
        callback.onLoaded(Utility.arrayMapToSparseArray(PERSON_SERVICE_DATA));
    }

    @Override
    public void getPerson(String personId, PeopleServiceCallback<Person> callback) {
        Person person = PERSON_SERVICE_DATA.get(personId);
        callback.onLoaded(person);

    }

    @Override
    public void savePerson(Person person) {
        PERSON_SERVICE_DATA.put(person.facebookId, person);
    }

    @Override
    public void deletePerson(String id) {
        PERSON_SERVICE_DATA.remove(id);
    }

    @Override
    public void setLikeInPerson(String id, boolean liked) {
        Person person = PERSON_SERVICE_DATA.get(id);
        person.liked = liked;
        //TODO make sure we have put here person.facebook id? Maybe?
        PERSON_SERVICE_DATA.put(person.id, person);

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
    public void savePeopleArray(@NonNull SparseArray<Person> personSparseArray, @NonNull SaveSparseArrayCallback callback) {
        Person person;
        int counter = 0;
        for (int i = 0; i < personSparseArray.size(); i++) {
            person = personSparseArray.get(i);
            PERSON_SERVICE_DATA.put(person.facebookId, person);
            counter++;
        }

        if(counter == personSparseArray.size()){
            callback.savedSparseArray(true);
        } else{
            callback.savedSparseArray(false);
        }
    }


}
