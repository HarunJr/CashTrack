package com.example.android.cashtracklibrary;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.android.cashtracklibrary.databinding.ItemTransactionsBinding;
import com.example.android.modellibrary.AmountFormatHelper;
import com.example.android.modellibrary.DateHelper;
import com.example.android.modellibrary.model.Transaction;

import static com.example.android.modellibrary.SystemQuery.COL_AMOUNT;
import static com.example.android.modellibrary.SystemQuery.COL_DESCRIPTION;
import static com.example.android.modellibrary.SystemQuery.COL_TIME_STAMP;
import static com.example.android.modellibrary.SystemQuery.COL_TYPE;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_KEY;
import static com.example.android.modellibrary.SystemQuery.INCOME_TAG;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>{
    private static final String LOG_TAG = TransactionsAdapter.class.getSimpleName();
    private static final int TYPE_ITEM = 0;
    private Cursor mCursor;
    private Context mContext;

    TransactionsAdapter(Context mContext){
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemTransactionsBinding mBinding = ItemTransactionsBinding.inflate(layoutInflater, parent, false);
        return new TransactionsViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {
        Transaction transaction = getTransactionData(position);
        holder.bind(transaction);
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

    public class TransactionsViewHolder extends ViewHolder {
        final ItemTransactionsBinding mBinding;

        TransactionsViewHolder(ItemTransactionsBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void bind(Transaction transaction){
            String formattedAmount;
            if (!transaction.getType().equals(INCOME_TAG)){
                formattedAmount = String.valueOf(transaction.getAmount());
            }else {
                formattedAmount = AmountFormatHelper.getFormattedIncome(mContext, transaction.getAmount());
            }
            mBinding.transactionLayout.itemTransactionDescription.setText(transaction.getDescription());
            mBinding.transactionLayout.itemTransactionDateTime.setText(transaction.getUpdated_at());
            mBinding.transactionLayout.itemTransactionAmount.setText(formattedAmount);
            Log.w(LOG_TAG, "bind: " + transaction.getAmount() + ">>" + transaction.getDescription() + ">>" + transaction.getUpdated_at());
        }
    }

    private Transaction getTransactionData(int position) {
        mCursor.moveToPosition(position);

        String vehicleId = mCursor.getString(COL_VEHICLE_KEY);
        double amount = mCursor.getDouble(COL_AMOUNT);
        String type = mCursor.getString(COL_TYPE);
        String description = mCursor.getString(COL_DESCRIPTION);
        long timestamp = mCursor.getLong(COL_TIME_STAMP);
        Log.w(LOG_TAG, "onBindViewHolder: " + vehicleId + ">>" + amount + ">>" + description+ ">>" + timestamp);

        String updatedDate = DateHelper.getFormattedTimeString(timestamp);

        return new Transaction(amount, type, description, updatedDate);
    }

    public interface TransactionAdapterOnClickHandler {
        void onItemClicked(Transaction transaction, TransactionsViewHolder vh);
    }

}
