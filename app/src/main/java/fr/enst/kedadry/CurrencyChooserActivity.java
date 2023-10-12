package fr.enst.kedadry;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CurrencyChooserActivity extends AppCompatActivity {

    protected JSONObject _Rates;
    protected String _FromCurrency;
    protected String _ToCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_chooser);
    }

    @Override
    protected void onStart(){
        super.onStart();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            // do work in background
            _Rates = loadJSON();

            handler.post(() -> {
                // run on main thread, make the ok button clickable
                Button okButton = findViewById(R.id.currency_chooser_button);
                okButton.setEnabled(true);
            });
        });
    }

    private JSONObject loadJSON(){
        URL exchangeRatesURL = null;
        InputStream inputStream = null;

        try {
            exchangeRatesURL = new URL("https://perso.telecom-paristech.fr/eagan/class/igr201/data/rates_2017_11_02.json");
            inputStream = exchangeRatesURL.openStream();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            String line;
            while((line = reader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }
            String jsonString = stringBuilder.toString();
            return new JSONObject(jsonString);
        } catch (IOException e){
            System.err.println("Warning: could not read rates: " + e.getLocalizedMessage());
        } catch (JSONException e){
            System.err.println("Warning: could not parse rates: " + e.getLocalizedMessage());
        }
        return null;
    }

    public void updateCurrencySelector(View view){
        boolean checked = ((RadioButton) view).isChecked();
        if(!checked) return;

        if(view.getId() == R.id.from_dollars){
            _FromCurrency = "USD";
        }
        if(view.getId() == R.id.from_euros){
            _FromCurrency = "EUR";
        }
        if(view.getId() == R.id.from_pounds){
            _FromCurrency = "GBP";
        }

        if(view.getId() == R.id.to_dollars){
            _ToCurrency = "USD";
        }
        if(view.getId() == R.id.to_euros){
            _ToCurrency = "EUR";
        }
        if(view.getId() == R.id.to_pounds){
            _ToCurrency = "GBP";
        }
    }

    private Double getRate(){
        try{
            Double from = _Rates.getJSONObject("rates").getDouble(_FromCurrency);
            Double to   = _Rates.getJSONObject("rates").getDouble(_ToCurrency);
            return (1.0/from)*to;
        } catch (JSONException e){
            System.err.println("Warning: could not get the rate from the JSON file: " + e.getLocalizedMessage());
        }
        return -1.0;
    }

    public void getRate(View sender){
        Double rate = getRate();
        final Intent editIntent = new Intent(this, MainActivity.class);
        editIntent.putExtra(RateContract.MESSAGE, rate.toString() + " " + _FromCurrency + " " + _ToCurrency);
        setResult(Activity.RESULT_OK, editIntent);
        finish();
    }
}