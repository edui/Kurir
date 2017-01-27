package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dwim on 1/3/2017.
 */

public class StatusHistory implements Parcelable {
    private String status;
    private String remarks;
    private User pic;
    private String location;
    private String created_date;
    private User created_by;


    protected StatusHistory(Parcel in) {
        created_date = in.readString();
        status = in.readString();
        remarks = in.readString();
        try {
            created_by = in.readParcelable(User.class.getClassLoader());
        }catch (Exception e){}

        try {
            pic = in.readParcelable(User.class.getClassLoader());
        }catch (Exception e){}
        location = in.readString();
    }

    public static final Creator<StatusHistory> CREATOR = new Creator<StatusHistory>() {
        @Override
        public StatusHistory createFromParcel(Parcel in) {
            return new StatusHistory(in);
        }

        @Override
        public StatusHistory[] newArray(int size) {
            return new StatusHistory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(created_date);
        dest.writeString(status);
        dest.writeString(remarks);
        dest.writeParcelable(created_by, flags);
        dest.writeParcelable(pic, flags);
        dest.writeString(location);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public User getPic() {
        return pic;
    }

    public void setPic(User pic) {
        this.pic = pic;
    }

    public User getCreated_by() {
        return created_by;
    }

    public void setCreated_by(User created_by) {
        this.created_by = created_by;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    @Override
    public String toString() {
        StringBuilder sb =new StringBuilder();
        sb.append(getCreated_date()+ (getCreated_by()==null? "":" \nby "+getCreated_by().getFirstname())+" "+getCreated_by().getLastname()+"\n");
        sb.append(getRemarks()+"\n");
        if(getPic()!=null){
            sb.append(getPic().getFirstname() +" "+getPic().getLastname() +" "+(getLocation()==null? "":" at "+getLocation())+"\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}
