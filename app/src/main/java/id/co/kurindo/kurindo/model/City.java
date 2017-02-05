package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DwiM on 11/8/2016.
 */
public class City implements Parcelable {
    public static final String KEY_CODE = "code";
    public static final String KEY_TEXT = "text";

    private String code;
    private String text;

    public City(String code, String text){
        this.code = code;
        this.text = text;
    }
    public City(JSONObject object){
        try {
            this.code = object.getString("code");
            this.text= object.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected City(Parcel in) {
        code = in.readString();
        text = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static ArrayList<City> fromJson(JSONArray jsonObjects) {
        ArrayList<City> cities = new ArrayList<City>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                cities.add(new City(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cities;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( code);
        dest.writeString( text);
    }

    @Override
    public String toString() {
        return text;
    }
}
