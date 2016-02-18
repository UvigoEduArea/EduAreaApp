package area.experiencias.tfg;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 25/02/14.
 */
public interface MiniActivityListInterface {

    // Guardar Actividad
    public void saveActivity(MiniActivity activity);

    // Borrar Lista
    public void deleteList();

    // Obtener lista de actividades
    public ArrayList<MiniActivity> getList();

    // set image
    public void setImage(int id, Bitmap image);
}
