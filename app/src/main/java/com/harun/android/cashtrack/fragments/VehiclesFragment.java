package com.harun.android.cashtrack.fragments;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.Vehicle;
import com.harun.android.cashtrack.R;
import com.harun.android.cashtrack.VehiclesAdapter;
import com.harun.android.cashtrack.databinding.FragmentVehiclesBinding;

import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_ID;
import static com.example.android.modellibrary.SystemQuery.VEHICLE_COLUMN;
import static com.example.android.modellibrary.data.CashTrackContract.VehicleEntry;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnVehicleInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VehiclesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VehiclesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = VehiclesFragment.class.getSimpleName();
    public static final int VEHICLE_LOADER = 0;
    private static final String SCROLL_POSITION_KEY = "scroll_position";
    private static final String RECYCLERVIEW_STATE_KEY = "rv_state";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Context mContext;
    RecyclerView vehicleRecyclerView;
    VehiclesAdapter vehiclesAdapter;
    Parcelable mRecyclerViewState;
    LinearLayoutManager linearLayoutManager;
    int mScrollPosition = RecyclerView.NO_POSITION;

    private OnVehicleInteractionListener mListener;

    public VehiclesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VehiclesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VehiclesFragment newInstance(String param1, String param2) {
        VehiclesFragment fragment = new VehiclesFragment();
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
        FragmentVehiclesBinding vehiclesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicles, container, false);
        vehicleRecyclerView = vehiclesBinding.recyclerViewVehicles;
        vehicleRecyclerView.setHasFixedSize(true);

        vehicleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        vehiclesAdapter = new VehiclesAdapter(mContext, new VehiclesAdapter.VehicleAdapterOnClickHandler() {
            @Override
            public void onItemClicked(Vehicle vehicle, VehiclesAdapter.VehiclesViewHolder vh) {
                Log.w(LOG_TAG, "onItemClicked: " + vehicle.getRegistration());
                onVehicleSelected(vehicle);
            }
        });

        vehicleRecyclerView.setAdapter(vehiclesAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLL_POSITION_KEY)) {
            mRecyclerViewState = savedInstanceState.getParcelable(RECYCLERVIEW_STATE_KEY);
            mScrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY);

            vehicleRecyclerView.scrollToPosition(mScrollPosition);
            Log.w(LOG_TAG, "state onRestoreInstanceState: " + mScrollPosition);
        }
        return vehiclesBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        linearLayoutManager = (LinearLayoutManager) vehicleRecyclerView.getLayoutManager();
        setScrollPosition(outState, linearLayoutManager);
    }

    private void setScrollPosition(Bundle outState, LinearLayoutManager linearLayoutManager) {
        if (linearLayoutManager != null) {
            mRecyclerViewState = vehicleRecyclerView.getLayoutManager().onSaveInstanceState();
            mScrollPosition = linearLayoutManager.findFirstVisibleItemPosition();
            outState.putInt(SCROLL_POSITION_KEY, mScrollPosition);
            outState.putParcelable(RECYCLERVIEW_STATE_KEY, mRecyclerViewState);
            Log.w(LOG_TAG, "state onSaveInstanceState: mScrollPosition "+mScrollPosition);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onVehicleSelected(Vehicle vehicle) {
        if (mListener != null) {
            mListener.onVehicleInteraction(vehicle);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnVehicleInteractionListener) {
            mListener = (OnVehicleInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddVehicleListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);
        Log.w(LOG_TAG, "onActivityCreated");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String sortOrder = VehicleEntry.COLUMN_UPDATE_TIME + " DESC";
        return new CursorLoader(
                mContext,
                VehicleEntry.CONTENT_URI,
                VEHICLE_COLUMN,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            vehiclesAdapter.swapCursor(data);

//            LocalStore localStore = new LocalStore(mContext);
//            int vehicleId = data.getInt(COL_VEHICLE_ID);
//            Vehicle vehicle = localStore.getVehicleDataFromTransaction(vehicleId);
//            Log.w(LOG_TAG, "onLoadFinished: getVehicleById: "+ vehicle.get_id()+ " " + vehicle.getRegistration() + ", " + vehicle.getCollection());
//            localStore.updateVehiclesTable(vehicle);
        }


//        getLoaderManager().restartLoader(VEHICLE_LOADER, null, VehiclesFragment.this);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        vehiclesAdapter.swapCursor(null);

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
    public interface OnVehicleInteractionListener {
        // TODO: Update argument type and name
        void onVehicleInteraction(Vehicle vehicle);
    }
}
