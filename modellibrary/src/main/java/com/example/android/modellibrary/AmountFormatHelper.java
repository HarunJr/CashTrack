package com.example.android.modellibrary;

import android.content.Context;

public class AmountFormatHelper {

    public static String getFormattedIncome(Context context, double amount){
        return context.getString(R.string.format_amount, amount);
    }
    public static String getFormattedExpense(Context context, double amount){
        return context.getString(R.string.format_expense, amount);
    }
}
