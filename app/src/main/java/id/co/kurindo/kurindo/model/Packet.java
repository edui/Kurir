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
    private String awb_number;

    private String nama_pengirim;
    private String telepon_pengirim;
    private String alamat_pengirim;
    private String kota_pengirim;
    private String kota_pengirim_text;
    private String gender_pengirim;

    private String nama_penerima;
    private String telepon_penerima;
    private String alamat_penerima;
    private String kota_penerima;
    private String kota_penerima_text;
    private String gender_penerima;

    private int berat_kiriman;
    private String isi_kiriman;
    private boolean via_mobil;
    private double biaya;
    private String service_code;

    private String status;
    private String status_text;
    private String created_date;
    private String updated_date;

    private User kurir;
    private Order order;
    private List<StatusHistory> statusHistoryList =new ArrayList<>();

    public Packet(){

    }
    protected Packet(Parcel in) {
        awb_number = in.readString();
        nama_pengirim = in.readString();
        gender_pengirim = in.readString();
        telepon_pengirim = in.readString();
        alamat_pengirim = in.readString();
        kota_pengirim = in.readString();
        kota_pengirim_text = in.readString();
        nama_penerima = in.readString();
        gender_penerima = in.readString();
        telepon_penerima = in.readString();
        alamat_penerima = in.readString();
        kota_penerima = in.readString();
        kota_penerima_text = in.readString();
        berat_kiriman = in.readInt();
        isi_kiriman = in.readString();
        via_mobil = Boolean.parseBoolean( in.readString() );
        service_code = in.readString();
        status = in.readString();
        status_text = in.readString();
        biaya= in.readDouble();
        created_date = in.readString();
        //try {
        //    order = in.readParcelable(Order.class.getClassLoader());
        //}catch (Exception e){}
        try {
            kurir = in.readParcelable(User.class.getClassLoader());
        }catch (Exception e){}
        updated_date = in.readString();
        statusHistoryList = new ArrayList<StatusHistory>();
        try {
            in.readTypedList(statusHistoryList, StatusHistory.CREATOR);
        }catch (Exception e){}

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
        return awb_number;
    }

    public void setResi(String resi) {
        this.awb_number = resi;
    }

    public String getNamaPengirim() {
        return nama_pengirim;
    }

    public void setNamaPengirim(String namaPengirim) {
        this.nama_pengirim = namaPengirim;
    }

    public String getTeleponPengirim() {
        return telepon_pengirim;
    }

    public void setTeleponPengirim(String teleponPengirim) {
        this.telepon_pengirim = teleponPengirim;
    }

    public String getAlamatPengirim() {
        return alamat_pengirim;
    }

    public void setAlamatPengirim(String alamatPengirim) {
        this.alamat_pengirim = alamatPengirim;
    }

    public String getKotaPengirim() {
        return kota_pengirim;
    }

    public void setKotaPengirim(String kotaPengirim) {
        this.kota_pengirim = kotaPengirim;
    }

    public String getNamaPenerima() {
        return nama_penerima;
    }

    public void setNamaPenerima(String namaPenerima) {
        this.nama_penerima = namaPenerima;
    }

    public String getTeleponPenerima() {
        return telepon_penerima;
    }

    public void setTeleponPenerima(String teleponPenerima) {
        this.telepon_penerima = teleponPenerima;
    }

    public String getAlamatPenerima() {
        return alamat_penerima;
    }

    public void setAlamatPenerima(String alamatPenerima) {
        this.alamat_penerima = alamatPenerima;
    }

    public String getKotaPenerima() {
        return kota_penerima;
    }

    public void setKotaPenerima(String kotaPenerima) {
        this.kota_penerima = kotaPenerima;
    }

    public int getBerat() {
        return berat_kiriman;
    }

    public void setBerat(int berat) {
        this.berat_kiriman = berat;
    }

    public String getInfoPaket() {
        return isi_kiriman;
    }

    public void setInfoPaket(String infoPaket) {
        this.isi_kiriman = infoPaket;
    }

    public boolean isViaMobil() {
        return via_mobil;
    }

    public void setViaMobil(boolean viaMobil) {
        this.via_mobil = viaMobil;
    }

    public String getServiceCode() {
        return service_code;
    }

    public void setServiceCode(String serviceCode) {
        this.service_code = serviceCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return created_date;
    }

    public void setCreatedDate(String createdDate) {
        this.created_date = createdDate;
    }

    public String getKotaPengirimText() {
        return kota_pengirim_text;
    }

    public void setKotaPengirimText(String kotaPengirimText) {
        this.kota_pengirim_text = kotaPengirimText;
    }

    public String getKotaPenerimaText() {
        return kota_penerima_text;
    }

    public void setKotaPenerimaText(String kotaPenerimaText) {
        this.kota_penerima_text = kotaPenerimaText;
    }

    public String getUpdatedDate() {
        return updated_date;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updated_date = updatedDate;
    }

    public String getStatusText() {
        return status_text;
    }

    public void setStatusText(String statusText) {
        this.status_text = statusText;
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

    public String getGenderPenerima() {
        return gender_penerima;
    }

    public void setGenderPenerima(String gender_penerima) {
        this.gender_penerima = gender_penerima;
    }

    public String getGenderPengirim() {
        return gender_pengirim;
    }

    public void setGenderPengirim(String gender_pengirim) {
        this.gender_pengirim = gender_pengirim;
    }

    public Map<String, String> getAsParams(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("nama_pengirim", nama_pengirim);
        params.put("gender_pengirim", gender_pengirim);
        params.put("telepon_pengirim", telepon_pengirim);
        params.put("alamat_pengirim", alamat_pengirim);
        params.put("kota_pengirim", kota_pengirim);
        params.put("nama_penerima", nama_penerima);
        params.put("gender_penerima", gender_penerima);
        params.put("telepon_penerima", telepon_penerima);
        params.put("alamat_penerima", alamat_penerima);
        params.put("kota_penerima", kota_penerima);

        params.put("service_code", service_code);
        params.put("berat_barang", ""+ berat_kiriman);
        params.put("info_barang", isi_kiriman);
        params.put("via_mobil", ""+ via_mobil);

        return params;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(awb_number);
        dest.writeString(nama_pengirim);
        dest.writeString(gender_pengirim);
        dest.writeString(telepon_pengirim);
        dest.writeString(alamat_pengirim);
        dest.writeString(kota_pengirim);
        dest.writeString(kota_pengirim_text);
        dest.writeString(nama_penerima);
        dest.writeString(gender_penerima);
        dest.writeString(telepon_penerima);
        dest.writeString(alamat_penerima);
        dest.writeString(kota_penerima);
        dest.writeString(kota_penerima_text);
        dest.writeInt(berat_kiriman);
        dest.writeString(isi_kiriman);
        dest.writeString( Boolean.toString(via_mobil));
        dest.writeString(service_code);
        dest.writeString(status);
        dest.writeString(status_text);
        dest.writeDouble(biaya);
        dest.writeString(created_date);

        //dest.writeParcelable( order , flags);
        dest.writeParcelable(kurir, flags);
        dest.writeString(updated_date);
        dest.writeTypedList(statusHistoryList);
    }
}
