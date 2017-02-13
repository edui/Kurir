package id.co.kurindo.kurindo.helper;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.Address;
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

public class DoShopHelper extends OrderViaMapHelper{

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
        if(shops != null) shops.clear();
        if(products != null) products.clear();
        clearRoutes();
    }

    public void clearRoutes() {
        if(routes != null) routes.clear();
    }
}
