package area.experiencias.tfg;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 01/03/14.
 */
public interface ReflectionListInterface {

    // Guardar Reflexión
    public void saveReflection(Reflection reflection);

    // Borrar Lista
    public void deleteList();

    // Obtener lista de reflexiones
    public ArrayList<Reflection> getList();

}
