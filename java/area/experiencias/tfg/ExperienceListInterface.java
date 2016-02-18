package area.experiencias.tfg;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 23/02/14.
 */
public interface ExperienceListInterface {

    // Guardar Experiencia
    public void saveExperience(Experience experience);
    // Borrar Lista
    public void deleteList();

    // Obtener lista de experiencias
    public ArrayList<Experience> getList();

    // set image
    public void setImage(int id, Bitmap image);

}
