package sledgehammerlabs.just_in;

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
    public void SendPin(double latitude, double longitude, int category, int timeToLive, String description){
        double userLat = latitude, userLong = longitude;
        int pinCategory = category, pinTimeToLive = timeToLive;
        String pinDescription = description;

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

            //TODO: Finish with userID
            //Creating the JSON object, it's basically a hash map
            //json.put(KEY, VALUE)
            try {
                json.put("UserID", 69);
                json.put("Longitude", userLong);
                json.put("Latitude", userLat);
                json.put("Description", pinDescription);
                json.put("Category", pinCategory);
                json.put("PinExpiration", pinTimeToLive);
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
    }

    public void SendVote(int pinID, int vote)
    {
        JSONObject json = new JSONObject();
        URL url;
        HttpURLConnection urlConnection;
        DataOutputStream printout;

        try{
            //url for register
            String http = "http://justin.sledgehammerlabs.com/votes";

            //make url object
            url = new URL (http);
            //open the connection, set methods and stuff
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");

            //TODO: Finish with userID
            //Creating the JSON object, it's basically a hash map
            //json.put(KEY, VALUE)
            try {
                json.put("UserID", 69);
                json.put("PinID", pinID);
                json.put("Vote", vote);
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
    }

    public void SendComment(int pinID, String comment)
    {
        JSONObject json = new JSONObject();
        URL url;
        HttpURLConnection urlConnection;
        DataOutputStream printout;

        try{
            //url for register
            String http = "http://justin.sledgehammerlabs.com/comments";

            //make url object
            url = new URL (http);
            //open the connection, set methods and stuff
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");

            //TODO: Finish with userID
            //Creating the JSON object, it's basically a hash map
            //json.put(KEY, VALUE)
            try {
                json.put("UserID", 69);
                json.put("PinID", pinID);
                json.put("Comment", comment);
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
    }
}
