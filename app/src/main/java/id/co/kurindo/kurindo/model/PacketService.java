package id.co.kurindo.kurindo.model;

/**
 * Created by DwiM on 11/12/2016.
 */
public class PacketService {
    private String code;
    private String text;
    private String etd;

    public PacketService(String code, String text, String etd){
        this.code = code;
        this.text = text;
        this.etd = etd;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEtd() {
        return etd;
    }

    public void setEtd(String etd) {
        this.etd = etd;
    }
}

