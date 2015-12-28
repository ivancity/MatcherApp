package io.pegacao.app;

import android.util.SparseArray;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.pegacao.app.data.PeopleRepository;
import io.pegacao.app.data.Person;
import io.pegacao.app.matcher.MatcherContract;
import io.pegacao.app.matcher.MatcherPresenter;

import static org.mockito.Mockito.verify;

/**
 * Created by ivanm on 12/14/15.
 */
public class MatcherPresenterTest {

    private static SparseArray<Person> PEOPLE_ARRAY;

    static{
        PEOPLE_ARRAY = new SparseArray<Person>();
        PEOPLE_ARRAY.append(0, new Person("123", "Barbara"));
    }

    private static SparseArray<Person> EMPTY_PEOPLE_ARRAY = new SparseArray<>(0);

    @Mock
    private PeopleRepository mPeopleRepository;
    @Mock
    private MatcherContract.View mMatcherView;

    @Captor
    private ArgumentCaptor<PeopleRepository.LoadPeopleCallback> mLoadPeopleCallbackCaptor;

    private MatcherPresenter mMatcherPresenter;

    @Before
    public void setupMatcherPresenter(){
        //Need to call this method in order to enable the annotation @Mock and inject a mocks.
        MockitoAnnotations.initMocks(this);

        //get reference for class about to be tested.
        mMatcherPresenter = new MatcherPresenter(mPeopleRepository, mMatcherView);
    }

    @Test
    public void loadPeopleFromRepositoryAndLoadIntoView(){
        /*
        In this scenario our MatcherPresenter is already initialized and with initialized
        SparseArray of Person objects. Request loading of people array.
         */
        //This as if the View (Fragment implementing View) is calling the MatcherPresenter to load the PeopleList
        mMatcherPresenter.loadPeople(true);

        /*
        Once inside the MatcherPresenter we have a few lines of code and we see the mPeopleRepository.getPeopleList()
        Which has a Callback argument that we need to capture. This is the LoadPeopleCallBack. That
        in turn our MatcherPresenter needs to implement the method onPeopleLoaded() with the
        SparseArray coming from the repository.
         */
        //Callback is captured and invoked with stubbed Person objects from above.
        verify(mPeopleRepository).getPeopleList(mLoadPeopleCallbackCaptor.capture());
        mLoadPeopleCallbackCaptor.getValue().onPeopleLoaded(PEOPLE_ARRAY);

        /*
        As we are now still in the MatcherPresenter.loadPeople() this is because at this point
        the call back has return to loadPeople() through the callback. Above we are calling
        onPeopleLoaded which now we have to define as if we were in MatcherPresenter.
         */
        verify(mMatcherView).setProgressIndicator(false);
        verify(mMatcherView).showPeople(PEOPLE_ARRAY);


    }





}
