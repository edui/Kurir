package id.co.kurindo.kurindo.helper;

import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by dwim on 2/12/2017.
 */

public class ViewHelper {
    TOrder order;
    TPacket packet;
    Shop shop;
    Product product;

    Address lastAddress;
    TUser location;

    boolean selectedShop;

    private static ViewHelper helper;
    public static ViewHelper getInstance(){
        if(helper == null) helper = new ViewHelper();
        return helper;
    }

    public TOrder getOrder() {
        return order;
    }

    public void setOrder(TOrder order) {
        this.order = order;
    }

    public TPacket getPacket() {
        return packet;
    }

    public void setPacket(TPacket packet) {
        this.packet = packet;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Shop getShop() {
        return shop;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setLastAddress(Address address) {
        this.lastAddress = address;
    }

    public Address getLastAddress() {
        return lastAddress;
    }

    public void clearAll(){
        shop = null;
        product = null;
        order = null;
        packet = null;
        location = null;
    }
    public void setSelectedShop(boolean selected){
        this.selectedShop = selected;
    }
    public boolean getSelectedShop(){
        return this.selectedShop;
    }

    public TUser getLocation() {
        return location;
    }

    public void setLocation(TUser location) {
        this.location = location;
    }


    protected TUser tempUser;
    protected String id;

    public void setTUser(TUser origin) {
        this.tempUser = origin;
    }

    public TUser getTUser() {
        return tempUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
