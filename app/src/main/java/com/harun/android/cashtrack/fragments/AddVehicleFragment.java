package com.harun.android.cashtrack.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.modellibrary.DateHelper;
import com.example.android.modellibrary.model.Vehicle;
import com.harun.android.cashtrack.R;
import com.harun.android.cashtrack.databinding.FragmentAddVehicleBinding;

//import com.example.android.model.DateHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAddVehicleListener} interface
 * to handle interaction events.
 * Use the {@link AddVehicleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddVehicleFragment extends Fragment {
    private static final String LOG_TAG = AddVehicleFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    FragmentActivity fragmentActivity;
    private OnAddVehicleListener mListener;
    private EditText mRegInput, mRegInputEnd, makeInput, modelInput, mYomInput, mPassCapInput;
    private String regEnd;

    public AddVehicleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddVehicleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddVehicleFragment newInstance(String param1, String param2) {
        AddVehicleFragment fragment = new AddVehicleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentAddVehicleBinding addVehicleBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_vehicle, container, false);
        fragmentActivity = getActivity();
        initViews(addVehicleBinding);
        showSoftKeyboard(mRegInput, fragmentActivity);
        watchVehicleText();
        return addVehicleBinding.getRoot();
    }

    private void initViews(FragmentAddVehicleBinding addVehicleBinding) {
        mRegInput = addVehicleBinding.vehicleInput;
        mRegInputEnd = addVehicleBinding.vehicleInputEnd;
        makeInput = addVehicleBinding.vehicleMakeInput;
        modelInput = addVehicleBinding.vehicleModelInput;
        mYomInput = addVehicleBinding.vehicleManYearInput;
        mPassCapInput = addVehicleBinding.passengerCapInput;

        Button mAddVehicleButton = addVehicleBinding.createVehicleButton;
        mAddVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionDone();
            }
        });
    }

    private void actionDone() {
        hideSoftKeyboard(mPassCapInput, fragmentActivity);
        String vehicleRegistration = mRegInput.getText().toString() + " " + regEnd;
        String vehicleMake = makeInput.getText().toString();
        String vehicleModel = modelInput.getText().toString();
        String yearOfManufacture = mYomInput.getText().toString();
        String passengerCapacity = mPassCapInput.getText().toString();
        long updatedAt = System.currentTimeMillis();
        String createdAt = String.valueOf(DateHelper.getFormattedDate(updatedAt));

        Vehicle vehicle = new Vehicle(vehicleRegistration, vehicleMake, vehicleModel,
                yearOfManufacture, passengerCapacity, createdAt);
        onButtonPressed(vehicle);
    }

    private void watchVehicleText() {
        mRegInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mRegInput.getText().toString().length() == 3)     //size as per your requirement
                {
                    mRegInputEnd.requestFocus();
                }
            }
        });

        mRegInputEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.w(LOG_TAG, "beforeTextChanged regEnd: " + regEnd);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                regEnd = mRegInputEnd.getText().toString();

                if (regEnd.length() == 3) {
                    mRegInputEnd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                } else if (regEnd.length() == 4) {
                    makeInput.requestFocus();
                }

                Log.w(LOG_TAG, "afterTextChanged regEnd: " + regEnd);
            }
        });

        mPassCapInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    actionDone();
                }
                return false;
            }
        });
    }

    public static void showSoftKeyboard(View view, FragmentActivity activity) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static void hideSoftKeyboard(View view, FragmentActivity activity) {
        view = activity.getCurrentFocus();
        Log.w(LOG_TAG, "hideSoftKeyboard " + view);

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Vehicle vehicle) {
        if (mListener != null) {
            mListener.onAddButtonPressed(vehicle);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddVehicleListener) {
            mListener = (OnAddVehicleListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddVehicleListener");
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
    public interface OnAddVehicleListener {
        // TODO: Update argument type and name
        void onAddButtonPressed(Vehicle vehicle);
    }
}
