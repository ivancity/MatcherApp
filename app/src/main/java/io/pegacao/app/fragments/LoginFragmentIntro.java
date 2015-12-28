package io.pegacao.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.pegacao.app.R;
import io.pegacao.app.Utility;

/**
 * Created by ivanm on 11/12/15.
 */
public class LoginFragmentIntro extends Fragment {

    private int drawableId;
    private String textDisplay;

    public static final String DRAWABLE_ID = "drwabaleId";
    public static final String TEXT_MESSAGE = "textMessage";
    private static final String TAG = "LoginFragmentIntro";

    public LoginFragmentIntro(){
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            drawableId = getArguments().getInt(DRAWABLE_ID);
            Log.d(TAG, "drawable id: " + String.valueOf(drawableId));
            textDisplay = getArguments().getString(TEXT_MESSAGE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_page_intro, container, false);
        view.setBackgroundResource(drawableId);
        TextView textMessage = (TextView) view.findViewById(R.id.txt_fragment_intro_message);
        Utility.setAppTypeFace(getResources().getAssets(), textMessage, false);
        textMessage.setText(textDisplay);
        return view;

    }
}
