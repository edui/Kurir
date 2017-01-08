package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dwim on 1/2/2017.
 */

public class ShippingCost implements Parcelable {
    private String information;
    private double cost;
    private String serviceCode;
    private int weight;
    private boolean doCar;
    private City origin;
    private City destination;
    public ShippingCost(){

    }

    public ShippingCost(Parcel in) {
        information = in.readString();
        cost = in.readDouble();
        serviceCode = in.readString();
        weight = in.readInt();
        doCar = Boolean.parseBoolean( in.readString() );
        origin = in.readParcelable(City.class.getClassLoader());
        destination = in.readParcelable(City.class.getClassLoader());
    }

    public static final Creator<ShippingCost> CREATOR = new Creator<ShippingCost>() {
        @Override
        public ShippingCost createFromParcel(Parcel in) {
            return new ShippingCost(in);
        }

        @Override
        public ShippingCost[] newArray(int size) {
            return new ShippingCost[size];
        }
    };

    public double getCost() {
        return cost;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isDoCar() {
        return doCar;
    }

    public void setDoCar(boolean doCar) {
        this.doCar = doCar;
    }

    public City getOrigin() {
        return origin;
    }

    public void setOrigin(City origin) {
        this.origin = origin;
    }

    public City getDestination() {
        return destination;
    }

    public void setDestination(City destination) {
        this.destination = destination;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(information);
        dest.writeDouble(cost);
        dest.writeString(serviceCode);
        dest.writeInt(weight);
        dest.writeString(Boolean.toString( doCar ));
        dest.writeParcelable(origin, flags);
        dest.writeParcelable(destination, flags);
    }
}
