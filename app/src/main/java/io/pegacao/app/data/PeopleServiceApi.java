package io.pegacao.app.data;

import android.support.annotation.NonNull;
import android.util.SparseArray;

/**
 * Created by ivanm on 12/3/15.
 * Structure for PeopleService to get all People, one person or save on person back to the
 * data. This is how we are going to interact with the data that is stored in the implementation class.
 */
public interface PeopleServiceApi {

    /**
     * Used mainly for two situations. When we want to return a Person or List of Person back to
     * the caller. Thus the generic type used to return either object when necessary.
     * @param <T>
     */
    interface PeopleServiceCallback<T>{
        void onLoaded(T person);
    }

    /**
     * Used to know if we saved or not in a PeopleServiceApi. This will confirmed to the calling
     * class.
     */
    interface SaveSparseArrayCallback{
        void savedSparseArray(boolean saved);
    }

    /**
     * Method that creates ALWAYS a new ArrayList from the ArrayMap in this object. Where this
     * ArrayMap is initialize using PeopleServiceApiEndpoint's loadPersistedNotes()
     * @param callback
     */
    void getAllPeople(PeopleServiceCallback<SparseArray<Person>> callback);

    /**
     * Gets one Person object from the ArrayMap. Such ArrayMap is initialize using
     * PeopleServiceApiEndpoint's loadPersistedNotes()
     * @param personId
     * @param callback
     */
    void getPerson(String personId, PeopleServiceCallback<Person> callback);

    /**
     * Puts back a Person object to the ArrayMap.
     * @param person
     */
    void savePerson(Person person);

    /**
     * Method to delete Person object in the ServiceApi data
     * @param id
     */
    void deletePerson(String id);

    /**
     * Update Person object in ArrayMap. Find using id and set its boolean with liked.
     * @param id
     * @param liked
     */
    void setLikeInPerson(String id, boolean liked);


    /**
     * Method used to send a SparseArray with the new values coming from network to the
     * PeopleServiceApi. Set callback according if we can save or not the incoming sparse array.
     * @param personSparseArray
     * @param callback
     */
    void savePeopleArray(@NonNull SparseArray<Person> personSparseArray,
                         @NonNull SaveSparseArrayCallback callback);
}
