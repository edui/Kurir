package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dwim on 2/9/2017.
 */

public class TPacket  implements Parcelable {
    private TUser origin;
    private TUser destination;
    private double distance;
    private BigDecimal berat_asli;
    private int berat_kiriman;
    private String isi_kiriman;
    private BigDecimal biaya;
    private double volume;
    private String catatan;
    private String received_by;
    private String received_date;

    public TPacket(){
    }
    protected TPacket(Parcel in) {
        distance = in.readDouble();
        berat_asli = new BigDecimal( in.readDouble() );
        berat_kiriman = in.readInt();
        isi_kiriman = in.readString();
        biaya = new BigDecimal( in.readDouble() );
        volume = in.readDouble();
        try {
            origin = in.readParcelable(Address.class.getClassLoader());
        }catch (Exception e){}
        try {
            destination = in.readParcelable(Address.class.getClassLoader());
        }catch (Exception e){}
        received_by = in.readString();
        received_date = in.readString();
        catatan = in.readString();

    }


    public static final Creator<TPacket> CREATOR = new Creator<TPacket>() {
        @Override
        public TPacket createFromParcel(Parcel in) {
            return new TPacket(in);
        }

        @Override
        public TPacket[] newArray(int size) {
            return new TPacket[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(distance);
        dest.writeDouble( berat_asli == null ? 0 :berat_asli.doubleValue());
        dest.writeInt(berat_kiriman);
        dest.writeString(isi_kiriman);
        dest.writeDouble( biaya == null ? 0 :biaya.doubleValue());
        dest.writeDouble(volume);
        if(origin != null) dest.writeParcelable(origin, flags);
        if(destination != null) dest.writeParcelable(destination, flags);
        dest.writeString(received_by);
        dest.writeString(received_date);
        dest.writeString(catatan);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public TUser getOrigin() {
        return origin;
    }

    public void setOrigin(TUser origin) {
        this.origin = origin;
    }

    public TUser getDestination() {
        return destination;
    }

    public void setDestination(TUser destination) {
        this.destination = destination;
    }

    public int getBerat_kiriman() {
        return berat_kiriman;
    }

    public void setBerat_kiriman(int berat_kiriman) {
        this.berat_kiriman = berat_kiriman;
    }

    public String getIsi_kiriman() {
        return isi_kiriman;
    }

    public void setIsi_kiriman(String isi_kiriman) {
        this.isi_kiriman = isi_kiriman;
    }

    public BigDecimal getBerat_asli() {
        return berat_asli;
    }

    public void setBerat_asli(BigDecimal berat_asli) {
        this.berat_asli = berat_asli;
    }

    public BigDecimal getBiaya() {
        return biaya;
    }

    public void setBiaya(BigDecimal biaya) {
        this.biaya = biaya;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getReceived_by() {
        return received_by;
    }

    public void setReceived_by(String received_by) {
        this.received_by = received_by;
    }

    public String getReceived_date() {
        return received_date;
    }

    public void setReceived_date(String received_date) {
        this.received_date = received_date;
    }

    public Map<String, String> getAsParams() {
        HashMap<String, String> params = new HashMap<>();

        return params;
    }
}
