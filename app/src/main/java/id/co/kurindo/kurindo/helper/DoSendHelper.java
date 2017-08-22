package id.co.kurindo.kurindo.helper;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by dwim on 2/6/2017.
 */

public class DoSendHelper extends OrderViaMapHelper{
    Route route;
    String doMoveType;

    static DoSendHelper helper;
    Set<TUser> destinations;

    public static DoSendHelper getInstance(){
        if(helper == null) helper = new DoSendHelper();
        return helper;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void addRoute(Route route) {
        this.route = route;
        if(packet != null) packet.setRoutes(route.getRoutes());
    }
    public void clearRoutes() {
        route = null;
    }

    @Override
    public void clearAll() {
        super.clearAll();
        clearRoutes();
    }
    public TUser addDestination(){
        TUser p = new TUser();
        if(destinations== null){
            destinations = new LinkedHashSet<>();
        }
        destinations.add(p);
        destination = p;
        return p;
    }
    public void addDoSendOrder(String payment, String serviceCode, String distance, double price){
        addDoSendOrder(payment, serviceCode, distance, price, 1);
    }
    public void addDoSendOrder(String payment, String serviceCode, String distance, double price, float beratkiriman){
        addOrder(payment, serviceCode, price);
        addNewProduct(AppConfig.KEY_DOSEND, price);
        order.setService_type(AppConfig.KEY_DOSEND);
        if(packet != null){
            packet.setDistance(Double.parseDouble( distance ));
            packet.setBerat_asli(new BigDecimal( beratkiriman ));
            packet.setBerat_kiriman((int)beratkiriman);
        }
    }

    public void addDoJekOrder(String payment, String serviceCode, String distanceStr, double price) {
        addOrder(payment, serviceCode, price);
        addNewProduct(AppConfig.KEY_DOJEK);
        order.setService_type(AppConfig.KEY_DOJEK);
        double distance = 0;
        try {
            distance = Double.parseDouble( distanceStr );
        }catch (Exception e){}
        if(packet != null) packet.setDistance(distance);
    }

    public void addDoCarOrder(String payment, String serviceCode, String distanceStr, double price) {
        addOrder(payment, serviceCode, price);
        addNewProduct(AppConfig.KEY_DOCAR);
        order.setService_type(AppConfig.KEY_DOCAR);
        double distance = 0;
        try {
            distance = Double.parseDouble( distanceStr );
        }catch (Exception e){}
        packet.setDistance(distance);
    }

    public void addDoMoveOrder(String payment, String serviceCode, String distance, double price, float beratkiriman){
        addOrder(payment, serviceCode, price);
        addNewProduct(AppConfig.KEY_DOMOVE, price);
        order.setService_type(AppConfig.KEY_DOMOVE);
        packet.setDistance(Double.parseDouble( distance ));
        packet.setBerat_asli(new BigDecimal( beratkiriman ));
        packet.setBerat_kiriman((int)beratkiriman);
    }
    public void setDoMoveType(String doMoveType){
        this.doMoveType = doMoveType;
    }

    public String getDoMoveType() {
        return doMoveType;
    }

    public TUser find(TUser packet) {
        for (TUser p : destinations) {
            if(p.equals(packet)) return p;
        }
        return null;
    }

    public void updateOrigin(TUser origin) {
        this.origin = origin;
    }
}
