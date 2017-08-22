package id.co.kurindo.kurindo.model;

/**
 * Created by DwiM on 5/18/2017.
 */

public class Help {
    private String id;
    private String title;
    private String description;
    private String clazz;
    private String image;

    public Help(String id, String title, String description, String image){
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
