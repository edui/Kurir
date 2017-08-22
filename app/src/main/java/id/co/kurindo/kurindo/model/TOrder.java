package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import id.co.kurindo.kurindo.app.AppConfig;

/**
 * Created by dwim on 2/9/2017.
 */

public class TOrder implements Parcelable{
    @Expose
    private int id;
    @Expose
    private String awb;
    @Expose
    private String service_type;
    @Expose
    private String sub_type;
    @Expose
    private String service_code;
    @Expose
    private String payment;
    @Expose
    private String status;
    @Expose
    private String statusText;

    private float rating;
    private String testimoni;

    private String created_date;
    private String created_by;
    private String updated_date;
    private String updated_by;
    private String user_agent;
    private String agen;
    @Expose
    private String pickup;
    @Expose
    private String droptime;

    @Expose
    private String dibayar;

    @Expose
    private BigDecimal totalPrice = BigDecimal.ZERO;
    @Expose
    private int totalQuantity = 0;
    @Expose
    private BigDecimal  cod = BigDecimal.ZERO;

    @Expose(serialize = false)
    private TUser pic;
    @Expose(serialize = false)
    private TUser pembeli;
    @Expose
    private Set<CartItem> products;
    @Expose
    private Set<TPacket> packets = new LinkedHashSet<>();
    @Expose
    private Set<DoService> services;
    @Expose
    private TUser place;
    @Expose
    private Set<DoMart> marts;
    @Expose
    private DoCarRental docar;

    public TOrder(){

    }
    protected TOrder(Parcel in) {
        //id = in.readInt();
        awb = in.readString();
        service_type = in.readString();
        service_code = in.readString();
        payment = in.readString();
        status = in.readString();
        statusText = in.readString();
        created_date = in.readString();
        created_by = in.readString();
        totalPrice = new BigDecimal( in.readDouble() );
        totalQuantity = in.readInt();

        try {
            pembeli = in.readParcelable(User.class.getClassLoader());
        }catch (Exception e){}

        try {
            List<CartItem> alCi = new ArrayList<CartItem>();
            in.readTypedList(alCi, CartItem.CREATOR);
            products= new LinkedHashSet<>();
            products.addAll(alCi);
        }catch (Exception e){}


        try {
            List<TPacket> alPac = new ArrayList<TPacket>();
            in.readTypedList(alPac, TPacket.CREATOR);
            packets = new LinkedHashSet<>();
            packets.addAll(alPac);
        }catch (Exception e){}

        try {
            pic = in.readParcelable(User.class.getClassLoader());
        }catch (Exception e){}
        pickup = in.readString();
        updated_date = in.readString();
        updated_by = in.readString();
    }

    public static final Parcelable.Creator<TOrder> CREATOR = new Parcelable.Creator<TOrder>() {
        @Override
        public TOrder createFromParcel(Parcel in) {
            return new TOrder(in);
        }

        @Override
        public TOrder[] newArray(int size) {
            return new TOrder[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
        if(services != null){
            Object[] datas  = services.toArray();
            for (int i = 0; i < datas.length; i++) {
                DoService data = (DoService) datas[i];
                data.setAwb(awb);
            }
        }
    }

    public TUser getBuyer() {
        return pembeli;
    }

    public void setBuyer(TUser buyer) {
        this.pembeli = buyer;
    }

    public String getSub_type() {
        return sub_type;
    }

    public void setSub_type(String sub_type) {
        this.sub_type = sub_type;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getAgen() {
        return agen;
    }

    public void setAgen(String agen) {
        this.agen = agen;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    /*
        public Map<Saleable, Integer> getProducts() {
            return products;
        }

        public void setProducts(Map<Saleable, Integer> products) {
            this.products = products;
        }
    */

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }


    public Set<CartItem> getProducts() {
        return products;
    }

    public void setProducts(Set<CartItem> items) {
        this.products = items;
    }

    public Set<TPacket> getPackets() {
        return packets;
    }

    public void setPackets(Set<TPacket> packets) {
        this.packets = packets;
    }

    public TUser getPic() {
        return pic;
    }

    public void setPic(TUser pic) {
        this.pic = pic;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup_time) {
        this.pickup = pickup_time;
    }

    public Set<DoService> getServices() {
        return services;
    }

    public void setServices(Set<DoService> services) {
        this.services = services;
    }

    public BigDecimal getCod() {
        return cod;
    }

    public void setCod(BigDecimal cod) {
        this.cod = cod;
    }

    public TUser getPlace() {
        return place;
    }

    public void setPlace(TUser place) {
        this.place = place;
    }

    public String getDroptime() {
        return droptime;
    }

    public void setDroptime(String droptime) {
        this.droptime = droptime;
    }

    public Set<DoMart> getMarts() {
        return marts;
    }

    public void setMarts(Set<DoMart> marts) {
        this.marts = marts;
    }

    public String getDibayar() {
        return dibayar;
    }

    public void setDibayar(String dibayar) {
        this.dibayar = dibayar;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTestimoni() {
        return testimoni;
    }

    public void setTestimoni(String testimoni) {
        this.testimoni = testimoni;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeInt(id);
        dest.writeString(awb);
        dest.writeString(service_type);
        dest.writeString(service_code);
        dest.writeString(payment);
        dest.writeString(status);
        dest.writeString(statusText);
        dest.writeString(created_date);
        dest.writeString(created_by);
        dest.writeDouble( totalPrice == null ? 0 :totalPrice.doubleValue());
        dest.writeInt(totalQuantity);
        if(pembeli != null) dest.writeParcelable(pembeli, flags);
        List<CartItem>  alCi = new ArrayList<>();
        if(products != null) alCi.addAll(products);
        dest.writeTypedList(alCi);
        List<TPacket>  alPac = new ArrayList<>();
        if(packets != null) alPac.addAll(packets);
        dest.writeTypedList(alPac);
        if(pic != null) dest.writeParcelable(pic, flags);
        dest.writeString(pickup);
        dest.writeString(updated_date);
        dest.writeString(updated_by);
    }

    public void setDocar(DoCarRental docar) {
        this.docar = docar;
    }

    public DoCarRental getDocar() {
        return docar;
    }

    public Date getPickupDate(){
        try {
            return AppConfig.getDateTimeServerFormat().parse(pickup);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public String formatPickup(){
        return AppConfig.getDateTimeServerFormat().format(getPickupDate());
    }

    public LatLng getLocation(){
        if(getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)
                || getService_type().equalsIgnoreCase(AppConfig.KEY_DOSERVICE)
                || getService_type().equalsIgnoreCase(AppConfig.KEY_DOHIJAMAH) ){
            if(getPlace() != null && getPlace().getAddress() != null)
                return getPlace().getAddress().getLocation();
        }else if(getService_type().equalsIgnoreCase(AppConfig.KEY_DOCAR) && getDocar() != null ){
            if(getDocar().getUser() != null && getDocar().getUser().getAddress() != null)
                return getDocar().getUser().getAddress().getLocation();
        }else if(getService_type().equalsIgnoreCase(AppConfig.KEY_DOSEND)
                || getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK)
                || getService_type().equalsIgnoreCase(AppConfig.KEY_DOCAR)
                || getService_type().equalsIgnoreCase(AppConfig.KEY_DOMOVE)
                || getService_type().equalsIgnoreCase(AppConfig.KEY_DOSHOP)){
            if(getPackets() != null && getPackets().size() > 0){
                for (TPacket p :    getPackets()) {
                    if(p.getOrigin() != null && p.getOrigin().getAddress() !=null)
                        return p.getOrigin().getAddress().getLocation();
                }
            }
        }else if(getService_type().equalsIgnoreCase(AppConfig.KEY_DOMART)){
            if(getMarts() != null && getMarts().size() > 0){
                for (DoMart m : getMarts()){
                    if(m.getOrigin() != null && m.getOrigin().getAddress() != null)
                        return m.getOrigin().getAddress().getLocation();
                }
            }
        }
        return new LatLng(0,0);
    }

    public String getLocationStr(){
        LatLng loc = getLocation();
        if(loc == null) return "-6.170166,106.831375";
        return loc.latitude+","+loc.longitude;
    }
}
