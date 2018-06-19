package com.harun.android.loginpin.Fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.User;
import com.harun.android.loginpin.NumPad;
import com.harun.android.loginpin.PinControlEditText;
import com.harun.android.loginpin.R;
import com.harun.android.loginpin.databinding.FragmentLoginPinBinding;

import static com.example.android.modellibrary.model.User.USER_KEY;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLocalAuthenticationListener} interface
 * to handle interaction events.
 * Use the {@link LoginPINFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginPINFragment extends Fragment implements NumPad.OnKeyInteractionListener, PinControlEditText.OnFullPinEnteredListener {
    private static final String LOG_TAG = LoginPINFragment.class.getSimpleName();
    private static final String PIN_KEY = "pin_key";

    Context mContext;
    NumPad numPad;

    TextView pinLabel;

    PinControlEditText pinControlEditText;
    String pin;

    private OnLocalAuthenticationListener mListener;
    User user;
    LocalStore userLocalStore;

    public LoginPINFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginPINFragment newInstance(User user) {
        LoginPINFragment fragment = new LoginPINFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_KEY, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(USER_KEY);
//            Log.w(LOG_TAG, "onCreate: " + user.getPhoneNo());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentLoginPinBinding loginPinBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_pin, container, false);
        pinLabel = loginPinBinding.pinLabel;

        KeyboardView keyboardView = loginPinBinding.keyboard.keyboardView;

        numPad = new NumPad(getActivity(), this, keyboardView, R.xml.num_pad);
        pinControlEditText = new PinControlEditText(loginPinBinding, numPad, this);

        return loginPinBinding.getRoot();
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(PIN_KEY, pinControlEditText.getFullPin());
//
//    }

    //Get the primaryCode for when a key is pressed i.e Delete
    @Override
    public void onKeyInteraction(int primaryCode, EditText edittext) {
        Log.w(LOG_TAG, "onKeyInteraction: " + primaryCode);
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                if (edittext.getText().toString().isEmpty()) {
                    Log.w(LOG_TAG, "KEYCODE_DELETE " + edittext.getText().toString());
                    pinControlEditText.moveBack(edittext);
                } else {
                    pinControlEditText.deleteFromEditText(edittext);
                }
                break;
            case Keyboard.KEYCODE_DONE:
                Log.w(LOG_TAG, "KEYCODE_DONE:" + primaryCode);
                break;
            default:
                if (!edittext.getText().toString().isEmpty()) {
                    pinControlEditText.moveForward(edittext, primaryCode);
                } else {
                    pinControlEditText.focusAndAdd(edittext, primaryCode);
                }
                Log.w(LOG_TAG, "normal button: " + edittext.getText().toString() + ">>>>>");
        }
    }

    public void clearPinView() {
        pinControlEditText.clearEditText();
        pinLabel.setText(R.string.confirm_pin_label);
        Log.w(LOG_TAG, "recreateFragment: " + pin);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onPinAuthenticated(User user) {
        if (mListener != null) {
            mListener.onLocalAuthentication(user);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnLocalAuthenticationListener) {
            mListener = (OnLocalAuthenticationListener) context;
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

    @Override
    public void onFullPinEntered(String fullPin) {
        Log.w(LOG_TAG, "onFullPinEntered: " + fullPin);
        String role = "offloader";
        String company = "udacity";
        userLocalStore = new LocalStore(mContext);
        user = new User(user.getName(),user.getEmail(),user.getPhoneNo(), fullPin, company, role);

        if (!userLocalStore.getStoredUser().getPin().isEmpty()){
            if (isSameAsStored(user.getPin())){
                Log.w(LOG_TAG, "onFullPinEntered: StartActivity: " + userLocalStore.getStoredUser().getPin());
                onPinAuthenticated(userLocalStore.getStoredUser());
            }else {
                Log.w(LOG_TAG, "Re-enter PIN: ");
                clearPinView();
            }

        }else {
//            onPinAuthenticated(user);
            userLocalStore.storeUserData(user);
            Log.w(LOG_TAG, "No User"+userLocalStore.getStoredUser().getPhoneNo());
            clearPinView();
        }
    }

    private boolean isSameAsStored(String fullPin){
        return userLocalStore.getStoredUser().getPin().equals(fullPin);
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
    public interface OnLocalAuthenticationListener {
        // TODO: Update argument type and name
        void onLocalAuthentication(User user);
    }
}
