package io.pegacao.app.persondetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.pegacao.app.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PERSON_ID = "argChosenPersonId";

    // TODO: Rename and change types of parameters
    private String chosenPersonId;

    private OnFragmentInteractionListener mListener;

    public PersonDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of PersonDetailFragment and displays data of the specified
     * person id.
     *
     * @param chosenPersonId Person Id to display .
     * @return A new instance of fragment PersonDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonDetailFragment newInstance(String chosenPersonId) {
        PersonDetailFragment fragment = new PersonDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PERSON_ID, chosenPersonId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chosenPersonId = getArguments().getString(ARG_PERSON_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person_detail, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String something) {
        if (mListener != null) {
            mListener.onFragmentInteraction(something);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String something);
    }
}
