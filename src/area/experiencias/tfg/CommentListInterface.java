package area.experiencias.tfg;

import java.util.ArrayList;

/**
 * Created by adrianbouza on 18/03/14.
 */
public interface CommentListInterface {

    // Guardar Evidencia
    public void saveComment(Comment comment);

    // Borrar Lista
    public void deleteList();

    // Obtener lista de evidencias
    public ArrayList<Comment> getList();

}
