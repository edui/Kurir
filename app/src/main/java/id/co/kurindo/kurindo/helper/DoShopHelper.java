package id.co.kurindo.kurindo.helper;

import com.android.tonyvu.sc.model.Saleable;
import com.android.tonyvu.sc.util.CartHelper;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by dwim on 2/6/2017.
 */

public class DoShopHelper extends OrderViaMapHelper {
    Set<Shop> shops;
    Set<Product> products;
    Set<Route> routes;

    static DoShopHelper helper;

    public static DoShopHelper getInstance(){
        if(helper == null) helper = new DoShopHelper();
        return helper;
    }

    public Set<Shop> getShops() {
        return shops;
    }

    public void setShops(Set<Shop> shops) {
        this.shops = shops;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

    @Override
    public TOrder getOrder() {
        return order;
    }

    @Override
    public void setOrder(TOrder order) {
        this.order = order;
    }

    public void addShop(Shop shop) {
        if(shops == null) shops = new LinkedHashSet<>();
        if(shop != null) shops.add(shop);
    }

    public void addRoute(Route route) {
        if(routes == null) routes = new LinkedHashSet<>();
        routes.add(route);
    }

    @Override
    public void clearAll() {
        super.clearAll();
        //if(shops != null) shops.clear();
        //if(products != null) products.clear();
        clearRoutes();
    }

    public void clearRoutes() {
        if(routes != null) routes.clear();
        clearPackets();
    }

    public void clearPackets() {
        if(order != null && order.getPackets() != null) order.getPackets().clear();
    }

    public void addDoShopOrder(String payment, double price, String serviceCode) {
        addOrder(payment, serviceCode, price);
        order.setProducts(getCartItems());
        double totalPrice = CartHelper.getCart().getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        addToProducts(AppConfig.KEY_DOSEND, price);
        totalPrice += price;
        order.setTotalPrice( new BigDecimal(totalPrice));
        order.setService_type(AppConfig.KEY_DOSHOP);
    }


    private Set<CartItem> getCartItems() {
        Set<CartItem> cartItems = new LinkedHashSet<>();
        Map<Saleable, Integer> itemMap = CartHelper.getCart().getItemWithQuantity();
        for (Map.Entry<Saleable, Integer> entry : itemMap.entrySet()) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct((Product) entry.getKey());
            cartItem.setQuantity(entry.getValue());
            cartItems.add(cartItem);
        }

        return cartItems;
    }

    public void addPacket(TUser origin, TUser destination, BigDecimal tariff, BigDecimal distance) {
        TPacket p = new TPacket();
        p.setOrigin(origin);
        p.setDestination(destination);
        p.setDistance(distance.doubleValue());
        p.setBiaya(tariff);
        if(order == null) order =new TOrder();
        Set<TPacket> packets = order.getPackets();
        packets.add(p);
    }
    public void updateDestination(String nama, String telepon){
        if(order != null){
            for(TPacket p : order.getPackets()){
                TUser destination = p.getDestination();
                destination.setFirstname(nama);
                destination.setPhone(telepon);
            }
        }
    }

}
