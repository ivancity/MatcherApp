package io.pegacao.app.matcher;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import io.pegacao.app.data.PeopleRepository;
import io.pegacao.app.data.Person;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by ivanm on 12/3/15.
 */
public class MatcherPresenter implements MatcherContract.UserActionsListener {

    PeopleRepository mPeopleRepository;
    MatcherContract.View mPeopleView;

    /**
     * Getting a PeopleRepository from mock or prod flavor Injection class. And set the
     * MatcherContract.View in this case it will be a Fragment implementing it.
     * @param peopleRepository
     * @param peopleView
     */
    public MatcherPresenter(@NonNull PeopleRepository peopleRepository
            , @NonNull MatcherContract.View peopleView) {
        mPeopleRepository = peopleRepository;
        mPeopleView = peopleView;
    }

    @Override
    public void loadPeople(boolean forceUpdate) {
        mPeopleView.setProgressIndicator(true);

        if(forceUpdate){
            mPeopleRepository.refreshData();
        }


        mPeopleRepository.getPeopleList(new PeopleRepository.LoadPeopleCallback() {
            @Override
            public void onPeopleLoaded(SparseArray<Person> people) {
                mPeopleView.setProgressIndicator(false);
                mPeopleView.showPeople(people);
            }
        });
    }

    @Override
    public void requestDataFromNetwork() {
        mPeopleView.setupDataFromNetwork();
    }

    @Override
    public void savePeopleArray(SparseArray<Person> peopleArray, final MatcherContract.SavePeopleSparseArrayContract callback) {
        if (peopleArray == null || peopleArray.size()==0) {
            mPeopleView.showEmptyNoteError();
        } else {
            mPeopleRepository.saveArrayPeople(peopleArray, new PeopleRepository.SavePeopleArrayCallback() {
                @Override
                public void onSavedArray(boolean saved) {
                    callback.onSavedSparseArray(saved);
                }
            });
        }
    }

    @Override
    public void addNewPerson() {
        
    }

    @Override
    public void deletePerson(String personId) {
        mPeopleRepository.deletePerson(personId);
    }

    @Override
    public void likeAPerson(String id, boolean liked) {
        checkNotNull(id, "id cannot be null");
        mPeopleRepository.setLikePerson(id, liked);
    }

    @Override
    public void openPersonDetails(@NonNull Person chosenPerson) {
        checkNotNull(chosenPerson, "Person object can't be null");
        mPeopleView.showPersonDetailUi(chosenPerson.id);
    }
}
