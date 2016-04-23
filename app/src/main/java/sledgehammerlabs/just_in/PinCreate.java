package sledgehammerlabs.just_in;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PinCreate extends AppCompatActivity
{
    private Spinner categorySpinner;
    private Spinner timeToLiveSpinner;
    private EditText descriptionField;

    private int  categorySelected, timeToLiveSelected;
    private double userLat, userLong;
    private final int DEFAULT_TIME_TO_LIVE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_create);

        Intent intent = getIntent();
        double[] userPos = intent.getDoubleArrayExtra("userPos");
        userLat = userPos[0];
        userLong = userPos[1];

        descriptionField = (EditText) findViewById(R.id.pin_description_edittext);

        fillCategorySpinner();
        addListenerToCategorySpinner();

        fillTimeToLiveSpinner();
        addListenerToTimeToLiveSpinner();
    }

    //Populate drop down box via an ArrayAdapter filled
    //      with categories listed in categories.xml
    private void fillCategorySpinner()
    {
        categorySpinner = (Spinner)
                findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence> categorySpinnerAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.categories,
                        android.R.layout.simple_spinner_item);

        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        categorySpinner.setAdapter(categorySpinnerAdapter);
    }

    //Listens for a category selection from the user
    private void addListenerToCategorySpinner()
    {
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySelected = parent.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categorySelected = 0;
            }
        });
    }

    //Fill time_to_live_spinner with values from the
    //  lifetime.xml file
    private void fillTimeToLiveSpinner()
    {
        timeToLiveSpinner = (Spinner) findViewById(R.id.time_to_live_spinner);

        ArrayAdapter<CharSequence> timeToLiveSpinnerAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.lifetimes,
                        android.R.layout.simple_spinner_item);

        timeToLiveSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        timeToLiveSpinner.setAdapter(timeToLiveSpinnerAdapter);
    }

    //Adds listener to the time_to_live_spinner and
    //  stores the selection in timeToLiveSelected
    private void addListenerToTimeToLiveSpinner()
    {
        timeToLiveSpinner = (Spinner) findViewById(R.id.time_to_live_spinner);

        timeToLiveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeToLiveSelected = parent.getSelectedItemPosition();
           }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                timeToLiveSelected = DEFAULT_TIME_TO_LIVE;
            }
        });
    }

    public void SubmitPinCreate(View view)
    {
        final String pinDescription = descriptionField.getText().toString();

        // TODO: test this, test to see how many the server can get (multiple threads, multiple sends)
        final SendToServer sender = new SendToServer();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    sender.SendPin(userLat, userLong, categorySelected, timeToLiveSelected, pinDescription);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.finish();
    }

    public void CancelPinCreate(View view)
    {
        this.finish();
    }
}
