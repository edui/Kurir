package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DwiM on 12/3/2016.
 */

public class Recipient implements Parcelable{
    private String name;
    private String telepon;
    private String gender;
    private Address address;
    private double cost;
    private String status;

    public Recipient(){}
    public Recipient(String name, String telepon, String gender, Address address){
        this.name = name;
        this.telepon = telepon;
        this.gender = gender;
        this.address = address;
    }

    protected Recipient(Parcel in) {
        name = in.readString();
        telepon = in.readString();
        gender = in.readString();
        cost = in.readDouble();
        status = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
    }

    public static final Creator<Recipient> CREATOR = new Creator<Recipient>() {
        @Override
        public Recipient createFromParcel(Parcel in) {
            return new Recipient(in);
        }

        @Override
        public Recipient[] newArray(int size) {
            return new Recipient[size];
        }
    };

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = hash * prime + (getName() == null ? 0 : getName().hashCode());
        hash = hash * prime + (getTelepon() == null ? 0 : getTelepon().hashCode());
        hash = hash * prime + (getAddress() == null ? 0 : getAddress().hashCode());

        return hash;
    }

    @Override
    public boolean equals(Object o) {
        Recipient r = (Recipient)o;
        if(getName().equals(r.getName())){
            if(getTelepon().equals(r.getTelepon())){
                return getAddress().equals(r.getAddress());
            }
        }
        return false;
    }

    public Map<String, String> getAsParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nama_penerima", name);
        params.put("telepon_penerima", telepon);
        params.put("gender_penerima", telepon);
        params.put("cost_penerima", ""+cost);
        params.putAll(getAddress().getAsParams("_penerima"));
        return params;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(telepon);
        dest.writeString(gender);
        dest.writeDouble(cost);
        dest.writeString(status);
        dest.writeParcelable(address,flags);
    }
}
