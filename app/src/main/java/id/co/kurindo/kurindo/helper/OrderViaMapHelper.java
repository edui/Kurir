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
 * Created by dwim on 2/6/2017.
 */

public class OrderViaMapHelper {
    TOrder order;
    TPacket packet;
    TUser origin;
    TUser destination;

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

    public void setRoute(Address originParam, Address destinationParam) {
        if(origin == null) origin = new TUser();
        if(destination == null) destination = new TUser();
        this.origin.setAddress( originParam );
        this.destination.setAddress( destinationParam );
        if(packet==null) packet = new TPacket();
        packet.setDestination(destination);
        packet.setOrigin(origin);
    }

    public void setRoute(TUser originParam, TUser destinationParam) {
        if(origin == null) origin = new TUser();
        if(destination == null) destination = new TUser();
        this.origin = originParam ;
        this.destination =  destinationParam ;
        if(packet==null) packet = new TPacket();
        packet.setDestination(destination);
        packet.setOrigin(origin);
    }

    public void addDoSendOrder(String payment, String serviceCode, String distance, double price){
        addOrder(payment, serviceCode, price);
        addNewProduct(AppConfig.KEY_DOSEND);
        order.setService_type(AppConfig.KEY_DOSEND);
        packet.setDistance(Double.parseDouble( distance ));
    }

    private void addNewProduct(String code) {
        Product p = new Product();
        if(code.equalsIgnoreCase(AppConfig.KEY_DOSEND)){
            p.setCode(code);
            p.setName(code);
            p.setShopid(1);
            p.setType("A");
            p.setQuantity(1);
            p.setWeight(new BigDecimal(1));
            p.setPrice(order.getTotalPrice());
            CartItem item = new CartItem();
            item.setProduct(p);
            item.setQuantity(p.getQuantity());
            Set items = new LinkedHashSet<>();
            items.add(item);
            order.setProducts(items);
        }

    }
    private void addToProducts(String code) {
        Product p = new Product();
        Set<CartItem> items = order.getProducts();
        if(code.equalsIgnoreCase(AppConfig.KEY_DOSEND)){
            p.setCode(code);
            p.setName(code);
            p.setShopid(1);
            p.setType("A");
            p.setQuantity(1);
            p.setWeight(new BigDecimal(1));
            p.setPrice(order.getTotalPrice());
            CartItem item = new CartItem();
            item.setProduct(p);
            item.setQuantity(p.getQuantity());
            if(items == null) {
                items = new LinkedHashSet<>();
                order.setProducts(items);
            }
            items.add(item);
        }

    }
    private void addOrder(String payment, String serviceCode, double price) {
        if(order == null) order= new TOrder();
        order.setPayment(payment);
        order.setService_code(serviceCode);
        order.setTotalPrice(new BigDecimal(price));
    }

    public void setServiceCode(String serviceCode){
        this.order.setService_code(serviceCode);
    }


    public void clearAll() {
        order = null;
        packet = null;
        origin = null;
        destination = null;
    }
}
