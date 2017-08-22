package id.co.kurindo.kurindo.model;

import android.content.Context;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;

/**
 * Created by DwiM on 5/14/2017.
 */

public class DoCarRental {
    @Expose
    private String city;
    @Expose
    private String activity;
    @Expose
    private String start_date;
    @Expose
    private String end_date;
    @Expose
    private String durasi;
    @Expose
    private String fasilitas;
    @Expose
    private String notes;
    @Expose
    private Vehicle vehicle;
    @Expose
    private String payment;

    @Expose
    private TUser user;

    private int rating;
    private String review;
    @Expose
    private BigDecimal price;

    public String getDateRange(){
        return start_date + " - "+end_date;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }


    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public BigDecimal getVehiclePrice(Context context, Vehicle vehicle){
        if(vehicle != null){
            double tarif = 0;
            double sopir = 0;
            double bbm = 0;
            double lainnya = 0;
            try {
                if(getDurasi().equalsIgnoreCase(context.getString(R.string.label_fullday))){
                    tarif = vehicle.getTarif_24();
                    sopir = vehicle.getTarif_sopir_24();
                    bbm = vehicle.getTarif_bbm_24();
                    lainnya = vehicle.getTarif_lainnya_24();
                }else{
                    tarif = vehicle.getTarif();
                    sopir = vehicle.getTarif_sopir();
                    bbm = vehicle.getTarif_bbm();
                    lainnya = vehicle.getTarif_lainnya();
                }
                if(getFasilitas().equalsIgnoreCase(context.getString(R.string.label_tanpabbm))){
                    bbm = 0;
                    lainnya = 0;
                }else if(getFasilitas().equalsIgnoreCase(context.getString(R.string.label_bbminclude))){
                    lainnya = 0;
                }
            }catch (Exception e){}

            BigDecimal price = new BigDecimal((tarif+sopir+bbm+lainnya));
            return price;
        }
        return new BigDecimal(0);
    }
    public BigDecimal getCalculatePrice(Context context, Vehicle vehicle) {
        return  price =  getVehiclePrice(context, vehicle).multiply(new BigDecimal( getDays() ));
    }

    public void setDateRange(Date startDate, Date endDate) {
        start_date = AppConfig.getDateTimeServerFormat().format(startDate);
        end_date = AppConfig.getDateTimeServerFormat().format(endDate);
    }
    public Date getStartDate(){
        try {
            return AppConfig.getDateTimeServerFormat().parse(start_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
    public Date getEndDate(){
        try {
            return AppConfig.getDateTimeServerFormat().parse(end_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
    public String displayStartDate(boolean shortFormat){
        if(shortFormat){
            return AppConfig.getDateFormat().format(getStartDate());
        }
        return AppConfig.getDateTimeFormat().format(getStartDate());
    }
    public String displayEndDate(boolean shortFormat){
        if(shortFormat){
            return AppConfig.getDateFormat().format(getEndDate());
        }
        return AppConfig.getDateTimeFormat().format(getEndDate());
    }
    public long getDays()  {
        long days = 0;
        Date s = null;
        Date e = null;
        long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
        try {
            s = AppConfig.getDateTimeServerFormat().parse(start_date);
            e = AppConfig.getDateTimeServerFormat().parse(end_date);
            long diff = e.getTime() - s.getTime();
            diff = diff + DAY_IN_MILLIS;
            days  = (diff / DAY_IN_MILLIS );
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return days;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPayment() {
        return payment;
    }

    public void setUser(TUser user) {
        this.user = user;
    }

    public TUser getUser() {
        if(user == null) user = new TUser();
        return user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String displayDate(boolean shortFormat) {
        if(getDays() > 1) return displayStartDate(shortFormat) + " - "+displayEndDate(shortFormat);
        else return displayStartDate(shortFormat);
    }
}
