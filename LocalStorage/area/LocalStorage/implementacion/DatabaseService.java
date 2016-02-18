package area.LocalStorage.implementacion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jsoup.helper.StringUtil;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.domain.modelo.AnexosRecursoModel;
import area.domain.modelo.CommandModel;
import area.domain.modelo.DetallesModel;
import area.domain.modelo.EntregaModel;
import area.domain.modelo.PerfilModel;
import area.domain.modelo.RecordModel;
import area.domain.modelo.RecursoModel;
import area.domain.services.Services;
import area.experiencias.tfg.Comment;
import area.experiencias.tfg.Experience;
import area.experiencias.tfg.MiniActivity;
/**
 * Clase que gestiona la base de datos local de la aplicación
 * Se utiliza la librería sqlcipher para que la base de datos esté cifrada
 * @author Luis Pereiro Melón
 *
 */
public class DatabaseService extends SQLiteOpenHelper{

	/**
	 * Nombre interno de la base de datos
	 */
	public static final String DATABASE_NAME = "eduArea";
	
	/**
	 * Versión actual de la base de datos
	 */
	public static final int DATABASE_VERSION = 18;
	
	/**
	 * Password para encriptar la base de datos	
	 */
	private static final String password = "password a usar";
	
	private SQLiteDatabase db = null;
	
	public DatabaseService(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE perfil (email TEXT, pass TEXT, id INT, type INT DEFAULT 0 );");
		db.execSQL("CREATE TABLE experiences_ids (ids TEXT, estado INT DEFAULT -1);");
		db.execSQL("CREATE TABLE experience (id INT, name TEXT, image TEXT, description TEXT, short_description TEXT, activity_sequence_id INT, lesson_plan_id INT, description_student TEXT, code_to_share TEXT, updated_at TEXT, is_at_date INT DEFAULT 0, get_local INT DEFAULT 0, mantener_actualizado INT DEFAULT 0, local_id INT DEFAULT 0, definition TEXT, autor TEXT, permiso TEXT);");
		db.execSQL("CREATE TABLE comments (model TEXT, experience_id INT, user TEXT, comment TEXT, date TEXT);");
		db.execSQL("CREATE TABLE synchronize (command TEXT, data TEXT, created TEXT, record_id INT DEFAULT -1, activity_id INT DEFAULT -1);");
		db.execSQL("CREATE TABLE activities_ids (experience_id INT, ids TEXT);");
		db.execSQL("CREATE TABLE mini_activity (id INT, name TEXT, image TEXT, start TEXT, end TEXT, progress TEXT, experience_id INT, description TEXT, local_id INT DEFAULT 0, description_student TEXT, definition TEXT, autor TEXT, updated_at TEXT DEFAULT '', permiso TEXT);");
		db.execSQL("CREATE TABLE records (activity_id INT, id INT, record_type TEXT, title TEXT, description, TEXT, created_at TEXT, updated_at TEXT, data TEXT, local_data TEXT, faces_array TEXT, blurred TEXT, video_frame TEXT, autor TEXT, permiso TEXT);");
		db.execSQL("CREATE TABLE detalles (experience_id INT, detalles TEXT);");
		db.execSQL("CREATE TABLE activity_id (activity_id INT, experience_id INT, position INT DEFAULT 0);");
		db.execSQL("CREATE TABLE resource_id (resource_id INT, experience_id INT);");
		db.execSQL("CREATE TABLE resources (resource_id INT, experience_id INT, url TEXT, descripcion TEXT, titulo TEXT, imagen TEXT, type TEXT, definition TEXT, autor TEXT,updated_at TEXT DEFAULT '', permiso TEXT);");
		db.execSQL("CREATE TABLE entrega_id (entrega_id INT, experience_id INT);");
		db.execSQL("CREATE TABLE entregas (entrega_id INT, experience_id INT, descripcion TEXT, titulo TEXT, imagen TEXT, local_id INT DEFAULT 0, definition TEXT, autor TEXT, updated_at TEXT DEFAULT '', permiso TEXT);");
		db.execSQL("CREATE TABLE resources_appends (resource_id INT, id INT, type_append TEXT, snippet_url TEXT, document TEXT, file_name TEXT, address TEXT, latitude REAL DEFAULT 0, longitude REAL DEFAULT 0);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2){
			db.execSQL("CREATE TABLE detalles (experience_id INT, detalles TEXT);");
		}
		if (oldVersion < 3){
			db.execSQL("ALTER TABLE records ADD COLUMN video_frame TEXT;");
		}
		if (oldVersion < 4){
			db.execSQL("CREATE TABLE activity_id (activity_id INT, experience_id INT);");
			db.execSQL("ALTER TABLE mini_activity ADD COLUMN local_id INT DEFAULT 0;");
		}
		if (oldVersion < 5){
			db.execSQL("CREATE TABLE resource_id (resource_id INT, experience_id INT);");
			db.execSQL("CREATE TABLE resources (resource_id INT, experience_id INT, url TEXT, descripcion TEXT, titulo TEXT, imagen TEXT);");
			db.execSQL("CREATE TABLE entrega_id (entrega_id INT, experience_id INT);");
			db.execSQL("CREATE TABLE entregas (entrega_id INT, experience_id INT, descripcion TEXT, titulo TEXT, imagen TEXT, local_id INT DEFAULT 0);");
		}
		if (oldVersion < 6){
			db.execSQL("ALTER TABLE activity_id ADD COLUMN position INT DEFAULT 0;");
		}
		if (oldVersion < 7){
			db.execSQL("ALTER TABLE experience ADD COLUMN description_student TEXT;");
			db.execSQL("ALTER TABLE mini_activity ADD COLUMN description_student TEXT;");
			db.execSQL("ALTER TABLE perfil ADD COLUMN id INT;");
			db.execSQL("ALTER TABLE perfil ADD COLUMN type INT DEFAULT 0;");
		}
		if (oldVersion < 8){
			db.execSQL("ALTER TABLE experience ADD COLUMN code_to_share TEXT;");
		}
		if (oldVersion < 9){
			db.execSQL("CREATE TABLE resources_appends (resource_id INT, id INT, type_append TEXT, snippet_url TEXT, document TEXT, file_name TEXT);");
		}
		if (oldVersion < 10){
			db.execSQL("ALTER TABLE experience ADD COLUMN updated_at TEXT;");
		}
		if (oldVersion < 11){
			db.execSQL("ALTER TABLE experience ADD COLUMN is_at_date INT DEFAULT 0;");
			db.execSQL("ALTER TABLE experience ADD COLUMN get_local INT DEFAULT 0;");
			db.execSQL("ALTER TABLE experience ADD COLUMN mantener_actualizado INT DEFAULT 0;");
		}
		if (oldVersion < 12){
			db.execSQL("ALTER TABLE experience ADD COLUMN local_id INT DEFAULT 0;");
		}
		if (oldVersion < 13){
			db.execSQL("ALTER TABLE resources_appends ADD COLUMN address TEXT;");
			db.execSQL("ALTER TABLE resources_appends ADD COLUMN latitude REAL DEFAULT 0;");
			db.execSQL("ALTER TABLE resources_appends ADD COLUMN longitude REAL DEFAULT 0;");
		}
		if (oldVersion < 14){
			db.execSQL("ALTER TABLE resources ADD COLUMN type TEXT;");
		}
		if (oldVersion < 15){
			db.execSQL("ALTER TABLE experience ADD COLUMN definition TEXT;");
			db.execSQL("ALTER TABLE mini_activity ADD COLUMN definition TEXT;");
			db.execSQL("ALTER TABLE resources ADD COLUMN definition TEXT;");
			db.execSQL("ALTER TABLE entregas ADD COLUMN definition TEXT;");
		}
		if (oldVersion < 16){
			db.execSQL("ALTER TABLE records ADD COLUMN autor TEXT;");
			db.execSQL("ALTER TABLE experience ADD COLUMN autor TEXT;");
			db.execSQL("ALTER TABLE mini_activity ADD COLUMN autor TEXT;");
			db.execSQL("ALTER TABLE resources ADD COLUMN autor TEXT;");
			db.execSQL("ALTER TABLE entregas ADD COLUMN autor TEXT;");
			
		}
		if (oldVersion < 17){
			db.execSQL("ALTER TABLE records ADD COLUMN permiso TEXT;");			
		}
		if (oldVersion < 18){
			db.execSQL("ALTER TABLE experience ADD COLUMN permiso TEXT;");	
			db.execSQL("ALTER TABLE mini_activity ADD COLUMN permiso TEXT;");
			db.execSQL("ALTER TABLE resources ADD COLUMN permiso TEXT;");	
			db.execSQL("ALTER TABLE entregas ADD COLUMN permiso TEXT;");
		}
		
		
	}

	/**
	 * Guarda en la tabla de perfil los datos del usuario
	 * @param perfil		Instancia del perfil con los datos del usuario
	 * @throws Exception	Lanza una excepción en caso de producirse
	 */
	public synchronized void setPerfil(PerfilModel perfil) throws Exception{
		
		try {
			
			ContentValues values = new ContentValues();
			values.put("email", perfil.getEmail());
			values.put("pass", perfil.getPass());
			values.put("id", perfil.getId());
			values.put("type", perfil.getType());
			db.insert("perfil", null, values);
			
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	public synchronized void open(){
		db = getWritableDatabase(password);
	}
	public synchronized void close(){
		db.close();
	}
	/**
	 * Obtiene una instancia del Perfil del usuario a partir de los datos guardados en la base de datos
	 * @return				Instancia del perfil del usuario
	 * @throws Exception	Lanza una excepción en caso de producirse
	 */
	public synchronized PerfilModel getPerfil() throws Exception{
		
		try {
			
			String[] columns = {"email",	//0
							   "pass",		//1
							   "id",		//2
							   "type"};		//3
			Cursor cursor = db.query("perfil", columns, null, null, null, null, null);
			if(cursor.moveToFirst()){
				PerfilModel perfil = new PerfilModel(cursor.getString(0), cursor.getString(1));
				perfil.setId(cursor.getInt(2));
				perfil.setType(cursor.getInt(3));
				cursor.close();
				
				return perfil;
			};
			cursor.close();
			
			return null;
		} catch (Exception e){
			if (db != null){
				
			}
			throw e;
		}
	}
	
	public synchronized void deleteAll(){
		
		db.delete("perfil", null, null);
		db.delete("experiences_ids", null, null);
		db.delete("experience", null, null);
		db.delete("comments", null, null);
		db.delete("synchronize", null, null);
		db.delete("activities_ids", null, null);
		db.delete("mini_activity", null, null);
		db.delete("records", null, null);
		db.delete("detalles", null, null);
		db.delete("activity_id", null, null);
		db.delete("resource_id", null, null);
		db.delete("resources", null, null);
		db.delete("entrega_id", null, null);
		db.delete("entregas", null, null);
		db.delete("resources_appends", null, null);
		
	}
	
	
	/**
	 * Borra el perfil actual de la base de datos
	 * @throws Exception 	Lanza una excepción en caso de producirse
	 */
	public synchronized void deletePerfil() throws Exception{
		
		try {
			
			db.delete("perfil", null, null);
			
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
		
	/**
	 * Guarda en la base de datos el string con los ids de las experiencias que se van a mostrar.
	 * Se borran los anteriores
	 * @param ids			String con los ids de la experiencias separados por comas
	 * @param estado		Estado de las experiencias de esos ids.
	 * @throws Exception 	Lanza la excepción en caso de producirse
	 */
	public synchronized void setExperiencesIds (String ids, int estado) throws Exception{
		try {
			String where = "estado=?";
			String[] whereArgs = {estado + ""};
			db.delete("experiences_ids", where, whereArgs);
			ContentValues values = new ContentValues();
			values.put("ids", ids);
			values.put("estado", estado);
			db.insert("experiences_ids", null, values);
			
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	/**
	 * Borra la tabla de los ids de las Experiencias
	 * @throws Exception
	 */
	public synchronized void deleteExperiencesIds() throws Exception{
		
		try {
			db.delete("experiences_ids", null, null);
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	
	/**
	 * Obtiene los ids de las experiencias guardados en la base de datos
	 * @return	String con los ids de la experiencias separados por comas
	 */
	public synchronized String getExperiencesIds(int estado){
		String ids = "";
		try {
			String[] columns = {"ids",		//0
								"estado"};	//1
			String where = "estado=?";
			String[] whereArgs = {estado + ""};
			Cursor cur = db.query("experiences_ids", columns, where, whereArgs, null, null, null);
			if(cur.moveToFirst())
				do{
					ids = cur.getString(0);
				}while(cur.moveToNext());
			cur.close();
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return ids;
	}
	
	/**
	 * Guarda una experiencia en la base de datos
	 * @param experience	Experiencia que se va a guardar
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized void setExperience(Experience experience) throws Exception{
		
		try {
			Experience exp = getExpirience(experience.getId());
			if(exp != null){
				
				ContentValues values = new ContentValues();
				values.put("name", experience.getName());
				values.put("image", experience.getElement_image_file_name());
				values.put("description", experience.getDescription());
				values.put("short_description", experience.getShortDescription());
				values.put("activity_sequence_id", experience.getActivity_sequence_id());
				values.put("lesson_plan_id", experience.getLessonPlanId());
				values.put("description_student", experience.getDescriptionStudent());
				values.put("updated_at", experience.getUpdatedAt());
				values.put("definition", experience.getDefinition());
				values.put("autor", experience.getAutor());
				values.put("permiso", experience.getPermiso());
				String where = "id=?";
				String[] whereArgs = {experience.getId()+""};
				db.update("experience", values, where, whereArgs);
				
				
			}else{
				
				ContentValues values = new ContentValues();
				values.put("id", experience.getId());
				values.put("name", experience.getName());
				values.put("image", experience.getElement_image_file_name());
				values.put("description", experience.getDescription());
				values.put("short_description", experience.getShortDescription());
				values.put("activity_sequence_id", experience.getActivity_sequence_id());
				values.put("lesson_plan_id", experience.getLessonPlanId());
				values.put("description_student", experience.getDescriptionStudent());
				values.put("updated_at", experience.getUpdatedAt());
				values.put("definition", experience.getDefinition());
				values.put("autor", experience.getAutor());
				values.put("permiso", experience.getPermiso());
				db.insert("experience", null, values);
					
			}
						
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	/**
	 * Obtiene la fecha de ultima modificación que hay guardada en la base de datos
	 * @param experienceId		Experiecia de la que se quiere la fecha de ultima modificación
	 * @return
	 */
	public synchronized String getExperienceUpdatedAt(int experienceId){
		String updatedAt = "";
		try {
			String[] columns = {"updated_at"};			//0
			String where = "id=?";
			String[] whereArgs = {experienceId+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				updatedAt = cursor.getString(0);
			}
			cursor.close();
			return updatedAt;
		} catch (Exception e){
			if (db != null){
			}
			throw e;
		}
	}
	/**
	 * Guarda en la tabla de experiencias si esta está actualizada con respecto al servidor
	 * @param isUpdated			Indica si la experiencia está actualizada (0) o no (1)
	 * @param experienceId		Id de la experiencia a la que pertenece el código
	 * @throws Exception
	 */
	public synchronized void setExperienceIsUpdated(int isUpdated, int experienceId) throws Exception{
		try {
			ContentValues values = new ContentValues();
			values.put("is_at_date", isUpdated);
			String where = "id=?";
			String[] whereArgs = {experienceId+""};
			db.update("experience", values, where, whereArgs);
		} catch (Exception e) {
			if (db != null){
			}
			throw e;
		}
	}
	
	/**
	 * Comprueba si la experiencia está actualizada
	 * @param experienceId		Experiecia de la que se quiere saber si está actualizada
	 * @return					False si no lo está, true en otro caso.
	 */
	public synchronized boolean getExperienceIsUpdated(int experienceId){
		boolean isAtDate = true;
		try {
			String[] columns = {"is_at_date"};			//0
			String where = "id=?";
			String[] whereArgs = {experienceId+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				isAtDate = cursor.getInt(0) == 0;
			}
			cursor.close();
			return isAtDate;
		} catch (Exception e){
			if (db != null){
			}
			throw e;
		}
	}
	
	/**
	 * Comprueba si la experiencia está actualizada
	 * @param experienceId		Experiecia de la que se quiere saber si está actualizada
	 * @return					False si no lo está, true en otro caso.
	 */
	public synchronized boolean getExperienceIsUpdatedFromLessonPlanId(int id){
		boolean isAtDate = true;
		try {
			String[] columns = {"is_at_date"};			//0
			String where = "lesson_plan_id=?";
			String[] whereArgs = {id+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				isAtDate = cursor.getInt(0) == 0;
			}
			cursor.close();
			return isAtDate;
		} catch (Exception e){
			if (db != null){
			}
			throw e;
		}
	}
	
	/**
	 * Guarda en la tabla de experiencias si se quiere que la experiencia se actualice automáticamente
	 * @param isUpdated			Indica si la experiencia está actualizará automaticamente (1 si) (0 no)
	 * @param experienceId		Id de la experiencia a la que pertenece el código
	 * @throws Exception
	 */
	public synchronized void setExperienceMantenerActualizado(int mantener, int experienceId){
		try {
			ContentValues values = new ContentValues();
			values.put("mantener_actualizado", mantener);
			String where = "id=?";
			String[] whereArgs = {experienceId+""};
			db.update("experience", values, where, whereArgs);
		} catch (Exception e) {
			if (db != null){
			}
			//throw e;
		}
	}
	
	
	/**
	 * Obtiene si la experiencia se tiene que mantener actualizada
	 * @param experienceId		Experiecia de la que se quiere saber si se tiene que mantener actualizada.
	 * @return					True si se tiene que mantener, false en otro caso.
	 */
	public synchronized boolean getExperienceMantenerActualizado(int experienceId){
		boolean mantener = false;
		try {
			String[] columns = {"mantener_actualizado"};			//0
			String where = "id=?";
			String[] whereArgs = {experienceId+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				mantener = cursor.getInt(0) == 1;
			}
			cursor.close();
			return mantener;
		} catch (Exception e){
			if (db != null){
			}
			throw e;
		}
	}
	
	/**
	 * Obtiene si la experiencia se tiene que mantener actualizada
	 * @param experienceId		Experiecia de la que se quiere saber si se tiene que mantener actualizada.
	 * @return					True si se tiene que mantener, false en otro caso.
	 */
	public synchronized boolean getExperienceMantenerActualizadoFromLessonPlanId(int id){
		boolean mantener = false;
		try {
			String[] columns = {"mantener_actualizado"};			//0
			String where = "lesson_plan_id=?";
			String[] whereArgs = {id+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				mantener = cursor.getInt(0) == 1;
			}
			cursor.close();
			return mantener;
		} catch (Exception e){
			if (db != null){
			}
			throw e;
		}
	}
	
	
	/**
	 * Guarda el codigo de la experiencia para compartir con los alumnos
	 * @param code				Código para compartir la experiencia
	 * @param experienceId		Id de la experiencia a la que pertenece el código
	 * @throws Exception
	 */
	public synchronized void setExperienceCode(String code, int experienceId) throws Exception{
		
		try {
			
			ContentValues values = new ContentValues();
			values.put("code_to_share", code);
			String where = "id=?";
			String[] whereArgs = {experienceId+""};
			db.update("experience", values, where, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	/**
	 * Obtiene el código de una expereiencia para compartir con los alumnos
	 * @param experienceId		Experiecia de la que se quiere el código
	 * @return
	 */
	public synchronized String getExperienceCode(int experienceId){
		String code = null;
		try {
			String[] columns = {"code_to_share"};			//0
			String where = "id=?";
			String[] whereArgs = {experienceId+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
					code = cursor.getString(0);
			}
			cursor.close();
			return code;
		} catch (Exception e){
			if (db != null){
			}
			throw e;
		}
	}
	
	/**
	 * Obtiene la experiencia identificada el id
	 * @param id			Id de la experiencia a buscar en la base de datos
	 * @return				Experiencia identificada por el id, o null en caso contrario
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized Experience getExpirience(int id) {
		Experience exp = null;
		try {
			String[] columns = {"id",						//0
							   "name",						//1
							   "image",						//2
							   "description",				//3
							   "short_description",			//4
							   "activity_sequence_id",		//5
							   "lesson_plan_id",			//6
							   "description_student",		//7
							   "updated_at",				//8
							   "definition",				//9
				  			   "autor",						//10
							   "permiso"};					//11
			String where = "id=?";
			String[] whereArgs = {id+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
					exp = new Experience();
					exp.setId(cursor.getInt(0));
					exp.setName(cursor.getString(1));
					exp.setElement_image_file_name(cursor.getString(2));
					exp.setDescription(cursor.getString(3));
					exp.setShortDescription(cursor.getString(4));
					exp.setActivity_sequence_id(cursor.getInt(5));
					exp.setLessonPlanId(cursor.getInt(6));
					exp.setDescriptionStudent(cursor.getString(7));
					exp.setUpdatedAt(cursor.getString(8));
					exp.setDefinition(cursor.getString(9));
					exp.setAutor(cursor.getString(10));
					exp.setPermiso(cursor.getString(11));
			}
			cursor.close();
		} catch (Exception e){
//			if (db != null){
//				
//			}
//			throw e;
		}
		return exp;
	}
		
	/**
	 * Obtiene una lista con las experiencias guardadas en la base de datos
	 * @return				Lista con las experiencias
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized List<Experience> getExperiences() throws Exception{
		
		List<Experience> experiencias = new ArrayList<Experience>();
		
		
		try {
			
			String[] columns = {"id",						//0
							   "name",						//1
							   "image",						//2
							   "description",				//3
							   "short_description",			//4
							   "activity_sequence_id",		//5
							   "lesson_plan_id",			//6
							   "description_student",		//7
							   "updated_at",				//8
							   "definition",				//9
							   "autor",						//10
							   "permiso"};					//11
			Cursor cursor = db.query("experience", columns, null, null, null, null, null);
			if(cursor.moveToFirst())
				do{
					Experience exp = new Experience();
					exp.setId(cursor.getInt(0));
					exp.setName(cursor.getString(1));
					exp.setElement_image_file_name(cursor.getString(2));
					exp.setDescription(cursor.getString(3));
					exp.setShortDescription(cursor.getString(4));
					exp.setActivity_sequence_id(cursor.getInt(5));
					exp.setLessonPlanId(cursor.getInt(6));
					exp.setDescriptionStudent(cursor.getString(7));
					exp.setUpdatedAt(cursor.getString(8));
					exp.setDefinition(cursor.getString(9));
					exp.setAutor(cursor.getString(10));
					exp.setPermiso(cursor.getString(11));
					experiencias.add(exp);
					
				}while(cursor.moveToNext());
				
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return experiencias;
	}
	
	/**
	 * Obtiene las experiencias indicadas en la lista de ids
	 * @param ids						Ids de las experiencias solicitadas separados por comas
	 * @return							Lista con las experiencias
	 * @throws NumberFormatException	Lanza la excepción en caso de producirse
	 * @throws Exception				Lanza la excepción en caso de producirse
	 */
	public synchronized List<Experience> getExperiences(String ids) throws NumberFormatException, Exception{
		List<Experience> lista = new ArrayList<Experience>();
		String[] idsArray = ids.split(",");
		for (int i = 0; i < idsArray.length; i++){
			Experience e = getExpirience(Integer.parseInt(idsArray[i]));
			if (e != null)
				lista.add(e);
		}
		return lista;
	}
	

	/**
	 * Guarda un comentario en la base de datos o lo actualiza en caso de que ya exista
	 * @param comentario	Objeto con los datos del comentario
	 * @throws Exception 	Lanza la excepción en caso de producirse
	 */
	public synchronized void setComment(Comment comentario) throws Exception{
		
		try {
			Comment com = getComment(comentario.getModelId(),comentario.getComment(), Services.Utilidades().DateToString(comentario.getDate()));
			if(com != null){
				
				ContentValues values = new ContentValues();
				values.put("experience_id", comentario.getModelId() + "");
				values.put("user", comentario.getUser());
				values.put("comment", comentario.getComment());
				values.put("date", Services.Utilidades().DateToString(comentario.getDate()));
				String where = "experience_id=? AND comment=?";
				String[] whereArgs = {comentario.getModelId()+"", comentario.getComment()};
				db.update("comments", values, where, whereArgs);
				
				
			}else{
				
				ContentValues values = new ContentValues();
				values.put("experience_id", comentario.getModelId());
				values.put("user", comentario.getUser());
				values.put("comment", comentario.getComment());
				values.put("date", Services.Utilidades().DateToString(comentario.getDate()));
				db.insert("comments", null, values);
					
			}
						
		} catch (Exception e) {
			if (db != null)
				
			throw e;
		}
	}
	
	/**
	 * Obtiene el comentario identificado por modelo, modelId y fecha
	 * @param model			Tipo de comentario
	 * @param modelId		Id del modelo
	 * @param comment		Comentario
	 * @param date			Fecha en que se hizo el comentario
	 * @return				Objeto con el comentario a buscar
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized Comment getComment(int modelId, String comment, String date) throws Exception{
		

		Comment com = null;
		
		
		try {
			
			String[] columns = {"user",			//0
							   "comment",		//1
							   "date"};			//2
			String where = "experience_id=? AND comment=?";
			String[] whereArgs = {modelId+"", comment};
			Cursor cursor = db.query("comments", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
					com = new Comment();
					com.setUser(cursor.getString(0));
					com.setComment(cursor.getString(1));
					com.setDate(Services.Utilidades().StringToDate(cursor.getString(2)));
					com.setModelId(modelId);
			}
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return com;
	}
	
	/**
	 * Obtiene la lista de comentarios guardados en la base de datos para la experiencia especificada
	 * @param experienceId	Id de la experiencia
	 * @return				Lista con los comentarios de este recurso
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized List<Comment> getComments(int experienceId) throws Exception{
		List<Comment> comentarios = new ArrayList<Comment>();
		
		
		
		try {
			
			String[] columns = {"experience_id",	//0
							   "user",				//1
							   "comment",			//2
							   "date"};				//3
			
			String where = "experience_id=?";
			String[] whereArgs = {experienceId+""};
			String orderBy = "date DESC";
			Cursor cursor = db.query("comments", columns, where, whereArgs, null, null, orderBy);
			if(cursor.moveToFirst())
				do{
					Comment com = new Comment();
					com.setModelId(cursor.getInt(0));
					com.setUser(cursor.getString(1));
					com.setComment(cursor.getString(2));
					com.setDate(Services.Utilidades().StringToDate(cursor.getString(3)));
					comentarios.add(com);
				}while(cursor.moveToNext());
				
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return comentarios;
	}
	
	public synchronized int getNumComentarios(int expId){
		int numComents = 0;
		String[] columns = {"experience_id"};

		String where = "experience_id=?";
		String[] whereArgs = {expId+""};
		Cursor cursor = db.query("comments", columns, where, whereArgs, null, null, null);
		
		numComents = cursor.getCount();
	
		cursor.close();
		return numComents;
	}
	
	public synchronized List<DetallesModel> getDetalles(int experienceId) throws Exception{
		List<DetallesModel> listaDetalles = new ArrayList<DetallesModel>();
		
		
		
		try {
			
			String[] columns = {"experience_id",	//0
							   "detalles"};			//1
			
			String where = "experience_id=?";
			String[] whereArgs = {experienceId+""};
			Cursor cursor = db.query("detalles", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst())
				do{
					DetallesModel det = new DetallesModel();
					det.setExperienceId(cursor.getInt(0));
					det.setDetalles(cursor.getString(1));
					listaDetalles.add(det);
				}while(cursor.moveToNext());
				
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return listaDetalles;
	}
	
	/**
	 * Guarda los detalles de una experiencia
	 * @param detalles
	 * @throws Exception
	 */
	public synchronized void setDetalles(DetallesModel detalles) throws Exception{
		
		try {
			
			
			
			ContentValues values = new ContentValues();
			values.put("experience_id", detalles.getExperienceId());
			values.put("detalles", detalles.getDetalles());
			db.insert("detalles", null, values);
			
		} catch (Exception e) {
			if (db != null)
				
			throw e;
		}
	}
	
	
	
	/**
	 * Guarda en la tabla de sincronización el comando y los datos que se enviarán al servidor
	 * @param comando		Comando a ejecutar en el servidor
	 * @param datos			Datos que se envían al servidor
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized void setCommand(CommandModel comando) throws Exception{
	
		
		try {
			deleteCommand(comando);
		
			
			ContentValues values = new ContentValues();
			values.put("command", comando.getCommand());
			values.put("data", comando.getData());
			values.put("created", comando.getCreado());
			values.put("record_id", comando.getRecordId());
			values.put("activity_id", comando.getActivityId());
			db.insert("synchronize", null, values);
			
		} catch (Exception e) {
			if (db != null)
				
			throw e;
		}	
	}
	
	/**
	 * Obtiene el primer comando que hay que ejecutar de la lista de sincronización
	 * @return		Comando a ejecutar con sus datos necesarios
	 */
	public synchronized CommandModel getCommand(){
		
		
		CommandModel comando = null;
		try {
			
			String[] columns = {"command",		//0
								"data",			//1
								"created",		//2
								"record_id",	//3
								"activity_id"};	//4
			Cursor cur = db.query("synchronize", columns, null, null, null, null, null);
			
			if(cur.moveToFirst()){
					comando = new CommandModel();
					comando.setCommand(cur.getString(0));
					comando.setData(cur.getString(1));
					comando.setCreado(cur.getString(2));
					comando.setRecordId(cur.getInt(3));
					comando.setActivityId(cur.getInt(4));
			}
			cur.close();
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return comando;
	}
	
	/**
	 * Elimina el comando de la tabla de sincronización
	 * @param comando	Comando a eliminar de la tabla
	 */
	public synchronized void deleteCommand(CommandModel comando){
		
		try {
			
			String whereClause = "command=? AND data=?";
			String[] whereArgs = {comando.getCommand(),
								  comando.getData()};
			db.delete("synchronize", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Borra la tabla de sincronización de la base de datos
	 * @throws Exception 	Lanza una excepción en caso de producirse
	 */
	public synchronized void deleteSincronizacion() throws Exception{
		
		try {
			
			db.delete("synchronize", null, null);
			
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	/**
	 * Elimina los detalles de una experiencia para poder actualizarlos
	 * @param experienceId Id de la experiencia de la que se borrarán los detalles
	 */
	public synchronized void deleteDetalles(int experienceId){
		
		try {
			
			String whereClause = "experience_id=?";
			String[] whereArgs = {experienceId + ""};
			db.delete("detalles", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Elimina el comando de la tabla de sincronización
	 * @param comando	Comando a eliminar de la tabla
	 */
	public synchronized void deleteCommand(String created){
		
		try {
			
			String whereClause = "created=?";
			String[] whereArgs = {created};
			db.delete("synchronize", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Elimina los ids de las actividades de la experiencia indicada
	 * @param experienceId		Id de la experiencia de la que se borran los ids de sus actividades
	 */
	public synchronized void deleteActivitiesIds(int experienceId){
		
		try {
			
			String whereClause = "experience_id=? AND activity_id>0";
			String[] whereArgs = {experienceId+""};
			db.delete("activity_id", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Elimina el activity Id de la tabla
	 * @param activityId
	 */
	public synchronized void deleteActivityId(int activityId){
		
		try {
			
			String whereClause = "activity_id=?";
			String[] whereArgs = {activityId+""};
			db.delete("activity_id", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Añade un id de actividad relacionado con su id de experiencia
	 * @param experienceId		Id de la experiencia a la que pertenece la actividad
	 * @param activityId		Id de la actividad
	 */
	public synchronized void setActivityId (int experienceId, int activityId, int position){
		
		
		try {
				ContentValues values = new ContentValues();
				values.put("experience_id", experienceId);
				values.put("activity_id", activityId);
				values.put("position", position);
				
				db.insert("activity_id", null, values);
				
					
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	/**
	 * Actualiza el id local de una actividad por el que acaba de dar el servidor
	 * @param activityId	Nuevo Id
	 * @param prevId		Id local
	 */
	public synchronized void updateActivityId(int activityId, int prevId){
		
		
		ContentValues values = new ContentValues();
		values.put("activity_id", activityId);
		String where = "activity_id=?";
		String[] whereArgs = {prevId+""};
		db.update("activity_id", values, where, whereArgs);
		
	}
	/**
	 * Actualiza el id local de una entrea por el que acaba de dar el servidor
	 * @param activityId	Nuevo Id
	 * @param prevId		Id local
	 */
	public synchronized void updateEntregaId(int activityId, int prevId){
		
		
		ContentValues values = new ContentValues();
		values.put("entrega_id", activityId);
		String where = "entrega_id=?";
		String[] whereArgs = {prevId+""};
		db.update("entrega_id", values, where, whereArgs);
		
	}
	
	/**
	 * Obtiene la lista de ids de las actividades pertenecientes a una experiencia
	 * @param experienceId
	 * @return
	 */
	public synchronized String getActivityIds(int experienceId){
	
		
		String ids = "";
		try {
			
			String[] columns = {"activity_id",	//0
								"position"};	//1
			String where = "experience_id=?";
			String[] whereArgs ={experienceId+""};
			String orderBy = "position DESC";
			Cursor cur = db.query("activity_id", columns, where, whereArgs, null, null, orderBy);
			if(cur.moveToFirst())
				do{
					ids = ids + cur.getString(0) + ",";
				}while(cur.moveToNext());
			if(!ids.equals(""))
				ids = ids.substring(0, ids.length()-1);
			cur.close();
			
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return ids;
		
	}
	
	/**
	 * Actualiza el id de una actividad que estaba creada localmente
	 * @param activityId
	 * @param prevId
	 */
	public synchronized void asignActivityId(int activityId, int prevId){
		
			
			ContentValues values = new ContentValues();
			values.put("id", activityId);
			String where = "id=?";
			String[] whereArgs = {prevId+""};
			db.update("mini_activity", values, where, whereArgs);
			
			updateActivityId(activityId, prevId);
	}
	
	
	/**
	 * Actualiza el id de una actividad que estaba creada localmente
	 * @param expId				Id de la experiencia que asigna el servidor
	 * @param prevId			Id local que tenía la experiencia
	 * @throws Exception 		Lanza la excepción en caso de producirse
	 */
	public synchronized void asignExperienceId(int expId, int lessonPlanId, int prevId) throws Exception{
		
			
			ContentValues values = new ContentValues();
			values.put("id", expId);
			values.put("lesson_plan_id", lessonPlanId);
			String where = "id=?";
			String[] whereArgs = {prevId+""};
			db.update("experience", values, where, whereArgs);
			
			//updateActivityId(activityId, prevId);
			
			setExperiencesIds(getExperiencesIds(Experience.ALL).replace(prevId+"", expId+""), Experience.ALL);
			
	}

	/**
	 * Obtiene la lista de ids de las actividades pertenecientes a una experiencia
	 * @param experienceId
	 * @return
	 */
	public synchronized String getActivityIdsNoLocal(int experienceId){
	
		
		String ids = "";
		try {
			
			String[] columns = {"activity_id"};	//0
			String where = "experience_id=?";
			String[] whereArgs ={experienceId+""};
			Cursor cur = db.query("activity_id", columns, where, whereArgs, null, null, null);
			if(cur.moveToFirst())
				do{
					ids = cur.getInt(0)<0 ? ids: ids + cur.getString(0) + ",";
					
				}while(cur.moveToNext());
			if(!ids.equals("") && ids.endsWith(","))
				ids = ids.substring(0, ids.length()-1);
			cur.close();
			
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return ids;
		
		
	}
	
	/**
	 * Comprueba si existe una actividad identificada por su id
	 * @param activityId	Id de la actividad a comprobar
	 * @return				True en caso de que exista, False si no existe.
	 */
	public synchronized boolean existeActivityId(int activityId){
		
		
		boolean result = false;
		try {
			
			String[] columns = {"activity_id"};	//0
			String where = "activity_id=?";
			String[] whereArgs ={activityId+""};
			Cursor cur = db.query("activity_id", columns, where, whereArgs, null, null, null);
			if(cur.moveToFirst())
				result = true;
			cur.close();
			
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		
		return result;
	}
	
	/**
	 * Obtiene la lista de ids de las actividades pertenecientes a una experiencia
	 * @param experienceId
	 * @return
	 */
	public synchronized String getEntregaIdsNoLocal(int experienceId){
	
		
		String ids = "";
		try {
			
			String[] columns = {"entrega_id"};	//0
			String where = "experience_id=?";
			String[] whereArgs ={experienceId+""};
			Cursor cur = db.query("entrega_id", columns, where, whereArgs, null, null, null);
			if(cur.moveToFirst())
				do{
					ids = cur.getInt(0)<0 ? ids: ids + cur.getString(0) + ",";
					
				}while(cur.moveToNext());
			if(!ids.equals("") && ids.endsWith(","))
				ids = ids.substring(0, ids.length()-1);
			cur.close();
			
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return ids;
		
		
	}
	
	
	
	/**
	 * Comprueba si existe una entrega identificada por su id
	 * @param entregaId		Id de la entrega a comprobar
	 * @return				True en caso de que exista, False si no existe.
	 */
	public synchronized boolean existeEntregaId(int entregaId){
		
		
		boolean result = false;
		try {
			
			String[] columns = {"entrega_id"};	//0
			String where = "entrega_id=?";
			String[] whereArgs ={entregaId+""};
			Cursor cur = db.query("entrega_id", columns, where, whereArgs, null, null, null);
			if(cur.moveToFirst())
				result = true;
			cur.close();
			
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return result;
	}
	
	/**
	 * Actualiza el id de una entrega que estaba creada localmente
	 * @param activityId
	 * @param prevId
	 */
	public synchronized void asignEntregaId(int activityId, int prevId){
			
			ContentValues values = new ContentValues();
			values.put("entrega_id", activityId);
			String where = "entrega_id=?";
			String[] whereArgs = {prevId+""};
			db.update("entregas", values, where, whereArgs);
			updateEntregaId(activityId, prevId);
	}
	
	/**
	 * Elimina el entrega Id de la tabla
	 * @param entregaId
	 */
	public synchronized void deleteEntregaId(int entregaId){
		
		try {
			
			String whereClause = "entrega_id=?";
			String[] whereArgs = {entregaId+""};
			db.delete("entrega_id", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Elimina los ids de las entregas de la experiencia indicada
	 * @param experienceId		Id de la experiencia de la que se borran los ids de sus entregas
	 */
	public synchronized void deleteEntregasIds(int experienceId){
		
		try {
			
			String whereClause = "experience_id=?";
			String[] whereArgs = {experienceId+""};
			db.delete("entrega_id", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Añade un id de una entrega relacionado con su id de experiencia
	 * @param experienceId		Id de la experiencia a la que pertenece el recurso
	 * @param entregaId			Id de la entrega
	 */
	public synchronized void setEntregaId (int experienceId, int entregaId){
		
		
		try {
			boolean nueva = existeEntregaId(entregaId);
			if(nueva){
				
				ContentValues values = new ContentValues();
				values.put("experience_id", experienceId);
				values.put("entrega_id", entregaId);
				String where = "entrega_id=?";
				String[] whereArgs ={entregaId+""};
				db.update("entrega_id", values, where, whereArgs);
				
				
			}else{
				
				ContentValues values = new ContentValues();
				values.put("experience_id", experienceId);
				values.put("entrega_id", entregaId);
				db.insert("entrega_id", null, values);
				
			}
						
		} catch (Exception e) {
			if (db != null)
				
			throw e;
		}
	}
	
	/**
	 * Obtiene la lista de ids de los recursos pertenecientes a una experiencia
	 * @param experienceId
	 * @return
	 */
	public synchronized String getEntregasIds(int experienceId){
	
		
		String ids = "";
		try {
			
			String[] columns = {"entrega_id"};	//0
			String where = "experience_id=?";
			String[] whereArgs ={experienceId+""};
			String orderBy = "entrega_id DESC";
			Cursor cur = db.query("entrega_id", columns, where, whereArgs, null, null, orderBy);
			if(cur.moveToFirst())
				do{
					ids = ids + cur.getString(0) + ",";
				}while(cur.moveToNext());
			if(!ids.equals(""))
				ids = ids.substring(0, ids.length()-1);
			cur.close();
			
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return ids;
		
	}
		
	
	/**
	 * Comprueba si existe un recurso identificada por su id
	 * @param recursoId		Id del recurso a comprobar
	 * @return				True en caso de que exista, False si no existe.
	 */
	public synchronized boolean existeResourceId(int recursoId){
		try {
			String[] columns = {"resource_id"};	//0
			String where = "resource_id=?";
			String[] whereArgs ={recursoId+""};
			Cursor cur = db.query("resource_id", columns, where, whereArgs, null, null, null);
			if(cur.moveToFirst())
				return true;
			cur.close();
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		
		return false;
	}
	
	/**
	 * Elimina el recurso Id de la tabla
	 * @param resourceId
	 */
	public synchronized void deleteRecursoId(int resourceId){
		
		try {
			
			String whereClause = "resource_id=?";
			String[] whereArgs = {resourceId+""};
			db.delete("resource_id", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Elimina los ids de las actividades de la experiencia indicada
	 * @param experienceId		Id de la experiencia de la que se borran los ids de sus actividades
	 */
	public synchronized void deleteRecursosIds(int experienceId){
		
		try {
			String whereClause = "experience_id=?";
			String[] whereArgs = {experienceId+""};
			db.delete("resource_id", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Añade un id de un recurso relacionado con su id de experiencia
	 * @param experienceId		Id de la experiencia a la que pertenece el recurso
	 * @param recursoId			Id del recurso
	 */
	public synchronized void setRecursoId (int experienceId, int recursoId){
		try {
			boolean nueva = existeActivityId(recursoId);
			if(nueva){
				
				ContentValues values = new ContentValues();
				values.put("experience_id", experienceId);
				values.put("resource_id", recursoId);
				String where = "resource_id=?";
				String[] whereArgs ={recursoId+""};
				db.update("resource_id", values, where, whereArgs);
				
				
			}else{
				
				ContentValues values = new ContentValues();
				values.put("experience_id", experienceId);
				values.put("resource_id", recursoId);
				db.insert("resource_id", null, values);
				
			}
						
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	/**
	 * Obtiene la lista de ids de los recursos pertenecientes a una experiencia
	 * @param experienceId
	 * @return
	 */
	public synchronized String getRecursosIds(int experienceId){
		String ids = "";
		try {
			String[] columns = {"resource_id"};	//0
			String where = "experience_id=?";
			String[] whereArgs ={experienceId+""};
			String orderBy = "resource_id DESC";
			Cursor cur = db.query("resource_id", columns, where, whereArgs, null, null, orderBy);
			if(cur.moveToFirst())
				do{
					ids = ids + cur.getString(0) + ",";
				}while(cur.moveToNext());
			if(!ids.equals(""))
				ids = ids.substring(0, ids.length()-1);
			cur.close();
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return ids;
		
	}
	
	/**
	 * Guarda en la base de datos el string con los ids de las actividades del activity sequence Id.
	 * @param ids			String con los ids de la actividades separados por comas
	 * @throws Exception 	Lanza la excepción en caso de producirse
	 */
	public synchronized void setActivitiesIds (int experienceId, String ids) throws Exception{
		
		
		try {
			String localIds = getActivitiesIds(experienceId);
			if(!localIds.equals("")){
				
				ContentValues values = new ContentValues();
				values.put("experience_id", experienceId);
				values.put("ids", ids);
				String where = "experience_id=?";
				String[] whereArgs ={experienceId+""};
				db.update("activities_ids", values, where, whereArgs);
				
				
			}else{
				
				ContentValues values = new ContentValues();
				values.put("experience_id", experienceId);
				values.put("ids", ids);
				db.insert("activities_ids", null, values);
				
			}
						
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	
	/**
	 * Obtiene los ids de las actividades guardados en la base de datos para el activity sequence Id pedido
	 * @return	String con los ids de la experiencias separados por comas
	 */
	public synchronized String getActivitiesIds(int activitySequenceId){
		
		
		String ids = "";
		try {
			
			String[] columns = {"ids"};	//0
			String where = "experience_id=?";
			String[] whereArgs ={activitySequenceId+""};
			Cursor cur = db.query("activities_ids", columns, where, whereArgs, null, null, null);
			if(cur.moveToFirst())
				do{
					ids = cur.getString(0);
				}while(cur.moveToNext());
			cur.close();
			
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return ids;
	}
	
	/**
	 * Guarda una mini activity en la base de datos
	 * @param miniActivity	Mini activity que se va a guardar
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized void setMiniActivity(MiniActivity miniActivity, int experienceId) throws Exception{
		
		try {
			MiniActivity mA = getMiniActivity(miniActivity.getId());
			if(mA != null){
				
				ContentValues values = new ContentValues();
				values.put("name", miniActivity.getName());
				values.put("image", miniActivity.getElement_image_file_name());
				values.put("start", miniActivity.getStart());
				values.put("end", miniActivity.getEnd());
				values.put("progress", miniActivity.getProgress());
				values.put("experience_id", experienceId);
				values.put("description", miniActivity.getDescription());
				values.put("description_student", miniActivity.getDescriptionStudent());
				values.put("definition", miniActivity.getDefinition());
				values.put("autor", miniActivity.getAutor());
				values.put("updated_at", miniActivity.getUpdatedAt());
				values.put("permiso", miniActivity.getPermiso());
				String where = "id=?";
				String[] whereArgs = {miniActivity.getId()+""};
				db.update("mini_activity", values, where, whereArgs);
				
				
			}else{
				
				ContentValues values = new ContentValues();
				values.put("id", miniActivity.getId());
				values.put("name", miniActivity.getName());
				values.put("image", miniActivity.getElement_image_file_name());
				values.put("start", miniActivity.getStart());
				values.put("end", miniActivity.getEnd());
				values.put("progress", miniActivity.getProgress());
				values.put("experience_id", experienceId);
				values.put("description", miniActivity.getDescription());
				values.put("description_student", miniActivity.getDescriptionStudent());
				values.put("definition", miniActivity.getDefinition());
				values.put("autor", miniActivity.getAutor());
				values.put("updated_at", miniActivity.getUpdatedAt());
				values.put("permiso", miniActivity.getPermiso());
				db.insert("mini_activity", null, values);
					
			}
						
		} catch (Exception e) {
			if (db != null)
				
			throw e;
		}
	}
	
	/**
	 * Obtiene la mini activity identificada el id
	 * @param id			Id de la mini activity a buscar en la base de datos
	 * @return				Mini activity identificada por el id, o null en caso contrario
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized MiniActivity getMiniActivity(int id) {
		
		MiniActivity mA = null;
		try {
			String[] columns = {"id",					//0
							   "name",					//1
							   "image",					//2
							   "start",					//3
							   "end",					//4
							   "progress",				//5
							   "description",			//6
							   "experience_id",			//7
							   "description_student",	//8
							   "definition",			//9
							   "autor",					//10
							   "updated_at",			//11
							   "permiso"};				//12
			String where;
			if(id < 0)
				where= "local_id=?";
			else
				where= "id=?";
			String[] whereArgs = {id+""};
			Cursor cursor = db.query("mini_activity", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				mA = new MiniActivity();
				mA.setId(cursor.getInt(0));
				mA.setName(cursor.getString(1));
				mA.setElement_image_file_name(cursor.getString(2));
				mA.setStart(cursor.getString(3));
				mA.setEnd(cursor.getString(4));
				mA.setProgress(cursor.getString(5));
				mA.setDescription(cursor.getString(6));
				mA.setExperienceId(cursor.getInt(7));
				mA.setDescriptionStudent(cursor.getString(8));
				mA.setDefinition(cursor.getString(9));
				mA.setAutor(cursor.getString(10));
				mA.setUpdatedAt(cursor.getString(11));
				mA.setPermiso(cursor.getString(12));
			}
			cursor.close();
			
			
		} catch (Exception e){
//			if (db != null){
//				
//			}
//			throw e;
		}
		return mA;
		
	}
	
	/**
	 * Obtiene el id más bajo de la tabla de actividades
	 * @return
	 */
	public synchronized int getMinActivityId(){
		int minId = -1;
		
		try {
			
			String[] columns = {"local_id"};	//0
			String orderBy = "local_id ASC" ;
			Cursor cursor = db.query("mini_activity", columns, null, null, null, null, orderBy);
			if(cursor.moveToFirst()){
				minId = cursor.getInt(0);
			}
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null){
				
			}
			throw e;
		}
		return minId>=0 ? -1 :minId;
	}
	
	
	/**
	 * Obtiene el id más bajo de la tabla de experiencias
	 * @return
	 */
	public synchronized int getExperienceId(){
		int minId = -1;
		
		try {
			
			String[] columns = {"local_id"};	//0
			String orderBy = "local_id ASC" ;
			Cursor cursor = db.query("experience", columns, null, null, null, null, orderBy);
			if(cursor.moveToFirst()){
				minId = cursor.getInt(0);
			}
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null){
				
			}
			throw e;
		}
		return minId>=0 ? -1 :minId;
	}
	
	/**
	 * Obtiene el id de la actividad a partir del id local creado
	 * @param localId		Id local de la actividad
	 * @return
	 */
	public synchronized int getActivityIdFromLocalId(int localId){
		int serverId = -1;
		
		try {
			
			String[] columns = {"id"};	//0
			String where = "local_id=?";
			String[] whereArgs = {localId+""};
			Cursor cursor = db.query("mini_activity", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				serverId = cursor.getInt(0);
			}
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return serverId;
	}
	
	/**
	 * Obtiene el id de la experiencia d a partir del id local creado
	 * @param localId		Id local de la experiencia
	 * @return
	 */
	public synchronized int getExperienceIdFromLocalId(int localId){
		int serverId = -1;
		
		try {
			
			String[] columns = {"id"};	//0
			String where = "local_id=?";
			String[] whereArgs = {localId+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				serverId = cursor.getInt(0);
			}
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return serverId;
	}
	
	/**
	 * Añade una nueva actividad vacia a la experiencia
	 * @param experienceId		Experiencia a la que se le añade la nueva actividad
	 */
	public synchronized int addMiniActivity(int experienceId){
		
		try {
				//int localID = getMinActivityId() - 1;
				int localID = ApplicationService.getLocalId();
				
				ContentValues values = new ContentValues();
				values.put("id", localID);
				values.put("experience_id", experienceId);
				values.put("image", "");
				values.put("start", "");
				values.put("end", "");
				values.put("progress", "");
				values.put("name", "");
				values.put("description", "");
				values.put("local_id", localID);
				values.put("description_student", "");
				values.put("definition", "");
				values.put("autor", "");
				db.insert("mini_activity", null, values);
				
				setActivityId(experienceId, localID, 0);
				return localID;
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	/**
	 * Añade una nueva actividad vacia a la experiencia
	 * @param experienceId		Experiencia a la que se le añade la nueva actividad
	 * @throws Exception		Lanza la expcepción en caso de producirse 
	 */
	public synchronized int addExperience() throws Exception{
		
		try {
				int localID = ApplicationService.getLocalId();
				
				ContentValues values = new ContentValues();
				values.put("id", localID);
				values.put("name", "");
				values.put("image", "");
				values.put("description", "");
				values.put("short_description", "");
				values.put("activity_sequence_id", 0);
				values.put("lesson_plan_id", localID);
				values.put("description_student", "");
				values.put("updated_at", Services.Utilidades().DateToString(new Date()));
				values.put("local_id", localID);
				values.put("definition", "");
				values.put("autor", "");
				db.insert("experience", null, values);
				
				setExperiencesIds(localID + "," + getExperiencesIds(Experience.ALL) , Experience.ALL);
				
				return localID;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Añade una nueva entrega vacia a la experiencia o actividad
	 * @param id		Experiencia o actividad a la que se le añade la nueva entrega
	 */
	public synchronized int addEntrega(int id){
		
		try {
				//int localID = getEntregaId() - 1;
				int localID = ApplicationService.getLocalId();
				
				ContentValues values = new ContentValues();
				values.put("entrega_id", localID);
				values.put("experience_id", id);
				values.put("imagen", "");
				values.put("titulo", "");
				values.put("descripcion", "");
				values.put("local_id", localID);
				values.put("definition", "");
				values.put("autor", "");
				
				db.insert("entregas", null, values);
				
				setEntregaId(id, localID);
				return localID;
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}
	}
	
	/**
	 * Obtiene el id de la entrega a partir del id local creado
	 * @param localId		Id local de la entrega
	 * @return
	 */
	public synchronized int getEntregaIdFromLocalId(int localId){
		int serverId = -1;
		
		try {
			
			String[] columns = {"entrega_id"};	//0
			String where = "local_id=?";
			String[] whereArgs = {localId+""};
			Cursor cursor = db.query("entregas", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				serverId = cursor.getInt(0);
			}
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return serverId;
	}
	
	/**
	 * Obtiene el id más bajo de la tabla de entregas
	 * @return
	 */
	public synchronized int getEntregaId(){
		int minId = -1;
		
		try {
			
			String[] columns = {"local_id"};	//0
			String orderBy = "local_id ASC" ;
			Cursor cursor = db.query("entregas", columns, null, null, null, null, orderBy);
			if(cursor.moveToFirst()){
				minId = cursor.getInt(0);
			}
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return minId>=0 ? -1 :minId;
	}
	
	/**
	 * Guarda una entrega en la base de datos
	 * @param EntregaModel	Entrega que se va a guardar
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized void setEntrega(EntregaModel entrega, int experienceId) throws Exception{
		
		try {
			EntregaModel rec = getEntrega(entrega.getId());
			if(rec != null){
				
				ContentValues values = new ContentValues();
				values.put("entrega_id", entrega.getId());
				values.put("experience_id", experienceId);
				values.put("descripcion", entrega.getDescripcion());
				values.put("titulo", entrega.getTitulo());
				values.put("imagen", entrega.getImagen());
				values.put("definition", entrega.getDefinition());
				values.put("autor", entrega.getAutor());
				values.put("updated_at", entrega.getUpdatedAt());
				values.put("permiso", entrega.getPermiso());
				String where = "entrega_id=?";
				String[] whereArgs = {entrega.getId()+""};
				db.update("entregas", values, where, whereArgs);
				
				
			}else{
				
				ContentValues values = new ContentValues();
				values.put("entrega_id", entrega.getId());
				values.put("experience_id", experienceId);
				values.put("descripcion", entrega.getDescripcion());
				values.put("titulo", entrega.getTitulo());
				values.put("imagen", entrega.getImagen());
				values.put("definition", entrega.getDefinition());
				values.put("autor", entrega.getAutor());
				values.put("updated_at", entrega.getUpdatedAt());
				values.put("permiso", entrega.getPermiso());
				db.insert("entregas", null, values);
					
			}		
		} catch (Exception e) {
			if (db != null)
				
			throw e;
		}
	}	
	
	/**
	 * Obtiene la entrega identificado por el id
	 * @param id			Id de la entrega a buscar en la base de datos
	 * @return				Entrega identificada por el id, o null en caso contrario
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized EntregaModel getEntrega(int id) throws Exception{
		EntregaModel entrega = null;
		try {
			String[] columns = {"entrega_id",		//0
							   "descripcion",		//1
							   "titulo",			//2
							   "imagen",			//3
							   "definition",		//4
							   "autor",				//5
							   "updated_at",		//6
							   "permiso"};			//7
			String where = "entrega_id=?";
			String[] whereArgs = {id+""};
			Cursor cursor = db.query("entregas", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				entrega = new EntregaModel();
				entrega.setId(cursor.getInt(0));
				entrega.setDescripcion(cursor.getString(1));
				entrega.setTitulo(cursor.getString(2));
				entrega.setImagen(cursor.getString(3));
				entrega.setDefinition(cursor.getString(4));
				entrega.setAutor(cursor.getString(5));
				entrega.setUpdatedAt(cursor.getString(6));
				entrega.setPermiso(cursor.getString(7));
			}
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return entrega;
	}
	
	/**
	 * Obtiene una lista de entregas a partir del id de la experiencia
	 * @param experienceId
	 * @return
	 * @throws Exception
	 */
	public synchronized List<EntregaModel> getEntregas(int experienceId) throws Exception{
		
		List<EntregaModel> lista = new ArrayList<EntregaModel>();
		
		try {
			
			String[] columns = {"entrega_id",	//0
							   "descripcion",	//1
							   "titulo",		//2
							   "imagen",		//3
							   "definition",	//4
							   "autor",			//5
							   "updated_at",	//6
			   				   "permiso"};		//7
			String where = "experience_id=?";
			String[] whereArgs = {experienceId + ""};
			Cursor cursor = db.query("entregas", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst())
				do{
					EntregaModel entrega = new EntregaModel();
					entrega.setId(cursor.getInt(0));
					entrega.setDescripcion(cursor.getString(1));
					entrega.setTitulo(cursor.getString(2));
					entrega.setImagen(cursor.getString(3));
					entrega.setDefinition(cursor.getString(4));
					entrega.setAutor(cursor.getString(5));
					entrega.setUpdatedAt(cursor.getString(6));
					entrega.setPermiso(cursor.getString(7));
					lista.add(entrega);
				}while(cursor.moveToNext());
			cursor.close();
			
			
		} catch (Exception e){
//			if (db != null)
//				
//			throw e;
		}
		return lista;
	}
	
	/**
	 * Obtiene una lista de entregas a partir del id de la experiencia
	 * @param experienceId
	 * @return
	 * @throws Exception
	 */
	public synchronized List<EntregaModel> getLocalEntregas(int experienceId) throws Exception{
		
		List<EntregaModel> lista = new ArrayList<EntregaModel>();
		
		try {
			
			String[] columns = {"entrega_id",	//0
							   "descripcion",	//1
							   "titulo",		//2
							   "imagen",		//3
			   				   "definition"};	//4
						
			String where = "experience_id=?";
			String[] whereArgs = {experienceId + ""};
			Cursor cursor = db.query("entregas", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst())
				do{
					EntregaModel entrega = new EntregaModel();
					entrega.setId(cursor.getInt(0));
					entrega.setDescripcion(cursor.getString(1));
					entrega.setTitulo(cursor.getString(2));
					entrega.setImagen(cursor.getString(3));
					entrega.setDefinition(cursor.getString(4));
					if(entrega.getId()<0)
						lista.add(entrega);
				}while(cursor.moveToNext());
			cursor.close();
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return lista;
	}
	
	/**
	 * Devuelve una lista de entregas a partir de sus ids
	 * @param ids							Ids de las entregas de las que se quiere la vista
	 * @return								Lista de vistas de las entregas
	 * @throws NumberFormatException		Lanza la expcepción en caso de producirse
	 * @throws Exception					Lanza la expcepción en caso de producirse
	 */
	public synchronized List<EntregaModel> getEntregas(String ids) throws NumberFormatException, Exception{
		
		List<EntregaModel> lista = new ArrayList<EntregaModel>();
		if(ids.equals(""))
			return lista;
		String[] idsArray = ids.split(",");
		for (int i = idsArray.length -1 ; i >=0 ; i--){	
			EntregaModel m = getEntrega(Integer.parseInt(idsArray[i]));
			if (m != null)
				lista.add(m);
		}
		return lista;
	}
	
	
	
	public synchronized List<EntregaModel> getAllEntregas(int id) throws Exception{
		List<EntregaModel> lista = new ArrayList<EntregaModel>();
		
		String desc = getActivityIds(id);
		int ascend = getExperienceIdFronActivityId(id);
		
		List<EntregaModel> propios = getEntregas(getEntregasIds(id));
		Collections.reverse(propios);
		
		if(getExpirience(id) != null && getExpirience(id).getName() != null && !getExpirience(id).getName().equals("") && propios.size() > 0){
			EntregaModel ent = new EntregaModel();
			ent.setSeparador(true);
			ent.setDefinition(getExpirience(id).getDefinition());
			ent.setId(id);
			ent.setTitulo(getExpirience(id).getName());
			lista.add(ent);	
		}else if (getMiniActivity(id) != null && getMiniActivity(id).getName() != null && !getMiniActivity(id).getName().equals("") && propios.size() > 0){
			EntregaModel ent = new EntregaModel();
			ent.setSeparador(true);
			ent.setDefinition(getMiniActivity(id).getDefinition());
			ent.setId(id);
			ent.setTitulo(getMiniActivity(id).getName());
			lista.add(ent);	
		}
		
		lista.addAll(propios);
		
		if(!desc.equals("")){
			String[] idsArray = desc.split(",");
			for (int i = idsArray.length -1 ; i >=0 ; i--){	
				int temp = Integer.parseInt(idsArray[i]);
				List<EntregaModel> l = getEntregas(getEntregasIds(temp));
				if(l.size() > 0){
					EntregaModel ent = new EntregaModel();
					ent.setSeparador(true);
					ent.setDefinition(getMiniActivity(temp).getDefinition());
					ent.setId(temp);
					ent.setTitulo(getMiniActivity(temp).getName());
					lista.add(ent);		
				}
				Collections.reverse(l);
				lista.addAll(l);
			}
		}
		if (ascend != -1){
			List<EntregaModel> l = getEntregas(getEntregasIds(ascend));
			if(l.size() > 0){
				EntregaModel ent = new EntregaModel();
				ent.setSeparador(true);
				ent.setDefinition(getExpirience(ascend).getDefinition());
				ent.setId(ascend);
				ent.setTitulo(getExpirience(ascend).getName());
				lista.add(ent);
			}
			Collections.reverse(l);
			lista.addAll(l);
		}
		
		
		return lista;
	}
	
	
	
	public synchronized List<RecordModel> getAllRecords(int id){
		List<RecordModel> lista = new ArrayList<RecordModel>();
		
		int expId = getExperienceIdFronLessonPlanId(id);
		
		String desc = getActivityIds(expId);
		int ascend = getLessonPlanIdFromExperienceId(getExperienceIdFronActivityId(id));
		
		
		List<RecordModel> propios = getRecords(id);
		//Collections.reverse(propios);
		
		if(getExpirience(expId) != null && getExpirience(expId).getName() != null && !getExpirience(expId).getName().equals("") && propios.size() > 0){
			RecordModel rec = new RecordModel();
			rec.setSeparador(true);
			rec.setId(expId);
			rec.setTitle(getExpirience(expId).getName());
			lista.add(rec);	
		}else if (getMiniActivity(id) != null && getMiniActivity(id).getName() != null && !getMiniActivity(id).getName().equals("") && propios.size() > 0){
			RecordModel rec = new RecordModel();
			rec.setSeparador(true);
			rec.setId(id);
			rec.setTitle(getMiniActivity(id).getName());
			lista.add(rec);	
		}
		
		lista.addAll(propios);
		
		if(!desc.equals("")){
			String[] idsArray = desc.split(",");
			for (int i = idsArray.length -1 ; i >=0 ; i--){	
				int temp = Integer.parseInt(idsArray[i]);
				List<RecordModel> l = getRecords(temp);
				if(l.size() > 0){
					RecordModel rec = new RecordModel();
					rec.setSeparador(true);
					rec.setId(temp);
					rec.setTitle(getMiniActivity(temp).getName());
					lista.add(rec);		
				}
				Collections.reverse(l);
				lista.addAll(l);
			}
		}
		if (ascend != -1){
			
			List<RecordModel> l = getRecords(ascend);
			if(l.size() > 0){
				RecordModel rec = new RecordModel();
				rec.setSeparador(true);
				rec.setId(getExperienceIdFronActivityId(id));
				rec.setTitle(getExpirience(getExperienceIdFronActivityId(id)).getName());
				lista.add(rec);
			}
			//Collections.reverse(l);
			lista.addAll(l);
		}
		
		
		
		return lista;
	}
	
	/**
	 * Guarda un recurso en la base de datos
	 * @param RecursoModel	Recurso que se va a guardar
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized void setRecurso(RecursoModel recurso, int experienceId) throws Exception{
		
		try {
			RecursoModel rec = getRecurso(recurso.getId(), recurso.getTipo().equals("Event"));
			if(rec != null){
				ContentValues values = new ContentValues();
				values.put("resource_id", recurso.getId());
				values.put("experience_id", experienceId);
				values.put("url",recurso.getUrl());
				values.put("descripcion", recurso.getDescripcion());
				values.put("titulo", recurso.getTitulo());
				values.put("imagen", recurso.getImagen());
				values.put("type", recurso.getTipo());
				values.put("definition", recurso.getDefinition());
				values.put("autor", recurso.getAutor());
				values.put("updated_at", recurso.getUpdatedAt());
				values.put("permiso", recurso.getPermiso());
				String where = "resource_id=?";
				String[] whereArgs = {recurso.getId()+""};
				db.update("resources", values, where, whereArgs);
			}else{
				ContentValues values = new ContentValues();
				values.put("resource_id", recurso.getId());
				values.put("experience_id", experienceId);
				values.put("url",recurso.getUrl());
				values.put("descripcion", recurso.getDescripcion());
				values.put("titulo", recurso.getTitulo());
				values.put("imagen", recurso.getImagen());
				values.put("type", recurso.getTipo());
				values.put("definition", recurso.getDefinition());
				values.put("autor", recurso.getAutor());
				values.put("updated_at", recurso.getUpdatedAt());
				values.put("permiso", recurso.getPermiso());
				db.insert("resources", null, values);
					
			}		
		} catch (Exception e) {
			if (db != null)
				
			throw e;
		}
	}	
	
	/**
	 * Obtiene el recurso identificado por el id
	 * @param id			Id del recurso a buscar en la base de datos
	 * @return				Recurso identificada por el id, o null en caso contrario
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized RecursoModel getRecurso(int id, boolean sitios) throws Exception{
		RecursoModel recurso = null;
		try {
			String[] columns = {"resource_id",		//0
							   "url",				//1
							   "descripcion",		//2
							   "titulo",			//3
							   "imagen",			//4
							   "type",				//5
							   "definition",		//6
							   "autor",				//7
							   "updated_at",		//8
							   "permiso"};			//9
			String where = sitios ? "resource_id=? AND type='Event'" : "resource_id=? AND NOT type='Event'";
			String[] whereArgs = {id+""};
			Cursor cursor = db.query("resources", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				recurso = new RecursoModel();
				recurso.setId(cursor.getInt(0));
				recurso.setUrl(cursor.getString(1));
				recurso.setDescripcion(cursor.getString(2));
				recurso.setTitulo(cursor.getString(3));
				recurso.setImagen(cursor.getString(4));
				recurso.setTipo(cursor.getString(5));
				recurso.setDefinition(cursor.getString(6));
				recurso.setAutor(cursor.getString(7));
				recurso.setUpdatedAt(cursor.getString(8));
				recurso.setPermiso(cursor.getString(9));
			}
			cursor.close();
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return recurso;
	}
	
	/**
	 * Obtiene una lista de recursos a partir del id de la experiencia
	 * @param experienceId
	 * @return
	 * @throws Exception
	 */
	public synchronized List<RecursoModel> getRecursos(int experienceId, boolean sitios) throws Exception{
		List<RecursoModel> lista = new ArrayList<RecursoModel>();
		try {
			
			String[] columns = {"resource_id",		//0
					   "url",				//1
					   "descripcion",		//2
					   "titulo",			//3
					   "imagen",			//4
					   "type",				//5
					   "definition",		//6
			   		   "autor",				//7
			   		   "updated_at",		//8
			   		   "permiso"};			//9
			String where = sitios ? "experience_id=? AND type='Event'" : "experience_id=? AND NOT type='Event'";
			String[] whereArgs = {experienceId+""};
			Cursor cursor = db.query("resources", columns, where, whereArgs, null, null, null);
		
			if(cursor.moveToFirst())
				do{
					RecursoModel recurso = new RecursoModel();
					recurso.setId(cursor.getInt(0));
					recurso.setUrl(cursor.getString(1));
					recurso.setDescripcion(cursor.getString(2));
					recurso.setTitulo(cursor.getString(3));
					recurso.setImagen(cursor.getString(4));
					recurso.setTipo(cursor.getString(5));
					recurso.setDefinition(cursor.getString(6));
					recurso.setAutor(cursor.getString(7));
					recurso.setUpdatedAt(cursor.getString(8));
					recurso.setPermiso(cursor.getString(9));
					lista.add(recurso);
				}while(cursor.moveToNext());
			cursor.close();
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return lista;
	}
	
	
	/**
	 * Devuelve una lista de recursos a partir de sus ids
	 * @param ids							Ids de los recursos de los que se quiere la vista
	 * @return								Lista de vistas de los recursos
	 * @throws NumberFormatException		Lanza la expcepción en caso de producirse
	 * @throws Exception					Lanza la expcepción en caso de producirse
	 */
	public synchronized List<RecursoModel> getRecursos(String ids, boolean sitios) throws NumberFormatException, Exception{
		
		List<RecursoModel> lista = new ArrayList<RecursoModel>();
		if(ids.equals(""))
			return lista;
		String[] idsArray = ids.split(",");
		for (int i = idsArray.length -1 ; i >=0 ; i--){	
			RecursoModel m = getRecurso(Integer.parseInt(idsArray[i]), sitios);
			if (m != null)
				lista.add(m);
		}
		return lista;
	}
	
	/**
	 * Obtiene la lista de recursos completa asociada a un id, sea curso o actividad
	 * @param id			Id del elemento del que se quieren todos los recuros
	 * @return				Lista de recursos solicitada
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized List<RecursoModel> getAllRecursos(int id, boolean sitios) throws Exception {
		List<RecursoModel> lista = new ArrayList<RecursoModel>();
		
		String desc = getActivityIds(id);
		int ascend = getExperienceIdFronActivityId(id);
		
		List<RecursoModel> propios = getRecursos(getRecursosIds(id), sitios);
		Collections.reverse(propios);
		
		if(getExpirience(id) != null && getExpirience(id).getName() != null && !getExpirience(id).getName().equals("") && propios.size() > 0){
			RecursoModel rec = new RecursoModel();
			rec.setSeparador(true);
			rec.setDefinition(getExpirience(id).getDefinition());
			rec.setId(id);
			rec.setTitulo(getExpirience(id).getName());
			lista.add(rec);	
		}else if (getMiniActivity(id) != null && getMiniActivity(id).getName() != null && !getMiniActivity(id).getName().equals("") && propios.size() > 0){
			RecursoModel rec = new RecursoModel();
			rec.setSeparador(true);
			rec.setId(id);
			rec.setDefinition(getMiniActivity(id).getDefinition());
			rec.setTitulo(getMiniActivity(id).getName());
			lista.add(rec);
		}
		
		lista.addAll(propios);
		
		if(!desc.equals("")){
			String[] idsArray = desc.split(",");
			for (int i = idsArray.length -1 ; i >=0 ; i--){	
				int temp = Integer.parseInt(idsArray[i]);
				List<RecursoModel> l = getRecursos(getRecursosIds(temp), sitios);
				if(l.size() > 0){
					RecursoModel rec = new RecursoModel();
					rec.setSeparador(true);
					rec.setId(temp);
					rec.setDefinition(getMiniActivity(temp).getDefinition());
					rec.setTitulo(getMiniActivity(temp).getName());
					lista.add(rec);	
				}
				Collections.reverse(l);
				lista.addAll(l);
			}
		}
		if (ascend != -1){
			
			List<RecursoModel> l = getRecursos(getRecursosIds(ascend), sitios);
			if(l.size() > 0){
				RecursoModel rec = new RecursoModel();
				rec.setSeparador(true);
				rec.setId(ascend);
				rec.setDefinition(getExpirience(ascend).getDefinition());
				rec.setTitulo(getExpirience(ascend).getName());
				lista.add(rec);
			}
			Collections.reverse(l);
			lista.addAll(l);
		}
		
		
		return lista;
	}
	
	
		
	/**
	 * Obtiene el id de la experiencia a la que pertenece una actividad
	 * @param activityId		Id de la actividad de la que se quiere obtener el id de la experiencia a la que pertenece
	 * @return					Id de la experience buscado
	 * @throws Exception		Lanza la expcepción en caso de producirse
	 */
	public synchronized int getExperienceIdFronActivityId(int activityId){
		int result = -1;
		try {
			
			String[] columns = {"experience_id"};		//0
			String where = "id=?";
			String[] whereArgs = {activityId+""};
			Cursor cursor = db.query("mini_activity", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				result = cursor.getInt(0);
			}
			cursor.close();
		} catch (Exception e){
			//if (db != null)
				
			//throw e;
		}		
		return result;
	}
	
	/**
	 * Obtiene el id de la experiencia a partir del id del lesson plan
	 * @param lessonPlanId		Id del lesson plan del que se quiere obtener el id de la experiencia a la que pertenece
	 * @return					Id de la experience buscado
	 * @throws Exception		Lanza la expcepción en caso de producirse
	 */
	public synchronized int getExperienceIdFronLessonPlanId(int lessonPlanId){
		int result = -1;
		try {
			String[] columns = {"id"};		//0
			String where = "lesson_plan_id=?";
			String[] whereArgs = {lessonPlanId+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				result = cursor.getInt(0);
			}
			cursor.close();
		} catch (Exception e){
			e.printStackTrace();
			//if (db != null)
				
			//throw e;
		}		
		return result;
	}
	
	public synchronized int getExperienceIdFronEntregaId(int entregaId) throws Exception{
		int result = -1;
		try {
			String[] columns = {"experience_id"};		//0
			String where = "entrega_id=?";
			String[] whereArgs = {entregaId+""};
			Cursor cursor = db.query("entregas", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				result = cursor.getInt(0);
			}
			cursor.close();
			
		} catch (Exception e){
			if (db != null){
				
			}
			throw e;
		}		
		return result;
	}
	
	
	public synchronized int getLessonPlanIdFromExperienceId(int experienceId){
		int result = -1;
		try {
			String[] columns = {"lesson_plan_id"};		//0
			String where = "id=?";
			String[] whereArgs = {experienceId+""};
			Cursor cursor = db.query("experience", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				result = cursor.getInt(0);
			}
			cursor.close();
		} catch (Exception e){
			if (db != null){
				
			}
		}		
		return result;
	}
	
	
	/**
	 * Devuelve una lista de mini activities a partir de sus ids
	 * @param ids							Ids de los activities de los que obtener su mini view
	 * @return								Lista de Mini activities pedida
	 * @throws NumberFormatException		Lanza la expcepción en caso de producirse
	 * @throws Exception					Lanza la expcepción en caso de producirse
	 */
	public synchronized List<MiniActivity> getMiniActivities(String ids) throws NumberFormatException, Exception{
		
		List<MiniActivity> lista = new ArrayList<MiniActivity>();
		if(ids.equals(""))
			return lista;
		String[] idsArray = ids.split(",");
		for (int i = idsArray.length -1 ; i >=0 ; i--){	
			MiniActivity m = getMiniActivity(Integer.parseInt(idsArray[i]));
			if (m != null)
				lista.add(m);
		}
		return lista;
		
	}
	
	/**
	 * Guarda en la base de datos un record o lo actualiza si ya existe
	 * @param activityId		Id del activity al que pertenece el record
	 * @param record			Record a guardar
	 * @throws Exception		Lanza la expcepción en caso de producirse
	 */
	public synchronized void setRecord(int activityId, RecordModel record) throws Exception{
		try {
			RecordModel rec = record.getId() < 0 ? getRecord(record.getId(), Services.Utilidades().DateToString(record.getCreationDate())) 
													: getRecord(record.getId());
			if(rec != null){
				ContentValues values = new ContentValues();
				values.put("activity_id", activityId);
				values.put("id", record.getId());
				values.put("record_type", record.getRecordType());
				values.put("title", record.getTitle());
				values.put("description", record.getDescription());
				values.put("created_at", Services.Utilidades().DateToString(record.getCreationDate()));
				values.put("updated_at", Services.Utilidades().DateToString(record.getUpdateDate()));
				values.put("data", record.getData());
				values.put("blurred", record.getBlurredImage());
				values.put("faces_array", record.getFacesArray());
				values.put("video_frame", record.getVideoFrame());
				values.put("autor", record.getAutor());
				values.put("permiso", record.getPermiso());
				if(record.getLocalData() != null)
					values.put("local_data",record.getLocalData());
				if(record.getId()<0){
					String where = "id=? AND created_at=?";
					String[] whereArgs = {record.getId()+"", Services.Utilidades().DateToString(record.getCreationDate())};
					db.update("records", values, where, whereArgs);
				}else{
					String where = "id=?";
					String[] whereArgs = {record.getId()+""};
					db.update("records", values, where, whereArgs);
				}
			}else{
				ContentValues values = new ContentValues();
				values.put("activity_id", activityId);
				values.put("id", record.getId());
				values.put("record_type", record.getRecordType());
				values.put("title", record.getTitle());
				values.put("description", record.getDescription());
				values.put("created_at", Services.Utilidades().DateToString(record.getCreationDate()));
				values.put("updated_at", Services.Utilidades().DateToString(record.getUpdateDate()));
				values.put("data", record.getData());
				values.put("local_data",record.getLocalData());
				values.put("blurred", record.getBlurredImage());
				values.put("faces_array", record.getFacesArray());
				values.put("video_frame", record.getVideoFrame());
				values.put("autor", record.getAutor());
				values.put("permiso", record.getPermiso());
				db.insert("records", null, values);
			}
		} catch (Exception e) {
			if (db != null){
				
			}
			throw e;
		}		
	}
	
	/**
	 * Elimina los Records de una actividad
	 * @param activityId	Id de la actividad de la que hay que eliminar los records
	 */
	public synchronized void deleteRecords(int activityId){
		try {
			String whereClause = "activity_id=?";
			String[] whereArgs = {activityId + ""};
			db.delete("records", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	/**
	 * Elimina los Records de un elemento, sea actividad o expereiencia que no estén en la lista de ids
	 * @param elementId		Id de la actividad o experiencia de la que se quieren borrar los records
	 * @param ids			Ids de los records de la actividad o experiencia que no se deben borrar
	 */
	public synchronized void deleteRecords(int elementId, String ids){
		try {
			String whereClause = "activity_id=? AND id NOT IN (" + ids + ")";
			String[] whereArgs = {elementId + ""};
			db.delete("records", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Elimina un Record
	 * @param recordId		Id del record a eliminar
	 */
	public synchronized void deleteRecord(int recordId){
		try {
			String whereClause = "id=?";
			String[] whereArgs = {recordId + ""};
			db.delete("records", whereClause, whereArgs);
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Elimina un Record a partir de la fecha de creación. Solo válido para records aún no enviados al servidor
	 * @param created		String con la fecha de creación.
	 */
	public synchronized void deleteRecord(String created){
		try {
			String whereClause = "id=? AND created_at=?";
			String[] whereArgs = {"-1",created };
			db.delete("records", whereClause, whereArgs);
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
	}
	
	/**
	 * Recupera un record de la base de datos a partir de su id
	 * @param id			Id del record a buscar
	 * @return				Record encontrado o null en caso de no estar en la base de datos
	 * @throws Exception	Lanza la expcepción en caso de producirse
	 */
	public synchronized RecordModel getRecord(int id) throws Exception{
		RecordModel record = null;
		try {
			String[] columns = {"id",			//0
							   "record_type",	//1
							   "title",			//2
							   "description",	//3
							   "created_at",	//4
							   "updated_at",	//5
							   "data",		    //6
							   "local_data",	//7
							   "blurred",		//8
							   "faces_array",	//9
							   "video_frame",	//10
							   "autor",			//11
							   "permiso"};		//12
			String where = "id=?";
			String[] whereArgs = {id+""};
			Cursor cursor = db.query("records", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				record = new RecordModel();
				record.setId(cursor.getInt(0));
				record.setRecordType(cursor.getString(1));
				record.setTitle(cursor.getString(2));
				record.setDescription(cursor.getString(3));
				record.setCreationDate(Services.Utilidades().StringToDate(cursor.getString(4)));
				record.setUpdateDate(Services.Utilidades().StringToDate(cursor.getString(5)));
				record.setData(cursor.getString(6));	
				record.setLocalData(cursor.getString(7));
				record.setBlurredImage(cursor.getString(8));
				record.setFacesArray(cursor.getString(9));
				record.setVideoFrame(cursor.getString(10));
				record.setAutor(cursor.getString(11));
				record.setPermiso(cursor.getString(12));
			}
			cursor.close();
		} catch (Exception e){
			if (db != null){
				
			}
			throw e;
		}
		return record;
	}
	
	/**
	 * Recupera un record de la base de datos a partir de su id y la fecha de creación
	 * @param id			Id del record a buscar
	 * @param createdAt		Fecha de creación del Record
	 * @return				Record encontrado o null en caso de no estar en la base de datos
	 * @throws Exception	Lanza la expcepción en caso de producirse
	 */
	public synchronized RecordModel getRecord(int id, String createdAt) throws Exception{
		RecordModel record = null;
		try {
			String[] columns = {"id",			//0
							   "record_type",	//1
							   "title",			//2
							   "description",	//3
							   "created_at",	//4
							   "updated_at",	//5
							   "data",		    //6
							   "local_data",	//7
							   "blurred",		//8
							   "faces_array",	//9
							   "video_frame",	//10
							   "autor",			//11
							   "permiso"};		//12
			String where = "id=? AND created_at=?";
			String[] whereArgs = {id+"",createdAt};
			Cursor cursor = db.query("records", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				record = new RecordModel();
				record.setId(cursor.getInt(0));
				record.setRecordType(cursor.getString(1));
				record.setTitle(cursor.getString(2));
				record.setDescription(cursor.getString(3));
				record.setCreationDate(Services.Utilidades().StringToDate(cursor.getString(4)));
				record.setUpdateDate(Services.Utilidades().StringToDate(cursor.getString(5)));
				record.setData(cursor.getString(6));	
				record.setLocalData(cursor.getString(7));
				record.setBlurredImage(cursor.getString(8));
				record.setFacesArray(cursor.getString(9));
				record.setVideoFrame(cursor.getString(10));
				record.setAutor(cursor.getString(11));
				record.setPermiso(cursor.getString(12));
			}
			cursor.close();
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return record;
	}
	
	public synchronized int getRecordId(String createdAt){
		
		try {
			String[] columns = {"id"};	//0
			String where = "created_at=?";
			String[] whereArgs = {createdAt};
			Cursor cursor = db.query("records", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				int x = cursor.getInt(0);
				cursor.close();
				
				return x;
			}
			cursor.close();
		} catch (Exception e){
			if (db != null){
				
			}
		}
		return -1;
	}
	
	public synchronized List<RecordModel> getRecords(int activityId) {
		
		List<RecordModel> lista = new ArrayList<RecordModel>();
		
		try {
			String[] columns = {"id",			//0
							   "record_type",	//1
							   "title",			//2
							   "description",	//3
							   "created_at",	//4
							   "updated_at",	//5
							   "data",			//6
							   "local_data",	//7
							   "blurred",		//8
							   "faces_array",	//9
							   "video_frame",	//10
							   "autor",			//11
							   "permiso"};		//12
			String where = "activity_id=?";
			String[] whereArgs = {activityId + ""};
			String orderBy = "created_at DESC";
			Cursor cursor = db.query(true,"records", columns, where, whereArgs, null, null, orderBy, null);
			
			if(cursor.moveToFirst())
				do{
					RecordModel record = new RecordModel();
					record.setId(cursor.getInt(0));
					record.setRecordType(cursor.getString(1));
					record.setTitle(cursor.getString(2));
					record.setDescription(cursor.getString(3));
					record.setCreationDate(Services.Utilidades().StringToDate(cursor.getString(4)));
					record.setUpdateDate(Services.Utilidades().StringToDate(cursor.getString(5)));
					record.setData(cursor.getString(6));
					record.setLocalData(cursor.getString(7));
					record.setBlurredImage(cursor.getString(8));
					record.setFacesArray(cursor.getString(9));
					record.setVideoFrame(cursor.getString(10));
					record.setAutor(cursor.getString(11));
					record.setPermiso(cursor.getString(12));
					lista.add(record);
				}while(cursor.moveToNext());
				
			cursor.close();
			
			
		} catch (Exception e){
//			if (db != null)
//				
//			throw e;
		}
		return lista;
	}
	
	public synchronized void asignRecordId(int id, String createdAt) throws Exception{
		
		RecordModel record = getRecord(-1, createdAt);
		if (record != null){
			record.setId(id);
			
			ContentValues values = new ContentValues();
			values.put("id", record.getId());
			values.put("record_type", record.getRecordType());
			values.put("title", record.getTitle());
			values.put("description", record.getDescription());
			values.put("created_at", Services.Utilidades().DateToString(record.getCreationDate()));
			values.put("updated_at", Services.Utilidades().DateToString(record.getUpdateDate()));
			values.put("data", record.getData());
			values.put("local_data", record.getLocalData());
			String where = "id=? AND created_at=?";
			String[] whereArgs = {"-1",createdAt};
			db.update("records", values, where, whereArgs);
			
		}
		
	}
	
	
	
	public synchronized boolean isRecordSinc(int recordId){
		try {
			String[] columns = {"record_id"};	//0
			String where = "record_id=?";
			String[] whereArgs = {recordId+ ""};					
			Cursor cur = db.query("synchronize", columns, where, whereArgs, null, null, null);
			
			if(cur.moveToFirst()){
				cur.close();
				
				return false;
			}
			cur.close();
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		
		return true;
	}
	
	public synchronized boolean isActivitySinc(int activityId){
		
		try {
			
			String[] columns = {"record_id"};	//0
			String where = "activity_id=?";
			String[] whereArgs = {activityId+ ""};					
			Cursor cur = db.query("synchronize", columns, where, whereArgs, null, null, null);
			
			if(cur.moveToFirst()){
				cur.close();
				
				return false;
			}
			cur.close();
			
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		
		return true;
	}
	
	
	public synchronized boolean isExperienceSinc(int experienceId){
		String ids = getActivityIds(experienceId);
		String entIds = getEntregasIds(experienceId);
		int lessonPlanId = getLessonPlanIdFromExperienceId(experienceId);
		ids = ids + "," + lessonPlanId;
		String [] idsArray = ids.split(",");
		boolean sinc = true;
		
		for (int i = 0; i < idsArray.length; i++){
			if(idsArray[i].equals("")) continue;
			
			String entActIds = getEntregasIds(Integer.parseInt(idsArray[i]));
			String [] eIds = entActIds.split(",");
			for (int j = 0; j < eIds.length; j++){
				if(eIds[j].equals("")) continue;
				sinc = isActivitySinc(Integer.parseInt(eIds[j]));
				if(!sinc)
					return false;
			}
			
			sinc = isActivitySinc(Integer.parseInt(idsArray[i]));
			if(!sinc)
				return false;
		}
		idsArray = entIds.split(",");
		for (int i = 0; i < idsArray.length; i++){
			if(idsArray[i].equals(""))continue;
			sinc = isActivitySinc(Integer.parseInt(idsArray[i]));
			if(!sinc)
				return false;
		}
		return isActivitySinc(experienceId);
	}
	
	public synchronized String getEntregasIds(String actIds){
		String ids = "";
		String [] idsArray = actIds.split(",");
		for (int i = 0; i < idsArray.length; i++){
			if(idsArray[i].equals("")) continue;
			ids = ids + "," + getEntregasIds(Integer.parseInt(idsArray[i]));
		}
		return ids;
	}
	
	public synchronized int getCountSinc(){
		int count = 0;
		try {
			String[] columns = {"command",		//0
								"data",			//1
								"created",		//2
								"record_id"};	//3
			Cursor cur = db.query("synchronize", columns, null, null, null, null, null);
			count = cur.getCount();
			cur.close();
		} catch (Exception e) {
			if (db != null){
				
			}
		}
		return count;
	}
	
	/**
	 * Guarda un anexo de un recurso en la base de datos
	 * @param experience	Experiencia que se va a guardar
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized void setAnexoRecurso(AnexosRecursoModel anexo) throws Exception{
		
		try {
			AnexosRecursoModel an = getAnexoRecurso(anexo.getId());
			if(an != null){
				ContentValues values = new ContentValues();
				values.put("id", anexo.getId());
				values.put("type_append", anexo.getType());
				values.put("snippet_url", anexo.getSnippetUrl());
				values.put("document", anexo.getDocument());
				values.put("resource_id", anexo.getRecursoId());
				values.put("file_name", anexo.getFileName());
				values.put("address", anexo.getDireccion());
				values.put("latitude", anexo.getLatitude());
				values.put("longitude", anexo.getLongitude());
				String where = "id=?";
				String[] whereArgs = {anexo.getId()+""};
				db.update("resources_appends", values, where, whereArgs);
				
			}else{
				
				ContentValues values = new ContentValues();
				values.put("id", anexo.getId());
				values.put("type_append", anexo.getType());
				values.put("snippet_url", anexo.getSnippetUrl());
				values.put("document", anexo.getDocument());
				values.put("resource_id", anexo.getRecursoId());
				values.put("file_name", anexo.getFileName());
				values.put("address", anexo.getDireccion());
				values.put("latitude", anexo.getLatitude());
				values.put("longitude", anexo.getLongitude());
				db.insert("resources_appends", null, values);
					
			}
						
		} catch (Exception e) {
			if (db != null)
				
			throw e;
		}
	}
	
	
	
	
	/**
	 * Obtiene un anexo a partir de su id
	 * @param id			Id de la experiencia a buscar en la base de datos
	 * @return				Experiencia identificada por el id, o null en caso contrario
	 * @throws Exception	Lanza la excepción en caso de producirse
	 */
	public synchronized AnexosRecursoModel getAnexoRecurso(int id) throws Exception{
		
		AnexosRecursoModel anexo = null;
		try {
			
			String[] columns = {"id",					//0
							   "resource_id",			//1
							   "type_append",			//2
							   "snippet_url",			//3
							   "document",				//4
							   "file_name",				//5
							   "address",				//6	
							   "latitude",				//7
							   "longitude"};			//8
			String where = "id=?";
			String[] whereArgs = {id+""};
			Cursor cursor = db.query("resources_appends", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst()){
				anexo = new AnexosRecursoModel();
				anexo.setId(cursor.getInt(0));
				anexo.setRecursoId(cursor.getInt(1));
				anexo.setType(cursor.getString(2));
				anexo.setSnippetUrl(cursor.getString(3));
				anexo.setDocument(cursor.getString(4));
				anexo.setFileName(cursor.getString(5));
				anexo.setDireccion(cursor.getString(6));
				anexo.setLatitude(cursor.getDouble(7));
				anexo.setLongitude(cursor.getDouble(8));
			}
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return anexo;
		
	}
	
	
	public synchronized List<AnexosRecursoModel> getAnexosRecurso (int resoucreId) throws Exception{
		List<AnexosRecursoModel> anexos = new ArrayList<AnexosRecursoModel>();
		try {
			
			String[] columns = {"id",					//0
							   "resource_id",			//1
							   "type_append",			//2
							   "snippet_url",			//3
							   "document",				//4
							   "file_name",				//5
							   "address",				//6	
							   "latitude",				//7
							   "longitude"};			//8
//			String where = "resource_id=? AND type_append=?";
//			String[] whereArgs = {resoucreId+"","Presentation"};
			String where = "resource_id=?";
			String[] whereArgs = {resoucreId+""};
			Cursor cursor = db.query("resources_appends", columns, where, whereArgs, null, null, null);
			if(cursor.moveToFirst())
				do{
					AnexosRecursoModel anexo = new AnexosRecursoModel();
					anexo.setId(cursor.getInt(0));
					anexo.setRecursoId(cursor.getInt(1));
					anexo.setType(cursor.getString(2));
					anexo.setSnippetUrl(cursor.getString(3));
					anexo.setDocument(cursor.getString(4));
					anexo.setFileName(cursor.getString(5));
					anexo.setDireccion(cursor.getString(6));
					anexo.setLatitude(cursor.getDouble(7));
					anexo.setLongitude(cursor.getDouble(8));
					anexos.add(anexo);
					
				}while(cursor.moveToNext());
				
			cursor.close();
			
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return anexos;
		
	}
	
	
	public synchronized List<AnexosRecursoModel> getAnexosRecurso (String ids) throws Exception{
		List<AnexosRecursoModel> anexos = new ArrayList<AnexosRecursoModel>();
		try {
			
			if(ids.equals(""))
				return anexos;
			String[] idsArray = ids.split(",");
			for (int i = idsArray.length -1 ; i >=0 ; i--){	
				//RecursoModel m = getRecurso(Integer.parseInt(idsArray[i]));
				if(!idsArray[i].equals("")){
					List<AnexosRecursoModel> a = getAnexosRecurso(Integer.parseInt(idsArray[i]));
				
					if (a != null)
						anexos.addAll(a);
				}
			}
			
		} catch (Exception e){
			if (db != null)
				
			throw e;
		}
		return anexos;
	}
	
}
