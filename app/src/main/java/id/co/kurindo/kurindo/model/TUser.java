package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by dwim on 2/9/2017.
 */

public class TUser implements Parcelable, Cloneable {
    @Expose
    private String firstname;
    @Expose
    private String lastname;

    private String email;
    @Expose
    private String phone;
    @Expose
    private String gender = "DEFAULT";
    @Expose
    private Address address;

    private String role;
    private String nik;
    private String simc;

    private boolean active;
    private boolean approved;
    private String created_at;

    private String api_key;

    public TUser() {
        address = new Address();
    }
    public TUser(Parcel in) {
        firstname = in.readString();
        lastname = in.readString();
        email = in.readString();
        phone = in.readString();
        gender = in.readString();
        role = in.readString();
        active = Boolean.parseBoolean( in.readString() );
        approved = Boolean.parseBoolean( in.readString() );
        created_at = in.readString();
        api_key = in.readString();
        try {
            address = in.readParcelable(Address.class.getClassLoader());
        }catch (Exception e){}
        nik = in.readString();
        simc = in.readString();
    }
    public static final Creator<TUser> CREATOR = new Creator<TUser>() {
        @Override
        public TUser createFromParcel(Parcel in) {
            return new TUser(in);
        }

        @Override
        public TUser[] newArray(int size) {
            return new TUser[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(role);
        dest.writeString(Boolean.toString(active));
        dest.writeString(Boolean.toString(approved));
        dest.writeString(created_at);
        dest.writeString(api_key);
        if(address != null) dest.writeParcelable(address, flags);
        dest.writeString(nik);
        dest.writeString(simc);
    }
    public String getName(){
        return (getFirstname() == null? "": getFirstname()) + " "+ (getLastname() == null ? "": getLastname());
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getSimc() {
        return simc;
    }

    public void setSimc(String simc) {
        this.simc = simc;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
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

    public String toStringFormatted(){
        String formatted = "";
        formatted += getName();
        if(phone != null) formatted += ", "+phone;
        if(email != null) formatted += ", "+email;
        return formatted;
    }

    @Override
    public TUser clone() throws CloneNotSupportedException {
        return (TUser) super.clone();
    }
}
