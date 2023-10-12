package fr.enst.kedadry;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> _CurrencyChooserLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init the launcher
        _CurrencyChooserLauncher = registerForActivityResult(new RateContract(),
                result -> {
                    if (result != null){
                        // result = rate + from + to
                        String[] parts = result.split(" ");
                        String rate = parts[0];
                        String from = parts[1];
                        String to   = parts[2];
                        // get the elements
                        EditText editFrom = findViewById(R.id.edit_from);
                        EditText editRate = findViewById(R.id.edit_rate);
                        EditText editTo   = findViewById(R.id.edit_to);
                        TextView textFrom = findViewById(R.id.from_currency_label);
                        TextView textTo   = findViewById(R.id.to_currency_label);
                        // update the text elements
                        editRate.setText(rate);
                        editFrom.setHint(from);
                        editFrom.setText("");
                        textFrom.setText(from);
                        editTo.setHint(to);
                        editTo.setText("");
                        textTo.setText(to);
                    }
                });
    }

    private boolean isEmpty(EditText text){
        return text.getText().toString().length() == 0;
    }

    public void convert(View sender){
        EditText editFrom = findViewById(R.id.edit_from);
        EditText editRate = findViewById(R.id.edit_rate);
        EditText editTo   = findViewById(R.id.edit_to);

        if(isEmpty(editFrom) || isEmpty(editRate)){
            return;
        }

        Double from = Double.parseDouble(editFrom.getText().toString());
        Double rate = Double.parseDouble(editRate.getText().toString());
        Double to   = from*rate;

        editTo.setText(to.toString());
    }

    public void changeRate(View sender){
        _CurrencyChooserLauncher.launch(""); // Data to send to the second activity
    }
}