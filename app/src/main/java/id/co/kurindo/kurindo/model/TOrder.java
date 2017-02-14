package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dwim on 2/9/2017.
 */

public class TOrder implements Parcelable{
    private int id;
    private String awb;
    private String service_type;
    private String service_code;
    private String payment;
    private String status;
    private String statusText;

    private String created_date;
    private String created_by;
    private String updated_date;
    private String updated_by;
    private String user_agent;
    private String agen;

    private BigDecimal totalPrice = BigDecimal.ZERO;
    private int totalQuantity = 0;

    private TUser pic;
    private TUser pembeli;
    private Set<CartItem> products;
    private Set<TPacket> packets = new LinkedHashSet<>();

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
    }

    public TUser getBuyer() {
        return pembeli;
    }

    public void setBuyer(TUser buyer) {
        this.pembeli = buyer;
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
        dest.writeString(updated_date);
        dest.writeString(updated_by);
    }


}
