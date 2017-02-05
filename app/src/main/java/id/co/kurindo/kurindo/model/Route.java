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
}
