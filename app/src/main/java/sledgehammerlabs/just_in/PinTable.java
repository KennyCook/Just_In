package sledgehammerlabs.just_in;

/**
 * Created by Matt on 4/15/2016.
 *
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;


public class PinTable extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Just_In_DB";
    private static final String TABLE_PIN = "PinTable";

    public static final String PIN_PIN_ID = "pinID";
    public static final String PIN_LONGITUDE = "longitude";
    public static final String PIN_LATITUDE = "latitude";
    public static final String PIN_CATEGORY = "category";
    public static final String PIN_SCORE = "score";

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
    /*
     *  Adds a pin to the database with the pinID, longitude, latitude, category, score, and pin expiration time
     *
     */
    public boolean addPin(PinModel pin){
        ContentValues values = new ContentValues();
        values.put(PIN_PIN_ID, pin.getPinID());
        values.put(PIN_LONGITUDE, pin.getLongitude());
        values.put(PIN_LATITUDE, pin.getLatitude());
        values.put(PIN_CATEGORY, pin.getCategory());
        values.put(PIN_SCORE, pin.getScore());

        SQLiteDatabase db = this.getWritableDatabase();
        if (db.insert(TABLE_PIN, null, values) == -1 )
        {
            db.close();
            return false;
        }
        else
        {
            db.close();
            return true;
        }
    }

    public PinModel findPin(int id) {
//        String query = "SELECT * FROM " + TABLE_PIN + " WHERE " + PIN_PIN_ID + " = \"" + id + "\"";
//        SQLiteDatabase db = this.getReadableDatabase();
//        //Think the error is coming here
//        //  Try using regular query
//        Cursor cursor = db.rawQuery(query, null);

        String query = "SELECT * FROM " + TABLE_PIN + " WHERE " + PIN_PIN_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        //Think the error is coming here
        // Try using regular query
        Cursor cursor = db.rawQuery(query, new String[] {Integer.toString(id)});

        PinModel pin  = new PinModel();
        if(cursor.moveToFirst()) {
            cursor.moveToFirst();
            pin.setPinID(Integer.parseInt(cursor.getString(0)));
            pin.setLongitude(Double.parseDouble(cursor.getString(1)));
            pin.setLatitude(Double.parseDouble(cursor.getString(2)));
            pin.setCategory(Integer.parseInt(cursor.getString(3)));
            pin.setScore(Integer.parseInt(cursor.getString(4)));
            Log.d("CURSOR_TAG", "Cursor at first");

            cursor.close();
        }
        else {
            pin = null;
            Log.d("CURSOR_TAG", "Cursor not at first");
        }
        db.close();
        return pin;
    }

    //Need a method to get a list of all pins in the table
    //  just need the id, lat and long of each pin
    //  return will probably be a list of PinModels

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

    public boolean deleteTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db.delete(TABLE_PIN, "WHERE", null) == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

}
