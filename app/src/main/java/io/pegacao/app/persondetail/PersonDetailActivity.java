package io.pegacao.app.persondetail;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.pegacao.app.Injection;
import io.pegacao.app.R;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by ivanm on 12/17/15.
 */
public class PersonDetailActivity extends AppCompatActivity implements PersonDetailContract.View {

    private static final String TAG = "PersonDetailActivity";
    @Bind(R.id.detail_toolbar)
    protected Toolbar toolBar;

    @Bind(R.id.txt_name_age)
    protected TextView txtNameAge;
    @Bind(R.id.txt_time_ago)
    protected TextView txtTimeAgo;
    @Bind(R.id.txt_distance)
    protected TextView txtDistance;
    @Bind(R.id.progressbar_detail)
    protected ProgressBar progressBar;
    @Bind(R.id.txt_profile_message)
    protected TextView txtProfileMessage;
    @Bind(R.id.viewpager_detail)
    protected ViewPager viewpagerDetail;
    @Bind(R.id.circle_indicator_detail)
    protected CircleIndicator circleIndicatorDetail;
    @Bind(R.id.fab_lips)
    protected FloatingActionButton fabLips;
    @Bind(R.id.fab_x)
    protected FloatingActionButton fabX;

    private PersonDetailContract.UserActionsListener mActionsListener;

    private String personId;
    private Animatable mAnimatable;

    public static final String EXTRA_PERSON_ID = "PERSON_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mActionsListener = new PersonDetailPresenter(Injection.providePeopleRepository(), this);

        //setupFabLipsAnim();//TODO enable when API 21 FAB morph is finished
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setupFabLipsAnim(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            fabLips.setImageResource(R.drawable.icn_morph_reverse);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get the requested note id
        personId = getIntent().getStringExtra(EXTRA_PERSON_ID);
        mActionsListener.openPersonDetail(personId);

    }

    //tracks the current state of the FAB as it gets clicked multiple times.
    private boolean tracker=true;
    //private Drawable fabDrawable;

    @OnClick({R.id.fab_lips, R.id.fab_x})
    public void fabClicked(FloatingActionButton fab){
        switch (fab.getId()){
            case R.id.fab_x:
                //Delete Person object from Repository
                mActionsListener.deletePerson(personId);
                break;
            case R.id.fab_lips:
                Drawable drawable;

                if(tracker) {
                    //Update like in Repository
                    mActionsListener.likeAPerson(personId, true);
                    //setup drawable and apply morph if possible to fab
                    drawable = ContextCompat.getDrawable(this, R.drawable.ic_done_24dp);
                    //applyFabMorph(drawable, fab);//TODO enable FAB morph for API 21 and above. Almost Ready
                    setFabDrawableLike(drawable, fab);
                    tracker=false;
                } else {
                    //update like in Repository
                    mActionsListener.likeAPerson(personId, false);
                    //setup drawable and apply reverse morph if possible.
                    drawable = ContextCompat.getDrawable(this, R.drawable.app_logo);
                    //applyFabReverseMorph(drawable, fab);//TODO enable FAB morph for API 21 and above. Almost Ready
                    setFabDrawableLikeReverse(drawable, fab, ImageView.ScaleType.CENTER_INSIDE);
                    tracker=true;
                }

                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void applyFabMorph(Drawable drawable, FloatingActionButton fab){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            fabLips.setImageResource(R.drawable.icn_morph);
            setToAnimatable(fabLips);
        } else {
            //TODO maybe update manifest from 15 to 16 API
            setFabDrawableLike(drawable, fab);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void applyFabReverseMorph(Drawable drawable, FloatingActionButton fab){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            fabLips.setImageResource(R.drawable.icn_morph_reverse);
            setToAnimatable(fabLips);
        } else {
            setFabDrawableLikeReverse(drawable, fab, ImageView.ScaleType.CENTER_INSIDE);
        }
    }

    private void setToAnimatable(FloatingActionButton fabLips){
        mAnimatable = (Animatable) (fabLips).getDrawable();
        mAnimatable.start();
    }

    /**
     * Putting back to FAB like the  app icon and white brackground.
     * @param drawable
     * @param fab
     * @param scaleType
     */
    private void setFabDrawableLikeReverse(Drawable drawable,
                                FloatingActionButton fab,
                                ImageView.ScaleType scaleType){
        setFabColorBackground(fab, ContextCompat.getColor(this, android.R.color.white));
        fab.setScaleType(scaleType);
        fab.setImageDrawable(drawable);
    }

    /**
     * Putting back to FAB like primaryColor background and check mark drawable.
     * @param drawable
     * @param fab
     */
    private void setFabDrawableLike(Drawable drawable,
                                    FloatingActionButton fab){
        setFabColorBackground(fab, ContextCompat.getColor(this, R.color.colorPrimary));
        fab.setImageDrawable(drawable);
    }

    /**
     * Method that sets a ColorStateList to the Tint List in this case ONLY the state_enabled to either white or
     * primaryColor
     * @param fab
     * @param color
     */
    private void setFabColorBackground(FloatingActionButton fab, int color){
        int[][] states = {
                {android.R.attr.state_enabled}
        };

        int[] colors = {
                color
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        fab.setBackgroundTintList(colorStateList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if(active){
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMissingPersonData() {

    }

    @Override
    public void showImage(int[] imageUrl) {
        //Adapter for the viewpager that is going to display images in this activity.
        PictureHolderDetailAdapter pictureHolderDetailAdapter = new PictureHolderDetailAdapter(getSupportFragmentManager());
        //Create sparseArray for all images url coming from server response
        SparseArray<PictureHolderFragment> sparseArrayPictureHolder = new SparseArray<>(imageUrl.length);

        //put images in sparse array
        for (int i = 0; i < imageUrl.length; i++) {
            sparseArrayPictureHolder.put(i, PictureHolderFragment.newInstance(imageUrl[i]));
        }

        //set sparse array to adapter for viewpager.
        pictureHolderDetailAdapter.setData(sparseArrayPictureHolder);
        viewpagerDetail.setAdapter(pictureHolderDetailAdapter);
        //set circle indicators to this view pager.
        circleIndicatorDetail.setViewPager(viewpagerDetail);

    }

    @Override
    public void hideImage() {

    }

    @Override
    public void hideMessage() {
        txtProfileMessage.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        txtProfileMessage.setText(message);
    }

    @Override
    public void showAgeName(String name, String age) {
        Resources res = getResources();
        String nameAndAge = String.format(res.getString(R.string.name_and_age_detail), name, age);
        txtNameAge.setText(nameAndAge);
    }

    @Override
    public void hideAgeName() {
        txtNameAge.setVisibility(View.GONE);
    }

    @Override
    public void showLastTimeSeen(String time) {
        //TODO: calculate with the time if we are talking about hours OR minutes and put it in the string
        Resources res = getResources();
        String lastSeen = String.format(
                res.getString(R.string.last_time),
                time,
                "hours" //Hard coding hours for now later change depending of the time string.
        );
        txtTimeAgo.setText(lastSeen);
    }

    @Override
    public void hideLastTimeSeen() {
        txtTimeAgo.setVisibility(View.GONE);
    }

    @Override
    public void showDistance(String distance) {
        Resources res = getResources();
        String lastSeen = String.format(
                res.getString(R.string.distance),
                distance);
        txtDistance.setText(lastSeen);
    }

    @Override
    public void hideDistance() {
        txtDistance.setVisibility(View.GONE);
    }

    @Override
    public void showMatcherUi() {
        this.finish();
    }

    @Override
    public void showLikeUi(boolean liked) {

    }


}
