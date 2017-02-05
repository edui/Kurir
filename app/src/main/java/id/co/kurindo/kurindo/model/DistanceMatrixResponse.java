package id.co.kurindo.kurindo.model;

import java.util.List;

/**
 * Created by dwim on 1/31/2017.
 */

public class DistanceMatrixResponse {
    private List<String> destination_addresses;
    private List<String> origin_addresses;
    private List<Element> rows;
    private String status;

    public List<String> getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(List<String> destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public List<String> getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(List<String> origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public List<Element> getRows() {
        return rows;
    }

    public void setRows(List<Element> rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
