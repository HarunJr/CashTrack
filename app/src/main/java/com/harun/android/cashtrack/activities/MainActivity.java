package com.harun.android.cashtrack.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.example.android.background.BaseActivity;
import com.example.android.background.sync.CashTrackSyncUtils;
import com.example.android.cashtracklibrary.CashTrackActivity;
import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.Vehicle;
import com.harun.android.cashtrack.CashTrackService;
import com.harun.android.cashtrack.R;
import com.harun.android.cashtrack.databinding.ActivityMainBinding;
import com.harun.android.cashtrack.fragments.VehiclesFragment;
import com.harun.android.loginpin.AuthActivity;

import static com.example.android.background.sync.CashTrackSyncUtils.SYNC_VEHICLES;
import static com.example.android.modellibrary.model.User.TOKEN_KEY;
import static com.example.android.modellibrary.model.Vehicle.VEHICLE_KEY;
import static com.harun.android.cashtrack.activities.AddVehicleActivity.vehicleReceiver;
import static com.harun.android.loginpin.AuthActivity.EXTRA_INTENT;
import static com.harun.android.loginpin.AuthActivity.PASS_TOKEN;

public class MainActivity extends BaseActivity implements VehiclesFragment.OnVehicleInteractionListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static String AUTH_TOKEN = null;
    ActivityMainBinding mainBinding;
    LocalStore localStore;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        localStore = new LocalStore(this);

        String intentAction = getIntent().getAction();
        assert intentAction != null;
        selectAction(intentAction);
        initViews();
//        testFatalError();
        Log.w(LOG_TAG, "onCreate: ");
    }

    private void selectAction(String action) {
        switch (action) {
            case PASS_TOKEN:
                if (getTokenFromLogin()) {
                    CashTrackSyncUtils.syncVehicles(this.getBaseContext(), vehicleReceiver, null);
                    CashTrackSyncUtils.syncTransactions(this.getBaseContext(), null);
                }
                Log.w(LOG_TAG, "FROM LOGIN:");
            case SYNC_VEHICLES:
                Log.w(LOG_TAG, "VEHICLES ENTERED SUCCESSFULLY");
                break;
            default:
                Log.w(LOG_TAG, "DEFAULT:");
                if (getTokenFromLogin()) {
                    CashTrackSyncUtils.syncVehicles(this.getBaseContext(), vehicleReceiver, null);
                    CashTrackSyncUtils.syncTransactions(this.getBaseContext(), null);
                }
        }
    }

    private boolean getTokenFromLogin() {
        Bundle bundle = getIntent().getBundleExtra(EXTRA_INTENT);
        if (bundle != null) {
            AUTH_TOKEN = bundle.getString(TOKEN_KEY, "");
            localStore.storeToken(AUTH_TOKEN);
            Log.w(LOG_TAG, "getTokenFromLogin: " + AUTH_TOKEN);
            return true;
        } else {
            launchLoginPin();
            Log.w(LOG_TAG, "BUNDLE IS NULL!!: ");
            return false;
        }
    }

    private void launchLoginPin() {
        startActivity(new Intent(getBaseContext(), AuthActivity.class));
        finish();
    }

    private void initViews() {
        Toolbar mToolbar = getActivityToolbar();
        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getResources().getString(R.string.vehicle_list_title));
    }

    public Toolbar getActivityToolbar() {
        return (Toolbar) mainBinding.mainActivityToolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vehicles_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_vehicle:
                launchAddVehicleActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void launchAddVehicleActivity() {
        startActivity(new Intent(getApplicationContext(), AddVehicleActivity.class));
    }

    @Override
    public void onVehicleInteraction(Vehicle vehicle) {
        Log.w(LOG_TAG, "onVehicleInteraction: " + vehicle.getRegistration());
        //store recipe to access from anywhere e.g widget
        LocalStore localStore = new LocalStore(this);
        localStore.storeVehiclePreference(vehicle);

        //update widget with recipe
        CashTrackService.startActionUpdateTransactionWidget(this);

        launchCashTrackActivity(vehicle);
    }

    public void launchCashTrackActivity(Vehicle vehicle) {
        Bundle bundle = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
             bundle = ActivityOptions
                    .makeSceneTransitionAnimation(this)
                    .toBundle();
        }
        startActivity(new Intent(getApplicationContext(), CashTrackActivity.class)
                .putExtra(VEHICLE_KEY, vehicle), bundle);
    }

//    @Override
//    public void onBackPressed() {
//        //do nothing
//        if (exit) {
//            finish(); // finish activity
//        } else {
//            Toast.makeText(this, getString(R.string.back_press_message),
//                    Toast.LENGTH_SHORT).show();
//            exit = true;
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    exit = false;
//                }
//            }, 3 * 1000);
//        }
//    }

    //Test crashlytics
    private void testFatalError() {
        Crashlytics.getInstance().crash();
    }

}
