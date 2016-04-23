package sledgehammerlabs.just_in;

/**
 * Created by Matt on 4/15/2016.
 */

public class PinModel {

    private int pinID;
    private double longitude;
    private double latitude;
    private int category;
    private int score;

    public PinModel(){

    }

    public PinModel(int id, double _long, double _lat, int cat, int s){
        this.pinID = id;
        this.longitude = _long;
        this.latitude = _lat;
        this.category = cat;
        this.score = s;

    }

    public void setPinID(int id){
        this.pinID = id;
    }

    public int getPinID() {
        return this.pinID;
    }

    public void setLongitude(double _long) {
        this.longitude = _long;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLatitude(double _lat) {
        this.latitude = _lat;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setCategory(int cat) {
        this.category = cat;
    }

    public int getCategory(){
        return this.category;
    }

    public void setScore(int s) {
        this.score = s;
    }

    public int getScore() {
        return this.score;
    }


}
