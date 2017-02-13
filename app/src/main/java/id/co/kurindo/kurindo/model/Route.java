package id.co.kurindo.kurindo.model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dwim on 1/31/2017.
 */

public class Route {
    private List<List<HashMap<String, String>>> routes;
    private TextValue distance;
    private TextValue duration;
    private double price;

    public List<List<HashMap<String, String>>> getRoutes() {
        return routes;
    }

    public void setRoutes(List<List<HashMap<String, String>>> routes) {
        this.routes = routes;
    }

    public TextValue getDistance() {
        return distance;
    }

    public void setDistance(TextValue distance) {
        this.distance = distance;
    }

    public TextValue getDuration() {
        return duration;
    }

    public void setDuration(TextValue duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        Route r = (Route) o;
        if(getDistance().equals(r.getDistance())){
            if(getDuration().equals(r.getDuration())){
                return true;
            }
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = hash * prime + (getDistance() == null ? 0 : getDistance().hashCode());
        hash = hash * prime + (getDuration() == null ? 0 : getDuration().hashCode());
        return hash;
    }
}
