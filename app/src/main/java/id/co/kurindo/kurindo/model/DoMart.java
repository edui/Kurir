package id.co.kurindo.kurindo.model;

import java.math.BigDecimal;

import id.co.kurindo.kurindo.app.AppConfig;

/**
 * Created by aspire on 4/21/2017.
 */

public class DoMart {
    private int id;
    private String type = AppConfig.KEY_DOMART;
    private String awb;
    private String notes;
    private int qty = 1;
    private TUser origin;
    private String unit = "unit";
    private BigDecimal price_unit;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal price_estimate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getPrice_estimate() {
        return price_estimate;
    }

    public void setPrice_estimate(BigDecimal price_estimate) {
        this.price_estimate = price_estimate;
    }

    public TUser getOrigin() {
        return origin;
    }

    public void setOrigin(TUser origin) {
        this.origin = origin;
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
