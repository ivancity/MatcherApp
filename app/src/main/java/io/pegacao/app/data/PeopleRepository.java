package io.pegacao.app.data;

import android.support.annotation.NonNull;
import android.util.SparseArray;

/**
 * Created by ivanm on 12/3/15.
 * Repository implementation in charge of interacting with the PeopleServiceApi. It has interface
 * callbacks to allow other objects to request data to this repository. The repository implementation
 * will have stored in memory the PeopleServiceApi instance to save and retrieve data.
 */
public interface PeopleRepository {

    /**
     * Used mainly for returning a List of Person objects back to the caller
     */
    interface LoadPeopleCallback {
        void onPeopleLoaded(SparseArray<Person> people);
    }

    /**
     * Used to return back one Person from the PeopleServiceApi object.
     */
    interface GetPersonCallback {
        void onPersonLoaded(Person person);
    }

    /**
     * Callback that returns a boolean that confirms if data was saved or not.
     */
    interface SavePeopleArrayCallback{
        void onSavedArray(boolean saved);
    }


    /**
     * Method that will call getAllPeople in mPeopleServiceApi. This getallPeople API will ALWAYS
     * create a new List<People> this is why it is important to cache the returning List from
     * PeopleServiceApi. Unless we update the ArrayMap in PeopleServiceApi then we update the cache.
     * @param callback
     */
    void getPeopleList(@NonNull LoadPeopleCallback callback);

    /**
     * Finds one person by id from our PeopleServiceApi instance.
     * @param personId
     * @param callback
     */
    void getPerson(@NonNull String personId, @NonNull GetPersonCallback callback);

    /**
     * It saves a Person instance back into the PeopleServiceApi instance. At the same time calls
     * refreshData to force an update in our cached data.
     * @param person
     */
    void savePerson(@NonNull Person person);

    /**
     * Method use to delete Person object in the PeopleServiceApi data, and update cache data in
     * PeopleRepository.
     * @param id Person id that will be erased from the PeopleServiceApi.
     */
    void deletePerson(@NonNull String id);

    /**
     * Set like boolean in Person object in ArrayMap collection in PeopleServiceApi.
     * @param liked
     * @param id
     */
    void setLikePerson(String id, boolean liked);

    /**
     * This method is used when we try to append all at once values in the ArrayMap of the PeopleServiceApi
     * accessing through PeopleRepository where the service is located.
     * @param peopleArray sparse array to send to a PeopleServiceApi.
     * @param callback callback that confirms if app saves sparsearray or not.
     */
    void saveArrayPeople(@NonNull SparseArray<Person> peopleArray, @NonNull SavePeopleArrayCallback callback);

    /**
     * Every time we call this method we force the method to initialize our cache data when
     * getPeople() gets called back again.
     */
    void refreshData();
}
