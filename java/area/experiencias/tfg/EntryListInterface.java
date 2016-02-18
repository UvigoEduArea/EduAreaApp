package area.experiencias.tfg;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 18/03/14.
 */
public interface EntryListInterface {

    // Guardar Evidencia
    public void saveEntry(Entry entry);

    // Borrar Lista
    public void deleteList();

    // Obtener lista de evidencias
    public ArrayList<Entry> getList();

}
