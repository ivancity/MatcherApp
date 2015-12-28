package io.pegacao.app.persondetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.pegacao.app.R;
import io.pegacao.app.data.PeopleRepository;
import io.pegacao.app.data.Person;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ivanm on 12/17/15.
 */
public class PersonDetailPresenter implements PersonDetailContract.UserActionsListener {

    private final PeopleRepository mPeopleRepository;
    private final PersonDetailContract.View mPersonDetailView;

    public PersonDetailPresenter(@NonNull PeopleRepository peopleRepository,
                                 @NonNull PersonDetailContract.View personDetailView){
        mPeopleRepository = checkNotNull(peopleRepository, "peopleRepository can't be null");
        mPersonDetailView = checkNotNull(personDetailView, "personDetailView can't be null");

    }

    @Override
    public void openPersonDetail(@Nullable String personId) {
        if(null == personId || personId.isEmpty()){
            mPersonDetailView.showMissingPersonData();
            return;
        }

        mPersonDetailView.setProgressIndicator(true);
        mPeopleRepository.getPerson(personId, new PeopleRepository.GetPersonCallback() {
            @Override
            public void onPersonLoaded(Person person) {
                mPersonDetailView.setProgressIndicator(false);
                if(person == null){
                    mPersonDetailView.showMissingPersonData();
                } else {
                    showDetails(person);
                }
            }
        });
    }

    @Override
    public void deletePerson(String personId) {
        mPeopleRepository.deletePerson(personId);
        mPersonDetailView.showMatcherUi();
    }

    @Override
    public void likeAPerson(@NonNull String personId, @NonNull boolean liked) {
        mPeopleRepository.setLikePerson(personId, liked);
    }

    private void showDetails(Person person){
        //Note: name is always NOT null
        String name = person.name;
        int age = person.age;
        int resourceImg = person.resDrawableProfile;
        String message = person.profileMessage;
        String distance = person.distance;
        String lastTimeSeen = person.lastTimeSeen;

        //TODO enable this when server is ready for now create fake int[] to hold resource images
        //String[] imgUrls = person.imageUrls;

        int[] imgUrls = {R.drawable.girl2, R.drawable.girl3};


        //Set profile message
        if (message != null && message.isEmpty()) {
            mPersonDetailView.hideMessage();
        } else {
            mPersonDetailView.showMessage(message);
        }

        //Set images
        if (imgUrls != null && imgUrls.length == 0) {
            mPersonDetailView.hideImage();
        } else {
            mPersonDetailView.showImage(imgUrls);
        }

        //Set name and age
        mPersonDetailView.showAgeName(name, age>0?String.valueOf(age):"N/A");

        //set last time seen
        if(lastTimeSeen != null && lastTimeSeen.isEmpty()){
            mPersonDetailView.hideLastTimeSeen();
        } else {
            mPersonDetailView.showLastTimeSeen(lastTimeSeen);
        }

        //Set distance
        if(distance != null && distance.isEmpty()){
            mPersonDetailView.hideDistance();
        } else {
            mPersonDetailView.showDistance(distance);
        }


    }
}
