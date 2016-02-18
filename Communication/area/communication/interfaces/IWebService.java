package area.communication.interfaces;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import area.domain.interfaces.*;
import area.domain.modelo.EntregaModel;
import area.domain.modelo.RecordModel;
import area.domain.modelo.RecursoModel;
import area.experiencias.tfg.Comment;
import area.experiencias.tfg.MiniActivity;
import area.experiencias.tfg.Experience;

public interface IWebService {
	
	/**
	 * 
	 * @param callback
	 */
	public void getToken(final IActionException<String> callback);
	
	/**
	 * Envía una reflexión al servidor
	 * @param ref			Reflexión a enviar
	 * @param callback		Delegado a ejecutar tras el envío de la reflexión
	 */
	//public void setReflection(Reflection ref, final IAction<Boolean> callback);
	
	/**
	 * Método que realiza el login del usuario
	 * @param email			Email con el que se intenta hacer login
	 * @param pass			Password del usuario
	 * @param callback		Delegado a ejecutar tras el intento de login
	 */
	public void login(String email, String pass, IAction<Boolean> callback);
	
	/**
	 *  Se encarga de sincronizar las reflexiones que haya guardadas localmente
	 * @return true si ya se consiguió sincronizar, false en otro caso
	 */
	//public boolean synchronizeReflection();
	
	/**
	 * Obtiene la lista de ids de las experiencias
	 * @param estado		Indica el estado de las experiencias que se solicitan
	 * @param callback		Delegado a ejecutar tras la obtención de los ids
	 */
	public void getExperiencesIds(int estado, IAction<String> callback);
	
	
	/**
	 * Obtiene los datos de la vista completa de las experiencias indicadas en los ids
	 * @param callback		Delegado a ejecutar tras la obtención de los datos
	 */
	public void getWholeViewExperiences(String ids, boolean local, IAction<List<Experience>> callback);
	
	/**
	 * Obtiene los comentarios de la experiencia
	 * @param experienceId		Id de la experiencia
	 * @param numComent			Número máximo de comentarios que vendrán en la respuesta
	 * @param callback			Delegado a ejecutar tras la obtención de los comentarios
	 */
	public void getComents(int experienceId, int numComent, IAction<List<Comment>> callback);
	
	/**
	 * Envía al servidor un comentario de manera asíncrona
	 * @param comentario		Comentario a enviar
	 * @param callback			Delegado a ejecutrar tras la el intento de envío
	 */
	public void setCommentAsync(Comment comentario, IAction<Boolean> callback);
	
	/**
	 * Envía un comentario al servidor en el mismo hilo desde que se llama
	 * @param datos		Dotos necesarios para enviar la petición
	 * @return
	 */
	public boolean setComment(JSONObject datos);
	
	/**
	 * Obtiene los Ids de las actividades a partir del activitySequenceId
	 * @param activitySequenceId		Id del activity sequence
	 * @param callback					Delegado a ejecutar tras obtener los ids
	 */
	public void getActivitiesIds(int activitySequenceId, IAction<String> callback);
	
	/**
	 * Obtine la vista mas pequeña de las actividades indicadas en el string de ids
	 * @param ids			String con los ids de las actividades de los que se pide la vista
	 * @param callback		Delegado a ejecutar tras la obtención de los datos
	 */
	public void getMiniViewActivities(String ids, int experienceId, IAction<List<MiniActivity>> callback);
	
	/**
	 * Envía una modificación de los datos de una experiencia de manera asíncrona.
	 * @param experiencia
	 * @param callback
	 */
	public void putExperienceAsync(Experience experiencia, IAction<Boolean> callback);
	
	/**
	 * Envía una modificación de los datos de una experiencia
	 * @param datos			Datos necesarios para enviar la petición
	 * @return				True si se actualizó correctamente y false en otro caso
	 */
	public boolean putExperience(JSONObject datos);
	
	/**
	 * Actualiza un campo de una experiencia
	 * @param datos		Datos a enviar al servidor para actualizar la experiencia
	 * @return			True si se actualizó correctamente y false en otro caso
	 */	
	public boolean putExperienceField(JSONObject datos);
	
	/**
	 * Envía una modificación de los datos de una experiencia de manera asíncrona.
	 * @param experiencia
	 * @param callback
	 */
	public void putActivityAsync(MiniActivity activity, IAction<Boolean> callback);
	
	/**
	 * Envía una modificación de los datos de una experiencia
	 * @param datos			Datos necesarios para enviar la petición
	 * @return				True si se actualizó correctamente y false en otro caso
	 */
	public boolean putActivity(JSONObject datos);
	
	/**
	 * Actualiza un campo de una experiencia
	 * @param datos		Datos a enviar al servidor para actualizar la experiencia
	 * @return			True si se actualizó correctamente y false en otro caso
	 */	
	public boolean putActivityField(JSONObject datos);
	
	/**
	 * Obtiene los records del activity o lesson plan solicitado
	 * @param id				Id del activity o lesson plan del que se piden los records
	 * @param tipoSolicitado	Indica si se solicitan los records de una actividad o de un lesson plan
	 * @param callback			Delegado a ejecutar tras la obtención de datos	
	 */
	public void getRecords(int id, String tipoSolicitado, boolean all, IAction<List<RecordModel>> callback);
	
	/**
	 * Crea un nuevo record con los datos proporcionados y lo asocia a la actividad o lesson plan indicado de manera asíncrona.
	 * Si el record ya existe actualiza sus campos.
	 * @param record		Record a crear en el servidor
	 * @param id			Id de la actividad o lesson plan al que se asocia el record
	 * @param tipo			Indica si se trata de añadir el record a un lesson plan o a una actividad.
	 * @param callback		Delegado a ejecutar tras la finalización del proceso
	 */
	public void createRecordAsync(RecordModel record, int id, String tipo, IAction<Boolean> callback);
	
	/**
	 * Crea un nuevo record con los datos proporcionados y lo asocia a la actividad o lesson plan indicado
	 * @param datos		Datos necesarios para enviar la petición
	 * @return			Id asignado al nuevo record
	 */
	public int createRecord(JSONObject datos);
	
	/**
	 * Actualiza el campo del record indicado en el Json
	 * @param datos		Datos necesarios para la petición
	 * @return			True si se actualizó correctamente y false en otro caso
	 */
	public boolean updateRecord(JSONObject datos);
	
	/**
	 * Elimina un record del servidor de manera asíncrona.
	 * @param recordId		Id del record a elimninar
	 * @param id			Id de la actividad a la que pertenece el record
	 * @param tipo			Indica si el record pertenece a un lesson plan o a una actividad
	 * @param callback		Delegado a ejecutar tras la respuesta del servidor
	 */
	public void deleteRecordAsync(int recordId, int id, Date created, String tipo, IAction<Boolean> callback);
	
	/**
	 * Elimina un record
	 * @param datos		Datos necesarios para poder ejecutar la petición
	 * @return			True si lo eliminó correctamente, false en otro caso.
	 */
	public boolean deleteRecord(JSONObject datos);
	
	/**
	 * Descarga el archivo asociado al record
	 * @param record
	 */
	public void descargaArchivoAsync(RecordModel record, int elementId);
	
	/**
	 * Crea una nueva actividad vacía en la experiencia indicada
	 * @param experienceId	Id de la experiencia donde se creará la actividad
	 * @param callback		Delgado a ejecutar tras la respuesta
	 */
	public void addActivityAsync(int experienceId, IAction<Integer> callback);
	
	/**
	 * Crea una nueva experiencia vacía
	 * @param callback		Delgado a ejecutar tras la respuesta
	 */
	public void addExperienceAsync(IAction<Integer> callback);
	
	/**
	 * Añade una experiencia de forma sincrona a partir de los datos del Json.
	 * @param datos
	 * @return
	 */
	public boolean addExperience(JSONObject datos);
	
	/**
	 * Añade una actividad de forma sincrona a partir de los datos del Json.
	 * @param datos
	 * @return
	 */
	public boolean addActivity(JSONObject datos);
	
	/**
	 * Obtiene la vista de los recursos de una experiencia
	 * @param experienceId	Id de la experiencia de la que se piden los recursos
	 * @param callback		Delegado a ejecutar tras la obtención de los datos
	 */
	public void getRecursosView(int experienceId, boolean all, boolean sitios, IAction<List<RecursoModel>> callback);
	
	/**
	 * Obtiene las entregas de una experiencia
	 * @param experienceId	Id de la experiencia de la que se piden las entregas
	 * @param callback		Delegado a ejecutar tras la obtención de los datos
	 */
	public void getEntregasView(int experienceId, boolean all, IAction<List<EntregaModel>> callback);
	
	/**
	 * Crea una nueva enterga vacía en la experiencia o actividad indicada indicada
	 * @param id			Id de la experiencia o actividad  donde se creará la entrega
	 * @param tipo			Indica si la entrega se creará en una experiencia o en una actividad
	 * @param callback		Delgado a ejecutar tras la respuesta
	 */
	public void addEntregaAsync(int id, String tipo, IAction<Integer> callback);
	
	/**
	 * Añade una entrega de forma sincrona a partir de los datos del Json.
	 * @param datos
	 * @return
	 */
	public boolean addEntrega(JSONObject datos);
	
	/**
	 * Envía una modificación de los datos de una experiencia de manera asíncrona.
	 * @param experiencia
	 * @param callback
	 */
	public void putEntregaAsync(EntregaModel entrega, int elementId, IAction<Boolean> callback);
	
	/**
	 * Envía una modificación de los datos de una experiencia
	 * @param datos			Datos necesarios para enviar la petición
	 * @return				True si se actualizó correctamente y false en otro caso
	 */
	public boolean putEntrega(JSONObject datos);
	
	/**
	 * Actualiza un campo de una experiencia
	 * @param datos		Datos a enviar al servidor para actualizar la experiencia
	 * @return			True si se actualizó correctamente y false en otro caso
	 */	
	public boolean putEntregaField(JSONObject datos);
	
	/**
	 * Petición por la que un alumno puede añadir un curso a su lista a través del código
	 * @param code			Código del curso a añadir
	 * @param callback		Delegado a ejecutar tras la petición
	 */
	public void addExperience(String code, IAction<Boolean> callback);
	
	/**
	 * Obtiene el código de la experiencia para compartir con los alumnos.
	 * @param expereienceId		Id de la expereiencia de la que se quiere el código
	 * @param callback			Delegado a ejecutar tras la obtención del código.
	 */
	public void getExperienceCode(int expereienceId, IAction<String> callback);
	
	/**
	 * Obtiene el json completo de una experiencia y lo procesa.
	 * @param id	Id de la experiencia de que se piden todos los datos
	 */
	public void getFullExperience(int id);
	
	/**
	 * Obtine el json completo de un activity y lo procesa.
	 * @param expId					Id de la experiencia a la que pertenece la actividad
	 * @param fullActivities		Json Array donde están las actividades
	 * @throws JSONException
	 * @throws Exception
	 */
	public void getFullActivity(int expId, JSONArray fullActivities) throws JSONException, Exception;

	/**
	 * Prepara para que la imagen de la ruta se guarde en la librería de imágenes
	 * @param ruta
	 */
	public void descargaImagenAsync(String ruta, boolean original);
	
	/**
	 * Comprueba el estado de actualización con respecto a lo almacenado en el servidor de las experiencias
	 * identificadas por los ids del parametro. Este método es síncrono.
	 * 
	 * @param ids	Ids de las experiencias de los que se quiere comprobar la sincronización
	 */
	public boolean getUpdatedAt(String ids);
	
	/**
	 * Elimina un elemento (actividad, curso, recurso, tarea, etc.)
	 * @param id			Id del elemento a eliminar.
	 * @param tipo			Tipo de elemento a eliminar
	 * @param callback		Delegado a ejecutar tras la petición
	 */
	public void deleteAsync(int id, String tipo, IAction<Boolean> callback);
	
	/**
	 * Elimina un elemento de forma sincrona
	 * @param datos
	 * @return
	 */
	public boolean delete(JSONObject datos);
	
	
}
