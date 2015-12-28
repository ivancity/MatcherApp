package io.pegacao.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import io.pegacao.app.fragments.MainSelectFragment;
import io.pegacao.app.model.AppTypefaceSpan;
import io.pegacao.app.model.VolleySingleton;

import static io.pegacao.app.persondetail.PersonDetailFragment.OnFragmentInteractionListener;
import static io.pegacao.app.persondetail.PersonDetailFragment.newInstance;

/**
 * Created by Ivan Morris
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener {

    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    private Bundle mSavedInstance = null;
    private DrawerLayout mDrawer;
    private MenuItem mPreviousMenuItem;

    protected static boolean loggedIn = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());//TODO enable for proper functionality

        //check if we have access token if not go to LoginActivity

        setContentView(R.layout.activity_main);

        mSavedInstance = savedInstanceState;

        //if(isInitAndLoggedOut()){//TODO uncomment for proper functionality
        if(false){
            //opening the app for the first time OR initializing a new facebook session.
            goToLoginActivity();
        } else {
            //We know that it is not our first time accessing this app.
            //loggedIn = AccessToken.getCurrentAccessToken() != null;
            //if(loggedIn){ TODO uncomment for proper functionailty
            if(true){
                updateUi();
            } else {
                accessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                               AccessToken currentAccessToken) {
                        loggedIn = AccessToken.getCurrentAccessToken() != null;
                        if(loggedIn){
                            updateUi();
                        } else {
                            goToLoginActivity();
                        }
                    }
                };
                callbackManager = CallbackManager.Factory.create();
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAccessTokenTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager != null) callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * This method checks if we are running the app for the first time, thus we need a login screen
     * or the user canceled the login process and it is returning to the app. Initialize in the context
     * of this app means that you are completely logged out and you are returning and opening the app
     * in the main activity.
     * @return value that represents if it is the first time opening or it is still logged out after
     * canceling login screen.
     */
    private boolean isInitAndLoggedOut(){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_state_values), MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(getString(R.string.is_first_time), true);
        if (isFirstTime) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.is_first_time), false);
            editor.apply();
        }
        return isFirstTime;
    }


    /**
     * Method in charage of crating and displaying all the UI elements for this screen
     */
    private void updateUi(){
        setToolBarActionBar();
        setDrawer();

        //Set Fragment
        //check if we actually have the fragment container on the layout

        Log.d(TAG, "updateUi: inside if");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_fragment_holder, MainSelectFragment.newInstance())
                //.commitAllowingStateLoss();
                .commit();


    }

    /**
     * method that sets the toolbar and action bar on the layout
     */
    private void setToolBarActionBar(){
        Toolbar toolBar = (Toolbar) findViewById(R.id.collapsible_toolbar);
        setSupportActionBar(toolBar);
        //MenuItemCompat.getActionView(menuItem);

        //get action bar
        final ActionBar ab = getSupportActionBar();

        //Set hamburger menu icon
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_24dp, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        ab.setHomeAsUpIndicator(drawable);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * We set in here the Drawer's layout, header profile info as well as images, menu items for
     * the NavigationView and make sure we get the correct type face for the fonts.
     */
    private void setDrawer(){
        //keep mDrawer to know when to close
        mDrawer = (DrawerLayout) findViewById(R.id.drawer);

        //Set navigationView selected listeners, and setup fonts
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        setupMenuItems(navigationView.getMenu());

        //Get navigation view header and style it
        View headerView = navigationView.getHeaderView(0);

        //set new typefaces to draw header
        TextView nameDrawer = (TextView)headerView.findViewById(R.id.txt_drawer_header_name);
        Utility.setAppTypeFace(getResources().getAssets(), nameDrawer, false);
        TextView viewProfileDrawer = (TextView)headerView.findViewById(R.id.txt_drawer_viewprofile);
        Utility.setAppTypeFace(getResources().getAssets(), viewProfileDrawer, false);
        CircleImageView circleImageView = (CircleImageView)headerView.findViewById(R.id.draw_profile_image);

        nameDrawer.setText(Utility.getPrefProfile(this, getString(R.string.pref_firstname), "Name"));


        setupProfilePic(circleImageView);

    }

    private void setupProfilePic(CircleImageView circleImageView){
        String url = Utility.getPrefProfile(this, getString(R.string.pref_urlStr), "N/A");
        try {
            URL profile_pic = new URL(url);
            volleyImageRequest(url, circleImageView);
        } catch (MalformedURLException e) {
            //Not valid UR
            e.printStackTrace();
        }
    }

    private void volleyImageRequest(String url, final CircleImageView circleImageView){

        Resources res = getResources();

        // Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        //mImageView.setImageBitmap(bitmap);
                        Log.d(TAG, "width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
                        circleImageView.setImageBitmap(bitmap);
                    }
                },
                Utility.dpToPx(res, 86.0f),//86dp to px depending on density
                Utility.dpToPx(res, 86.0f),
                circleImageView.getScaleType(),//crop center in this image view
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        circleImageView.setImageResource(R.drawable.placeholder_drawer);
                    }
                });
        //TAG used to later on refer to it to cancel if necessary.
        request.setTag(getString(R.string.drawer_image_view));
        //To cancel specifically this image request call it using its tag.
        //VolleySingleton.getInstance(this).getRequestQueue().cancelAll(getString(R.string.drawer_image_view));

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    /**
     * Traverse all menus and submenus and change type face.
     * @param m
     */
    private void setupMenuItems(Menu m){
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    /**
     * Apply a type face to the menu item.
     * @param mi
     */
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextLTPro-Medium.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new AppTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    /**
     * Method is called if we know that the user is not logged in Facebook. No valid access token
     */
    private void goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Method to stop tracking the access token when leaving leaving main activity.
     */
    public void stopAccessTokenTracking(){
        if(accessTokenTracker != null && accessTokenTracker.isTracking())
            accessTokenTracker.stopTracking();
    }

    /**
     * Method call for logging out from Facebook, and erasing the current access token, and also
     * routing the user to the Login Activity.
     */
    private void logout(){
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            // user has logged in
            Log.d(TAG, "run logOut()");
            LoginManager.getInstance().logOut();
            stopAccessTokenTracking();
            loggedIn = false;
            clearSharedPreference();
            goToLoginActivity();
        } else {
            Log.d(TAG, "user has not logged in");
        }
    }

    /**
     * Making sure after logout the user doesn't have any of its data on the device.
     */
    public void clearSharedPreference(){
        SharedPreferences myPrefs = getSharedPreferences(getString(R.string.pref_profile_values),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * It closes the drawer after someone chose an option on the menus in NavigationView.
     */
    public void closeDrawer(){
        if(mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawers();
        }

    }

    /**
     * handling clicks in the NavigationView menu list in the Drawer
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        closeDrawer();
        if (mPreviousMenuItem != null) {
            mPreviousMenuItem.setChecked(false);
        }
        mPreviousMenuItem = menuItem;
        switch (menuItem.getItemId()) {
            case R.id.menu_logout:
                Log.d(TAG, "click logout menu");
                Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }


    public void switchPersonDetailFragment(String chosenId){
        //create MainSelectFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_fragment_holder, newInstance(chosenId))
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }


    @Override
    public void onFragmentInteraction(String something) {

    }
}
