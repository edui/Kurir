package id.co.kurindo.kurindo.model;

import com.google.android.gms.maps.model.LatLng;

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
    private TUser origin;
    private TUser destination;
    private List<Leg> legs;
    private List<LatLng> bounds;

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

    public TUser getOrigin() {
        return origin;
    }

    public void setOrigin(TUser origin) {
        this.origin = origin;
    }

    public TUser getDestination() {
        return destination;
    }

    public void setDestination(TUser destination) {
        this.destination = destination;
    }

    public List<LatLng> getBounds() {
        return bounds;
    }

    public void setBounds(List<LatLng> bounds) {
        this.bounds = bounds;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
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
