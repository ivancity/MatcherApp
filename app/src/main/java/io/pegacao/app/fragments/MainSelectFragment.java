package io.pegacao.app.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.pegacao.app.Injection;
import io.pegacao.app.R;
import io.pegacao.app.Utility;
import io.pegacao.app.data.Person;
import io.pegacao.app.matcher.MatcherContract;
import io.pegacao.app.matcher.MatcherPresenter;
import io.pegacao.app.model.PeopleCardItem;
import io.pegacao.app.persondetail.PersonDetailActivity;
import io.pegacao.app.sync.PictureLoader;

import static com.google.common.base.Preconditions.checkNotNull;

//import io.pegacao.app.model.PeopleCardItem;

/**
 * Created by Ivan Morris on 10/30/15.
 */
public class MainSelectFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<SparseArray<Person>>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MatcherContract.View{


    @Bind(R.id.recycler_people)
    protected RecyclerView peopleGrid;
    @Bind(R.id.progressbar_matcher)
    protected ProgressBar progressBar;
    protected PeopleRecyclerAdapter mPeopleAdapter;

    private MatcherPresenter mActionsListener;

    private static final int LOADER_PEOPLE_SEARCH = 1;

    private static final String TAG = "MainSelectFragment";
    private static final int SPAN_COUNT = 2;

    public MainSelectFragment(){
        //Required empty public constructor
    }

    public static MainSelectFragment newInstance(){
        return new MainSelectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPeopleAdapter = new PeopleRecyclerAdapter(new SparseArray<Person>(0));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_select_fragment, container, false);
        ButterKnife.bind(this, rootView);

        //peopleGrid = (RecyclerView) rootView.findViewById(R.id.recycler_people);
       // progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar_matcher);

        setRecyclerViewLayoutManager();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Init presenter, and inject app data. Also pass this View (MainSelectFragment) to the Presenter.
        mActionsListener = new MatcherPresenter(Injection.providePeopleRepository(), this);

        mActionsListener.requestDataFromNetwork();

    }

    @Override
    public void onResume() {
        //Calling presenter to load data to the RecyclerView adapter.
        mActionsListener.loadPeople(false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public Loader<SparseArray<Person>> onCreateLoader(int id, Bundle args) {
        return new PictureLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<SparseArray<Person>> loader, SparseArray<Person> peopleArray) {
        Log.d(TAG, "onLoadFinished: " + peopleArray.toString());
        mActionsListener.savePeopleArray(peopleArray, new MatcherContract.SavePeopleSparseArrayContract() {
            @Override
            public void onSavedSparseArray(boolean saved) {
                if(saved)
                    mActionsListener.loadPeople(true);
                else
                    showEmptyNoteError();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<SparseArray<Person>> loader) {
        //TODO remove any data related to the loader task used by the fragment.
    }

    public void setRecyclerViewLayoutManager(){
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.

        if (peopleGrid.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) peopleGrid.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        //TODO erase this block of code. We don't load the data of the adapter directly. we call a Presenter instead
        //mPeopleAdapter = setupAdapter();
        peopleGrid.setAdapter(mPeopleAdapter);

        peopleGrid.setLayoutManager(new GridLayoutManager(getActivity(), SPAN_COUNT));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
        peopleGrid.addItemDecoration(itemDecoration);
        peopleGrid.scrollToPosition(scrollPosition);

    }

    /**
     * This method will check the cases were we need to add a message for an empty screen. We try
     * to find a reason why the screen will be blank and display some info for the user. It will
     * check network connection and server status
        Updates the empty list view with contextually relevant information that the user can
        use to determine why they aren't seeing weather.
     */
    private void updateEmptyView() {
        if (mPeopleAdapter != null && mPeopleAdapter.getItemCount() == 0 ) {
            TextView tv = (TextView) getView().findViewById(R.id.text_network_error);
            if ( null != tv ) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_grid_list;
                @PictureLoader.PeopleRequestStatus int location = Utility.getLocationStatus(getActivity());
                switch (location) {
                    case PictureLoader.PEOPLE_SERVER_DOWN:
                        message = R.string.empty_grid_list_server_down;
                        break;
                    case PictureLoader.PEOPLE_STATUS_SERVER_INVALID:
                        message = R.string.empty_grid_list_server_error;
                        break;
                    default:
                        if (!Utility.isNewtorkWorking(getActivity()) ) {
                            message = R.string.no_network_connection;
                        }
                }
                tv.setText(message);
                tv.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals(getString(R.string.pref_people_server_status_key)) ) {
            updateEmptyView();
        }
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active){
            progressBar.setVisibility(View.VISIBLE);
            peopleGrid.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            peopleGrid.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showPeople(SparseArray<Person> repositoryPeople) {
        mPeopleAdapter.replaceData(repositoryPeople);
    }

    @Override
    public void setupDataFromNetwork() {
        getLoaderManager().initLoader(LOADER_PEOPLE_SEARCH, null, this).forceLoad();
    }

    @Override
    public void showEmptyNoteError() {
        Snackbar.make(peopleGrid, "No data to save in repository", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showAddPerson() {

    }

    @Override
    public void showDeletePerson() {

    }

    @Override
    public void showPersonDetailUi(String personId) {
        //((MainActivity)getActivity()).switchPersonDetailFragment(personId);
        Intent intent = new Intent(getContext(), PersonDetailActivity.class);
        intent.putExtra(PersonDetailActivity.EXTRA_PERSON_ID, personId);
        startActivity(intent);
    }

    //****PEOPLERECYCLERADAPTER****//

    private class PeopleRecyclerAdapter extends RecyclerView.Adapter<PeopleRecyclerAdapter.CustomViewHolder> {

        private static final String TAG = "PeopleRecyclerAdapter";
        //private ArrayList<PeopleCardItem> peopleArray;

        private SparseArray<Person> peopleSparseArray;

        /**
         * Deprecated constructor use instead PeopleRecyclerAdapter(SparseArray<PeopleCardItem> peopleSparseArrayArray)
         * @param peopleArray
         */
        public PeopleRecyclerAdapter(ArrayList<Person> peopleArray) {
            //this.peopleArray = peopleArray;
        }

        public PeopleRecyclerAdapter(SparseArray<Person> peopleSparseArrayArray) {
            setList(peopleSparseArrayArray);
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_people_item, null);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            //PeopleCardItem peopleCardItem = peopleArray.get(position);
            //Get the corresponding Person object to populate
            Person peopleCardItem = peopleSparseArray.valueAt(position);

            //Set the counter of images. if null pass 0 if it has some images in the array pass its length
            int counter = peopleCardItem.imageUrls!=null?peopleCardItem.imageUrls.length:0;
            holder.imgCounter.setText(String.valueOf(counter));

            //Set the name of the person on the CardView
            holder.nameText.setText(peopleCardItem.name);

            if(peopleCardItem.liked){
                holder.kissState.setVisibility(View.VISIBLE);
                holder.positiveButton.setVisibility(View.GONE);
            } else {
                holder.kissState.setVisibility(View.GONE);
                holder.positiveButton.setVisibility(View.VISIBLE);
            }

            //Get image to the ImageView in the CardView.
            Glide.with(getContext())
                    .load(peopleCardItem.resDrawableProfile)
                    .override(Utility.dpToPx(getResources(), 100), Utility.dpToPx(getResources(), 100))
                    .placeholder(R.drawable.placeholder_drawer)
                    .error(R.drawable.x_20x20_icon)
                    .into(holder.imgPeopleCard);

        }



        @Override
        public int getItemCount() {
            //return (null != peopleArray ? peopleArray.size() : 0);
            return (null != peopleSparseArray ? peopleSparseArray.size() : 0);
        }

        /**
         * Deprecated method. Use addItemSparse instead.
         * @param cardItem
         */
        public void addItem(PeopleCardItem cardItem){
/*
        peopleArray.add(cardItem);
        notifyItemInserted(peopleArray.size() - 1);
*/
        }

        /**
         * We have to add an item into peopleSparseArray.
         * @param cardItem
         */
        public void addItemSparse(Person cardItem){
            peopleSparseArray.put(peopleSparseArray.size() - 1, cardItem);
            notifyItemInserted(peopleSparseArray.size() - 1);
        }

        /**
         * Deprecated use deleteSparse instead.
         * @param position
         */
        public void delete(int position){
        /*
        peopleArray.remove(position);
        notifyItemRemoved(position);
        */
        }

        public void deleteSparse(int position){
            //Get the Person object before deleting in the adapter and make sure is not in the cache anymore.
            mActionsListener.deletePerson(peopleSparseArray.valueAt(position).id);

            peopleSparseArray.removeAt(position);
            notifyItemRemoved(position);
        }

        /**
         * Method used for replacing the current values in the peopleSparseArray with the new
         * values in its parameter.
         * @param peopleList New values to set in the RecyclerView
         */
        public void replaceData(SparseArray<Person> peopleList){
            setList(peopleList);
            notifyDataSetChanged();
        }

        /**
         * Sets to peopleSparseArray new data for the RecyclerView.
         * @param peopleList Array to set in this adapter.
         */
        private void setList(SparseArray<Person> peopleList){
            peopleSparseArray = checkNotNull(peopleList);
        }



        public class CustomViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener{
            protected ImageView imgPeopleCard;
            protected TextView nameText;
            protected TextView imgCounter;
            protected TextView kissState;
            protected View cardView;
            protected ImageButton positiveButton;
            protected ImageButton negativeButton;
            protected LinearLayout linearLayoutsButtons;
            protected Button buttonChat;



            public CustomViewHolder(View view) {
                super(view);

                //Get all the views and put it into the holder
                this.imgPeopleCard = (ImageView) view.findViewById(R.id.img_card_people);
                this.negativeButton = (ImageButton) view.findViewById(R.id.btn_card_negative);
                this.positiveButton = (ImageButton) view.findViewById(R.id.btn_card_positive);
                this.linearLayoutsButtons = (LinearLayout) view.findViewById(R.id.linear_layout_buttons);
                this.buttonChat = (Button) view.findViewById(R.id.button_chat);
                this.nameText = (TextView) view.findViewById(R.id.txt_people_name);
                this.imgCounter = (TextView) view.findViewById(R.id.txt_picture_counter);
                this.kissState = (TextView) view.findViewById(R.id.txt_kissed_state);
                //set listeners
                this.negativeButton.setOnClickListener(this);
                this.positiveButton.setOnClickListener(this);
                this.buttonChat.setOnClickListener(this);
                this.imgPeopleCard.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                //delete(getLayoutPosition());
                switch (v.getId()){
                    case R.id.btn_card_negative:
                        deleteSparse(getLayoutPosition());
                        break;
                    case R.id.btn_card_positive:
                        //linearLayoutsButtons.setVisibility(View.GONE);
                        //Make necessary changes in layout
                        positiveButton.setVisibility(View.GONE);
                        kissState.setVisibility(View.VISIBLE);
                        //update values in peopleSparseArray for later layout references
                        Person person = peopleSparseArray.valueAt(getLayoutPosition());
                        person.liked = true;
                        //save new changes in PeopleServiceApi and cache response.
                        mActionsListener.likeAPerson(person.id, true);
                        break;
                    case R.id.button_chat:
                        linearLayoutsButtons.setVisibility(View.VISIBLE);
                        buttonChat.setVisibility(View.GONE);
                        break;
                    case R.id.img_card_people:
                        mActionsListener.openPersonDetails(peopleSparseArray.valueAt(getLayoutPosition()));
                        break;
                    default:
                        Log.d(TAG, "onClick: click not handle in people card");
                }

            }
        }
    }


}
