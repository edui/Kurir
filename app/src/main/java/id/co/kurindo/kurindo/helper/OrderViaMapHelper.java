package id.co.kurindo.kurindo.helper;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by dwim on 2/14/2017.
 */

public class OrderViaMapHelper {
    protected TOrder order;
    protected TPacket packet;
    protected TUser origin;
    protected TUser destination;

    private static OrderViaMapHelper helper;
    public static OrderViaMapHelper getInstance(){
        if(helper == null) helper = new OrderViaMapHelper();
        return helper;
    }

    public TOrder getOrder() {
        return order;
    }

    public void setOrder(TOrder order) {
        this.order = order;
    }

    public TUser getOrigin() {
        return origin;
    }

    public void setOrigin(TUser origin) {
        this.origin = origin;
        if(packet==null) packet = new TPacket();
        packet.setOrigin(origin);
    }

    public TPacket getPacket() {
        return packet;
    }

    public void setPacket(TPacket packet) {
        this.packet = packet;
    }

    public TUser getDestination() {
        return destination;
    }

    public void setDestination(TUser destination) {
        this.destination = destination;
        if(packet==null) packet = new TPacket();
        packet.setDestination(destination);
    }

    public void setPacketRoute(Address originParam, Address destinationParam) {
        if(origin == null) origin = new TUser();
        if(destination == null) destination = new TUser();
        this.origin.setAddress( originParam );
        this.destination.setAddress( destinationParam );
        if(packet==null) packet = new TPacket();
        packet.setDestination(destination);
        packet.setOrigin(origin);
    }

    public void setPacketRoute(TUser originParam, TUser destinationParam) {
        if(origin == null) origin = new TUser();
        if(destination == null) destination = new TUser();
        this.origin = originParam ;
        this.destination =  destinationParam ;
        if(packet==null) packet = new TPacket();
        packet.setDestination(destination);
        packet.setOrigin(origin);
    }

    protected void addNewProduct(String code) {
        addNewProduct(code, 0);
    }

    public void addNewProduct(String code, double price) {
        if(code.equalsIgnoreCase(AppConfig.KEY_DOSEND) || code.equalsIgnoreCase(AppConfig.KEY_DOJEK) || code.equalsIgnoreCase(AppConfig.KEY_DOCAR) || code.equalsIgnoreCase(AppConfig.KEY_DOMOVE)|| code.equalsIgnoreCase(AppConfig.KEY_DOHIJAMAH)){
            Set items = new LinkedHashSet<>();
            items.add(addCartItem(code, price));
            order.setProducts(items);
        }
    }

    protected void addToProducts(String code) {
        addToProducts(code, 0);
    }

    protected void addToProducts(String code, double price) {
        Set<CartItem> items = order.getProducts();
        if(code.equalsIgnoreCase(AppConfig.KEY_DOSEND) || code.equalsIgnoreCase(AppConfig.KEY_DOJEK)){
            if(items == null) {
                items = new LinkedHashSet<>();
                order.setProducts(items);
            }
            items.add(addCartItem(code, price));
        }
    }

    public CartItem addCartItem(String code, double price){
        Product p = new Product();

        p.setId(code.equalsIgnoreCase(AppConfig.KEY_DOSEND)?1:
                        (code.equalsIgnoreCase(AppConfig.KEY_DOJEK)?3:
                                (code.equalsIgnoreCase(AppConfig.KEY_DOCAR)? 2:
                                        (code.equalsIgnoreCase(AppConfig.KEY_DOMOVE)?7:
                                                (code.equalsIgnoreCase(AppConfig.KEY_DOWASH)?4:
                                                        (code.equalsIgnoreCase(AppConfig.KEY_DOSERVICE)?5:
                                                                (code.equalsIgnoreCase(AppConfig.KEY_DOHIJAMAH)?6:0)
                                                        ))))));
        p.setCode(code);
        p.setName(code);
        p.setShopid(1);
        p.setType("A");
        p.setQuantity(1);
        p.setPrice(new BigDecimal(price));
        p.setWeight(new BigDecimal(1));
        //p.setPrice(order.getTotalPrice());
        CartItem item = new CartItem();
        item.setProduct(p);
        item.setQuantity(p.getQuantity());
        return item;
    }

    protected void addOrder(String payment, String serviceCode, double price) {
        if(order == null) order= new TOrder();
        order.setPayment(payment);
        order.setService_code(serviceCode);
        order.setTotalPrice(new BigDecimal(price));
    }

    public void setServiceCode(String serviceCode){
        this.order.setService_code(serviceCode);
    }

    public String getServiceCode(){
        if(this.order != null)
            return this.order.getService_code();
        return null;
    }
    public void setDoType(String doType){
        if(order== null) order = new TOrder();
        order.setService_type(doType);
    }
    public String getDoType(){
        if(this.order != null)
            return this.order.getService_type();
        return null;
    }

    public void clearAll() {
        order = null;
        packet = null;
        origin = null;
        destination = null;

    }
}
