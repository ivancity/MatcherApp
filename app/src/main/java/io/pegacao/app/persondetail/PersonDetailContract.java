package io.pegacao.app.persondetail;

import android.support.annotation.NonNull;

/**
 * Created by ivanm on 12/17/15.
 */
public interface PersonDetailContract {

    interface View{
        void setProgressIndicator(boolean active);

        //Error message
        void showMissingPersonData();

        //Content Ui text
        //TODO change int[] later to String[] which will hold URLs
        void showImage(int[] imageUrls);
        void hideImage();

        void hideMessage();
        void showMessage(String message);

        void showAgeName(String name, String age);
        void hideAgeName();

        void showLastTimeSeen(String time);
        void hideLastTimeSeen();

        void showDistance(String distance);
        void hideDistance();

        //FAB ui updates
        void showMatcherUi();
        void showLikeUi(@NonNull boolean liked);

    }

    interface UserActionsListener{
        void openPersonDetail(@NonNull String personId);
        void deletePerson(String personId);
        void likeAPerson(@NonNull String personId, @NonNull boolean liked);
    }
}
