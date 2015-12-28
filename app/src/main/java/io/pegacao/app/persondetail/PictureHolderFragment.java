package io.pegacao.app.persondetail;

import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.pegacao.app.R;
import io.pegacao.app.Utility;

/**
 * Created by ivanm on 12/21/15.
 */
public class PictureHolderFragment extends Fragment {

    private static final String IMG_RES_KEY = "imgResourceKey";

    @IntegerRes
    private int imgResKey;

    public PictureHolderFragment(){}

    public static PictureHolderFragment newInstance(int imgResource){
        PictureHolderFragment fragment = new PictureHolderFragment();
        Bundle args = new Bundle();
        args.putInt(IMG_RES_KEY, imgResource);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imgResKey = getArguments().getInt(IMG_RES_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_holder_fragment, container, false);
        //TODO remove imgResKey from drawable resource to URL for images
        ImageView imgPicture = (ImageView) view.findViewById(R.id.detail_img_person_pictures);
        Glide.with(this)
                .load(imgResKey)
                .override(Utility.dpToPx(getResources(), 300), Utility.dpToPx(getResources(), 300))
                .placeholder(R.drawable.placeholder_drawer)
                .error(R.drawable.x_20x20_icon)
                .into(imgPicture);

        return view;
    }
}
