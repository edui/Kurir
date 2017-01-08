package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DwiM on 12/3/2016.
 */

public class Sender extends Recipient {
    public Map<String, String> getAsParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nama_pengirim", getName());
        params.put("telepon_pengirim", getTelepon());
        params.putAll(getAddress().getAsParams("_pengirim"));
        return params;
    }
}
