package sledgehammerlabs.just_in;

import java.lang.Object;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONObject;

public class Test extends Activity
{
    public JSONObject json = new JSONObject();
    private boolean GHPushable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void DoStuff()
    {
    }
}
