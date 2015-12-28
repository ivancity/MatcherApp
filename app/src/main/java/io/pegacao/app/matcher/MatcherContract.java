package io.pegacao.app.matcher;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import io.pegacao.app.data.Person;

/**
 * Created by ivanm on 12/3/15.
 */
public interface MatcherContract {

    interface SavePeopleSparseArrayContract{
        void onSavedSparseArray(boolean saved);
    }


    interface View{
        void setProgressIndicator(boolean active);
        void showPeople(SparseArray<Person> people);
        void setupDataFromNetwork();
        void showEmptyNoteError();
        void showAddPerson();
        void showDeletePerson();
        void showPersonDetailUi(String personId);
    }

    interface UserActionsListener{
        void loadPeople(boolean forceUpdate);
        void requestDataFromNetwork();
        void savePeopleArray(SparseArray<Person> people, SavePeopleSparseArrayContract callback);
        void addNewPerson();
        void deletePerson(String personId);
        void likeAPerson(@NonNull String id, @NonNull boolean liked);
        void openPersonDetails(@NonNull Person chosenPerson);
    }
}
