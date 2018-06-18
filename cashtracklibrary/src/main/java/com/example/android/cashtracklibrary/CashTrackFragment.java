package com.example.android.cashtracklibrary;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cashtracklibrary.databinding.FragmentCashTrackBinding;
import com.example.android.modellibrary.AmountFormatHelper;
import com.example.android.modellibrary.DateHelper;
import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.Transaction;
import com.example.android.modellibrary.model.Vehicle;

import static com.example.android.modellibrary.SystemQuery.EXPENSE_TAG;
import static com.example.android.modellibrary.SystemQuery.INCOME_TAG;
import static com.example.android.modellibrary.SystemQuery.TRANSACTION_COLUMNS;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry;
import static com.example.android.modellibrary.model.Vehicle.VEHICLE_KEY;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCashTrackFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CashTrackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CashTrackFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = CashTrackFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int TRANSACTION_LOADER = 1;

    // TODO: Rename and change types of parameters
    private Vehicle vehicle;
    private Context mContext;
    private EditText editText;
    private TextView headerCollectionView;
    private TextView headerExpenseView;
    private TextView headerDifferenceView;
    CoordinatorLayout coordinatorLayout;

    private OnCashTrackFragmentInteractionListener mListener;
    FragmentCashTrackBinding cashTrackBinding;
    TransactionsAdapter transactionsAdapter;
    ImageButton collectionButton;
    long timestamp = System.currentTimeMillis();

    public CashTrackFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CashTrackFragment newInstance(Vehicle vehicle) {
        Log.w(LOG_TAG, "newInstance: " + vehicle.getRegistration());
        CashTrackFragment fragment = new CashTrackFragment();
        Bundle args = new Bundle();
        args.putParcelable(VEHICLE_KEY, vehicle);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicle = getArguments().getParcelable(VEHICLE_KEY);
            assert vehicle != null;
            Log.w(LOG_TAG, "onCreate: " + vehicle.get_id());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cashTrackBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cash_track, container, false);
        headerCollectionView = cashTrackBinding.cashTrackToolbar.headerCardView.itemHeaderCollectionTextView;
        headerExpenseView = cashTrackBinding.cashTrackToolbar.headerCardView.itemHeaderExpenseTextView;
        headerDifferenceView = cashTrackBinding.cashTrackToolbar.headerCardView.itemHeaderProfitTextView;

        coordinatorLayout = cashTrackBinding.coordinator;
        editText = cashTrackBinding.chatBar.edittextChatbox;
        collectionButton = cashTrackBinding.chatBar.buttonPositive;
        collectionButton.setTag(INCOME_TAG);
        ImageButton sendButton = cashTrackBinding.chatBar.buttonChatboxSend;

        initViews();

        NumPad mNumPad = initNumPad();
        mNumPad.registerEditText(editText);

        RecyclerView recyclerView = cashTrackBinding.recyclerViewCashTrack;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        transactionsAdapter = new TransactionsAdapter(mContext);
        recyclerView.setAdapter(transactionsAdapter);
//        recyclerView.scrollToPosition();

        //TODO: Check if below methods can work together to handle all button clicks.
        setAmountTag();
        sendButtonClicked(sendButton);

        Activity activity = getActivity();
        assert activity != null;
        ((CashTrackActivity) activity).setOnBackPressedListener(new BaseBackPressedListener(getActivity(), mNumPad));

        return cashTrackBinding.getRoot();
    }

    public Toolbar getToolbar() {
        return (Toolbar) cashTrackBinding.cashTrackToolbar.cashTrackActivityToolbar;
    }

    private void initViews() {
        Toolbar mToolbar = getToolbar();
        getActivityCast().setSupportActionBar(mToolbar);

        assert getActivityCast().getSupportActionBar() != null;
        getActivityCast().getSupportActionBar().setHomeButtonEnabled(true);
        getActivityCast().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivityCast().getSupportActionBar().setTitle(vehicle.getRegistration());
    }

    public CashTrackActivity getActivityCast() {
        return (CashTrackActivity) getActivity();
    }

    public NumPad initNumPad() {
        KeyboardView keyboardView = cashTrackBinding.keyboardView.keyboardView;
        return new NumPad(getActivity(), keyboardView, R.xml.num_pad);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TRANSACTION_LOADER, null, this);
    }

    private void sendButtonClicked(ImageButton sendButton) {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description;
                String editTextString = editText.getText().toString().trim();
                if (!editTextString.isEmpty() && !editTextString.equals("0") && !editTextString.equals("00") && !editTextString.equals("000")) {
                    if (collectionButton.getTag().equals(INCOME_TAG)) {

                        description = "Collection...";
                        setTransactionData(description, INCOME_TAG);

                    } else {
                        description = "Expense...";
                        setTransactionData(description, EXPENSE_TAG);
                    }
                } else {
                    Toast.makeText(mContext, "Enter Transaction Amount To Continue.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setTransactionData(String description, String amountTag) {
        double amount = Double.parseDouble(editText.getText().toString());
        String createdAt = String.valueOf(DateHelper.getFormattedDate(System.currentTimeMillis()));
        String sync = "0";

        if (amountTag.equals(EXPENSE_TAG)){
            amount = -amount;
        }

        Transaction transaction = new Transaction(vehicle.get_id(), amount, amountTag, description, timestamp, createdAt, sync);
        onButtonPressed(transaction, coordinatorLayout);
        editText.setText("");
        Log.w(LOG_TAG, "AMOUNT_TAG: " + vehicle.get_id() + " " + transaction.getDescription() + " " + transaction.getAmount() + " " + transaction.getType());
    }

    private void setAmountTag() {
        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (collectionButton.getTag().equals(INCOME_TAG)) {
                    collectionButton.setImageResource(R.drawable.ic_remove_black_24dp);
                    editText.setHint(R.string.string_expense_hint);
                    collectionButton.setTag(EXPENSE_TAG);
                    Log.w(LOG_TAG, "EXPENSE_TAG: ");
                } else if (collectionButton.getTag().equals(EXPENSE_TAG)) {
                    collectionButton.setImageResource(R.drawable.ic_add_black_24dp);
                    editText.setHint(R.string.string_collection_hint);
                    collectionButton.setTag(INCOME_TAG);
                    Log.w(LOG_TAG, "INCOME_TAG: ");
                } else {
                    Log.w(LOG_TAG, "Oops!!: ");
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Transaction transaction, CoordinatorLayout coordinatorLayout) {
        if (mListener != null) {
            mListener.onCashTrackFragmentInteraction(transaction, coordinatorLayout);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnCashTrackFragmentInteractionListener) {
            mListener = (OnCashTrackFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCashTrackFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String startDate = null;
        String endDate = null;
        String todayDate = DateHelper.getFormattedDateHyphenString(timestamp);

        long dayInMilli = (24 * 60 * 60) * 1000;
        long yesterday = timestamp - dayInMilli;
        long tomorrow = timestamp + dayInMilli;

        final String yesterdayStartDate = DateHelper.getFormattedDateHyphenString(yesterday) + " 03:00:00";
        final String todayStartDate = todayDate + " 03:00:00";
        final String tomorrowEndDate = DateHelper.getFormattedDateHyphenString(tomorrow) + " 03:00:00";

        long todayStartDateMilli = DateHelper.getMilliDateTimeFromString(todayStartDate);
        long todayDateMilli = DateHelper.getMilliDateTimeFromString(todayDate + " 00:00:00");
        long tomorrowEndDateMilli = DateHelper.getMilliDateTimeFromString(tomorrowEndDate);
        Log.w(LOG_TAG, "todayStartDateMilli: " + todayStartDateMilli + " " + todayDateMilli + " " + tomorrowEndDateMilli);

        if (timestamp > todayDateMilli && timestamp < todayStartDateMilli) {
            startDate = yesterdayStartDate;
            endDate = todayStartDate;
        } else if (timestamp > todayStartDateMilli && timestamp < tomorrowEndDateMilli) {
            startDate = todayStartDate;
            endDate = tomorrowEndDate;
        }

        Uri transactionForVehicleIdUri = TransactionEntry.buildKeyTransactionWithDateUri(vehicle.get_id(), startDate, endDate);
        String sortOrder = TransactionEntry.COLUMN_TIME_STAMP + " DESC";
        Log.w(LOG_TAG, "onCreateLoader: " + vehicle.getRegistration() + " " + vehicle.getCollection() + " " + transactionForVehicleIdUri);

        return new CursorLoader(
                mContext,
                transactionForVehicleIdUri,
                TRANSACTION_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Log.w(LOG_TAG, "onLoadFinished: " + data.getCount() + ", " + vehicle);

            getHeaderValues();

            transactionsAdapter.swapCursor(data);
        } else {
            Log.w(LOG_TAG, "onLoadFinished: NULL cursor");
        }
    }

    private void getHeaderValues(){
        double dailyVehicleCollection;
        double dailyVehicleExpense;
        double difference;

        LocalStore localStore = new LocalStore(getActivityCast());
        vehicle = localStore.getVehicleDataFromTransaction(vehicle.get_id());
        Log.w(LOG_TAG, "onLoadFinished: getVehicleById: "+ vehicle.get_id()+ " " + vehicle.getRegistration() + ", " + vehicle.getCollection());
        localStore.updateVehiclesTable(vehicle);

        dailyVehicleCollection = vehicle.getCollection();
        dailyVehicleExpense = vehicle.getExpense(); //vehicle.getExpense is negative
        difference = dailyVehicleCollection + dailyVehicleExpense;

//            String day = DateHelper.getFormattedDayString(dateTime);
        String formattedCollection = AmountFormatHelper.getFormattedIncome(mContext, dailyVehicleCollection);
        String formattedExpense = AmountFormatHelper.getFormattedIncome(mContext, dailyVehicleExpense);
        String formattedDifference = AmountFormatHelper.getFormattedIncome(mContext, difference);

//            headerDateView.setText(day);
        headerCollectionView.setText(formattedCollection);
        headerExpenseView.setText(formattedExpense);
        headerDifferenceView.setText(formattedDifference);
        Log.w(LOG_TAG, "getHeaderValues: " + vehicle.get_id() + ", " + dailyVehicleCollection);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.w(LOG_TAG, "onLoaderReset: " + loader.toString());
        transactionsAdapter.swapCursor(null);
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
    public interface OnCashTrackFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCashTrackFragmentInteraction(Transaction transaction, CoordinatorLayout coordinatorLayout);
    }

    public interface OnBackPressedListener {
        // TODO: Update argument type and name
        void onBackPressed();
    }


}
