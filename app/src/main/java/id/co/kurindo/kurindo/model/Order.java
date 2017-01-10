package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.model.Saleable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by DwiM on 12/9/2016.
 */

public class Order implements Parcelable{
    private int id;
    private String awb;
    private String type;
    private String payment;
    private String status;
    private String statusText;

    private String created;

    private BigDecimal totalPrice = BigDecimal.ZERO;
    private int totalQuantity = 0;

    private User pic;
    private User pembeli;
    private Set<CartItem> items;
    //private Map<Saleable, Integer> products;
    private Set<Recipient> recipients = new LinkedHashSet<>();
    private Set<Packet> packets = new LinkedHashSet<>();

    public Order(){

    }
    protected Order(Parcel in) {
        //id = in.readInt();
        awb = in.readString();
        type = in.readString();
        payment = in.readString();
        status = in.readString();
        statusText = in.readString();
        created = in.readString();
        totalPrice = new BigDecimal( in.readDouble() );
        totalQuantity = in.readInt();

        try {
            pembeli = in.readParcelable(User.class.getClassLoader());
        }catch (Exception e){}

        try {
            List<CartItem> alCi = new ArrayList<CartItem>();
            in.readTypedList(alCi, CartItem.CREATOR);
            items= new LinkedHashSet<>();
            items.addAll(alCi);
        }catch (Exception e){}

        try {
            List<Recipient> alRec = new ArrayList<Recipient>();
            in.readTypedList(alRec, Recipient.CREATOR);
            recipients = new LinkedHashSet<>();
            recipients.addAll(alRec);
        }catch (Exception e){}

        try {
            List<Packet> alPac = new ArrayList<Packet>();
            in.readTypedList(alPac, Packet.CREATOR);
            packets = new LinkedHashSet<>();
            packets.addAll(alPac);
        }catch (Exception e){}

        try {
            pic = in.readParcelable(User.class.getClassLoader());
        }catch (Exception e){}
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
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

    public User getBuyer() {
        return pembeli;
    }

    public void setBuyer(User buyer) {
        this.pembeli = buyer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
    public Set<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<Recipient> recipients) {
        this.recipients = recipients;
    }

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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Set<CartItem> getItems() {
        return items;
    }

    public void setItems(Set<CartItem> items) {
        this.items = items;
    }

    public Set<Packet> getPackets() {
        return packets;
    }

    public void setPackets(Set<Packet> packets) {
        this.packets = packets;
    }

    public User getPic() {
        return pic;
    }

    public void setPic(User pic) {
        this.pic = pic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeInt(id);
        dest.writeString(awb);
        dest.writeString(type);
        dest.writeString(payment);
        dest.writeString(status);
        dest.writeString(statusText);
        dest.writeString(created);
        dest.writeDouble( totalPrice == null ? 0 :totalPrice.doubleValue());
        dest.writeInt(totalQuantity);
        if(pembeli != null) dest.writeParcelable(pembeli, flags);
        List<CartItem>  alCi = new ArrayList<>();
        if(items != null) alCi.addAll(items);
        dest.writeTypedList(alCi);
        List<Recipient>  alRec = new ArrayList<>();
        if(recipients != null) alRec.addAll(recipients);
        dest.writeTypedList(alRec);
        List<Packet>  alPac = new ArrayList<>();
        if(packets != null) alPac.addAll(packets);
        dest.writeTypedList(alPac);
        if(pic != null) dest.writeParcelable(pic, flags);
    }
}
