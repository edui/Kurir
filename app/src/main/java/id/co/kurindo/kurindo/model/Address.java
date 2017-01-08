package id.co.kurindo.kurindo.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DwiM on 11/12/2016.
 */
public class Address implements Parcelable{

    private String alamat;
    private String rt;
    private String rw;
    private String dusun;
    private String desa;
    private String kecamatan;
    private String kabupaten;
    private String propinsi;
    private String negara;
    private String kodepos;
    private Location location;

    private City city; //code

    public Address(){

    }
    protected Address(Parcel in) {
        alamat = in.readString();
        rt = in.readString();
        rw = in.readString();
        dusun = in.readString();
        desa = in.readString();
        kecamatan = in.readString();
        kabupaten= in.readString();
        propinsi= in.readString();
        negara= in.readString();
        kodepos= in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        city= in.readParcelable(City.class.getClassLoader());
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getRw() {
        return rw;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }

    public String getDusun() {
        return dusun;
    }

    public void setDusun(String dusun) {
        this.dusun = dusun;
    }

    public String getDesa() {
        return desa;
    }

    public void setDesa(String desa) {
        this.desa = desa;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getKabupaten() {
        return kabupaten;
    }

    public void setKabupaten(String kabupaten) {
        this.kabupaten = kabupaten;
    }

    public String getPropinsi() {
        return propinsi;
    }

    public void setPropinsi(String propinsi) {
        this.propinsi = propinsi;
    }

    public String getKodepos() {
        return kodepos;
    }

    public void setKodepos(String kodepos) {
        this.kodepos = kodepos;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getNegara() {
        return negara;
    }

    public void setNegara(String negara) {
        this.negara = negara;
    }


    public Map<String, String> getAsParams(String suffix){
        Map<String, String> params = new HashMap<String, String>();
        params.put("alamat"+suffix, alamat);
        params.put("rt"+suffix, rt);
        params.put("rw"+suffix, rw);
        params.put("dusun"+suffix, dusun);
        params.put("desa"+suffix, desa);
        params.put("kecamatan"+suffix, kecamatan);
        params.put("kabupaten"+suffix, kabupaten);
        params.put("propinsi"+suffix, propinsi);
        params.put("negara"+suffix, negara);
        params.put("kodepos"+suffix, kodepos);
        params.put("kota"+suffix, (city != null? city.getCode(): ""));
        params.put("kotaText"+suffix, (city != null? city.getText(): ""));
        params.put("lokasi"+suffix, (location != null? location.getLatitude()+","+location.getLongitude() : ""));

        return params;
    }

    @Override
    public int hashCode() {
        return (getAlamat() + ", "+getCity()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        Address a = ((Address)o);
        if(getAlamat().equals(a.getAlamat())){
            return getCity().equals(a.getCity());
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(alamat);
        dest.writeString(rt);
        dest.writeString(rw);
        dest.writeString(dusun);
        dest.writeString(desa);
        dest.writeString(kecamatan);
        dest.writeString(kabupaten);
        dest.writeString(propinsi);
        dest.writeString(negara);
        dest.writeString(kodepos);
        dest.writeParcelable(location, flags);
        dest.writeParcelable(city, flags);
    }

    public String toStringFormatted() {
        String formatted = "";
        formatted += alamat;
        formatted += "\n"+city;
        return formatted;
    }
}
