package id.co.kurindo.kurindo.model;

/**
 * Created by dwim on 2/2/2017.
 */

public class Payment {
    private String text;
    private String description;
    private String action;
    private double credit;
    private int status = 1;

    public Payment(String text){
        this(text, null);
    }
    public Payment(String text, String description){
        this(text, description, null);
    }
    public Payment(String text, String description, String action){
        this.text = text;
        this.description = description;
        this.action = action;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }
}
