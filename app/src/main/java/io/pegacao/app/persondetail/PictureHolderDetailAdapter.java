package io.pegacao.app.persondetail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

/**
 * Created by ivanm on 12/21/15.
 * Adapter that will hold images set in the Person Detail Activity. This is the adapter for the view pager
 * in the layout.
 */
public class PictureHolderDetailAdapter extends FragmentPagerAdapter {
    SparseArray<PictureHolderFragment> sparseArrayPictureHolder;

    public PictureHolderDetailAdapter(FragmentManager fm) {super(fm);}

    public void setData(SparseArray<PictureHolderFragment> data){ sparseArrayPictureHolder = data;}

    @Override
    public Fragment getItem(int position) { return sparseArrayPictureHolder.valueAt(position); }

    @Override
    public int getCount() { return sparseArrayPictureHolder.size(); }

    @Override
    public long getItemId(int position) { return sparseArrayPictureHolder.keyAt(position); }
}
