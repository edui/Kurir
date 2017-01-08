package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DwiM on 11/12/2016.
 */
public class Packet implements Parcelable{
    private String resi;

    private String namaPengirim;
    private String teleponPengirim;
    private String alamatPengirim;
    private String kotaPengirim;
    private String kotaPengirimText;

    private String namaPenerima;
    private String teleponPenerima;
    private String alamatPenerima;
    private String kotaPenerima;
    private String kotaPenerimaText;

    private int berat;
    private String infoPaket;
    private boolean viaMobil;
    private double biaya;
    private String serviceCode;

    private String status;
    private String statusText;
    private String createdDate;
    private String updatedDate;

    private User kurir;
    private Order order;
    private List<StatusHistory> statusHistoryList =new ArrayList<>();

    public Packet(){

    }
    protected Packet(Parcel in) {
        resi = in.readString();
        namaPengirim = in.readString();
        teleponPengirim = in.readString();
        alamatPengirim = in.readString();
        kotaPengirim = in.readString();
        kotaPengirimText = in.readString();
        namaPenerima = in.readString();
        teleponPenerima = in.readString();
        alamatPenerima = in.readString();
        kotaPenerima = in.readString();
        kotaPenerimaText = in.readString();
        berat = in.readInt();
        infoPaket = in.readString();
        viaMobil = Boolean.parseBoolean( in.readString() );
        serviceCode= in.readString();
        status = in.readString();
        statusText = in.readString();
        biaya= in.readDouble();
        createdDate= in.readString();
        try {
            order = in.readParcelable(Order.class.getClassLoader());
        }catch (Exception e){}
        statusHistoryList = new ArrayList<StatusHistory>();
        in.readTypedList(statusHistoryList, StatusHistory.CREATOR);
        try {
            kurir = in.readParcelable(User.class.getClassLoader());
        }catch (Exception e){}
        updatedDate= in.readString();

    }

    public static final Creator<Packet> CREATOR = new Creator<Packet>() {
        @Override
        public Packet createFromParcel(Parcel in) {
            return new Packet(in);
        }

        @Override
        public Packet[] newArray(int size) {
            return new Packet[size];
        }
    };

    public String getResi() {
        return resi;
    }

    public void setResi(String resi) {
        this.resi = resi;
    }

    public String getNamaPengirim() {
        return namaPengirim;
    }

    public void setNamaPengirim(String namaPengirim) {
        this.namaPengirim = namaPengirim;
    }

    public String getTeleponPengirim() {
        return teleponPengirim;
    }

    public void setTeleponPengirim(String teleponPengirim) {
        this.teleponPengirim = teleponPengirim;
    }

    public String getAlamatPengirim() {
        return alamatPengirim;
    }

    public void setAlamatPengirim(String alamatPengirim) {
        this.alamatPengirim = alamatPengirim;
    }

    public String getKotaPengirim() {
        return kotaPengirim;
    }

    public void setKotaPengirim(String kotaPengirim) {
        this.kotaPengirim = kotaPengirim;
    }

    public String getNamaPenerima() {
        return namaPenerima;
    }

    public void setNamaPenerima(String namaPenerima) {
        this.namaPenerima = namaPenerima;
    }

    public String getTeleponPenerima() {
        return teleponPenerima;
    }

    public void setTeleponPenerima(String teleponPenerima) {
        this.teleponPenerima = teleponPenerima;
    }

    public String getAlamatPenerima() {
        return alamatPenerima;
    }

    public void setAlamatPenerima(String alamatPenerima) {
        this.alamatPenerima = alamatPenerima;
    }

    public String getKotaPenerima() {
        return kotaPenerima;
    }

    public void setKotaPenerima(String kotaPenerima) {
        this.kotaPenerima = kotaPenerima;
    }

    public int getBerat() {
        return berat;
    }

    public void setBerat(int berat) {
        this.berat = berat;
    }

    public String getInfoPaket() {
        return infoPaket;
    }

    public void setInfoPaket(String infoPaket) {
        this.infoPaket = infoPaket;
    }

    public boolean isViaMobil() {
        return viaMobil;
    }

    public void setViaMobil(boolean viaMobil) {
        this.viaMobil = viaMobil;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getKotaPengirimText() {
        return kotaPengirimText;
    }

    public void setKotaPengirimText(String kotaPengirimText) {
        this.kotaPengirimText = kotaPengirimText;
    }

    public String getKotaPenerimaText() {
        return kotaPenerimaText;
    }

    public void setKotaPenerimaText(String kotaPenerimaText) {
        this.kotaPenerimaText = kotaPenerimaText;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public User getKurir() {
        return kurir;
    }

    public void setKurir(User kurir) {
        this.kurir = kurir;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public double getBiaya() {
        return biaya;
    }

    public void setBiaya(double biaya) {
        this.biaya = biaya;
    }

    public List<StatusHistory> getStatusHistoryList() {
        return statusHistoryList;
    }

    public void setStatusHistoryList(List<StatusHistory> statusHistoryList) {
        this.statusHistoryList = statusHistoryList;
    }

    public Map<String, String> getAsParams(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("nama_pengirim", namaPengirim);
        params.put("telepon_pengirim", teleponPengirim);
        params.put("alamat_pengirim", alamatPengirim);
        params.put("kota_pengirim", kotaPengirim);
        params.put("nama_penerima", namaPenerima);
        params.put("telepon_penerima", teleponPenerima);
        params.put("alamat_penerima", alamatPenerima);
        params.put("kota_penerima", kotaPenerima);

        params.put("service_code", serviceCode);
        params.put("berat_barang", ""+berat);
        params.put("info_barang", infoPaket);
        params.put("via_mobil", ""+viaMobil);

        return params;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( resi);
        dest.writeString( namaPengirim);
        dest.writeString(teleponPengirim);
        dest.writeString(alamatPengirim);
        dest.writeString(kotaPengirim);
        dest.writeString(kotaPengirimText);
        dest.writeString(namaPenerima);
        dest.writeString(teleponPenerima);
        dest.writeString(alamatPenerima);
        dest.writeString(kotaPenerima);
        dest.writeString(kotaPenerimaText);
        dest.writeInt( berat);
        dest.writeString(infoPaket);
        dest.writeString( Boolean.toString( viaMobil ));
        dest.writeString(serviceCode);
        dest.writeString(status);
        dest.writeString(statusText);
        dest.writeDouble(biaya);
        dest.writeString(createdDate);

        dest.writeParcelable( order , flags);
        dest.writeTypedList(statusHistoryList);
        dest.writeParcelable(kurir, flags);
        dest.writeString(updatedDate);
    }
}
