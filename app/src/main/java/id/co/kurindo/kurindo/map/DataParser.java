package id.co.kurindo.kurindo.map;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.DistanceMatrixResponse;
import id.co.kurindo.kurindo.model.Element;
import id.co.kurindo.kurindo.model.Leg;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.Step;
import id.co.kurindo.kurindo.model.TextValue;
import id.co.kurindo.kurindo.model.Vehicle;

/**
 * Created by dwim on 1/31/2017.
 */

public class DataParser {

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public Route parseRoutes(JSONObject jObject) {
        //Route route = new Route();
        List<Route> routeList = new ArrayList<>();
        //List<String> polylines = new ArrayList<>();
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {

            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                Route route = new Route();
                routeList.add(route);

                JSONObject boundsObj = ((JSONObject) jRoutes.get(i)).getJSONObject("bounds");
                JSONObject norteastObj = boundsObj.getJSONObject("northeast");
                JSONObject southwestObj = boundsObj.getJSONObject("southwest");
                LatLng norteast = new LatLng(norteastObj.getDouble("lat"), norteastObj.getDouble("lng"));
                LatLng southwest = new LatLng(southwestObj.getDouble("lat"), southwestObj.getDouble("lng"));
                List<LatLng> bounds = new ArrayList<>();
                bounds.add(norteast);
                bounds.add(southwest);
                route.setBounds(bounds);

                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                List path = new ArrayList<>();
                List<Leg> legs = new ArrayList<>();
                route.setLegs(legs);

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    JSONObject jLeg = (JSONObject) jLegs.get(j);
                    JSONObject distanceObj = jLeg.getJSONObject("distance");
                    JSONObject durationObj = jLeg.getJSONObject("distance");
                    JSONObject startLocObj = jLeg.getJSONObject("start_location");
                    JSONObject endLocObj = jLeg.getJSONObject("end_location");

                    TextValue distance = new TextValue(distanceObj.getString("text"), distanceObj.getString("value"));
                    TextValue duration = new TextValue(durationObj.getString("text"), durationObj.getString("value"));
                    LatLng startLocation = new LatLng(startLocObj.getDouble("lat"), startLocObj.getDouble("lng"));
                    LatLng endLocation = new LatLng(endLocObj.getDouble("lat"), endLocObj.getDouble("lng"));

                    route.setDistance(distance);
                    route.setDuration(duration);
                    Leg leg = new Leg();
                    legs.add(leg);
                    leg.setDistance(distance);
                    leg.setDuration(duration);
                    leg.setStart_location(startLocation);
                    leg.setEnd_location(endLocation);

                    jSteps = jLeg.getJSONArray("steps");
                    List<Step> steps = new ArrayList<>();
                    leg.setSteps(steps);

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        Step step = new Step();
                        steps.add(step);

                        JSONObject jStep = (JSONObject) jSteps.get(k);
                        JSONObject distanceObjS = jStep.getJSONObject("distance");
                        JSONObject durationObjS = jStep.getJSONObject("distance");
                        JSONObject startLocObjS = jStep.getJSONObject("start_location");
                        JSONObject endLocObjS = jStep.getJSONObject("end_location");

                        TextValue distanceS = new TextValue(distanceObjS.getString("text"), distanceObjS.getString("value"));
                        TextValue durationS = new TextValue(durationObjS.getString("text"), durationObjS.getString("value"));
                        LatLng startLocationS = new LatLng(startLocObjS.getDouble("lat"), startLocObjS.getDouble("lng"));
                        LatLng endLocationS = new LatLng(endLocObjS.getDouble("lat"), endLocObjS.getDouble("lng"));

                        step.setDistance(distanceS);
                        step.setDuration(durationS);
                        step.setStart_location(startLocationS);
                        step.setEnd_location(endLocationS);

                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        step.setPolyline(polyline);
                        path.addAll(step.getPath());
                            /*List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points /
                            for(int l=0;l<list.size();l++){
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude) );
                                hm.put("lng", Double.toString((list.get(l)).longitude) );
                                path.add(hm);
                            }*/
                    }
                    routes.add(path);
                }
                route.setRoutes(routes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getBestRoute(routeList);
    }

    private Route getBestRoute(List<Route> routeList) {
        if (routeList != null) {
            if (routeList.size() > 0) {
                return routeList.get(0);
            }
        }
        return null;
    }

    public PolylineOptions drawRoutes(List<List<HashMap<String, String>>> routes) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;

        // Traversing through all the routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = routes.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.RED);

            Log.d("drawRoutes", "drawRoutes lineoptions decoded");

        }
        return lineOptions;

    }

    public DistanceMatrixResponse parseDistance(JSONObject jObj) {
        DistanceMatrixResponse mtx = new DistanceMatrixResponse();
        List<String> origins = new ArrayList<>();
        List<String> destinations = new ArrayList<>();
        List<Element> elements = new ArrayList<>();
        try {
            JSONArray originArr = jObj.getJSONArray("origin_addresses");
            JSONArray destinationArr = jObj.getJSONArray("destination_addresses");
            JSONArray rows = jObj.getJSONArray("rows");
            for (int i = 0; i < originArr.length(); i++) {
                origins.add(originArr.getString(i));
                destinations.add(destinationArr.getString(i));

                JSONObject rowObj = rows.getJSONObject(i);
                JSONArray elementsArr = rowObj.getJSONArray("elements");
                for (int j = 0; j < elementsArr.length(); j++) {
                    Element element = new Element();
                    JSONObject elementObj = elementsArr.getJSONObject(j);
                    JSONObject distanceObj = elementObj.getJSONObject("distance");
                    JSONObject durationObj = elementObj.getJSONObject("duration");
                    element.setDistance(new TextValue(distanceObj.getString("text"), distanceObj.getString("value")));
                    element.setDuration(new TextValue(durationObj.getString("text"), durationObj.getString("value")));
                    elements.add(element);
                }
            }
            mtx.setDestination_addresses(destinations);
            mtx.setOrigin_addresses(origins);
            mtx.setRows(elements);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mtx;
    }

    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public Address parseAddress(JSONObject jObj) {
        Address address = null;
        try {
            JSONArray results = jObj.getJSONArray("results");
            if (results.length() > 0) {
                address = new Address();
                JSONObject result = results.getJSONObject(0);
                JSONArray addressArr = result.getJSONArray("address_components");
                for (int i = 0; i < addressArr.length(); i++) {
                    JSONObject addrObj = addressArr.getJSONObject(i);
                    JSONArray types = addrObj.getJSONArray("types");
                    String longName = addrObj.getString("long_name");
                    String type = "";
                    if (types.length() > 0) {
                        type = types.getString(0);
                    }
                    if (type.equalsIgnoreCase("route")) {
                        address.setAlamat(longName);
                    } else if (type.equalsIgnoreCase("administrative_area_level_4")) {
                        address.setDesa(longName);
                    } else if (type.equalsIgnoreCase("administrative_area_level_3")) {
                        address.setKecamatan(longName);
                    } else if (type.equalsIgnoreCase("administrative_area_level_2")) {
                        address.setKabupaten(longName);
                    } else if (type.equalsIgnoreCase("administrative_area_level_1")) {
                        address.setPropinsi(longName);
                    } else if (type.equalsIgnoreCase("country")) {
                        address.setNegara(longName);
                    }
                }
                String formattedAddress = result.getString("formatted_address");
                address.setFormattedAddress(formattedAddress);

                JSONObject geometry = result.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                address.setLocation(new LatLng(location.getDouble("lat"), location.getDouble("lng")));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

}