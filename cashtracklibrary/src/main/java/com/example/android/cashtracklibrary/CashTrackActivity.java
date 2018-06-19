package com.example.android.cashtracklibrary;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.android.background.BaseActivity;
import com.example.android.background.sync.CashTrackSyncUtils;
import com.example.android.cashtracklibrary.CashTrackFragment.OnCashTrackFragmentInteractionListener;
import com.example.android.cashtracklibrary.databinding.ActivityCashTrackBinding;
import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.Transaction;
import com.example.android.modellibrary.model.Vehicle;

import static com.example.android.modellibrary.model.Vehicle.VEHICLE_KEY;

public class CashTrackActivity extends BaseActivity implements OnCashTrackFragmentInteractionListener {
    private static final String LOG_TAG = CashTrackActivity.class.getSimpleName();
//    public static final String VEHICLE_KEY = "vehicle_key";
    ActivityCashTrackBinding cashTrackBinding;
    public Vehicle vehicle;
    private String message;

    FragmentManager fragmentManager = getSupportFragmentManager();
    protected CashTrackFragment.OnBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cashTrackBinding = DataBindingUtil.setContentView(this, R.layout.activity_cash_track);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.TOP);
            slide.addTarget(R.id.header_cardView);
            slide.addTarget(R.id.recyclerView_cashTrack);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in));
            slide.setDuration(30000);
            getWindow().setEnterTransition(slide);
        }

        getDataFromMainActivity();
        if (savedInstanceState == null){
            displayCashTrackFragment();
            Log.w(LOG_TAG, "savedInstanceState NULL: ");

        }
    }

    private void getDataFromMainActivity() {
        //get vehicle_key value from uri path
        if (getIntent().getExtras() != null) {
            vehicle = getIntent().getParcelableExtra(VEHICLE_KEY);
            Log.w(LOG_TAG, "getDataFromMainActivity " + vehicle.get_id() + " " + vehicle.getRegistration());
        } else {
            Log.w(LOG_TAG, "getDataFromMainActivity IS NULL" + vehicle.get_id());
        }
    }

    public void displayCashTrackFragment() {
        CashTrackFragment cashTrackFragment = CashTrackFragment.newInstance(vehicle);
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_cash_track);

        if (fragment == null) {

            fragmentManager.beginTransaction()
                    .add(R.id.fragment_cash_track, cashTrackFragment)
                    .commit();

            Log.w(LOG_TAG, "add: " + vehicle.get_id());
        } else {

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_cash_track, cashTrackFragment)
                    .addToBackStack(null)
                    .commit();
            Log.w(LOG_TAG, "replace: " + vehicle.getRegistration());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCashTrackFragmentInteraction(final Transaction transaction, CoordinatorLayout coordinatorLayout) {
        Log.w(LOG_TAG, "onCashTrackFragmentInteraction: " + transaction.getAmount());
        final Context context = getApplicationContext();
        message = getString(R.string.sending_amount_message) + transaction.getAmount();
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                LocalStore localStore = new LocalStore(context);
                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                        message = getString(R.string.sending_amount_message) + transaction.getAmount();
                        if (localStore.storeTransactionData(transaction)) {
                            showToast(context, message);
                            CashTrackSyncUtils.syncTransactions(getBaseContext(), transaction);
                        }
                        Log.w(LOG_TAG, "DISMISS_EVENT_SWIPE: " + transaction.getAmount());
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        Log.w(LOG_TAG, "DISMISS_EVENT_ACTION: ");
                        message = getString(R.string.cancelled_transaction);
                        showToast(context, message);
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                        //Post to server after 4000ms
                        Log.w(LOG_TAG, "DISMISS_EVENT_SWIPE: ");
                        message = getString(R.string.sending_amount_message) + transaction.getAmount();
                        if (localStore.storeTransactionData(transaction)) {
                            showToast(context, message);
                            CashTrackSyncUtils.syncTransactions(getBaseContext(), transaction);
                        }
                        break;
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        }).setAction("Undo! ", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        }).setDuration(5000)
                .show();
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void setOnBackPressedListener(CashTrackFragment.OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.onBackPressed();
        else
            super.onBackPressed();
        Log.w(LOG_TAG, "onBackPressed(): ");
    }
}
