package sledgehammerlabs.just_in;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PinCreate extends Activity
{
    private Spinner categorySpinner;
    private EditText descriptionField;

    private String categorySelected;
    private double myLatitude;
    private double myLongitude;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_create_screen);

        descriptionField = (EditText) findViewById(R.id.pin_description_edittext);

        fillCategorySpinner();
        addListenerToCategorySpinner();
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
                categorySelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void SubmitPin(){}

    private void CancelPin(){}
}
