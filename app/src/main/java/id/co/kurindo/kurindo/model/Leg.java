package id.co.kurindo.kurindo.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by DwiM on 5/7/2017.
 */

public class Leg {
    private TextValue distance;
    private TextValue duration;
    private LatLng start_location;
    private LatLng end_location;
    List<Step> steps;

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

    public LatLng getStart_location() {
        return start_location;
    }

    public void setStart_location(LatLng start_location) {
        this.start_location = start_location;
    }

    public LatLng getEnd_location() {
        return end_location;
    }

    public void setEnd_location(LatLng end_location) {
        this.end_location = end_location;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
