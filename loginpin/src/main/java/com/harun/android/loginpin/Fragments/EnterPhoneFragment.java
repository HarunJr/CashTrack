package com.harun.android.loginpin.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.modellibrary.model.User;
import com.harun.android.loginpin.R;
import com.harun.android.loginpin.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMobileNumberEnteredListener} interface
 * to handle interaction events.
 * Use the {@link EnterPhoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterPhoneFragment extends Fragment {
    private static final String LOG_TAG = EnterPhoneFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    EditText countryCode;
    EditText etNames;
    EditText etEmail;
    EditText mobileNo;
    Context mContext;

    private OnMobileNumberEnteredListener mListener;


    public EnterPhoneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnterPhoneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnterPhoneFragment newInstance(String param1, String param2) {
        EnterPhoneFragment fragment = new EnterPhoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_enter_phone, container, false);
        countryCode = rootView.findViewById(R.id.country_code);
        etNames = rootView.findViewById(R.id.name_register);
        etEmail = rootView.findViewById(R.id.email_register);
        mobileNo = rootView.findViewById(R.id.phone_no);
        countryCode.setText(Utilities.getCountryZipCode(mContext));

        editTextKeyboard();

        return rootView;
    }

    private void editTextKeyboard() {
        if (!countryCode.getText().toString().isEmpty()) {
            Utilities.showSoftKeyboard(mobileNo, getActivity());
        } else {
            Utilities.showSoftKeyboard(countryCode, getActivity());
        }

        mobileNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String phoneNo = mobileNo.getText().toString().replaceFirst("^0+(?!$)", "");
                    String name = etNames.getText().toString();
                    String email = etEmail.getText().toString();
                    String mobileNumber = countryCode.getText().toString() + phoneNo;
                    Log.i(LOG_TAG, "Enter pressed: " + mobileNumber);
                    User user = new User(name, email, mobileNumber);
                    onButtonPressed(user);
                }
                return false;
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(User user) {
        if (mListener != null) {
            mListener.onMobileNumberEntered(user);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnMobileNumberEnteredListener) {
            mListener = (OnMobileNumberEnteredListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMobileNumberEnteredListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMobileNumberEnteredListener {
        // TODO: Update argument type and name
        void onMobileNumberEntered(User user);
    }
}
