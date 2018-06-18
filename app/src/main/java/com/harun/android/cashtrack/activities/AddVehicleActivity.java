package com.harun.android.cashtrack.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.android.background.BaseActivity;
import com.example.android.background.sync.CashTrackSyncUtils;
import com.example.android.modellibrary.CashTrackResultReceiver;
import com.example.android.modellibrary.Utilities;
import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.Vehicle;
import com.harun.android.cashtrack.R;
import com.harun.android.cashtrack.databinding.ActivityAddVehicleBinding;
import com.harun.android.cashtrack.fragments.AddVehicleFragment;

import static com.example.android.background.sync.CashTrackSyncUtils.SYNC_VEHICLES;

public class AddVehicleActivity extends BaseActivity implements AddVehicleFragment.OnAddVehicleListener, CashTrackResultReceiver.OnVehicleReceived {
    private static final String LOG_TAG = AddVehicleActivity.class.getSimpleName();
    ActivityAddVehicleBinding mAddVehicleBinding;
    public static CashTrackResultReceiver vehicleReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddVehicleBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_vehicle);
        vehicleReceiver = new CashTrackResultReceiver(new Handler());
        vehicleReceiver.setVehicleReceiver(this);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) mAddVehicleBinding.addVehicleToolbar;
        setSupportActionBar(mToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.add_vehicle));
    }

    @Override
    public void onAddButtonPressed(Vehicle vehicle) {
        Log.w(LOG_TAG, "onAddButtonPressed: " + vehicle.getRegistration());
        CashTrackSyncUtils.syncVehicles(getBaseContext(), vehicleReceiver, vehicle);
    }

    @Override
    public void onVehicleReceivedListener(Vehicle vehicle) {
        Log.w(LOG_TAG, "onVehicleReceivedListener ..." + vehicle.get_id() + "..." + vehicle.getRegistration());
        startMainActivity(vehicle);
        vehicleReceiver.setTokenReceiver(null);
    }

    protected void startMainActivity(Vehicle vehicle) {
        Log.w(LOG_TAG, "startMainActivity ...");
        LocalStore localStore = new LocalStore(this);
        if (localStore.storeVehicleData(vehicle)) {
            startActivity(new Intent(getBaseContext(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .setAction(SYNC_VEHICLES));
        }
    }
}
