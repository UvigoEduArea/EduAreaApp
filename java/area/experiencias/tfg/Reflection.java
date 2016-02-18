package area.experiencias.tfg;

import java.util.Date;

/**
 * Created by adrianbouza on 01/03/14.
 */
public class Reflection extends Entry{

    public static final String POSITIVE = "positive comment";
    public static final String NEGATIVE = "negative comment";
    public static final String FREE = "free text";

    private int id;
    private String type;
    private String text;

    public Reflection(String type, String text, Date lastUpdate) {
        super(Entry.REFLECTION, lastUpdate);
        this.type = type;
        this.text = text;
    }

    public Reflection() {
        super(Entry.REFLECTION);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Reflection{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                "} " + super.toString();
    }
}
