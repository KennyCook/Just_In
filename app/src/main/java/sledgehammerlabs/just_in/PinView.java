package sledgehammerlabs.just_in;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class PinView extends AppCompatActivity implements OnMapReadyCallback
{
    private boolean upVoted, downVoted, reported;
    private String pinDescriptionText = "Testing, 1, 2, 3.", pinCategoryText = "Test";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_view);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView pinCategoryTV = (TextView) findViewById(R.id.pin_category);
        pinCategoryTV.setText(pinCategoryText);

        TextView pinDescriptionTV = (TextView) findViewById(R.id.pin_description_text);
        pinDescriptionTV.setText(pinDescriptionText);

        ImageView pinCategoryIcon = (ImageView) findViewById(R.id.category_icon);
        pinCategoryIcon.setImageResource(R.drawable.shopping);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // TODO: make map unclickable?

        DropLitePin();
    }

    // TODO: get lat long based on pin id from DB
    private void DropLitePin()
    {
        double pinLat, pinLng;

        Intent intent = getIntent();
        double[] pinPos = intent.getDoubleArrayExtra("pinPos");
        pinLat = pinPos[0];
        pinLng = pinPos[1];
        LatLng pinPosition = new LatLng(pinLat, pinLng);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(pinPosition));
        mMap.addMarker(new MarkerOptions().position(pinPosition));
    }

    // TODO: Disable and "fade" up vote button, add pinID to App Vote table
    public void OnUpVoteButtonPress(View viw)
    {
        final int upVote = 1;

        final SendToServer voteSender = new SendToServer();
        // TODO: test this, test to see how many the server can get (multiple threads, multiple sends)
        final SendToServer sender = new SendToServer();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    voteSender.SendVote(420, upVote);
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
    }

    // TODO: Disable and "fade" down vote button, add pinID to Appside Vote table
    public void OnDownVoteButtonPress(View viw)
    {
        final int downVote = -1;

        final SendToServer voteSender = new SendToServer();
        // TODO: test this, test to see how many the server can get (multiple threads, multiple sends)
        final SendToServer sender = new SendToServer();
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    voteSender.SendVote(420, downVote);
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
    }

    public void OnReportButtonPress(View viw){}

    // TODO: Add go to comment layout and move the sending code to that class
    public void OnCommentButtonPress(View viw)
    {
        final String comment = "This is the beeeeest comment ever";

        final SendToServer commentSender = new SendToServer();
        // TODO: test this, test to see how many the server can get (multiple threads, multiple sends)
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    commentSender.SendComment(420, comment);
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
    }

    public void OnShareButtonPress(View viw)
    {
        PinTable pinTable = new PinTable(this, "Just_In_DB", null, 1);
        PinModel test = pinTable.findPin(69);
        Toast.makeText(this, test.getPinID(), Toast.LENGTH_SHORT).show();
    }
}

// TODO: Add functionality to button press methods

