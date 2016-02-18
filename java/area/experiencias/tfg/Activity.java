package area.experiencias.tfg;

import android.graphics.Bitmap;

/**
 * Created by adrianbouza on 01/03/14.
 */
public class Activity extends MiniActivity {

    private String description;

    public Activity(int id, String name, String element_image_file_name, Bitmap image, String description) {
        super(id, name, element_image_file_name, image);
        this.description = description;
    }

    public Activity() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "description='" + description + '\'' +
                "} " + super.toString();
    }
}
