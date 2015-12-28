package io.pegacao.app.data;

import android.support.annotation.NonNull;
import android.util.SparseArray;

/**
 * Created by ivanm on 12/3/15.
 * This is a PeopleRepository implementation that will communicate with PeopleServiceApi. It can
 * store the instance of be a fake data for testing or the real deal in mPeopleServiceApi. At the same
 * time it will keep a cached list of the People this way doesn't have to retrieve multplie times
 * from the PeopleServiceApi instance.
 */
public class InMemoryPeopleRepository implements PeopleRepository{

    //It can have an instance of a fake data for testing or real data. Check the flavor classes.
    private final PeopleServiceApi mPeopleServiceApi;

    SparseArray<Person> mCachedPeople;

    public InMemoryPeopleRepository(@NonNull PeopleServiceApi peopleServiceApi) {
        mPeopleServiceApi = peopleServiceApi;
    }


    @Override
    public void getPeopleList(@NonNull final LoadPeopleCallback callback) {
        if(mCachedPeople == null){
            mPeopleServiceApi.getAllPeople(new PeopleServiceApi.PeopleServiceCallback<SparseArray<Person>>() {
                @Override
                public void onLoaded(SparseArray<Person> people) {
                    //mCachedPeople = ImmutableList.copyOf(people);
                    //TODO maybe create an Immutable sparse array mCachedPeople?
                    mCachedPeople = people;
                    callback.onPeopleLoaded(mCachedPeople);
                }
            });
        } else {
            callback.onPeopleLoaded(mCachedPeople);
        }
    }

    @Override
    public void getPerson(@NonNull String personId, @NonNull final GetPersonCallback callback) {
        mPeopleServiceApi.getPerson(personId, new PeopleServiceApi.PeopleServiceCallback<Person>() {
            @Override
            public void onLoaded(Person person) {
                callback.onPersonLoaded(person);
            }
        });
    }

    @Override
    public void savePerson(@NonNull Person person) {
        mPeopleServiceApi.savePerson(person);
        refreshData();
    }

    @Override
    public void deletePerson(@NonNull String id) {
        //modify arrayMap in PeopleServiceApi
        mPeopleServiceApi.deletePerson(id);
        refreshData();
    }

    @Override
    public void setLikePerson(String id, boolean liked) {
        //modify arrayMap in PeopleServiceApi
        mPeopleServiceApi.setLikeInPerson(id, liked);
        refreshData();
    }

    @Override
    public void saveArrayPeople(@NonNull SparseArray<Person> peopleArray, @NonNull final SavePeopleArrayCallback callback) {
        mPeopleServiceApi.savePeopleArray(peopleArray, new PeopleServiceApi.SaveSparseArrayCallback() {
            @Override
            public void savedSparseArray(boolean saved) {
                if (saved) {
                    refreshData();
                    callback.onSavedArray(saved);
                }
            }
        });

    }

    @Override
    public void refreshData() {
        //We have some new changes in PeopleServiceApi thus get rid of the cached collection in
        //this PeopleRepository
        mCachedPeople = null;
    }
}
