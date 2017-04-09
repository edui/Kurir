package id.co.kurindo.kurindo.model;

import java.math.BigDecimal;

/**
 * Created by aspire on 3/30/2017.
 */

public class DoService {
    private String awb;
    private int int_layanan;
    private String kode_layanan;
    private int int_ac_type;
    private String jenis_barang;
    private int qty;
    private String location;
    private String notes;
    private String unit = "unit";
    private BigDecimal price_unit;
    private BigDecimal price;
    private BigDecimal discount;

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public int getInt_layanan() {
        return int_layanan;
    }

    public void setInt_layanan(int int_layanan) {
        this.int_layanan = int_layanan;
    }

    public String getKode_layanan() {
        return kode_layanan;
    }

    public void setKode_layanan(String kode_layanan) {
        this.kode_layanan = kode_layanan;
    }

    public int getInt_ac_type() {
        return int_ac_type;
    }

    public void setInt_ac_type(int int_ac_type) {
        this.int_ac_type = int_ac_type;
    }

    public String getJenis_barang() {
        return jenis_barang;
    }

    public void setJenis_barang(String jenis_barang) {
        this.jenis_barang = jenis_barang;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getPrice_unit() {
        return price_unit;
    }

    public void setPrice_unit(BigDecimal price_unit) {
        this.price_unit = price_unit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(kode_layanan +" : ");
        sb.append(qty+" "+unit+" : "+ jenis_barang);
        if(location != null) sb.append(" di "+location);
        if(notes != null) sb.append("\nNote: "+ notes);
        return sb.toString();
    }
}
