package area.experiencias.tfg;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 18/03/14.
 */
public interface EvidenceListInterface {

    // Guardar Evidencia
    public void saveEvidence(Evidence evidence);

    // Borrar Lista
    public void deleteList();

    // Obtener lista de evidencias
    public ArrayList<Evidence> getList();

    // Establece image
    public void setImage(int position, Bitmap bitmap);

}
