package area.communication.implementation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.exception.WebServiceException;
import area.communication.exception.WebServiceNotAvailableException;
import area.communication.interfaces.IWebService;
import area.communication.services.WebServiceServices;
import area.domain.interfaces.IAction;
import area.domain.interfaces.IActionException;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import eduarea.facedetector.FaceDetector;
/**
 * Clase donde se implementan todas las llamadas al servidor
 * @author luis
 *
 */
public class WebService implements IWebService {

	private DefaultHttpClient _cli = null;
	
	/**
	 * Tipo de métodos HTTP para comunicarse con la API REST
	 */
	private final int _GET 		= 0;
	private final int _POST 	= 1;
	private final int _PUT 		= 2;
	private final int _DELETE	= 3;
	
	
	public static final String _BASE_URL = "Dominio del sistema Edu-AREA";
	
	/**
	 * Comandos de comunicación con el servidor
	 */
	
	public static final String _CMD_LOGIN							= "/users/sign_in?locale=gl";
	public static final String _CMD_GET_USER						= "/users.json";
	public static final String _CMD_GET_EXPERIENCES					= "/gl/experiences.json?page=#&state=$";
	public static final String _CMD_GET_OWN_EXPERIENCES				= "/gl/experiences/search.json?search=&owner=true&page=";
	public static final String _CMD_GET_EXPERIENCES_WHOLE_VIEW		= "/gl/experiences/#.json";
	public static final String _CMD_GET_COMMENTS					= "/gl/experiences/#/getComments.json";
	public static final String _CMD_SET_COMMENT						= "/gl/experiences/#/setComments";
	public static final String _CMD_GET_ACTIVITY_SEQUENCE_IDS		= "/gl/activitySequence/getWholeView.json?id=";
	public static final String _CMD_GET_ACTIVITIES_MINI_VIEW		= "/gl/activities/#/mini.json";
	public static final String _CMD_GET_ACTIVITIES_SMALL_VIEW		= "/gl/activities/#/small.json";
	public static final String _CMD_GET_ACTIVITIES_WHOLE_VIEW		= "/gl/activities/#.json";
	public static final String _CMD_PUT_EXPERIENCE					= "/gl/lesson_plans/#.json";
	public static final String _CMD_SET_REFLECTION 					= "/gl/experienceActivityEntries/setReflection.json?";
	public static final String _CMD_GET_RECORDS						= "/gl/$/#/getRecords.json";
	public static final String _CMD_ADD_RECORD						= "/gl/$/#/addRecord.json";
	public static final String _CMD_UPDATE_RECORD					= "/gl/experiences/#.json";
	public static final String _CMD_DELETE_RECORD					= "/gl/$/#/deleteRecord.json";
	public static final String _CMD_ADD_ACTIVITY					= "/gl/experiences/#/addFastElement.json";
	public static final String _CMD_PUT_ACTIVITY					= "/gl/activities/#.json";
	public static final String _CMD_GET_RESOURCES_VIEW				= "/gl/resources/#.json";
	public static final String _CMD_GET_SUBMISSIONS_VIEW			= "/gl/submissions/#.json";
	public static final String _CMD_ADD_SUBMISSION					= "/gl/$/#/addSubmission.json";
	public static final String _CMD_PUT_SUBMISSION					= "/gl/submissions/#.json";
	public static final String _CMD_ENTER_CODE						= "/gl/experiences/useCode.json";
	public static final String _CMD_GET_SHARE_CODE					= "/gl/experiences/#/getCodeToShare.json";
	public static final String _CMD_GET_FULL_EXPERIENCE				= "/gl/experiences/#/full.json";
	public static final String _CMD_GET_UPDATED_AT					= "/gl/experiences/#/getUpdatedAt.json";
	public static final String _CMD_ADD_EXPERIENCE					= "/gl/experiences.json";
	public static final String _CMD_DELETE_ELEMNT					= "/gl/$/#.json";
	
	
	
	/**
	 * Constructor
	 */
	public WebService(){
		
	}
	
	private InputStream descargaArchivo(String host, String comand) throws ClientProtocolException, IOException, WebServiceException{
		DefaultHttpClient cli = new DefaultHttpClient();
		String uri = host + comand;
		HttpGet get = new HttpGet(uri);
		HttpResponse resp = cli.execute(get);
		if(resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
			throw new WebServiceException("El servidor ha devuelto HTTP STATUS CODE: " + resp.getStatusLine().getStatusCode());
		}
		return resp.getEntity().getContent();
	}
	
	private Object ejecutarPeticion(String host, String comand, int metodo, List<NameValuePair> datos, String rutaArchivo, String element_image, Integer tipoUpdade) throws WebServiceException, Exception{
		Object respJSON = null;
		try {
			DefaultHttpClient cli; 
			if(_cli == null)
				cli = new DefaultHttpClient();
			else
				cli = _cli;
			
			
			
			comand = comand.replace("/gl/", ApplicationService.get_idioma());
			switch (metodo) {
			case _POST:
				respJSON = ejecutarPost(cli, host, comand, datos);
				break;
			case _GET:
				respJSON = ejecutarGet(cli, host, comand, datos);
				break;
			case _PUT:
				respJSON = ejecutaPut(cli, host, comand, datos, rutaArchivo, element_image, tipoUpdade);
				break;
			case _DELETE:
				respJSON = ejecutaDelete(cli, host, comand, datos);
				break;

			}
			
			
			//Toast.makeText(ApplicationService.getContext(), "ffffffff", Toast.LENGTH_LONG).show();
			
			return respJSON;

			
			
		} catch (final WebServiceNotAvailableException e){
			e.printStackTrace();
			
			Handler mH = new Handler(ApplicationService.getContext().getMainLooper());
			mH.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(ApplicationService.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			});
			return respJSON;
			//throw e;
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private Object ejecutarGet(DefaultHttpClient cli, String host,
			String comand, List<NameValuePair> datos) throws ClientProtocolException, IOException, WebServiceException, JSONException {
		JSONObject jResp = null;
		String uri = host + comand;
		
		uri = datos == null ? uri : uri + "?" + URLEncodedUtils.format(datos, "UTF-8");
		
		
		HttpGet get = new HttpGet(uri);
		ApplicationService.getContext();
		SharedPreferences preferences = ApplicationService.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
		get.addHeader("Cookie", "_EduAreaBeta2_session=" + preferences.getString("Cookie", "0"));
		
		
		String datosTXT = datos == null ? "" : datos.toString();
		Log.d("WebService", "Peticion lanzada a: " + uri + "datos: " + datosTXT);
		long inicio = new Date().getTime();
		HttpResponse resp = cli.execute(get);
		Log.d("WebService", "Respuesta del servidor en: " + (new Date().getTime() - inicio) + " milisegundos.");
		
		
		
		if(resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
			throw new WebServiceException("El servidor ha devuelto HTTP STATUS CODE: " + resp.getStatusLine().getStatusCode());
		}
		if(resp.getEntity().getContentType().getValue().contains("application/json")){
			String respString = EntityUtils.toString(resp.getEntity());
			Log.d("WebService", "Se recibe respuesta del servidor: " + respString);
			return respString;
		}
		return jResp;
	}

	private Object ejecutarPost(DefaultHttpClient cli, String host, String comand, List<NameValuePair> datos) throws UnsupportedEncodingException, IOException,
			ClientProtocolException, WebServiceException, JSONException, WebServiceNotAvailableException {
		
		JSONObject jResp = null;
		String uri = host + comand;
		HttpPost post = new HttpPost(uri.replace("lessons_plans", "lesson_plans"));
		
		ApplicationService.getContext();
		SharedPreferences preferences = ApplicationService.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
		// Añadie la cabecera con la cookie
		if (comand != _CMD_LOGIN){
			
			post.addHeader("Cookie", "_EduAreaBeta2_session=" + preferences.getString("Cookie", "0"));
		}
		
		if (datos != null) {
			String datosTXT = datos.toString();
			Log.i("WebService", "Peticion lanzada a: " + uri + "\n datos: " + datosTXT);
		}
		if(datos != null)
			post.setEntity(new UrlEncodedFormEntity(datos,"UTF-8"));
		long inicio = new Date().getTime();
		HttpResponse resp = cli.execute(post);
		if(comand == _CMD_LOGIN){
			String respStr = EntityUtils.toString(resp.getEntity());
			if(respStr.contains("user[password]"))
				jResp = null;
			else{
				jResp = new JSONObject("{\"error\": \"0\"}");
				Cookie cookie = cli.getCookieStore().getCookies().get(0);
				preferences.edit().putString("Cookie", cookie.getValue()).commit();
			}
		}
		Log.d("WebService", "Respuesta del servidor en: " + (new Date().getTime() - inicio) + " milisegundos.");
		if(resp.getStatusLine().getStatusCode() == 450){
			throw new WebServiceNotAvailableException("El servidor no ha permitido realizar una operación");
		}
		if(resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
			throw new WebServiceException("El servidor ha devuelto HTTP STATUS CODE: " + resp.getStatusLine().getStatusCode());
		}
		if(resp.getEntity().getContentType().getValue().contains("application/json")){
			String respString = EntityUtils.toString(resp.getEntity());
			Log.i("WebService", "Se recibe respuesta del servidor: " + respString);
			jResp = new JSONObject(respString);
		}
		return jResp;
	}
	
	private Object ejecutaPut(DefaultHttpClient cli, String host, String comand, List<NameValuePair> datos, String rutaArchivo, String element_image, int tipoUpdate) 
			throws ClientProtocolException, IOException, WebServiceException, WebServiceNotAvailableException{
		
		JSONObject jResp = null;
		String uri = host + comand;
		
		HttpPut put = new HttpPut(uri);
		
		SharedPreferences preferences = ApplicationService.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
		put.addHeader("Cookie", "_EduAreaBeta2_session=" + preferences.getString("Cookie", "0"));
		
		if (datos != null) {
			String datosTXT = datos.toString();
			Log.i("WebService", "Peticion lanzada a: " + uri + "\n datos: " + datosTXT);
		}
		
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		Iterator<NameValuePair> it = datos.iterator();
		while (it.hasNext()) {
			NameValuePair dato = it.next();
			entity.addPart(dato.getName(), new StringBody(dato.getValue(), Charset.forName("UTF-8")) );
		}
		if (rutaArchivo != null && !rutaArchivo.equals("")){
			String fileExtension = MimeTypeMap.getFileExtensionFromUrl(rutaArchivo);
			String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
			if (mimeType != null)
				entity.addPart(element_image, new FileBody(new File(rutaArchivo),mimeType));
			else{
				if(tipoUpdate == RecordModel.RECORD_IMAGE)
					entity.addPart(element_image, new FileBody(new File(rutaArchivo),"image/jpeg"));
				if(tipoUpdate == RecordModel.RECORD_VIDEO)
					entity.addPart(element_image, new FileBody(new File(rutaArchivo),"video/mp4"));
			}
		}
        put.setEntity(entity);
        long inicio = new Date().getTime();
        HttpResponse resp = cli.execute(put);
        Log.i("WebService", "Respuesta del servidor en: " + (new Date().getTime() - inicio) + " milisegundos.");
        if(resp.getStatusLine().getStatusCode() == 450){
			throw new WebServiceNotAvailableException("El servidor no ha permitido realizar una operación");
		}
        if(resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
			throw new WebServiceException("El servidor ha devuelto HTTP STATUS CODE: " + resp.getStatusLine().getStatusCode());
		}
		return null;
		
	}
	
	private Object ejecutaDelete(DefaultHttpClient cli, String host, String comand, List<NameValuePair> datos) throws UnsupportedEncodingException, IOException,
			ClientProtocolException, WebServiceException, JSONException {
		
		JSONObject jResp = null;
		String uri = host + comand;
		uri = datos == null ? uri : uri + "?" + URLEncodedUtils.format(datos, "UTF-8");
		
		HttpDelete delete = new HttpDelete(uri);
		
		
		
		// Añadie la cabecera con la cookie
		SharedPreferences preferences = ApplicationService.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
		delete.addHeader("Cookie", "_EduAreaBeta2_session=" + preferences.getString("Cookie", "0"));
		
		if (datos != null) {
			String datosTXT = datos.toString();
			Log.d("WebService", "Peticion lanzada a: " + uri + "\n datos: " + datosTXT);
		}
		
		long inicio = new Date().getTime();
		HttpResponse resp = cli.execute(delete);
		
		Log.d("WebService", "Respuesta del servidor en: " + (new Date().getTime() - inicio) + " milisegundos.");
		if(resp.getStatusLine().getStatusCode() != HttpStatus.SC_NO_CONTENT && resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
			throw new WebServiceException("El servidor ha devuelto HTTP STATUS CODE: " + resp.getStatusLine().getStatusCode());
		}
		
		return jResp;
	}
	
	
	@Override
	public void getToken(IActionException<String> callback) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void login(String email, String pass, IAction<Boolean> callback) {
		class LoginTask extends AsyncTask<Void, Void, Boolean>{

			private String _email;
			private String _pass;
			private IAction<Boolean> _delegado;
			
			public LoginTask(String email, String pass, IAction<Boolean> delegado) {
				_email = email;
				_pass = pass;
				_delegado = delegado;
			}
			
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					PerfilModel perfil = LocalStorageServices.DatabaseService().getPerfil();
					if(perfil != null){
						if(perfil.getEmail().equals(_email) && perfil.getPass().equals(Services.Utilidades().md5(_pass)))
							return true;
					}

					return login(_email,_pass);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				_delegado.Do(result);
			}
			
		}
		LoginTask login = new LoginTask(email, pass, callback);
		login.execute();
		
	}

	public Boolean login(String _email, String _pass) {
		//TODO implementar el envío del md5 del password al servidor
		JSONObject resp = null;
		PerfilModel perfil;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("user[email]",_email));
		nameValuePairs.add(new BasicNameValuePair("user[password]",_pass));
		try {
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, _CMD_LOGIN, _POST, nameValuePairs, null, null, null);
			if (resp != null){
				perfil = new PerfilModel(_email, Services.Utilidades().md5(_pass));
				JSONObject respUser = new JSONObject((String)ejecutarPeticion(_BASE_URL, _CMD_GET_USER, _GET, null, null, null, null));
				if(respUser.isNull("id"))
					throw new WebServiceException("No hay usuario");
				if(!respUser.isNull("type"))
					perfil.setType(respUser.getString("type"));
				if(!respUser.isNull("id"))
					perfil.setId(respUser.getInt("id"));
									
				LocalStorageServices.DatabaseService().deleteExperiencesIds();
				LocalStorageServices.DatabaseService().deletePerfil();
				LocalStorageServices.DatabaseService().setPerfil(perfil);
				return true;
			}else
				return false;
			
		} catch (WebServiceException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public void getExperiencesIds(int estado, IAction<String> callback) {
		
		
		class GetExperiencesTask extends AsyncTask<Void, Void, Void>{

			private IAction<String> _delegado;
			private int _estado;
			
			public GetExperiencesTask(int estado, IAction<String> delegado) {
				_delegado = delegado;
				_estado = estado;
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				JSONArray expIdsArray = null;
				String ids = "";
				try {
					String state = "";
					switch (_estado) {
					case Experience.ALL:
						state = "all";
						break;
					case Experience.BEING_PREPARED:
						state = "beingPrepared";
						break;
					case Experience.IN_PROGRESS:
						state = "inProgress";
						break;
					case Experience.FINISHED:
						state = "finished";
						break;
					
					}
					String comando = _CMD_GET_EXPERIENCES.replace("#", "1").replace("$", state);
					
					String sResp = (String)ejecutarPeticion(_BASE_URL, comando, _GET, null, null, null, null);
					expIdsArray = new JSONArray(sResp);
					for (int i = 0; i < expIdsArray.length(); i++) {
						JSONObject expId = expIdsArray.getJSONObject(i);
						if (!expId.isNull("id"))
							ids = ids + expId.getString("id");
						ids = (i == expIdsArray.length()-1) ? ids+"" : ids+",";						
					}
					LocalStorageServices.DatabaseService().setExperiencesIds(ids, _estado);
					
				} catch (WebServiceException e) {
					
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				_delegado.Do(LocalStorageServices.DatabaseService().getExperiencesIds(_estado));
			}
			
		}
		GetExperiencesTask task = new GetExperiencesTask(estado, callback);
		task.execute();
	}
	
	
	@Override
	public void getWholeViewExperiences(final String ids, boolean local, IAction<List<Experience>> callback) {
		
		class GetViewExperiencesTask extends AsyncTask<Void, Void, Void>{

			private IAction<List<Experience>> _delegado;
			private String _ids;
			private boolean _local;
			
			public GetViewExperiencesTask(String ids, boolean local, IAction<List<Experience>> delegado) {
				_delegado = delegado;
				_ids = ids;
				_local = local;
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				JSONArray experiencesViews = null;
				try {
					if(LocalStorageServices.DatabaseService().getExperiences(_ids).size() > 0)
						publishProgress();
					
					String comando = _CMD_GET_EXPERIENCES_WHOLE_VIEW.replace("#", ids);
					String sResp =  (String) ejecutarPeticion(_BASE_URL, comando , _GET, null, null, null, null);
					experiencesViews = new JSONArray(sResp);
					
					
					for (int i = 0; i < experiencesViews.length(); i++) {
						JSONObject expView = experiencesViews.getJSONObject(i);						
						Experience exp = extraerExperience(expView, false);						
						if (!expView.isNull("activities")) {
							JSONArray aIds = expView.getJSONArray("activities");
							LocalStorageServices.DatabaseService().deleteActivitiesIds(exp.getId());
							for (int j = 0; j < aIds.length(); j++) {
								JSONObject activityId = aIds.getJSONObject(j);
								if (!activityId.isNull("id")){
									if(!activityId.isNull("position")){
										LocalStorageServices.DatabaseService().setActivityId(exp.getId(), activityId.getInt("id"), activityId.getInt("position"));
									}
								}
							}
						}
						// Se extraen los ids de los recursos
						if (!expView.isNull("resources")) {
							JSONArray rIds = expView.getJSONArray("resources");
							LocalStorageServices.DatabaseService().deleteRecursosIds(exp.getId());
							for (int j = 0; j < rIds.length(); j++) {
								JSONObject recursoId = rIds.getJSONObject(j);
								if (!recursoId.isNull("id")){
									LocalStorageServices.DatabaseService().setRecursoId(exp.getId(), recursoId.getInt("id"));
								}
							}
						}
						// Se extraen los ids de los recursos
						if (!expView.isNull("submissions")) {
							JSONArray eIds = expView.getJSONArray("submissions");
							LocalStorageServices.DatabaseService().deleteEntregasIds(exp.getId());
							for (int j = 0; j < eIds.length(); j++) {
								JSONObject entregaId = eIds.getJSONObject(j);
								if (!entregaId.isNull("id")){
									LocalStorageServices.DatabaseService().setEntregaId(exp.getId(), entregaId.getInt("id"));
								}
							}
						}
						// obtención de datos para la pestaña de detalles
						if(!expView.isNull("template")){
							JSONObject template = expView.getJSONObject("template");
							extraerTemplate(exp.getId(), template);
						}
						if(!expView.isNull("comments")){
							extraerComentarios(expView.getJSONArray("comments"), exp.getLessonPlanId());
						}
						
					}
					
				} catch (WebServiceException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Void... values) {
				try {
					_delegado.Do(LocalStorageServices.DatabaseService().getExperiences(_ids));
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
			@Override
			protected void onPostExecute(Void result) {
				try {
					_delegado.Do(LocalStorageServices.DatabaseService().getExperiences(_ids));
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
			
		}
		GetViewExperiencesTask task = new GetViewExperiencesTask(ids, local, callback);
		task.execute();
	}

	@Override
	public void getComents(int experienceId, int numComent,IAction<List<Comment>> callback) {
		
		class GetCommentTask extends AsyncTask<Void, Void, Void>{

			private int _experienceId;
			private int _numComent;
			private IAction<List<Comment>> _delegado;

			public GetCommentTask(int experienceId, int numComent,IAction<List<Comment>> callback) {
				_experienceId = experienceId;
				_numComent = numComent;
				_delegado = callback;
			}

			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					if(LocalStorageServices.DatabaseService().getComments(_experienceId).size() > 0)
						publishProgress();
					JSONArray jComentarios = null;
				
					String comando = _CMD_GET_COMMENTS.replace("#", _experienceId + "");
					String sResp =  (String) ejecutarPeticion(_BASE_URL, comando, _GET, null, null, null, null);
					jComentarios = new JSONArray(sResp);
					extraerComentarios(jComentarios,_experienceId);
					
					
				} catch (WebServiceException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			
			@Override
			protected void onPostExecute(Void result) {
				try {
					_delegado.Do(LocalStorageServices.DatabaseService().getComments(_experienceId));
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
			
		}
		GetCommentTask task = new GetCommentTask(experienceId, numComent, callback);
		task.execute();
	}

	@Override
	public void setCommentAsync(Comment comentario, IAction<Boolean> callback) {
		class setCommentAsync extends AsyncTask<Void, Void, Boolean>{

			private IAction<Boolean> _delegado;
			private Comment _comentario;
			
			public setCommentAsync(Comment comentario, IAction<Boolean> delegado) {
				_comentario = comentario;
				_delegado = delegado;
			}
			
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					JSONObject resp = null;
					
					JSONObject js = new JSONObject();
					js.put("object_id", _comentario.getModelId()+"");
					js.put("description", _comentario.getComment());
					

						CommandModel comando = new CommandModel();
						comando.setCommand(_CMD_SET_COMMENT);
						comando.setData(js.toString());
						LocalStorageServices.DatabaseService().setComment(_comentario);
						LocalStorageServices.DatabaseService().setCommand(comando);
						WebServiceServices.Sincronizador().iniciar();
						return false;
					//}
	               
	                			
				} catch (WebServiceException e){
					e.printStackTrace();
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				//return true;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				_delegado.Do(result);
			}
			
		}
		
		setCommentAsync task = new setCommentAsync(comentario, callback);
		task.execute();
	}

	@Override
	public boolean setComment(JSONObject datos) {
		try {
			JSONObject resp;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("description", datos.getString("description")));
			String comando = _CMD_SET_COMMENT.replace("#", datos.getString("object_id"));
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _POST, nameValuePairs, null, null, null);
			return true;
        } catch (WebServiceException e) {
			e.printStackTrace();
			CommandModel comando = new CommandModel();
			comando.setCommand(_CMD_SET_COMMENT);
			comando.setData(datos.toString());
			try {
				LocalStorageServices.DatabaseService().setCommand(comando);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}




	@Override
	public void getActivitiesIds(int expereinceId,IAction<String> callback) {
		
		class GetActivitiesIdsTask extends AsyncTask<Void, Void, Void>{

			private IAction<String> _delegado;
			private int _expereinceId;
			
			public GetActivitiesIdsTask(int expereinceId, IAction<String> delegado) {
				_delegado = delegado;
				_expereinceId = expereinceId;
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				_delegado.Do(LocalStorageServices.DatabaseService().getActivityIds(_expereinceId));
			}
		}
		GetActivitiesIdsTask task = new GetActivitiesIdsTask(expereinceId, callback);
		task.execute();
	}

	@Override
	public void getMiniViewActivities(String ids,  int experienceId, IAction<List<MiniActivity>> callback) {
		
		class GetMiniViewActivitiesTask extends AsyncTask<Void, Void, Void>{

			private IAction<List<MiniActivity>> _delegado;
			private String _ids;
			private int _experienceId;
			
			public GetMiniViewActivitiesTask(String ids, int experienceId, IAction<List<MiniActivity>> delegado) {
				_delegado = delegado;
				_ids = ids;
				_experienceId = experienceId;
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				JSONArray activitiesMiniViews = null;
				try {
					if(LocalStorageServices.DatabaseService().getMiniActivities(_ids).size() > 0)
						publishProgress();
					String comando = _CMD_GET_ACTIVITIES_WHOLE_VIEW.replace("#",LocalStorageServices.DatabaseService().getActivityIdsNoLocal(_experienceId));
					String sResp =  (String) ejecutarPeticion(_BASE_URL, comando, _GET, null, null, null, null);
					activitiesMiniViews = new JSONArray(sResp);
					for (int i = 0; i < activitiesMiniViews.length(); i++) {
						JSONObject expView = activitiesMiniViews.getJSONObject(i);
						
						MiniActivity miniActivity = extraerActivity(expView, _experienceId, false);
						
						// obtención de datos para la pestaña de detalles
						if(!expView.isNull("template")){
							JSONObject template = expView.getJSONObject("template");
							extraerTemplate(miniActivity.getId(), template);
						}
																	
						// Se extraen los ids de los recursos
						if (!expView.isNull("resources")) {
							JSONArray rIds = expView.getJSONArray("resources");
							LocalStorageServices.DatabaseService().deleteRecursosIds(miniActivity.getId());
							for (int j = 0; j < rIds.length(); j++) {
								JSONObject recursoId = rIds.getJSONObject(j);
								if (!recursoId.isNull("id")){
									LocalStorageServices.DatabaseService().setRecursoId(miniActivity.getId(), recursoId.getInt("id"));
								}
							}
						}
						// Se extraen los ids de los recursos
						if (!expView.isNull("submissions")) {
							JSONArray eIds = expView.getJSONArray("submissions");
							LocalStorageServices.DatabaseService().deleteEntregasIds(miniActivity.getId());
							for (int j = 0; j < eIds.length(); j++) {
								JSONObject entregaId = eIds.getJSONObject(j);
								if (!entregaId.isNull("id")){
									LocalStorageServices.DatabaseService().setEntregaId(miniActivity.getId(), entregaId.getInt("id"));
								}
							}
						}
						if(!expView.isNull("comments")){
							extraerComentarios(expView.getJSONArray("comments"), miniActivity.getId());
						}
						
					}
					
				} catch (WebServiceException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Void... values) {
				try {
					_delegado.Do(LocalStorageServices.DatabaseService().getMiniActivities(_ids));
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
			
			@Override
			protected void onPostExecute(Void result) {
				try {
					_delegado.Do(LocalStorageServices.DatabaseService().getMiniActivities(_ids));
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
		}
		GetMiniViewActivitiesTask task = new GetMiniViewActivitiesTask(ids, experienceId, callback);
		task.execute();
		
	}

	@Override
	public void putExperienceAsync(Experience experiencia, IAction<Boolean> callback) {
				
		class putExperienceAsync extends AsyncTask<Void, Void, Boolean>{

			private IAction<Boolean> _delegado;
			private Experience _experiencia;
			
			public putExperienceAsync(Experience experiencia, IAction<Boolean> delegado) {
				_experiencia = experiencia;
				_delegado = delegado;
			}
			
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					JSONObject resp = null;
					JSONObject js = new JSONObject();
					js.put("title", _experiencia.getName());
					js.put("description", _experiencia.getDescription());
					js.put("description_student", _experiencia.getDescriptionStudent());
					js.put("lesson_plan_id", _experiencia.getLessonPlanId());
					js.put("parent_element", "true");
					js.put("definition", _experiencia.getDefinition());
					js.put("image", _experiencia.getElement_image_file_name());
					LocalStorageServices.DatabaseService().setExperience(_experiencia);

						CommandModel comando = new CommandModel();
						comando.setCommand(_CMD_PUT_EXPERIENCE);
						comando.setData(js.toString());
						LocalStorageServices.DatabaseService().setCommand(comando);
						WebServiceServices.Sincronizador().iniciar();
						return false;
					//}	                			
				} catch (WebServiceException e){
					e.printStackTrace();
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				_delegado.Do(result);
			}
		}
		putExperienceAsync task = new putExperienceAsync(experiencia, callback);
		task.execute();
	}

	@Override
	public boolean putExperience(JSONObject datos) {
		try {
			JSONObject js = new JSONObject();
			
			if(datos.getInt("lesson_plan_id") < 0){
				js.put("lesson_plan_id", LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(datos.getInt("lesson_plan_id"))));
				
				//js.put("lesson_plan_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(datos.getInt("lesson_plan_id")));
			}else
				js.put("lesson_plan_id", datos.getString("lesson_plan_id"));
			
			//js.put("lesson_plan_id", datos.getString("lesson_plan_id"));
			js.put("campo", "title");
			js.put("contenido", datos.getString("title"));
			js.put("parent_element", "true");
			boolean titulo = putExperienceField(js);
			js.put("campo", "description");
			js.put("contenido", datos.getString("description"));
			boolean desc = putExperienceField(js);
			boolean image = true;
			if(!datos.getString("image").startsWith("/system") && !datos.getString("image").equals("none")){
				js.put("campo", "image");
				js.put("contenido", datos.getString("image"));
				image = putExperienceField(js);
			}
			js.put("campo", "description_student");
			js.put("contenido", datos.getString("description_student"));
			boolean descStudent = putExperienceField(js);
			js.put("campo", "definition");
			js.put("contenido", datos.getString("definition"));
			boolean definition = putExperienceField(js);
			if(titulo&&desc&&image&&descStudent&&definition)
				return true;
						
		} catch (JSONException e) {
			e.printStackTrace();
		}
//		CommandModel comando = new CommandModel();
//		comando.setCommand(_CMD_PUT_EXPERIENCE);
//		comando.setData(datos.toString());
//		try {
//			LocalStorageServices.DatabaseService().setCommand(comando);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		WebServiceServices.Sincronizador().iniciar();
		
		return false;
		
	}
	
	@Override
	public boolean putExperienceField(JSONObject datos) {
		
		try {
			JSONObject resp;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id",datos.getString("lesson_plan_id")));
			nameValuePairs.add(new BasicNameValuePair("campo",datos.getString("campo")));
			nameValuePairs.add(new BasicNameValuePair("parent_element", datos.getString("parent_element")));
			String comando = _CMD_PUT_EXPERIENCE.replace("#", datos.getString("lesson_plan_id"));
			String rutaArchivo = null;
			
			if(datos.getString("campo").equals("image"))
				rutaArchivo = datos.getString("contenido");
			else

				nameValuePairs.add(new BasicNameValuePair("contenido",datos.getString("contenido")));
			
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _PUT, nameValuePairs, rutaArchivo, "contenido", RecordModel.RECORD_IMAGE);
			return true;
        } catch (WebServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void putActivityAsync(MiniActivity activity, IAction<Boolean> callback) {
				
		class putExperienceAsync extends AsyncTask<Void, Void, Boolean>{

			private IAction<Boolean> _delegado;
			private MiniActivity _activity;
			
			public putExperienceAsync(MiniActivity activity, IAction<Boolean> delegado) {
				_activity = activity;
				_delegado = delegado;
			}
			
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					JSONObject resp = null;
					JSONObject js = new JSONObject();
					js.put("title", _activity.getName());
					js.put("description", _activity.getDescription());
					js.put("description_student", _activity.getDescriptionStudent());
					js.put("lesson_plan_id", _activity.getId());
					js.put("parent_element", "false");
					js.put("definition", _activity.getDefinition());
					js.put("image", _activity.getElement_image_file_name());
					
					LocalStorageServices.DatabaseService().setMiniActivity(_activity, _activity.getExperienceId());
					
						CommandModel comando = new CommandModel();
						comando.setCommand(_CMD_PUT_ACTIVITY);
						comando.setData(js.toString());
						comando.setActivityId(_activity.getId());
						LocalStorageServices.DatabaseService().setCommand(comando);
						WebServiceServices.Sincronizador().iniciar();
						return false;
						
				} catch (WebServiceException e){
					e.printStackTrace();
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				_delegado.Do(result);
			}
		}
		putExperienceAsync task = new putExperienceAsync(activity, callback);
		task.execute();
	}

	@Override
	public boolean putActivity(JSONObject datos) {
		try {
			JSONObject js = new JSONObject();
			if(datos.getInt("lesson_plan_id") < 0){
				js.put("lesson_plan_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(datos.getInt("lesson_plan_id")));
			}else
				js.put("lesson_plan_id", datos.getString("lesson_plan_id"));
			js.put("campo", "title");
			js.put("contenido", datos.getString("title"));
			js.put("parent_element", datos.getString("parent_element"));
			boolean titulo = putActivityField(js);
			js.put("campo", "description");
			js.put("contenido", datos.getString("description"));
			boolean desc = putActivityField(js);
			boolean image = true;
			if(!datos.getString("image").startsWith("/system") && !datos.getString("image").equals("none")){
				js.put("campo", "image");
				js.put("contenido", datos.getString("image"));
				image = putActivityField(js);
			}
			js.put("campo", "description_student");
			js.put("contenido", datos.getString("description_student"));
			boolean descStud = putActivityField(js);
			js.put("campo", "definition");
			js.put("contenido", datos.getString("definition"));
			boolean definition = putActivityField(js);
			if(titulo&&desc&&image&&descStud&&definition)
				return true;
						
		} catch (JSONException e) {
			e.printStackTrace();
		}
		CommandModel comando = new CommandModel();
		comando.setCommand(_CMD_PUT_ACTIVITY);
		comando.setData(datos.toString());
		try {
			LocalStorageServices.DatabaseService().setCommand(comando);
		} catch (Exception e) {
			e.printStackTrace();
		}
		WebServiceServices.Sincronizador().iniciar();
		
		return false;
		
	}
	
	@Override
	public boolean putActivityField(JSONObject datos) {
		
		try {
			JSONObject resp;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id",datos.getString("lesson_plan_id")));
			nameValuePairs.add(new BasicNameValuePair("campo",datos.getString("campo")));
			nameValuePairs.add(new BasicNameValuePair("parent_element", datos.getString("parent_element")));
			
			String comando = _CMD_PUT_ACTIVITY.replace("#", datos.getString("lesson_plan_id"));
			String rutaArchivo = null;
			
			if(datos.getString("campo").equals("image"))
				rutaArchivo = datos.getString("contenido");
			else

				nameValuePairs.add(new BasicNameValuePair("contenido",datos.getString("contenido")));
			
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _PUT, nameValuePairs, rutaArchivo, "contenido", RecordModel.RECORD_IMAGE);
			
			return true;
        } catch (WebServiceException e) {
			e.printStackTrace();
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        	return true;
        } catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
		
	

	@Override
	public void getRecords(int id, String tipoSolicitado, boolean all, IAction<List<RecordModel>> callback) {
				
		class GetRecordsTask extends AsyncTask<Void, Void, Void>{

			private IAction<List<RecordModel>> _delegado;
			private int _id;
			private String _tipo;
			private boolean _all;
			
			public GetRecordsTask(int id, String tipo, boolean all, IAction<List<RecordModel>> delegado) {
				_delegado = delegado;
				_id = id;
				_tipo = tipo;
				_all = all;
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				JSONArray records = null;
				try {
					if(LocalStorageServices.DatabaseService().getCommand() != null || _id < 0)
						return null;
					if(LocalStorageServices.DatabaseService().getRecords(_id).size() > 0)
						publishProgress();
					String comando = _CMD_GET_RECORDS.replace("#",_id + "").replace("$", _tipo);
					String sResp =  (String) ejecutarPeticion(_BASE_URL, comando, _GET, null, null, null, null);
					records = new JSONArray(sResp);
					extraerRecords(records, _id, false);
					
				} catch (WebServiceException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Void... values) {
				try {
					
					if(_all)
						_delegado.Do(LocalStorageServices.DatabaseService().getAllRecords(_id));
					else
						_delegado.Do(LocalStorageServices.DatabaseService().getRecords(_id));
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
			
			@Override
			protected void onPostExecute(Void result) {
				try {
					
					if(_all)
						_delegado.Do(LocalStorageServices.DatabaseService().getAllRecords(_id));
					else
						_delegado.Do(LocalStorageServices.DatabaseService().getRecords(_id));
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
		}
		GetRecordsTask task = new GetRecordsTask(id, tipoSolicitado, all, callback);
		task.execute();
		
	}

	@Override
	public void createRecordAsync(RecordModel record, int id, String tipo, IAction<Boolean> callback) {
		
		class CreateRecordAsync extends AsyncTask<Void, Void, Boolean>{

			private IAction<Boolean> _delegado;
			private RecordModel _record;
			private int _id;
			private String _tipo;
			
			public CreateRecordAsync(RecordModel record, IAction<Boolean> delegado, int id, String tipo) {
				_record = record;
				_delegado = delegado;
				_id = id;
				_tipo = tipo;
				
			}	
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					JSONObject js = new JSONObject();
					js.put("id", _record.getId());
					js.put("record_type_id", _record.getRecordTypeId());
					js.put("title", _record.getTitle());
					js.put("description", _record.getDescription());
					js.put("data", _record.getData());
					js.put("local_data", _record.getLocalData());
					js.put("element_id", _id);
					js.put("tipo", _tipo);
					js.put("faces_array", _record.getFacesArray());
					js.put("blurred", _record.getBlurredImage());
					js.put("created_at", Services.Utilidades().DateToString(_record.getCreationDate()));
					LocalStorageServices.DatabaseService().setRecord(_id, _record);
						CommandModel comando = new CommandModel();
						comando.setCommand(_CMD_ADD_RECORD);
						comando.setData(js.toString());
						comando.setCreado(Services.Utilidades().DateToString(_record.getCreationDate()));
						comando.setRecordId(_record.getId());
						comando.setActivityId(_id);
						LocalStorageServices.DatabaseService().setCommand(comando);
						WebServiceServices.Sincronizador().iniciar();
						return false;
					            			
				} catch (WebServiceException e){
					e.printStackTrace();
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				_delegado.Do(result);
			}
		}
		CreateRecordAsync task = new CreateRecordAsync(record, callback, id, tipo);
		task.execute();
				
	}

	@Override
	public int createRecord(JSONObject datos) {

		try {
			boolean titulo = true, desc = true, data = true;
			JSONObject js = new JSONObject();
			JSONObject resp;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			switch (datos.getInt("record_type_id")) {
			case RecordModel.RECORD_DOCUMENT:
				nameValuePairs.add(new BasicNameValuePair("type","document"));
				break;
			case RecordModel.RECORD_IDEA:
				nameValuePairs.add(new BasicNameValuePair("type","idea"));
				break;
			case RecordModel.RECORD_IMAGE:
				nameValuePairs.add(new BasicNameValuePair("type","image"));
				break;
			case RecordModel.RECORD_NEGATIVE_COMMENT:
				nameValuePairs.add(new BasicNameValuePair("type","negative_comment"));
				break;
			case RecordModel.RECORD_POSITIVE_COMMENT:
				nameValuePairs.add(new BasicNameValuePair("type","positive_comment"));
				break;
			case RecordModel.RECORD_SNIPPET:
				nameValuePairs.add(new BasicNameValuePair("type","snippet"));
				break;
			case RecordModel.RECORD_TEXT:
				nameValuePairs.add(new BasicNameValuePair("type","free_text"));
				break;
			case RecordModel.RECORD_VIDEO:
				nameValuePairs.add(new BasicNameValuePair("type","video"));
				break;
			}
			if(datos.getInt("id") == -1 && LocalStorageServices.DatabaseService().getRecordId(datos.getString("created_at")) == -1 ){
				if(datos.getInt("element_id") < 0){
					if(datos.getString("tipo").equals("activities"))
						datos.put("element_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(datos.getInt("element_id")));
					if(datos.getString("tipo").equals("submissions"))
						datos.put("element_id", LocalStorageServices.DatabaseService().getEntregaIdFromLocalId(datos.getInt("element_id")));
					if(datos.getString("tipo").equals("lesson_plans"))
						datos.put("element_id", LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(datos.getInt("element_id"))));
				}
				String comando = _CMD_ADD_RECORD.replace("#", datos.getString("element_id")).replace("$", datos.getString("tipo"));
				resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _POST, nameValuePairs, null, null, null);
				if(!resp.isNull("id")){
					datos.put("id", resp.getInt("id"));
					LocalStorageServices.DatabaseService().asignRecordId(resp.getInt("id"), datos.getString("created_at"));
				}else
					throw new WebServiceException("Error al crear el Record");
			}
			else{
				if(LocalStorageServices.DatabaseService().getRecordId(datos.getString("created_at")) > 0 ){
					datos.put("id",LocalStorageServices.DatabaseService().getRecordId(datos.getString("created_at")));
				}
			}
			
			
			if(datos.getInt("element_id") < 0){
				if(datos.getString("tipo").equals("activities"))
					js.put("experience_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(datos.getInt("element_id")));
				if(datos.getString("tipo").equals("submissions"))
					js.put("experience_id", LocalStorageServices.DatabaseService().getEntregaIdFromLocalId(datos.getInt("element_id")));
				if(datos.getString("tipo").equals("lesson_plans"))
					js.put("experience_id", LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(datos.getInt("element_id"))));
			}else{
				if(datos.getString("tipo").equals("activities")){
					if( LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(datos.getInt("element_id")) > 0)
						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(datos.getInt("element_id")));
					else
						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(datos.getInt("element_id"))));
				}
				if (datos.getString("tipo").equals("lesson_plans")){
//					if (LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(datos.getInt("element_id"))>0)
//						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(datos.getInt("element_id")));
//					else
					js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(datos.getInt("element_id")));
				}
				if (datos.getString("tipo").equals("submissions")){
					if (LocalStorageServices.DatabaseService().getExperienceIdFronEntregaId(datos.getInt("element_id"))>0)
						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronEntregaId(datos.getInt("element_id")));
					else{
						
						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(LocalStorageServices.DatabaseService().getExperienceIdFronEntregaId(datos.getInt("element_id"))));
					
					}
				}
				
			}
			if(js.getInt("experience_id")<0)
				js.put("experience_id", 3418);
			
			
			if(!datos.getString("title").equals("")){
				//JSONObject js = new JSONObject();
				js.put("type","title" );
				js.put("record_id", datos.getInt("id"));
				js.put("content", datos.getString("title"));
				js.put("tipo", datos.getString("tipo"));
				if(datos.getInt("element_id") < 0){
					if(datos.getString("tipo").equals("activities"))
						js.put("experience_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(datos.getInt("element_id")));
					if(datos.getString("tipo").equals("submissions"))
						js.put("experience_id", LocalStorageServices.DatabaseService().getEntregaIdFromLocalId(datos.getInt("element_id")));
					if(datos.getString("tipo").equals("lesson_plans"))
						js.put("experience_id", LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(datos.getInt("element_id"))));
				}else{
					if(datos.getString("tipo").equals("activities")){
						if( LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(datos.getInt("element_id")) > 0)
							js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(datos.getInt("element_id")));
						else
							js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(datos.getInt("element_id"))));
					}
					if (datos.getString("tipo").equals("lesson_plans")){
//						if (LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(datos.getInt("element_id"))>0)
//							js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(datos.getInt("element_id")));
//						else
						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(datos.getInt("element_id")));
					}
					if (datos.getString("tipo").equals("submissions")){
						if (LocalStorageServices.DatabaseService().getExperienceIdFronEntregaId(datos.getInt("element_id"))>0)
							js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronEntregaId(datos.getInt("element_id")));
						else{
							
							js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(LocalStorageServices.DatabaseService().getExperienceIdFronEntregaId(datos.getInt("element_id"))));
						
						}
					}
					
				}
				if(js.getInt("experience_id")<0)
					js.put("experience_id", 3418);
					
//				if(js.getInt("experience_id")<0 && LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(js.getInt("experience_id"))>0 ){
//					//js.put("experience_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(js.getInt("experience_id")));
//					js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(js.getInt("experience_id")));
//				}
//				if(js.getInt("experience_id")<0 && LocalStorageServices.DatabaseService().getActivityIdFromLocalId(js.getInt("experience_id"))>0 ){
//					js.put("experience_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(js.getInt("experience_id")));
//					//js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(js.getInt("experience_id")));
//				}
				
				titulo = updateRecord(js);
			}
			if(!datos.getString("description").equals("")){
				//JSONObject js = new JSONObject();
				js.put("type","description" );
				js.put("record_id", datos.getInt("id"));
				js.put("content", datos.getString("description"));
				js.put("tipo", datos.getString("tipo"));
				
//				if(datos.getInt("element_id") < 0){
//					if(datos.getString("tipo").equals("activities"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(datos.getInt("element_id")));
//					if(datos.getString("tipo").equals("submissions"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getEntregaIdFromLocalId(datos.getInt("element_id")));
//					if(datos.getString("tipo").equals("lesson_plans"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(datos.getInt("element_id"))));
//				}else{
//				
//					if(datos.getString("tipo").equals("activities"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(datos.getInt("element_id")));
//					if (datos.getString("tipo").equals("lesson_plans"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(datos.getInt("element_id")));
//					if (datos.getString("tipo").equals("submissions"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronEntregaId(datos.getInt("element_id")));
//					
//				}
//				if(js.getInt("experience_id")<0){
//					//js.put("experience_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(js.getInt("experience_id")));
//					js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(js.getInt("experience_id")));
//				}
				
				desc = updateRecord(js);
			}
			if(!datos.getString("data").equals("") && !datos.getString("data").contains("/system") ){
				
				//JSONObject js = new JSONObject();
				switch (datos.getInt("record_type_id")) {
				case RecordModel.RECORD_DOCUMENT:
					js.put("type","document");
					break;
				case RecordModel.RECORD_IMAGE:
					if(!datos.isNull("faces_array") && datos.getString("faces_array") != null && !datos.getString("faces_array").equals("")){
						js.put("type", "blurred_image");
						js.put("faces_array", datos.getString("faces_array"));
					}else
						js.put("type","original_image");
					break; 
				case RecordModel.RECORD_VIDEO:
					js.put("type","video");
					break;
				default:
					js.put("type","text_content");
					break;
				}
				js.put("record_id", datos.getInt("id"));
				if(!datos.isNull("faces_array") && datos.getString("faces_array") != null){
					js.put("content", datos.getString("blurred"));
				}else 
					js.put("content", datos.getString("data"));
				js.put("tipo", datos.getString("tipo"));
				
//				if(datos.getInt("element_id") < 0){
//					if(datos.getString("tipo").equals("activities"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(datos.getInt("element_id")));
//					if(datos.getString("tipo").equals("submissions"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getEntregaIdFromLocalId(datos.getInt("element_id")));
//					if(datos.getString("tipo").equals("lesson_plans"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(datos.getInt("element_id"))));
//				}else{
//				
//					if(datos.getString("tipo").equals("activities"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronActivityId(datos.getInt("element_id")));
//					if (datos.getString("tipo").equals("lesson_plans"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronLessonPlanId(datos.getInt("element_id")));
//					if (datos.getString("tipo").equals("submissions"))
//						js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFronEntregaId(datos.getInt("element_id")));
//					
//				}
//				if(js.getInt("experience_id")<0){
//					//js.put("experience_id", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(js.getInt("experience_id")));
//					js.put("experience_id", LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(js.getInt("experience_id")));
//				}
				
				data = updateRecord(js);
			}	
			if(titulo && desc && data)
				return datos.getInt("id");
			else{
				try {
					CommandModel comando = new CommandModel();
					comando.setCommand(_CMD_ADD_RECORD);
					comando.setData(datos.toString());
					comando.setCreado(datos.getString("created_at"));
					comando.setRecordId(datos.getInt("id"));
					comando.setActivityId(datos.getInt("element_id"));
					LocalStorageServices.DatabaseService().setCommand(comando);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				return datos.getInt("id");
			}
			
        } catch (WebServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public boolean updateRecord(JSONObject datos) {
		try {
			JSONObject resp;
			String rutaArchivo = null;
			int tipoUpdate = -1;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("type",datos.getString("type")));
			nameValuePairs.add(new BasicNameValuePair("record_id",datos.getInt("record_id") + ""));
			if(datos.getString("type").equals("original_image") || datos.getString("type").equals("video") || datos.getString("type").equals("document") || datos.getString("type").equals("blurred_image"))
				rutaArchivo = datos.getString("content");
				if(datos.getString("type").equals("blurred_image")){
					nameValuePairs.add(new BasicNameValuePair("faces_array",datos.getString("faces_array")));
				}
			else
				nameValuePairs.add(new BasicNameValuePair("content", datos.getString("content")));	
				
			if(datos.getString("type").equals("original_image") || datos.getString("type").equals("blurred_image"))
				tipoUpdate = RecordModel.RECORD_IMAGE;
			if(datos.getString("type").equals("video"))
				tipoUpdate = RecordModel.RECORD_VIDEO;
			String comando = _CMD_UPDATE_RECORD.replace("#", datos.getInt("experience_id") + "");
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _PUT, nameValuePairs, rutaArchivo, "content", tipoUpdate);
			return true;
		} catch (WebServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void deleteRecordAsync(int recordId, int activityId, Date created, String tipo, IAction<Boolean> callback) {
		class DeleteRecordAsync extends AsyncTask<Void, Void, Boolean>{

			private IAction<Boolean> _delegado;
			private int _recordId, _activityId;
			private Date _created;
			private String _tipo;
			
			public DeleteRecordAsync(int recordId, int activityId, Date created, String tipo, IAction<Boolean> delegado) {
				_recordId = recordId;
				_activityId = activityId;
				_delegado = delegado;
				_created = created;
				_tipo = tipo;
			}
			
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					JSONObject js = new JSONObject();
					js.put("record_id", _recordId + "");
					js.put("activity_id", _activityId + "");
					js.put("tipo", _tipo);
					
						if (_recordId != -1){
							CommandModel comando = new CommandModel();
							comando.setCommand(_CMD_DELETE_RECORD);
							comando.setData(js.toString());
							LocalStorageServices.DatabaseService().deleteRecord(_recordId);
							LocalStorageServices.DatabaseService().setCommand(comando);
							WebServiceServices.Sincronizador().iniciar();
						}else{
							LocalStorageServices.DatabaseService().deleteRecord(Services.Utilidades().DateToString(_created));
							LocalStorageServices.DatabaseService().deleteCommand(Services.Utilidades().DateToString(_created));
							return true;
						}
						return false;
					//}		
				} catch (WebServiceException e){
					e.printStackTrace();
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				
				_delegado.Do(result);
			}
		}
		
		DeleteRecordAsync task = new DeleteRecordAsync(recordId, activityId, created, tipo, callback);
		task.execute();
	}
	
	
	@Override
	public boolean deleteRecord(JSONObject datos) {
		try {
			JSONObject resp;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("record_id",datos.getString("record_id")));
			String comando = _CMD_DELETE_RECORD.replace("#", datos.getString("activity_id")).replace("$", datos.getString("tipo"));
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _DELETE, nameValuePairs, null, null, null);
			return true;
        } catch (WebServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}



	@Override
	public void descargaArchivoAsync(final RecordModel record, final int elementId) {
		
		class DescargarArchivo implements Runnable{
			
			private String _ruta;
			
			public DescargarArchivo(String ruta) {
				_ruta = ruta;
			}
			@Override
			public void run() {
				try {
					String nombre = _ruta.indexOf("?") != -1 ? _ruta.substring(_ruta.lastIndexOf("/"), _ruta.indexOf("?")) : _ruta.substring(_ruta.lastIndexOf("/"));
					
					if(LocalStorageServices.ExternalStorage().existeVideo(nombre)){
						record.setLocalData(LocalStorageServices.ExternalStorage().getDirVideos() + nombre);
						LocalStorageServices.DatabaseService().setRecord(elementId, record);
						return;
					}
					ApplicationService.setDownload(true);
					InputStream in = descargaArchivo(_BASE_URL, _ruta);
					record.setLocalData(LocalStorageServices.ExternalStorage().guardarVideo(in, nombre));
					LocalStorageServices.DatabaseService().setRecord(elementId, record);
					
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (WebServiceException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				ApplicationService.setDownload(false);
			}
			
		}
		DescargarArchivo tarea = new DescargarArchivo(record.getData());
		new Thread(tarea).start();
	}



	@Override
	public void addActivityAsync(int experienceId, IAction<Integer> callback) {
		
		class AddAcivityAsync extends AsyncTask<Void, Void, Integer>{

			private IAction<Integer> _delegado;
			private int _experienceId;
			
			public AddAcivityAsync(int experienceId, IAction<Integer> delegado) {
				_delegado = delegado;
				_experienceId = experienceId;
			}	
			
			@Override
			protected Integer doInBackground(Void... arg0) {
				int id = 0;
				try {
					id = LocalStorageServices.DatabaseService().addMiniActivity(_experienceId);
					
					JSONObject js = new JSONObject();
					js.put("experienceId", _experienceId);
					js.put("localId", id);
				

				
					CommandModel comando = new CommandModel();
					comando.setCommand(_CMD_ADD_ACTIVITY);
					comando.setData(js.toString());
					comando.setActivityId(_experienceId);
					LocalStorageServices.DatabaseService().setCommand(comando);
					WebServiceServices.Sincronizador().iniciar();
					return id;
					//}	                			
				} catch (Exception e) {
					e.printStackTrace();
					return id;
				}
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				_delegado.Do(result);
			}
		}
		AddAcivityAsync task = new AddAcivityAsync(experienceId, callback);
		task.execute();
		
	}



	@Override
	public boolean addActivity(JSONObject datos) {
		
		try {
			JSONObject resp;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			if(datos.getInt("experienceId") < 0){
				datos.put("experienceId", LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(datos.getInt("experienceId")));
			}
			String comando = _CMD_ADD_ACTIVITY.replace("#", datos.getString("experienceId"));
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _POST, nameValuePairs, null, null, null);
			if(!resp.isNull("id")){
				LocalStorageServices.DatabaseService().asignActivityId(resp.getInt("id"), datos.getInt("localId"));
			}else
				throw new WebServiceException("Error al crear la actividad");
			return true;
        } catch (WebServiceException e) {
			e.printStackTrace();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	
	
	@Override
	public void addEntregaAsync(int id, String tipo, IAction<Integer> callback) {
		
		class AddEntregaAsync extends AsyncTask<Void, Void, Integer>{

			private IAction<Integer> _delegado;
			private int _id;
			private String _tipo;
			
			public AddEntregaAsync(int id, String tipo, IAction<Integer> delegado) {
				_delegado = delegado;
				_id = id;
				_tipo = tipo;
				
			}	
			@Override
			protected Integer doInBackground(Void... arg0) {
				int id = 0;
				try {
					
					id = LocalStorageServices.DatabaseService().addEntrega(_id);
					JSONObject js = new JSONObject();
					if(_tipo.equals("lesson_plans")){
						
						js.put("experienceId", LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(_id));
					}
					else
						js.put("experienceId", _id);
					js.put("tipo", _tipo);
					js.put("localId", id);

					
						CommandModel comando = new CommandModel();
						comando.setCommand(_CMD_ADD_SUBMISSION);
						comando.setData(js.toString());
						if(_tipo.equals("lesson_plans"))
							comando.setActivityId(LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(_id));
						else
							comando.setActivityId(_id);
						LocalStorageServices.DatabaseService().setCommand(comando);
						WebServiceServices.Sincronizador().iniciar();
						return id;
					//}	                			
				} catch (Exception e) {
					e.printStackTrace();
					return id;
				}
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				_delegado.Do(result);
			}
		}
		AddEntregaAsync task = new AddEntregaAsync(id, tipo, callback);
		task.execute();
		
	}



	@Override
	public boolean addEntrega(JSONObject datos) {
		
		try {
			JSONObject resp;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if(datos.getInt("experienceId") < 0){
				if(datos.getString("tipo").equals("activities"))
					datos.put("experienceId", LocalStorageServices.DatabaseService().getActivityIdFromLocalId(datos.getInt("experienceId")));
				if(datos.getString("tipo").equals("lesson_plans"))
					datos.put("experienceId", LocalStorageServices.DatabaseService().getLessonPlanIdFromExperienceId(LocalStorageServices.DatabaseService().getExperienceIdFromLocalId(datos.getInt("experienceId"))));
			}
			String comando = _CMD_ADD_SUBMISSION.replace("#", datos.getString("experienceId")).replace("$", datos.getString("tipo"));
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _POST, nameValuePairs, null, null, null);
			if(!resp.isNull("id")){
				LocalStorageServices.DatabaseService().asignEntregaId(resp.getInt("id"), datos.getInt("localId"));
			}else
				throw new WebServiceException("Error al crear la actividad");
			return true;
        } catch (WebServiceException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	

	@Override
	public void getRecursosView(int experienceId, boolean all, boolean sitios, IAction<List<RecursoModel>> callback) {
		
		class GetRecursosViewTask extends AsyncTask<Void, Void, Void>{

			private IAction<List<RecursoModel>> _delegado;
			private int _experienceId;
			private boolean _all, _sitios;
			
			public GetRecursosViewTask(int experienceId, boolean all, boolean sitios, IAction<List<RecursoModel>> delegado) {
				_delegado = delegado;
				_experienceId = experienceId;
				_all = all;
				_sitios = sitios;
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				JSONArray recursosArray = null;
				try {
					if(LocalStorageServices.DatabaseService().getRecursos(LocalStorageServices.DatabaseService().getRecursosIds(_experienceId), _sitios).size() > 0)
						publishProgress();
					String comando = _CMD_GET_RESOURCES_VIEW.replace("#",LocalStorageServices.DatabaseService().getRecursosIds(_experienceId));
					String sResp =  (String) ejecutarPeticion(_BASE_URL, comando, _GET, null, null, null, null);
					recursosArray = new JSONArray(sResp);
					extraerRecursos(recursosArray, _experienceId, false);
					
				} catch (WebServiceException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Void... values) {
				try {
					if(_all)
						_delegado.Do(LocalStorageServices.DatabaseService().getAllRecursos(_experienceId, _sitios));
					else
					_delegado.Do(LocalStorageServices.DatabaseService().getRecursos(LocalStorageServices.DatabaseService().getRecursosIds(_experienceId), _sitios));
					
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
			
			@Override
			protected void onPostExecute(Void result) {
				try {
					
					if(_all)
						_delegado.Do(LocalStorageServices.DatabaseService().getAllRecursos(_experienceId, _sitios));
					else
						_delegado.Do(LocalStorageServices.DatabaseService().getRecursos(LocalStorageServices.DatabaseService().getRecursosIds(_experienceId), _sitios));
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
		}
		GetRecursosViewTask task = new GetRecursosViewTask(experienceId, all, sitios, callback);
		task.execute();
		
	}
	
	@Override
	public void getEntregasView(int experienceId, boolean all, IAction<List<EntregaModel>> callback) {
		
		class GetEntregasViewTask extends AsyncTask<Void, Void, Void>{

			private IAction<List<EntregaModel>> _delegado;
			private int _experienceId;
			private boolean _all;
			
			public GetEntregasViewTask(int experienceId, IAction<List<EntregaModel>> delegado, boolean all) {
				_delegado = delegado;
				_experienceId = experienceId;
				_all = all;
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				JSONArray recursosArray = null;
				try {
					if(LocalStorageServices.DatabaseService().getEntregas(_experienceId).size() > 0)
						publishProgress();
					String comando = _CMD_GET_SUBMISSIONS_VIEW.replace("#",LocalStorageServices.DatabaseService().getEntregaIdsNoLocal(_experienceId));
					
					String sResp =  (String) ejecutarPeticion(_BASE_URL, comando, _GET, null, null, null, null);
					recursosArray = new JSONArray(sResp);
					extraerEntrega(recursosArray, _experienceId, false);
					
				} catch (WebServiceException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Void... values) {
				try {
					if (_all)
						_delegado.Do(LocalStorageServices.DatabaseService().getAllEntregas(_experienceId));
					else
					_delegado.Do(LocalStorageServices.DatabaseService().getEntregas(LocalStorageServices.DatabaseService().getEntregasIds(_experienceId)));
					
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
			
			@Override
			protected void onPostExecute(Void result) {
				try {
					if (_all)
						_delegado.Do(LocalStorageServices.DatabaseService().getAllEntregas(_experienceId));
					else
						_delegado.Do(LocalStorageServices.DatabaseService().getEntregas(LocalStorageServices.DatabaseService().getEntregasIds(_experienceId)));
					//List<EntregaModel> lista = LocalStorageServices.DatabaseService().getEntregas(LocalStorageServices.DatabaseService().getEntregasIds(_experienceId));
					//List<EntregaModel> lista = LocalStorageServices.DatabaseService().getAllEntregas(_experienceId);
					
				} catch (Exception e) {
					e.printStackTrace();
					_delegado.Do(null);
				}
			}
		}
		GetEntregasViewTask task = new GetEntregasViewTask(experienceId, callback, all);
		task.execute();
		
	}

	
	
	
	@Override
	public void putEntregaAsync(EntregaModel entrega, int elementId, IAction<Boolean> callback) {
				
		class putEntregaAsync extends AsyncTask<Void, Void, Boolean>{

			private IAction<Boolean> _delegado;
			private EntregaModel _entrega;
			private int _elementId;
			
			public putEntregaAsync(EntregaModel entrega, int elementId, IAction<Boolean> delegado) {
				_entrega = entrega;
				_delegado = delegado;
				_elementId = elementId;
			}
			
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					JSONObject resp = null;
					JSONObject js = new JSONObject();
					js.put("title", _entrega.getTitulo());
					js.put("description", _entrega.getDescripcion());
					js.put("lesson_plan_id", _entrega.getId());
					js.put("definition", _entrega.getDefinition());
					js.put("parent_element", "false");
					js.put("image", _entrega.getImagen());
					
					LocalStorageServices.DatabaseService().setEntrega(_entrega, _elementId);
					
						CommandModel comando = new CommandModel();
						comando.setCommand(_CMD_PUT_SUBMISSION);
						comando.setData(js.toString());
						comando.setActivityId(_entrega.getId());
						LocalStorageServices.DatabaseService().setCommand(comando);
						WebServiceServices.Sincronizador().iniciar();
						return false;
						
				} catch (WebServiceException e){
					e.printStackTrace();
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				_delegado.Do(result);
			}
		}
		putEntregaAsync task = new putEntregaAsync(entrega, elementId, callback);
		task.execute();
	}

	@Override
	public boolean putEntrega(JSONObject datos) {
		try {
			JSONObject js = new JSONObject();
			if(datos.getInt("lesson_plan_id") < 0){
				js.put("lesson_plan_id", LocalStorageServices.DatabaseService().getEntregaIdFromLocalId(datos.getInt("lesson_plan_id")));
			}else
				js.put("lesson_plan_id", datos.getString("lesson_plan_id"));
			js.put("campo", "title");
			js.put("contenido", datos.getString("title"));
			js.put("parent_element", datos.getString("parent_element"));
			boolean titulo = putEntregaField(js);
			js.put("campo", "description");
			js.put("contenido", datos.getString("description"));
			boolean desc = putEntregaField(js);
			boolean image = true;
			if(!datos.getString("image").startsWith("/system") && !datos.getString("image").equals("none")){
				js.put("campo", "image");
				js.put("contenido", datos.getString("image"));
				image = putEntregaField(js);
			}
			js.put("campo", "definition");
			js.put("contenido", datos.getString("definition"));
			boolean definition = putEntregaField(js);
			
			if(titulo&&desc&&image&&definition)
				return true;
						
		} catch (JSONException e) {
			e.printStackTrace();
		}
		CommandModel comando = new CommandModel();
		comando.setCommand(_CMD_PUT_SUBMISSION);
		comando.setData(datos.toString());
		try {
			LocalStorageServices.DatabaseService().setCommand(comando);
		} catch (Exception e) {
			e.printStackTrace();
		}
		WebServiceServices.Sincronizador().iniciar();
		
		return false;
		
	}
	
	@Override
	public boolean putEntregaField(JSONObject datos) {
		
		try {
			JSONObject resp;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id",datos.getString("lesson_plan_id")));
			nameValuePairs.add(new BasicNameValuePair("campo",datos.getString("campo")));
			nameValuePairs.add(new BasicNameValuePair("parent_element", datos.getString("parent_element")));
			
			String comando = _CMD_PUT_SUBMISSION.replace("#", datos.getString("lesson_plan_id"));
			String rutaArchivo = null;
			
			if(datos.getString("campo").equals("image"))
				rutaArchivo = datos.getString("contenido");
			else

				nameValuePairs.add(new BasicNameValuePair("contenido",datos.getString("contenido")));
			
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _PUT, nameValuePairs, rutaArchivo, "contenido", RecordModel.RECORD_IMAGE);
			
			return true;
        } catch (WebServiceException e) {
			e.printStackTrace();
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        	return true;
        } catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}



	@Override
	public void addExperience(String code, IAction<Boolean> callback) {
		

		class AddExperienceAsync extends AsyncTask<Void, Void, Boolean>{

			private IAction<Boolean> _delegado;
			private String _code;
			
			public AddExperienceAsync(String code, IAction<Boolean> delegado) {
				_code = code;
				_delegado = delegado;
			}
			
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					JSONObject resp;
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("enter_code",_code));
					resp = (JSONObject) ejecutarPeticion(_BASE_URL, _CMD_ENTER_CODE, _POST, nameValuePairs, null, null, null);
					if(!resp.isNull("shared"))
						return true;
					else
						return false;
						
				} catch (WebServiceException e){
					e.printStackTrace();
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				_delegado.Do(result);
			}
		}
		AddExperienceAsync task = new AddExperienceAsync(code, callback);
		task.execute();
				
	}



	@Override
	public void getExperienceCode(int expereienceId, IAction<String> callback) {
		
		class AddExperienceAsync extends AsyncTask<Void, Void, Void>{

			private IAction<String> _delegado;
			private int _expId;
			
			public AddExperienceAsync(int expId, IAction<String> delegado) {
				_expId = expId;
				_delegado = delegado;
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					JSONObject resp;
					String comando = _CMD_GET_SHARE_CODE.replace("#", _expId + "");
					String sResp = (String) ejecutarPeticion(_BASE_URL, comando, _GET, null, null, null, null);
					resp = new JSONObject(sResp);
					if(!resp.isNull("code_to_share"))
						LocalStorageServices.DatabaseService().setExperienceCode(resp.getString("code_to_share"), _expId);
					
				} catch (WebServiceException e){
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
				return null;
				
			}
			
			@Override
			protected void onPostExecute(Void result) {
				_delegado.Do(LocalStorageServices.DatabaseService().getExperienceCode(_expId));
			}
		}
		AddExperienceAsync task = new AddExperienceAsync(expereienceId, callback);
		task.execute();
		
		
	}



	@Override
	public void getFullExperience(int id) {
		try {
			JSONArray fullElement = null;
			String comando = _CMD_GET_FULL_EXPERIENCE.replace("#",id + "");
			String sResp;
		
			sResp = (String) ejecutarPeticion(_BASE_URL, comando, _GET, null, null, null, null);
			fullElement = new JSONArray(sResp);
			
			for (int i = 0; i < fullElement.length(); i++) {
				JSONObject experiencia = fullElement.getJSONObject(i);
				Experience exp = extraerExperience(experiencia, true);
				if(!experiencia.isNull("template")){
					extraerTemplate(exp.getId(), experiencia.getJSONObject("template"));
				}
				if(!experiencia.isNull("resources")){
					extraerRecursos(experiencia.getJSONArray("resources"), exp.getId(), true);
				}
				if(!experiencia.isNull("submissions")){
					extraerEntrega(experiencia.getJSONArray("submissions"), exp.getId(), true);
				}
				if(!experiencia.isNull("element_records")){
					extraerRecords(experiencia.getJSONArray("element_records"), exp.getLessonPlanId(), true);
				}
				if(!experiencia.isNull("activities")){
					JSONArray fullActivities = experiencia.getJSONArray("activities");
					getFullActivity(exp.getId(), fullActivities);
				}
				if(!experiencia.isNull("comments")){
					extraerComentarios(experiencia.getJSONArray("comments"), exp.getLessonPlanId());
				}
				if(!experiencia.isNull("code_to_share"))
					LocalStorageServices.DatabaseService().setExperienceCode(experiencia.getString("code_to_share"), exp.getId());
			}
			
			Log.w("webService", "Fin de get full element");
		} catch (WebServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	@Override
	public void getFullActivity(int expId, JSONArray fullActivities)
			throws JSONException, Exception {
		LocalStorageServices.DatabaseService().deleteActivitiesIds(expId);
		for (int j = 0; j < fullActivities.length(); j++) {
			JSONObject activity = fullActivities.getJSONObject(j);
			MiniActivity mA = extraerActivity(activity, expId, true);
			if(!activity.isNull("template")){
				extraerTemplate(mA.getId(), activity.getJSONObject("template"));
			}
			if(!activity.isNull("submissions")){
				extraerEntrega(activity.getJSONArray("submissions"), mA.getId(), true);
			}
			if(!activity.isNull("element_records")){
				extraerRecords(activity.getJSONArray("element_records"), mA.getId(), true);
			}
			if(!activity.isNull("resources")){
				extraerRecursos(activity.getJSONArray("resources"), mA.getId(), true);
			}
			if(!activity.isNull("comments")){
				extraerComentarios(activity.getJSONArray("comments"), mA.getId());
			}
		}
	}



	private void extraerTemplate(int expId, JSONObject template)
			throws JSONException, Exception {
		if(!template.isNull("boxes")){
			JSONArray boxes = template.getJSONArray("boxes");
			LocalStorageServices.DatabaseService().deleteDetalles(expId);
			for (int j = 0; j < boxes.length(); j++){
				JSONObject box = boxes.getJSONObject(j);
				DetallesModel detalles = new DetallesModel();
				detalles.setExperienceId(expId);
				detalles.setDetalles(box.toString());
				LocalStorageServices.DatabaseService().setDetalles(detalles);
			}
		}
	}



	private Experience extraerExperience(JSONObject expView, boolean descargarImagenes)
			throws JSONException, Exception {
		Experience exp = new Experience();
		if (!expView.isNull("id"))
			exp.setId(expView.getInt("id"));
		if (!expView.isNull("title"))
			exp.setName(expView.getString("title"));
		if (!expView.isNull("image")){
			exp.setElement_image_file_name(expView.getString("image"));
			if(descargarImagenes)
				descargaImagenAsync(expView.getString("image"),false);
		}
		if (!expView.isNull("description")){
			exp.setDescription(expView.getString("description"));
			if (exp.getDescription().length() >= 200)
		        exp.setShortDescription(exp.getDescription().substring(0,199)+"...");
		    else exp.setShortDescription(exp.getDescription());
		}
		if(!expView.isNull("lesson_plan"))
			exp.setLessonPlanId(expView.getInt("lesson_plan"));
		if(!expView.isNull("description_student"))
			exp.setDescriptionStudent(expView.getString("description_student"));
		if(!expView.isNull("updated_at"))
			exp.setUpdatedAt(expView.getString("updated_at"));
		if(!expView.isNull("definition"))
			exp.setDefinition(expView.getString("definition"));
		if(!expView.isNull("first_name") && !expView.isNull("last_name"))
			exp.setAutor(expView.getString("first_name") + " " + expView.getString("last_name"));
		if(!expView.isNull("permission"))
			exp.setPermiso(expView.getString("permission"));
		
		LocalStorageServices.DatabaseService().setExperience(exp);
		return exp;
	}



	private void extraerRecursos(JSONArray recursosArray, int expId, boolean descargarImagenes) throws JSONException,
			Exception {
		LocalStorageServices.DatabaseService().deleteRecursosIds(expId);
		for (int i = 0; i < recursosArray.length(); i++) {
			JSONObject recView = recursosArray.getJSONObject(i);
			
			RecursoModel recurso = new RecursoModel();
			if (!recView.isNull("id")){
				recurso.setId(recView.getInt("id"));
				LocalStorageServices.DatabaseService().setRecursoId(expId, recView.getInt("id"));
			}
			if (!recView.isNull("title"))
				recurso.setTitulo(recView.getString("title"));
			if (!recView.isNull("image")){
				recurso.setImagen(recView.getString("image"));
				if(descargarImagenes)
					descargaImagenAsync(recView.getString("image"),false);
				
			}
			if (!recView.isNull("description"))
				recurso.setDescripcion(recView.getString("description"));
			if (!recView.isNull("URL"))
				recurso.setUrl(recView.getString("URL"));
			if (!recView.isNull("type"))
				recurso.setTipo(recView.getString("type"));
			if (!recView.isNull("definition"))
				recurso.setDefinition(recView.getString("definition"));
			if(!recView.isNull("updated_at"))
				recurso.setUpdatedAt(recView.getString("updated_at"));
			if(!recView.isNull("first_name") && !recView.isNull("last_name"))
				recurso.setAutor(recView.getString("first_name") + " " + recView.getString("last_name"));
			if(!recView.isNull("permission"))
				recurso.setPermiso(recView.getString("permission"));
			
			LocalStorageServices.DatabaseService().setRecurso(recurso, expId);
			
			// Resources Appends
			if (!recView.isNull("resources_appends")){
				JSONArray appends = recView.getJSONArray("resources_appends");
				for (int j = 0; j < appends.length(); j++) {
					JSONObject append = appends.getJSONObject(j);
					AnexosRecursoModel anexo = new AnexosRecursoModel();
					if(!append.isNull("type_append"))
						anexo.setType(append.getString("type_append"));
					if(!append.isNull("snippet_url"))
						anexo.setSnippetUrl(append.getString("snippet_url"));
					if(!append.isNull("document"))
						anexo.setDocument(append.getString("document"));
					if(!append.isNull("id"))
						anexo.setId(append.getInt("id"));
					if(!append.isNull("document_file_name"))
						anexo.setFileName(append.getString("document_file_name"));
					if(!append.isNull("address"))
						anexo.setDireccion(append.getString("address"));
					//anexo.setDireccion("direccion" + j);
					if(!append.isNull("latitude") && !append.getString("latitude").equals(""))
						anexo.setLatitude(append.getDouble("latitude"));
						//anexo.setLatitude(42.265754);
					if(!append.isNull("longitude") && !append.getString("longitude").equals(""))
						anexo.setLongitude(append.getDouble("longitude"));
						//anexo.setLongitude(-8.6756992);
					
					anexo.setRecursoId(recurso.getId());
					LocalStorageServices.DatabaseService().setAnexoRecurso(anexo);
				}
			}
									
		}
	}



	private void extraerRecords(JSONArray records, int _id, boolean descargarImagenes) throws JSONException,
			Exception {
		String ids = "";
		
		for (int i = 0; i < records.length(); i++) {
			JSONObject rec = records.getJSONObject(i);
			RecordModel record = new RecordModel();
			if (!rec.isNull("id"))
				record.setId(rec.getInt("id"));
			if (!rec.isNull("record_type"))
				record.setRecordType(rec.getString("record_type"));
			if (!rec.isNull("title"))
				record.setTitle(rec.getString("title"));
			if (!rec.isNull("description"))
				record.setDescription(rec.getString("description"));
			if (!rec.isNull("created_at"))
				record.setCreationDate(Services.Utilidades().StringToDate(rec.getString("created_at")));
			if (!rec.isNull("updated_at"))
				record.setUpdateDate(Services.Utilidades().StringToDate(rec.getString("updated_at")));
			if(!rec.isNull("text"))
				record.setData(rec.getString("text"));
			if(!rec.isNull("original_image")){
				record.setData(rec.getString("original_image"));
				if(descargarImagenes)
					descargaImagenAsync(record.getData(),false);
			}
			if(!rec.isNull("video")){
				record.setData(rec.getString("video"));
				
			}
			if(!rec.isNull("blurred_image")){
				record.setBlurredImage(rec.getString("blurred_image").equals("/images/defaults/no_image.jpg") ? "" : rec.getString("blurred_image"));
				if(descargarImagenes)
					descargaImagenAsync(record.getBlurredImage(),false);
			}
			if(!rec.isNull("faces_array"))
				record.setFacesArray(rec.getString("faces_array"));
			if(!rec.isNull("video_frame")){
				record.setVideoFrame(rec.getString("video_frame"));
				if(descargarImagenes)
					descargaImagenAsync(record.getVideoFrame(),true);
			}
			if(!rec.isNull("first_name") && !rec.isNull("last_name"))
				record.setAutor(rec.getString("first_name") + " " + rec.getString("last_name"));
			
			if(!rec.isNull("permission")){
				record.setPermiso(rec.getString("permission"));
			}
			
			ids = (i == records.length()-1) ? ids + record.getId() + "" : ids +record.getId() + ",";
			LocalStorageServices.DatabaseService().setRecord(_id, record);
			
			if(!rec.isNull("video") && descargarImagenes){
				descargaArchivoAsync(record, _id);
				
			}
		}
		LocalStorageServices.DatabaseService().deleteRecords(_id, ids);
	}



	private void extraerEntrega(JSONArray recursosArray, int expId, boolean descargarImagenes) throws JSONException,
			Exception {
		LocalStorageServices.DatabaseService().deleteEntregasIds(expId);
		for (int i = 0; i < recursosArray.length(); i++) {
			JSONObject entView = recursosArray.getJSONObject(i);
			
			EntregaModel entrega = new EntregaModel();
			if (!entView.isNull("id")){
				entrega.setId(entView.getInt("id"));
				LocalStorageServices.DatabaseService().setEntregaId(expId, entView.getInt("id"));
			}
			if (!entView.isNull("title"))
				entrega.setTitulo(entView.getString("title"));
			if (!entView.isNull("image")){
				entrega.setImagen(entView.getString("image"));
				if(descargarImagenes)
					descargaImagenAsync(entView.getString("image"),false);
			}
			if (!entView.isNull("description"))
				entrega.setDescripcion(entView.getString("description"));
			if (!entView.isNull("definition"))
				entrega.setDefinition(entView.getString("definition"));
			if(!entView.isNull("updated_at"))
				entrega.setUpdatedAt(entView.getString("updated_at"));
			if(!entView.isNull("first_name") && !entView.isNull("last_name"))
				entrega.setAutor(entView.getString("first_name") + " " + entView.getString("last_name"));
			if(!entView.isNull("permission"))
				entrega.setPermiso(entView.getString("permission"));
			if(!entView.isNull("comments")){
				extraerComentarios(entView.getJSONArray("comments"), entrega.getId());
			}
			LocalStorageServices.DatabaseService().setEntrega(entrega, expId);
			if(!entView.isNull("element_records")){
				extraerRecords(entView.getJSONArray("element_records"), entrega.getId(), true);
			}
									
		}
	}



	private MiniActivity extraerActivity(JSONObject expView, int expId, boolean descargarImagenes)
			throws JSONException, Exception {
		MiniActivity miniActivity = new MiniActivity();
		if (!expView.isNull("id")){
			miniActivity.setId(expView.getInt("id"));
			if(!expView.isNull("position"))
				LocalStorageServices.DatabaseService().setActivityId(expId, expView.getInt("id"), expView.getInt("position"));
		}
		if (!expView.isNull("title"))
			miniActivity.setName(expView.getString("title"));
		if (!expView.isNull("image")){
			miniActivity.setElement_image_file_name(expView.getString("image"));
			if(descargarImagenes)
				descargaImagenAsync(expView.getString("image"),false);
		}
		if (!expView.isNull("start"))
			miniActivity.setStart(expView.getString("start"));
		if (!expView.isNull("end"))
			miniActivity.setEnd(expView.getString("end"));
		if (!expView.isNull("progress"))
			miniActivity.setProgress(expView.getString("progress"));
		if (!expView.isNull("description"))
			miniActivity.setDescription(expView.getString("description"));
		if (!expView.isNull("description_student"))
			miniActivity.setDescriptionStudent(expView.getString("description_student"));
		if (!expView.isNull("definition"))
			miniActivity.setDefinition(expView.getString("definition"));
		if(!expView.isNull("updated_at"))
			miniActivity.setUpdatedAt(expView.getString("updated_at"));
		if(!expView.isNull("first_name") && !expView.isNull("last_name"))
			miniActivity.setAutor(expView.getString("first_name") + " " + expView.getString("last_name"));
		if(!expView.isNull("permission"))
			miniActivity.setPermiso(expView.getString("permission"));
		if(!expView.isNull("comments")){
			extraerComentarios(expView.getJSONArray("comments"), miniActivity.getId());
		}
		LocalStorageServices.DatabaseService().setMiniActivity(miniActivity, expId);
		return miniActivity;
	}



	@Override
	public void descargaImagenAsync(String ruta, boolean original) {
		
		ApplicationService.setDownload(true);
		try {
			ruta = original ? ruta : ruta.replace("/original/", "/medium/");
				
		//ImageLoader.getInstance().loadImage(WebService._BASE_URL + ruta.replace("/original/", "/medium/"), new SimpleImageLoadingListener() {
		ImageLoader.getInstance().loadImage(WebService._BASE_URL + ruta, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				ApplicationService.setDownload(false);
			}
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				ApplicationService.setDownload(false);
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				ApplicationService.setDownload(false);
			}
			
		});
		
		} catch (Exception e) {
			ApplicationService.setDownload(false);
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean getUpdatedAt(String ids) {
		boolean desactualizado = false;
		try {
			JSONObject resp;
			String comando = _CMD_GET_UPDATED_AT.replace("#", ids);
			String sResp;
			sResp = (String) ejecutarPeticion(_BASE_URL, comando, _GET, null, null, null, null);
			resp = new JSONObject(sResp);
			
			if(!resp.isNull("updated_dates")){
				JSONArray updatedDates = resp.getJSONArray("updated_dates");
				for (int i = 0; i < updatedDates.length(); i++) {
					JSONObject up = updatedDates.getJSONObject(i);
					if(!up.isNull("id")){
						int id = up.getInt("id");
						String fechaLocal = LocalStorageServices.DatabaseService().getExperienceUpdatedAt(id);
						if(!up.isNull("date")){
							int isUpdated = fechaLocal.equals(up.getString("date")) ? 0 : 1;
							LocalStorageServices.DatabaseService().setExperienceIsUpdated(isUpdated, id);
							desactualizado = desactualizado == false ? isUpdated == 1 : desactualizado;
						}
						
					}
						
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return desactualizado;
	}
	
	
	@Override
	public void addExperienceAsync(IAction<Integer> callback) {
		
		class AddExperienceAsync extends AsyncTask<Void, Void, Integer>{

			private IAction<Integer> _delegado;
			
			public AddExperienceAsync(IAction<Integer> delegado) {
				_delegado = delegado;
			}	
			
			@Override
			protected Integer doInBackground(Void... arg0) {
				int id = 0;
				try {
				//	id = LocalStorageServices.DatabaseService().addMiniActivity(_experienceId);
					id = LocalStorageServices.DatabaseService().addExperience();
					JSONObject js = new JSONObject();
					js.put("localId", id);					
					CommandModel comando = new CommandModel();
					comando.setCommand(_CMD_ADD_EXPERIENCE);
					comando.setData(js.toString());
					LocalStorageServices.DatabaseService().setCommand(comando);
					WebServiceServices.Sincronizador().iniciar();
					return id;
					
				} catch (Exception e) {
					e.printStackTrace();
					return id;
				}
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				_delegado.Do(result);
			}
		}
		AddExperienceAsync task = new AddExperienceAsync(callback);
		task.execute();
		
	}
	
	@Override
	public boolean addExperience(JSONObject datos) {
		
		try {
			JSONObject resp;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, _CMD_ADD_EXPERIENCE, _POST, nameValuePairs, null, null, null);
			if(!resp.isNull("id")){
				if(!resp.isNull("lesson_plan")){
					LocalStorageServices.DatabaseService().asignExperienceId(resp.getInt("id"), resp.getInt("lesson_plan"), datos.getInt("localId"));
				}
				
				
			}else
				throw new WebServiceException("Error al crear la actividad");
			return true;
        } catch (WebServiceException e) {
			e.printStackTrace();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}

	@Override
	public void deleteAsync(int id, String tipo, IAction<Boolean> callback) {
		
		class DeleteRecordAsync extends AsyncTask<Void, Void, Boolean>{

			private IAction<Boolean> _delegado;
			private int _id;
			private String _tipo;
			
			public DeleteRecordAsync(int id, String tipo, IAction<Boolean> delegado) {
				_id = id;
				_delegado = delegado;
				_tipo = tipo;
			}
			
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					JSONObject js = new JSONObject();
					js.put("id", _id + "");
					js.put("tipo", _tipo);
					
						if (_id > 0){
							CommandModel comando = new CommandModel();
							comando.setCommand(_CMD_DELETE_ELEMNT);
							comando.setData(js.toString());
							LocalStorageServices.DatabaseService().setCommand(comando);
							WebServiceServices.Sincronizador().iniciar();
						}
						if (_tipo.equals("activities")){
							LocalStorageServices.DatabaseService().deleteActivityId(_id);
						}
						return false;
						
				} catch (WebServiceException e){
					e.printStackTrace();
					return false;
				}catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				
				_delegado.Do(result);
			}
		}
		
		DeleteRecordAsync task = new DeleteRecordAsync(id, tipo, callback);
		task.execute();
		
		
		
	}

	@Override
	public boolean delete(JSONObject datos) {
		try {
			JSONObject resp;
			String comando = _CMD_DELETE_ELEMNT.replace("#", datos.getString("id")).replace("$", datos.getString("tipo"));
			resp = (JSONObject) ejecutarPeticion(_BASE_URL, comando, _DELETE, null, null, null, null);
			return true;
        } catch (WebServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void extraerComentarios(JSONArray jComentarios, int expId)
			throws JSONException, Exception {
		for (int i = 0; i < jComentarios.length(); i++) {
			JSONObject comment = jComentarios.getJSONObject(i);
			Comment comentario = new Comment();
			
			if(!comment.isNull("text"))
				comentario.setComment(comment.getString("text"));
			if(!comment.isNull("published"))
				comentario.setDate(Services.Utilidades().StringToDate(comment.getString("published")));
			
			if(!comment.isNull("user")){
				JSONObject user = comment.getJSONObject("user");
				String nombre = "";
				if (!user.isNull("name"))
					nombre = user.getString("name");
				if (!user.isNull("surname"))
					nombre = nombre + " " + user.getString("surname");
				comentario.setUser(nombre);
			}
			comentario.setModelId(expId);
			LocalStorageServices.DatabaseService().setComment(comentario);
			
		}
	}
	

		
}
