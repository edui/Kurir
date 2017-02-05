package id.co.kurindo.kurindo.model;

/**
 * Created by dwim on 1/31/2017.
 */

public class TextValue {
    String text;
    String value;
    public TextValue(String text, String value){
        this.text = text;
        this.value = value;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
