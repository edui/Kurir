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
    Set<Route> routes;

    static DoSendHelper helper;

    public static DoSendHelper getInstance(){
        if(helper == null) helper = new DoSendHelper();
        return helper;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route) {
        if(routes == null) routes = new LinkedHashSet<>();
        routes.add(route);
    }
    public void clearRoutes() {
        if(routes != null) routes.clear();
    }

    @Override
    public void clearAll() {
        super.clearAll();
        clearRoutes();
    }
}
