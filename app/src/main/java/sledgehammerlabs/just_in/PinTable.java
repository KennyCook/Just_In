package sledgehammerlabs.just_in;

/**
 * Created by Matt on 4/15/2016.
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;


public class PinTable extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Just_In_DB";
    private static final String TABLE_PIN = "PinTable";

    public static final String PIN_PIN_ID = "pinID";
    public static final String PIN_LONGITUDE = "longitude";
    public static final String PIN_LATITUDE = "latitude";
    public static final String PIN_CATEGORY = "category";
    public static final String PIN_SCORE = "score";

    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;


    public PinTable(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PIN + "(" + PIN_PIN_ID + " INTEGER PRIMARY KEY, " +
                PIN_LONGITUDE + " REAL," + PIN_LATITUDE + " REAL, " +
                PIN_CATEGORY + " INTEGER, " + PIN_SCORE + " INTEGER" + ")";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PIN);
    }

    public PinTable open(){
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    /*
     *  Adds a pin to the database with the pinID, longitude, latitude, category, score, and pin expiration time
     */

    public void addPin(PinModel pin){
        ContentValues values = new ContentValues();
        values.put(PIN_PIN_ID, pin.getPinID());
        values.put(PIN_LONGITUDE, pin.getLongitude());
        values.put(PIN_LATITUDE, pin.getLatitude());
        values.put(PIN_CATEGORY, pin.getCategory());
        values.put(PIN_SCORE, pin.getScore());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PIN, null, values);
        db.close();
    }

    public PinModel findPin(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PIN, new String[]{PIN_PIN_ID,
                        PIN_LONGITUDE, PIN_LATITUDE, PIN_CATEGORY, PIN_SCORE},
                PIN_PIN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Log.d("CURSOR", "Cursor != null");
        }

        PinModel pin = new PinModel(Integer.parseInt(cursor.getString(0)),
                Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)),
                Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)));
        Log.d("PIN", "Pin contents: " + pin.getPinID() + pin.getLatitude() + pin.getLongitude() +
                pin.getCategory() + pin.getScore());

        cursor.close();

        db.close();
        return pin;
    }

    public ArrayList<PinModel> findMultiPin(ArrayList<PinModel> yuck) {
        //int id;
        SQLiteDatabase db = this.getWritableDatabase();
        int index = 0;
        PinModel pin  = new PinModel();
        ArrayList<PinModel> elephant = new ArrayList<>();
        while(!yuck.isEmpty()){
            String query = "SELECT * FROM " + TABLE_PIN + " WHERE " + PIN_PIN_ID + " = \"" + yuck.get(index).getPinID() + "\"";
            Cursor cursor = db.rawQuery(query, null);

            if(cursor.moveToFirst()) {
                cursor.moveToFirst();
                pin.setPinID(Integer.parseInt(cursor.getString(0)));
                pin.setLongitude(Double.parseDouble(cursor.getString(1)));
                pin.setLatitude(Double.parseDouble(cursor.getString(2)));
                pin.setCategory(Integer.parseInt(cursor.getString(3)));
                pin.setScore(Integer.parseInt(cursor.getString(4)));

                cursor.close();

            } else {
                pin = null;
            }
        }
        return elephant;
    }


    public boolean deletePin(int id) {

        boolean result = false;

        String query = "SELECT * FROM " + TABLE_PIN + " WHERE " +
                PIN_PIN_ID + " = \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PinModel pin = new PinModel();

        if(cursor.moveToFirst()) {
            pin.setPinID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_PIN, PIN_PIN_ID + " = ?",
                    new String[] { String.valueOf(pin.getPinID())});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
}
