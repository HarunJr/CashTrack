package com.example.android.cashtracklibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

    public NumPad(Activity activity, KeyboardView keyboard_view, int num_pad) {
        mHostActivity = activity;
        mKeyboardView = keyboard_view; //Lookup the KeyboardView
        mKeyboardView.setKeyboard(new Keyboard(activity, num_pad)); // Attach the keyboard to the view
        mKeyboardView.setPreviewEnabled(false); // Do not show the preview balloons
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

    @SuppressLint("ClickableViewAccessibility")
    public void registerEditText(final EditText edittext) {
        Log.w(LOG_TAG, "registerEditText:" + edittext);
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showCustomKeyboard(edittext);
                else hideCustomKeyboard();
            }
        });
        //TODO: Review code below
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomKeyboard(edittext);
            }
        });

        // Disable standard keyboard hard way
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.w(LOG_TAG, "onTouch: ");
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input company
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input company
                return true; // Consume touch event
            }
        });

        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        edittext.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
    }



    private void getEditTextInFocus(int primaryCode) {
        // Get the EditText and its Editable
        View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
        if (focusCurrent == null || !(focusCurrent instanceof EditText)) {
            Log.w(LOG_TAG, "getEditTextInFocus:" + primaryCode +", "+ focusCurrent +", "+ mHostActivity);
            return;
        }
        getKeyCodeActions(primaryCode, focusCurrent);
    }

    public void getKeyCodeActions(int primaryCode, View focusCurrent) {
        Log.w(LOG_TAG, "getKeyCodeActions: ");
        // This wouldn't work for subclasses of EditText.
        //if (focusCurrent == null || focusCurrent.getClass() != EditText.class ) { return; }
        EditText edittext = (EditText) focusCurrent;
        Editable editable = edittext.getText();
        int start = edittext.getSelectionStart();

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                //function for deleting text from edit text
                if (editable != null && start > 0) editable.delete(start - 1, start);
                   Log.w(LOG_TAG, "KEYCODE_DELETE:" + primaryCode);
                break;
            case Keyboard.KEYCODE_DONE:
                Log.w(LOG_TAG, "KEYCODE_DONE:" + primaryCode);
                break;
            default:
                editable.insert(start, Character.toString((char) primaryCode));
                Log.w(LOG_TAG, "insert:" + primaryCode +" "+start+" "+ edittext+": "+ focusCurrent);
        }
    }

    private void buttonClicked(Editable editable, int start){
        if (mKeyListener != null){
            mKeyListener.onKeyCashTrackInteraction(editable);
        }
    }

    private void showCustomKeyboard(EditText editText) {
        Log.w(LOG_TAG, "showCustomKeyboard: ");
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (editText != null) {
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public void hideCustomKeyboard() {
        Log.w(LOG_TAG, "hideCustomKeyboard: ");
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public interface OnKeyInteractionListener {
        // TODO: Update argument type and name
        void onKeyInteraction(int primaryCode, EditText edittext);
        void onKeyCashTrackInteraction(Editable editable);
    }

    public boolean isCustomKeyboardVisible() {
        Log.w(LOG_TAG, "onKey:" + mKeyboardView);
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }
}
