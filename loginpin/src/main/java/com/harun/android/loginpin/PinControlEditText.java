package com.harun.android.loginpin;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.harun.android.loginpin.Fragments.LoginPINFragment;
import com.harun.android.loginpin.databinding.FragmentLoginPinBinding;

/**
 * Created by HARUN on 4/14/2018.
 */

public class PinControlEditText {
    private static final String LOG_TAG = PinControlEditText.class.getSimpleName();

    private EditText mPinIndicatorOne;
    private EditText mPinIndicatorTwo;
    private EditText mPinIndicatorThree;
    private EditText mPinIndicatorFour;
    private NumPad numPad;

    private OnFullPinEnteredListener mListener;

    public PinControlEditText(FragmentLoginPinBinding loginPinBinding, NumPad numPad, LoginPINFragment loginPINFragment) {
        this.mPinIndicatorOne = loginPinBinding.indicatorView.pinIndicator1;
        this.mPinIndicatorTwo = loginPinBinding.indicatorView.pinIndicator2;
        this.mPinIndicatorThree = loginPinBinding.indicatorView.pinIndicator3;
        this.mPinIndicatorFour = loginPinBinding.indicatorView.pinIndicator4;
        this.numPad = numPad;

        focusEditText(mPinIndicatorOne);

        this.mListener = loginPINFragment;
    }

    private EditText focusEditText(EditText editText) {
        numPad.registerEditText(editText);
        editText.requestFocus();
        return editText;
    }

    private void watchText(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editText.getText().toString().isEmpty()) {
                    moveToEditText(editText);
                } else {
                    moveToEditText(editText);
                }
            }
        });
    }

    private void moveToEditText(EditText editText) {
        Log.w(LOG_TAG, "moveForwardEditText " + editText.getText().toString());
        if (editText.equals(mPinIndicatorOne) || editText.equals(mPinIndicatorThree)) {

            watchText(focusEditText(mPinIndicatorTwo));

        } else if (editText.equals(mPinIndicatorTwo) || editText.equals(mPinIndicatorFour)) {

            watchText(focusEditText(mPinIndicatorThree));

        } else if (editText.equals(mPinIndicatorThree)) {

            watchText(focusEditText(mPinIndicatorFour));
            String fullPin = getFullPin(editText);
            if (fullPin.length() == 4){
                Log.w(LOG_TAG, "getFullPin " + fullPin.length());
                onPinEntered(fullPin);
            }

//            Log.w(LOG_TAG, "moveToEditText Go to next activity " + getFullPin(editText));
        } else {
            focusEditText(mPinIndicatorOne);
        }
    }

    public void moveForward(EditText editText, int primaryCode) {
        Log.w(LOG_TAG, "moveForwardEditText " + editText.getText().toString());
        if (editText.equals(mPinIndicatorOne)) {

            focusAndAdd(mPinIndicatorTwo, primaryCode);

        } else if (editText.equals(mPinIndicatorTwo)) {

            focusAndAdd(mPinIndicatorThree, primaryCode);

        } else if (editText.equals(mPinIndicatorThree)) {

            focusAndAdd(mPinIndicatorFour, primaryCode);
            String fullPin = getFullPin(editText);
            if (fullPin.length() == 4){
                Log.w(LOG_TAG, "getFullPin " + fullPin.length());
                onPinEntered(fullPin);
            }

//            Log.w(LOG_TAG, "moveForward Go to next activity " + getFullPin(editText));
        }
    }

    public void moveBack(EditText editText) {
        Log.w(LOG_TAG, "moveBackEditText " + editText.getText().toString());

        if (editText.equals(mPinIndicatorFour)) {
            Log.w(LOG_TAG, "editText is mPinIndicatorFour");
            deleteFromEditText(mPinIndicatorThree);

        } else if (editText.equals(mPinIndicatorThree)) {
            Log.w(LOG_TAG, "editText is mPinIndicatorThree");
            deleteFromEditText(mPinIndicatorTwo);

        } else {
            Log.w(LOG_TAG, "editText is mPinIndicatorTwo");
            deleteFromEditText(mPinIndicatorOne);
        }
    }

    public String getFullPin(EditText editText) {
        String fullPin = null;

        if (editText.getText().toString().length() > 0) { //size as per your requirement
//            editText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_fill));
            String pinUnitOne = mPinIndicatorOne.getText().toString();
            String pinUnitTwo = mPinIndicatorTwo.getText().toString();
            String pinUnitThree = mPinIndicatorThree.getText().toString();
            String pinUnitFour = mPinIndicatorFour.getText().toString();

            fullPin = pinUnitOne + pinUnitTwo + pinUnitThree + pinUnitFour;
        }
        return fullPin;
    }

    public void clearEditText(){
        mPinIndicatorOne.setText("");
        mPinIndicatorTwo.setText("");
        mPinIndicatorThree.setText("");
        mPinIndicatorFour.setText("");

        focusEditText(mPinIndicatorOne);
    }

    private void onPinEntered(String fullPin) {
        if (mListener != null) {
            mListener.onFullPinEntered(fullPin);
        }
    }

    public void deleteFromEditText(EditText editText) {
        int start = editText.getSelectionStart();
        Editable editable = focusEditText(editText).getText();
        if (editable != null && start > 0) editable.delete(start - 1, start);
    }

    public void focusAndAdd(EditText editText, int primaryCode) {
        int start = editText.getSelectionStart();
        Editable editable = focusEditText(editText).getText();
        editable.insert(start, Character.toString((char) primaryCode));
    }

    public interface OnFullPinEnteredListener{
        void onFullPinEntered(String fullPin);
    }
}
