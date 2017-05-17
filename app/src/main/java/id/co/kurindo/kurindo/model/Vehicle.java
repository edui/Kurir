package id.co.kurindo.kurindo.model;

import com.google.gson.annotations.Expose;

/**
 * Created by DwiM on 4/11/2017.
 */

public class Vehicle {
    @Expose()
    private int id;
    @Expose(serialize = false)
    private String name;
    @Expose(serialize = false)
    private String description;
    @Expose(serialize = false)
    private String image;
    @Expose(serialize = false)
    private String jenis;
    @Expose(serialize = false)
    private String merk;
    @Expose(serialize = false)
    private String model;
    @Expose(serialize = false)
    private String tahun;
    @Expose()
    private String plat;
    @Expose(serialize = false)
    private String warna;
    @Expose(serialize = false)
    private String dimensi;
    @Expose(serialize = false)
    private String dayamuat;
    @Expose(serialize = false)
    private String bbm;
    @Expose(serialize = false)
    private String transmisi;
    @Expose(serialize = false)
    private String tarif;
    @Expose(serialize = false)
    private int ac;
    @Expose(serialize = false)
    private int kebersihan;
    @Expose(serialize = false)
    private int kondisi;
    @Expose(serialize = false)
    private TUser pemilik;
    @Expose(serialize = false)
    private String kota;
    @Expose(serialize = false)
    private float rating;
    @Expose(serialize = false)
    private String status;

    public Vehicle(String name, String description, String image){

    }
    public Vehicle(String name, String description, String size, String berat, String image){
        this.name = name;
        this.description = description;
        this.dimensi = size;
        this.dayamuat = berat;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    public String getDimensi() {
        return dimensi;
    }

    public void setDimensi(String dimensi) {
        this.dimensi = dimensi;
    }

    public String getDayamuat() {
        return dayamuat;
    }

    public void setDayamuat(String dayamuat) {
        this.dayamuat = dayamuat;
    }

    public String getBbm() {
        return bbm;
    }

    public void setBbm(String bbm) {
        this.bbm = bbm;
    }

    public String getTarif() {
        return tarif;
    }

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public int getKebersihan() {
        return kebersihan;
    }

    public void setKebersihan(int kebersihan) {
        this.kebersihan = kebersihan;
    }

    public int getKondisi() {
        return kondisi;
    }

    public void setKondisi(int kondisi) {
        this.kondisi = kondisi;
    }

    public TUser getPemilik() {
        return pemilik;
    }

    public void setPemilik(TUser pemilik) {
        this.pemilik = pemilik;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public float getRating() {
        return rating;
    }

    public String getTransmisi() {
        return transmisi;
    }

    public void setTransmisi(String transmisi) {
        this.transmisi = transmisi;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
