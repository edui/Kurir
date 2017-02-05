package id.co.kurindo.kurindo.model;

/**
 * Created by dwim on 1/31/2017.
 */

public class Element {
    TextValue distance;
    TextValue duration;
    String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
