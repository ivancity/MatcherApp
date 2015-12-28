package io.pegacao.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.util.SparseArray;
import android.widget.TextView;

import io.pegacao.app.data.Person;
import io.pegacao.app.sync.PictureLoader;

/**
 * Created by ivanm on 10/30/15.
 */
public class Utility {

    /**
     * This method will check if we are CONNECTED or CONNECTING to the network
     * @param context app context in order to request for ConnectivityManager
     * @return
     */
    static public boolean isNewtorkWorking(Context context){
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        //TODO: Decide if we need to isConnected better to garantee that we are CONNECTED.
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     *
     * @param c context to access shared preferences and string file
     * @return int describing server people status
     */
    @SuppressWarnings("ResourceType")
    static public @PictureLoader.PeopleRequestStatus
    int getLocationStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_people_server_status_key), PictureLoader.PEOPLE_STATUS_UNKNOWN);
    }

    public static String getPrefProfile(Context c, String pref_key, String defVal){
        SharedPreferences preferences = c.getSharedPreferences(c.getString(R.string.pref_profile_values), 0);
        return preferences.getString(pref_key, defVal);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        //options.inSampleSize = 4;

        options.inSampleSize = calculateInSampleSize(options
                , reqWidth
                , reqHeight);

        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Create a simple Dialog using AppCompat styling. Buttons ok and cancel can be added. And
     * no further listeners are attach to these buttons. Thus don't expect some action after the
     * user click on any of these buttons.
     * @param builder Alert Dialog Builder used to setup AppCompatDialog
     * @param title title for the dialog
     * @param message message to display as the body of the dialog
     * @param positive postivie button true if we want to display it or false if we want to hide
     * @param negative negative button set to true this boolean to display on dialog.
     */
    public static void createAppCompatDialog(AlertDialog.Builder builder,
                                      String title,
                                      String message,
                                      boolean positive,
                                      boolean negative){
        AppCompatDialog dialog = builder.create();

        //dialog.setTitle("Some title");
        //dialog.setContentView(R.layout.login_dialog);
        //dialog.show();

        builder.setTitle(title);
        builder.setMessage(message);
        if(positive) builder.setPositiveButton("OK", null);
        if(negative) builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    /**
     * Set textViews with the app's custom font
     * @param asset asset access where our font to set a new typeface
     * @param textView text view to mutate and set the new typeface
     * @param isBold decide if typeface is bold or normal
     */
    public static void setAppTypeFace(AssetManager asset, TextView textView, boolean isBold){
        Typeface myTypeface = Typeface.createFromAsset(
                asset,
                isBold ? "fonts/AvenirNextLTPro-Bold.otf" : "fonts/AvenirNextLTPro-Medium.otf");
        textView.setTypeface(myTypeface);
    }

    public static int dpToPx(Resources res, float dp){
        // Get the screen's density scale
        // Convert the dps to pixels, based on density scale
        return (int) (dp * (res.getDisplayMetrics().density) + 0.5f);
    }

    public static SparseArray<Person> arrayMapToSparseArray(ArrayMap<String, Person> peopleData){
        SparseArray<Person> personSparseArray = new SparseArray<>();
        Person person;
        for (int i = 0; i < peopleData.size(); i++) {
            person = peopleData.valueAt(i);
            personSparseArray.append(i, person);
        }

        return personSparseArray;
    }

}
