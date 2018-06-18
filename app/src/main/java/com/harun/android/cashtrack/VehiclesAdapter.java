package com.harun.android.cashtrack;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.modellibrary.AmountFormatHelper;
import com.example.android.modellibrary.model.Vehicle;
import com.harun.android.cashtrack.databinding.ItemVehiclesBinding;

import static com.example.android.modellibrary.SystemQuery.COL_UPDATE_TIME;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_COLLECTION;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_EXPENSE;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_ID;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_REGISTRATION;

public class VehiclesAdapter extends RecyclerView.Adapter<VehiclesAdapter.VehiclesViewHolder>{
    private static final String LOG_TAG = VehiclesAdapter.class.getSimpleName();
    final private VehicleAdapterOnClickHandler mClickHandler;
    private static final int TYPE_ITEM = 0;
    private Cursor mCursor;
    private Context mContext;
    private Vehicle vehicle;

    public VehiclesAdapter(Context mContext, VehicleAdapterOnClickHandler vehicleAdapterOnClickHandler){
        this.mContext = mContext;
        this.mClickHandler = vehicleAdapterOnClickHandler;
    }

    @NonNull
    @Override
    public VehiclesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemVehiclesBinding mBinding = ItemVehiclesBinding.inflate(layoutInflater, parent, false);
        return new VehiclesViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiclesViewHolder holder, int position) {
        vehicle = getVehicleData(position);
        holder.bind(vehicle);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        Log.w(LOG_TAG, "getItemViewType: " + position);
        return TYPE_ITEM;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
//        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public class VehiclesViewHolder extends ViewHolder implements View.OnClickListener{
        final ItemVehiclesBinding mBinding;

        public VehiclesViewHolder(ItemVehiclesBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            itemView.setOnClickListener(this);
        }

        public void bind(Vehicle vehicle){

            mBinding.vehicleLayout.itemVehicleReg.setText(vehicle.getRegistration());
            mBinding.vehicleLayout.itemVehicleCollection.setText(AmountFormatHelper.getFormattedIncome(mContext, vehicle.getCollection()));
            mBinding.vehicleLayout.itemVehicleExpense.setText(String.valueOf(vehicle.getExpense()));
            mBinding.vehicleLayout.itemVehicleDateTime.setText(vehicle.getUpdated_at());
            Log.w(LOG_TAG, "bind: " + vehicle.getRegistration() + ">>" + vehicle.getCollection() + ">>" + vehicle.getExpense() + ">>" + vehicle.getUpdated_at());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            vehicle = getVehicleData(adapterPosition);

            Log.w(LOG_TAG, "onItemClicked: "+ vehicle.getRegistration());
            mClickHandler.onItemClicked(vehicle, this);
        }
    }

    private Vehicle getVehicleData(int position) {
        mCursor.moveToPosition(position);

        int vehicleId = mCursor.getInt(COL_VEHICLE_ID);
        String vehicleReg = mCursor.getString(COL_VEHICLE_REGISTRATION);
        double collection = mCursor.getDouble(COL_VEHICLE_COLLECTION);
        double expense = mCursor.getDouble(COL_VEHICLE_EXPENSE);
        String updatedDate = mCursor.getString(COL_UPDATE_TIME);
        Log.w(LOG_TAG, "onBindViewHolder: "+vehicleId+" " + vehicleReg + ">>" + collection + ">>" + expense + ">>" + updatedDate);

        return new Vehicle(vehicleId, vehicleReg, collection, expense, updatedDate);
    }

    public interface VehicleAdapterOnClickHandler {
        void onItemClicked(Vehicle vehicle, VehiclesViewHolder vh);
    }

}
