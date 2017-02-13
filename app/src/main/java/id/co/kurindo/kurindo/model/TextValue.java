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

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = hash * prime + (getText() == null ? 0 : getText().hashCode());
        hash = hash * prime + (getValue() == null ? 0 : getValue().hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        TextValue t = (TextValue) o;
        if(getValue().equalsIgnoreCase(t.getValue())){
            return  getText().equalsIgnoreCase(t.getText());
        }
        return super.equals(o);
    }
}
