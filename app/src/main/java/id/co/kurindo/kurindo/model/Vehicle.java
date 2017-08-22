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
    private int tarif;
    @Expose(serialize = false)
    private int tarif_24;
    @Expose(serialize = false)
    private int tarif_sopir;
    @Expose(serialize = false)
    private int tarif_sopir_24;
    @Expose(serialize = false)
    private int tarif_bbm;
    @Expose(serialize = false)
    private int tarif_bbm_24;
    @Expose(serialize = false)
    private int tarif_lainnya;
    @Expose(serialize = false)
    private int tarif_lainnya_24;
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

    public int getTarif() {
        return tarif;
    }

    public void setTarif(int tarif) {
        this.tarif = tarif;
    }

    public int getTarif_24() {
        return tarif_24;
    }

    public void setTarif_24(int tarif_24) {
        this.tarif_24 = tarif_24;
    }

    public int getTarif_sopir() {
        return tarif_sopir;
    }

    public void setTarif_sopir(int tarif_sopir) {
        this.tarif_sopir = tarif_sopir;
    }

    public int getTarif_sopir_24() {
        return tarif_sopir_24;
    }

    public void setTarif_sopir_24(int tarif_sopir_24) {
        this.tarif_sopir_24 = tarif_sopir_24;
    }

    public int getTarif_bbm() {
        return tarif_bbm;
    }

    public void setTarif_bbm(int tarif_bbm) {
        this.tarif_bbm = tarif_bbm;
    }

    public int getTarif_bbm_24() {
        return tarif_bbm_24;
    }

    public void setTarif_bbm_24(int tarif_bbm_24) {
        this.tarif_bbm_24 = tarif_bbm_24;
    }

    public int getTarif_lainnya() {
        return tarif_lainnya;
    }

    public void setTarif_lainnya(int tarif_lainnya) {
        this.tarif_lainnya = tarif_lainnya;
    }

    public int getTarif_lainnya_24() {
        return tarif_lainnya_24;
    }

    public void setTarif_lainnya_24(int tarif_lainnya_24) {
        this.tarif_lainnya_24 = tarif_lainnya_24;
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
