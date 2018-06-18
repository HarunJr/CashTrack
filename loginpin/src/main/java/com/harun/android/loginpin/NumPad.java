package com.harun.android.loginpin;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.harun.android.loginpin.Fragments.LoginPINFragment;

/**
 * Created by HARUN on 7/4/2016.
 * The Numeric keypad Control Class
 * http://www.fampennings.nl/maarten/android/09keyboard/index.htm
 */
public class NumPad {
    private static final String LOG_TAG = NumPad.class.getSimpleName();
    private OnKeyInteractionListener mKeyListener;
    private Activity mHostActivity;
    private KeyboardView mKeyboardView;
    int position;

    public NumPad(Context context){

    }

    public NumPad(Activity activity, LoginPINFragment loginPINFragment, KeyboardView keyboard_view, int num_pad) {
        mHostActivity = activity;
        mKeyboardView = keyboard_view; //Lookup the KeyboardView
        mKeyboardView.setKeyboard(new Keyboard(activity, num_pad)); // Attach the keyboard to the view
        mKeyboardView.setPreviewEnabled(false); // Do not show the preview balloons
        mKeyListener = loginPINFragment;
        KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int i) {
            }

            @Override
            public void onRelease(int i) {
            }

            @Override
            public void onKey(int primaryCode, int[] ints) {
                getEditTextInFocus(primaryCode);
            }

            @Override
            public void onText(CharSequence charSequence) {
            }

            @Override
            public void swipeLeft() {
            }

            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeDown() {
            }

            @Override
            public void swipeUp() {
            }
        };
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.w(LOG_TAG, "OnKeyboardActionListener:" + mHostActivity +", "+ activity);
    }

    public void registerEditText(EditText edittext) {
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showCustomKeyboard(v);
                else hideCustomKeyboard();
            }
        });
        //TODO: Review code below
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });

        // Disable standard keyboard hard way
//        mEditText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                EditText edittext = (EditText) v;
//                int inType = edittext.getInputType();       // Backup the input company
//                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
//                edittext.onTouchEvent(event);               // Call native handler
//                edittext.setInputType(inType);              // Restore input company
//                return true; // Consume touch event
//            }
//        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Log.w(LOG_TAG, "registerEditText: " + edittext.toString());
    }

    public void getEditTextInFocus(int primaryCode) {
        // Get the EditText and its Editable
        View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
        if (focusCurrent == null || !(focusCurrent instanceof EditText)) {
            Log.w(LOG_TAG, "getEditTextInFocus:" + primaryCode +", "+ focusCurrent +", "+ mHostActivity);
            return;
        }
        getKeyCodeActions(primaryCode, focusCurrent);
    }

    public void getKeyCodeActions(int primaryCode, View focusCurrent) {
        // This wouldn't work for subclasses of EditText.
        //if (focusCurrent == null || focusCurrent.getClass() != EditText.class ) { return; }
        EditText edittext = (EditText) focusCurrent;
        Editable editable = edittext.getText();
        int start = edittext.getSelectionStart();

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                //function for deleting text from edit text
//                if (editable != null && start > 0) editable.delete(start - 1, start);
                buttonClicked(primaryCode, edittext);
                   Log.w(LOG_TAG, "KEYCODE_DELETE:" + primaryCode);
                break;
            case Keyboard.KEYCODE_DONE:
                Log.w(LOG_TAG, "KEYCODE_DONE:" + primaryCode);
                break;
            default:
//                editable.insert(start, Character.toString((char) primaryCode));
                buttonClicked(primaryCode, edittext);
                Log.w(LOG_TAG, "insert:" + primaryCode +" "+start+" "+ edittext+": "+ focusCurrent);
        }
    }

    private void buttonClicked(int primaryCode, EditText edittext){
        if (mKeyListener != null){
            mKeyListener.onKeyInteraction(primaryCode, edittext);
        }
    }

    public boolean showCustomKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null)
        ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        return true;
    }

    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public interface OnKeyInteractionListener {
        // TODO: Update argument type and name
        void onKeyInteraction(int primaryCode, EditText edittext);
    }


//    public boolean isCustomKeyboardVisible() {
//        Log.w(LOG_TAG, "onKey:" + mKeyboardView);
//        return mKeyboardView.getVisibility() == View.VISIBLE;
//    }
}
