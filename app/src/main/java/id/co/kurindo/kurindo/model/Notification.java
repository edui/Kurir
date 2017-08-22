package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DwiM on 7/13/2017.
 */

public class Notification implements Parcelable{
    private String status;
    private String awb;
    private String tag;
    private String action;
    private String kotaPengirim;
    private String kotaPenerima;
    private String message;
    private String price;
    private String cod;


    public Notification() {

    }
    protected Notification(Parcel in) {
        status = in.readString();
        awb = in.readString();
        tag = in.readString();
        action = in.readString();
        kotaPengirim = in.readString();
        kotaPenerima = in.readString();
        message = in.readString();
        price = in.readString();
        cod = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKotaPengirim() {
        return kotaPengirim;
    }

    public void setKotaPengirim(String kotaPengirim) {
        this.kotaPengirim = kotaPengirim;
    }

    public String getKotaPenerima() {
        return kotaPenerima;
    }

    public void setKotaPenerima(String kotaPenerima) {
        this.kotaPenerima = kotaPenerima;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(status);
        parcel.writeString(awb);
        parcel.writeString(tag);
        parcel.writeString(action);
        parcel.writeString(kotaPengirim);
        parcel.writeString(kotaPenerima);
        parcel.writeString(message);
        parcel.writeString(price);
        parcel.writeString(cod);
    }
}
