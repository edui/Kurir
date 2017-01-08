package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by DwiM on 12/7/2016.
 */

public class User implements Parcelable{
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String gender = "LAKI-LAKI";
    private String uid;
    private String role;
    private String city;
    private String active;
    private String approved;
    private String created_at;
    private String api_key;
    private String cityText;
    private Address address;

    public User(){

    }

    protected User(Parcel in) {
        id = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        email = in.readString();
        phone = in.readString();
        gender = in.readString();
        uid = in.readString();
        role = in.readString();
        city = in.readString();
        active = in.readString();
        approved = in.readString();
        created_at = in.readString();
        api_key = in.readString();
        cityText = in.readString();
        try {
            address = in.readParcelable(Address.class.getClassLoader());
        }catch (Exception e){}
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getCityText() {
        return cityText;
    }

    public void setCityText(String cityText) {
        this.cityText = cityText;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(uid);
        dest.writeString(role);
        dest.writeString(city);
        dest.writeString(active);
        dest.writeString(approved);
        dest.writeString(created_at);
        dest.writeString(api_key);
        dest.writeString(cityText);
        if(address != null) dest.writeParcelable(address, flags);
    }
}
