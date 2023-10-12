package fr.enst.kedadry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RateContract extends ActivityResultContract<String, String> {

    public static final String MESSAGE = "new_rate";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String s) {
        return new Intent(context, CurrencyChooserActivity.class); // Pass data to the currency chooser
    }

    @Override
    public String parseResult(int i, @Nullable Intent intent) {
        if(i != Activity.RESULT_OK) return null;
        return intent.getStringExtra(MESSAGE); // Get result from the currency chooser
    }
}
