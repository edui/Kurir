package id.co.kurindo.kurindo.helper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.DoMart;
import id.co.kurindo.kurindo.model.DoService;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TPrice;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by aspire on 3/30/2017.
 */

public class DoMartHelper {
    private static DoMartHelper helper;
    public static DoMartHelper getInstance(){
        if(helper == null) helper = new DoMartHelper();
        return helper;
    }

    Set<Route> routes;

    HashMap<String, TPrice> priceMaps = new HashMap<>();

    protected TOrder order;
    protected DoMart doMart;
    protected Set<DoMart> services;

    public void addRoute(Route route) {
        if(routes == null) routes = new LinkedHashSet<>();
        routes.add(route);
    }
    public Set<Route> getRoutes() {
        return routes;
    }

    public void clearRoutes() {
        if(routes != null) routes.clear();
        clearPackets();
    }
    public void clearPackets() {
        if(order != null && order.getPackets() != null) order.getPackets().clear();
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

    public void addOrder(BigDecimal price, String service_type){
        if(order ==null) order = new TOrder();
        order.setService_type(service_type);
        order.setService_code(AppConfig.PACKET_NDS);
        order.setTotalPrice(price);
        order.setPayment(AppConfig.CASH_PAYMENT);
    }

    public DoMart addMart(){
        DoMart mart = new DoMart();
        if(services == null){
            services = new HashSet<>();
            order.setMarts(services);
        }
        services.add(mart);
        return mart;
    }
    public void addPacket(TUser origin, TUser destination, List<TUser> waypoints, BigDecimal tariff, BigDecimal distance) {
        TPacket p = new TPacket();
        p.setOrigin(origin);
        p.setDestination(destination);
        p.setWaypoints(waypoints);
        p.setDistance(distance.doubleValue());
        p.setBiaya(tariff);
        if(order == null) order =new TOrder();
        Set<TPacket> packets = order.getPackets();
        packets.add(p);
    }
    public void clearOrder() {
        order = null;
        services = null;
    }

    public DoMart getDoMart() {
        return doMart;
    }

    public void setDoMart(DoMart doMart) {
        this.doMart = doMart;
    }

    public Set<DoMart> getServices() {
        return services;
    }

    public void setServices(Set<DoMart> services) {
        this.services = services;
    }

    public DoMart find(DoMart domart){
        for (DoMart dom : services) {
            if(dom.equals(domart)) return dom;
        }
        return null;
    }

    public TOrder getOrder() {
        return order;
    }

    public HashMap<String, TPrice> getPriceMaps() {
        return priceMaps;
    }

    public void setPriceMaps(HashMap<String, TPrice> priceMaps) {
        this.priceMaps = priceMaps;
    }
}
