package io.pegacao.app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import io.pegacao.app.controllers.LoginPagerAdapter;
import io.pegacao.app.fragments.LoginFragmentIntro;
import io.pegacao.app.model.FbProfileResponse;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by ivanm on 11/5/15.
 */
public class LoginActivity extends AppCompatActivity
            implements FacebookCallback<LoginResult>
                , View.OnClickListener
                , DialogInterface.OnClickListener{

    private CallbackManager callbackManager;
    private Button btnLogin;
    private static final String TAG = "LoginActivity";

    private static final String[] corePermissions = new String[]{"public_profile"
            ,"user_birthday"
            , "user_relationship_details"
            , "user_photos"
            , "email"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.login_layout);


        ViewPager defaultViewpager = (ViewPager) findViewById(R.id.viewpager_default);
        CircleIndicator defaultIndicator = (CircleIndicator) findViewById(R.id.indicator_default);

        //Create fragment for the ViewPager
        LoginFragmentIntro fragmentIntro = new LoginFragmentIntro();
        fragmentSetup(fragmentIntro
                , getString(R.string.hit_your_crush)
                , R.drawable.slider_image_1);

        LoginFragmentIntro fragmentIntro2 = new LoginFragmentIntro();
        fragmentSetup(fragmentIntro2
                , getString(R.string.meet_people_world)
                , R.drawable.slider_image_2);

        LoginFragmentIntro fragmentIntro3 = new LoginFragmentIntro();
        fragmentSetup(fragmentIntro3
                , getString(R.string.chat_your_match)
                , R.drawable.slider_image_3);

        LoginFragmentIntro fragmentIntro4 = new LoginFragmentIntro();
        fragmentSetup(fragmentIntro4
                , getString(R.string.get_notified_nearby)
                , R.drawable.slider_image_4);


        //add Fragment created above to the LoginPagerAdapter. We need 4 fragments in the adapter
        LoginPagerAdapter defaultPagerAdapter = new LoginPagerAdapter(getSupportFragmentManager());

        SparseArray<LoginFragmentIntro> sparseArray = new SparseArray<>(4);
        sparseArray.put(0, fragmentIntro);
        sparseArray.put(1, fragmentIntro2);
        sparseArray.put(2, fragmentIntro3);
        sparseArray.put(3, fragmentIntro4);
        defaultPagerAdapter.setData(sparseArray);

        defaultViewpager.setAdapter(defaultPagerAdapter);
        defaultIndicator.setViewPager(defaultViewpager);

        //register call back for facebook login
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, this);

        btnLogin = (Button) findViewById(R.id.app_login_button);
        btnLogin.setOnClickListener(this);

        //createKeyHash();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        enableLoginButton();
        AppEventsLogger.activateApp(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "login.. onPause");
        if(AccessToken.getCurrentAccessToken() == null ||
                AccessToken.getCurrentAccessToken().getDeclinedPermissions().size() > 0){
            setPreferenceFirst(true);
        }
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    /**
     * Set share preference in AppStateValues xml file. this will update isFirstTime preference
     * to some boolean passed to the method. We do this if we know that we are closing the app,
     * OR when we know that we have a valid access token after login in.
     * @param setBool boolean to set isFirstTime shared preference.
     */
    private void setPreferenceFirst(boolean setBool){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_state_values), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.is_first_time), setBool);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "login.. onStop");
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /**
     * Setting up fragments sent to this method. Mutate fragment by adding a new bundle with text,
     * and resource id that goes on the Fragment layout.
     * @param fragment fragment to mutate
     * @param text text to display in fragment layout
     * @param resourceDrawable drawable background for the fragment.
     */
    private void fragmentSetup(LoginFragmentIntro fragment, String text, int resourceDrawable){
        Bundle args = new Bundle();
        args.putInt(LoginFragmentIntro.DRAWABLE_ID, resourceDrawable);
        args.putString(LoginFragmentIntro.TEXT_MESSAGE, text);
        fragment.setArguments(args);

    }

    /**
     * disable btnLogin that is the Facebook Login button.
     */
    private void disableLoginButton() {
        Log.d(TAG, "disable button");
        if (btnLogin.isEnabled())
            btnLogin.setEnabled(false);
    }

    /**
     * enable btnLogin that is the Facebook Login button.
     */
    private void enableLoginButton() {
        Log.d(TAG, "enable button");
        if (!btnLogin.isEnabled())
            btnLogin.setEnabled(true);
    }

    public void createKeyHash() {
        Log.d(TAG, "INSIDE CREATEKEYHASH");
        try {
            String pk = "io.pegacao.id.grid";
            String pk2 = "io.pegacao.app";
            PackageInfo info = getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "error: " + e.toString());
        } catch (java.security.NoSuchAlgorithmException e) {
            Log.d(TAG, "error: " + e.toString());
        }
    }


    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void setupActivityBackground(FrameLayout frameLayout) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        Bitmap bitmap = Utility.decodeSampledBitmapFromResource(getResources()
                , R.drawable.fb_connect //TODO: erase this drawable id
                , width
                , height);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Log.d(TAG, "About to use setBackgroundDrawable");
            frameLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        } else {
            Log.d(TAG, "About to use setBackground");
            frameLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
        }

    }

    /**
     * User succesfully login and went throught the Facebook Activity process, and finished it.
     * @param loginResult
     */
    @Override
    public void onSuccess(LoginResult loginResult) {
        this.getApplicationContext();
        /*
        LoginResult parameter has the new AccessToken, and the most recently granted or declined permissions.
         */
        Set<String> setPermissionDenied =  loginResult.getRecentlyDeniedPermissions();
        String[] permissionDeniedArray = setPermissionDenied.toArray(new String[setPermissionDenied.size()]);

        Set<String> setPermissionGranted = loginResult.getRecentlyGrantedPermissions();
        String[] permissionGrantedArray = setPermissionGranted.toArray(new String[setPermissionGranted.size()]);

        Log.d(TAG, "permissionDeniedArray = " + Arrays.toString(permissionDeniedArray));
        Log.d(TAG, "permissionGrantedArray = " + Arrays.toString(permissionGrantedArray));

        //Permissions Denied at the Login Facebook Screen
        if(permissionDeniedArray.length > 0){
            //create dialog
            createDeniedPermissionDialog(permissionDeniedArray);
            Log.d(TAG, "we have some permissions denied");
        } else {
            //We set shared preference firstTime to false because we access the app for the first time
            //after logging out OR simply we access the app for the first time with a succesful log in
            Log.d(TAG, "no permissions denied in array result");
            setPreferenceFirst(false);
            //proceed to ask for all the details about the user using Facebook APIs.
            fbMeRequest(loginResult);
        }

    }

    /**
     * Method that creates a specialized dialog when any permission is denied during the login process
     * @param permissionDeniedArray result array coming from Facebook API identified as denied permissions
     */
    private void createDeniedPermissionDialog(String[] permissionDeniedArray){
        /*
            permissionDeniedArray = [email, user_photos, user_birthday, user_relationship_details]
            permissionGrantedArray = [public_profile]
             */
        String message = "", title = getString(R.string.permission_denied);

        for(int i = 0; i < permissionDeniedArray.length; i++){
            switch (permissionDeniedArray[i]){
                case "email":
                    message += getString(R.string.email_denied);//TODO check if this string in xml keeps the new line
                    break;
                case "user_photos":
                    message += "\nPhotos: Your photos are important to show others who you are, and increase your chance to meet someone";
                    break;
                case "user_birthday":
                    message += "\nBirthday: We need your birthday because we want to show you with ages that you are interested in";
                    break;
                case "user_relationship_details":
                    message += "\nGender Preference: Your gender preference is important to show you people you can match with";
                    break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        createAppCompatDialog(builder,
                title,
                message,
                getString(R.string.try_again),
                getString(R.string.cancel));

    }

    /**
     * method that creates an AppcompatDialog when user denies permissions
     * @param builder
     * @param title
     * @param message
     * @param postiveButtonLabel
     * @param negativeButtonLabel
     */
    public void createAppCompatDialog(AlertDialog.Builder builder,
                                             String title,
                                             String message,
                                             String postiveButtonLabel,
                                             String negativeButtonLabel){
        AppCompatDialog dialog = builder.create();
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(postiveButtonLabel, this);
        builder.setNegativeButton(negativeButtonLabel, this);
        builder.show();
    }



    public void volleyFbRequest(LoginResult loginResult){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET
                , ""
                , new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
    }

    /**
     * after we know that the user is logged in on Facebook call this method to open the Main
     * Activity.
     */
    public void openMainActivity() {
        disableLoginButton();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * calls the /user/me endpoint to fetch the user data for the given access token.
     * @param loginResult result from Login Request to Facebook.
     */
    public void fbMeRequest(LoginResult loginResult){
        /*
        GraphResponse ex:
 SERVER RESPONSE: {Response:  responseCode: 200, graphObject: {"id":"12343234","birthday":"01\/01\/1987","first_name":"xxxx","email":"xxx.xxx@gmail.com","verified":true,"name":"xxxx xxxxx","gender":"male","age_range":{"min":21}}, error: null}
object response: {"id":"123124","birthday":"01\/01\/1987","first_name":"xxx","email":"xxx.xxx@gmail.com","verified":true,"name":"xx xxx","gender":"male","age_range":{"min":21}}
profile_pic: http://graph.facebook.com/1234323/picture?type=large */

//https://graph.facebook.com/me/?access_token=[accessToken]
//https://graph.facebook.com/me?fields=email,first_name,[anotherField]&access_token=[accessToken]
//http://graph.facebook.com/12343234/picture?type=large


        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    /*
                    server error example:
                    SERVER RESPONSE: {Response:  responseCode: 400,
                    graphObject: null,
                    error: {HttpStatus: 400,
                        errorCode: 100,
                        errorType: OAuthException,
                        errorMessage: (#100) Tried accessing nonexisting field (close_friends) on node type (User)}}

                     */

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d(TAG, "SERVER RESPONSE: " + response.toString());

                        if (object == null) {
                            Log.d(TAG, "null object from server response. canceling onCompleted()");
                            Toast.makeText(LoginActivity.this, "Empty response", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            FbProfileResponse fbResponse = new FbProfileResponse();
                            int responseCode = response.getConnection().getResponseCode();
                            if (responseCode == -1 || responseCode != 200) {
                                Log.d(TAG, "No valid response, or something failed on the server");
                                return;
                            }

                            //core profile details
                            fbResponse.name = object.getString("name");
                            fbResponse.id = object.getString("id");
                            fbResponse.firstName = object.getString("first_name");

                            try {
                                String urlStr = "https://graph.facebook.com/" + fbResponse.id + "/picture?type=large";
                                URL profile_pic = new URL(urlStr);
                                Log.i("profile_pic", profile_pic + "");
                                fbResponse.urlStr = urlStr;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

                            if (object.has("gender")) fbResponse.gender = object.getString("gender");
                            if (object.has("birthday")) fbResponse.birthday = object.getString("birthday");
                            if (object.has("verified")) fbResponse.verified = object.getString("verified");
                            if (object.has("email")) fbResponse.email = object.getString("email");
                            if (object.has("age_range")) {
                                fbResponse.ageRangeMin = object.getJSONObject("age_range").getInt("min");
                            }
                            if (object.has("interested_in")) {
                                //TODO check how interested_in is presented
                            }

                            setProfilePref(fbResponse);
                            openMainActivity();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "something went wrong with the server response");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Something went wrong connecting to the server");
                        }
                    }
                }
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields",
                "id,name,first_name,gender,birthday,verified,interested_in, age_range,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void setProfilePref(FbProfileResponse fbResponse){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_profile_values), MODE_PRIVATE);
        SharedPreferences.Editor spe = preferences.edit();

        if(fbResponse.name != null)spe.putString(getString(R.string.pref_name), fbResponse.name);
        if(fbResponse.id != null)spe.putString(getString(R.string.pref_id), fbResponse.id);
        if(fbResponse.firstName != null)spe.putString(getString(R.string.pref_firstname), fbResponse.firstName);
        if(fbResponse.email != null)spe.putString(getString(R.string.pref_email), fbResponse.email);
        if(fbResponse.ageRangeMin != 0) spe.putInt(getString(R.string.pref_agerangemin), fbResponse.ageRangeMin);
        if(fbResponse.birthday != null) spe.putString(getString(R.string.pref_birthday), fbResponse.birthday);
        if(fbResponse.gender != null) spe.putString(getString(R.string.pref_gender), fbResponse.gender);
        if(fbResponse.urlStr != null) spe.putString(getString(R.string.pref_urlStr), fbResponse.urlStr);
        if(fbResponse.verified != null)spe.putString(getString(R.string.pref_verified), fbResponse.verified);

        spe.apply();
    }


    /**
     * User either cancel on the facebook ui by clicking on the cancel button
     or pressed back on the facebook login ui
     */
    @Override
    public void onCancel() {
        Log.d(TAG, "onCancel facebook login");
        //AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Utility.createAppCompatDialog(builder
                , getString(R.string.login_cancelled_title)
                , getString(R.string.login_cancelled_message)
                , true
                , false);

    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "onError facebook login");
        Log.d(TAG, error.getMessage() + " - " + error.getLocalizedMessage());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Utility.createAppCompatDialog(builder
                , getString(R.string.dialog_error_title)
                , getString(R.string.dialog_error_message) + error.getMessage()
                , true
                , false);

    }

    @Override
    public void onClick(View v) {
        //TODO: check if we have network conection Utility.isNewtorkWorking()
        Log.d(TAG, "click");
        startLogin();
    }

    /**
     * Start login by checking first if we have any declined permissions in our current access token.
     * When ready either call Facebook API login if we need to send core permissions OR
     * declined permissions.
     */
    public void startLogin(){
        disableLoginButton();
        //TODO debug erase this if block for release
        if(AccessToken.getCurrentAccessToken() != null){
            Log.d(TAG, "Login.. NO ACCESS TOKEN NOT NULL");
        } else {
            Log.d(TAG, "Login.. YES ACCESS TOKEN IS NULL");
        }

        //AccessToken.getCurrentAccessToken().getDeclinedPermissions();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken != null){
            //there is an access token but we have declined permissions then request again those permissions
            //this permissions might be declined from the Facebook settings of the user.
            if(accessToken.getDeclinedPermissions() != null && accessToken.getDeclinedPermissions().size() > 0){
                callFacebookLogin(accessToken.getDeclinedPermissions());
            } else {
                callFacebookLogin(Arrays.asList(corePermissions));
            }
        } else {
            callFacebookLogin(Arrays.asList(corePermissions));
        }
    }

    /**
     * Calling Facebook API Login Manager to start Login Activity from Facebook.
     * @param pendingPermissions
     */
    public void callFacebookLogin(Collection<String> pendingPermissions){
        LoginManager.getInstance().logInWithReadPermissions(
                this
                , pendingPermissions);
                //,AccessToken.getCurrentAccessToken().getDeclinedPermissions());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //enableLoginButton();
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * onclick method that handles when AppCompatDialog Denied Permission buttons are clicked
     * @param dialog
     * @param which
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == -1){
            startLogin();
        }
    }
}
