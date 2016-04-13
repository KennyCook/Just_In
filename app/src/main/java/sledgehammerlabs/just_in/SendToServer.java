package sledgehammerlabs.just_in;

import android.util.JsonReader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONException;

public class SendToServer {
    public void SendOut(){
        boolean success = false;

        JSONObject json = new JSONObject();
        URL url;
        HttpURLConnection urlConnection;
        DataOutputStream printout;

        try{
            //url for register
            String http = "http://justin.sledgehammerlabs.com/pins";

            //make url object
            url = new URL (http);
            //open the connection, set methods and stuff
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");

            //Creating the JSON object, it's basically a hash map
            //json.put(KEY, VALUE)
            try {
                json.put("UserID", 69);
                json.put("Longitude", 4);
                json.put("Latitude", 12);
                json.put("Description", "this is my pin description");
                json.put("Category", 1);
            }catch(JSONException e){
                e.printStackTrace();
            }
            printout = new DataOutputStream(urlConnection.getOutputStream ());

            String str = json.toString();
            byte[] data=str.getBytes("UTF-8");
            printout.write(data);
            printout.flush ();
            printout.close ();

            //if(urlConnection.getResponseCode() == SUCCESS_CODE)
            //    success = true;

            System.out.print("Json String:   " + json.toString());
            System.out.print("\nResponse code: " + urlConnection.getResponseCode() + "\n");

            urlConnection.disconnect();

        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: 11/16/2015 test this


    }
}
