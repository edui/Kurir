package id.co.kurindo.kurindo.helper;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.model.City;

/**
 * Created by DwiM on 11/8/2016.
 */
public class Response {
    private List<City> cities = new ArrayList<City>();

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<City> getCities() {
        return cities;
    }
}
