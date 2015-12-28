package io.pegacao.app.controllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import io.pegacao.app.fragments.LoginFragmentIntro;

/**
 * Created by ivanm on 11/12/15.
 */
public class LoginPagerAdapter extends FragmentPagerAdapter {
    private SparseArray<LoginFragmentIntro> fragmentsPageArray;

    public LoginPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(SparseArray<LoginFragmentIntro> data) {
        fragmentsPageArray = data;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsPageArray.valueAt(position);
    }

    @Override
    public int getCount() {
        return fragmentsPageArray.size();
    }

    @Override
    public long getItemId(int position) {
        return fragmentsPageArray.keyAt(position);
    }


}
