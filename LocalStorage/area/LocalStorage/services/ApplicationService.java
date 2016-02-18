package area.LocalStorage.services;

import java.util.Stack;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import area.domain.modelo.PerfilModel;

public class ApplicationService extends Application {
	
	private static Context _context;
	private static Stack<Boolean> _descargando = new Stack<Boolean>();
	private static String _idioma = "/gl/";
	private static PerfilModel _perfil = null;
	
	/**
	 * Guarda el contexto de la aplicación
	 * @param contexto
	 */
	public static void setContext(Context contexto){
		_context = contexto;
	}
	
	/**
	 * Obtiene el contexto de la aplicación
	 * @return
	 */
	public static Context getContext(){
		return _context;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	/**
     * Comprueba si el dispositivo tiene disponible conexión a Internet.
     * @return		true si la tiene o false en caso contrario.
     */
    public static boolean redDisponible() {
    	if (_context == null) return false;
		ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
		return false;
    }
    
    /**
     * Guarda en una pila si está descargando algún documento.
     * @param descargando		True indica que se empieza a descargar, false que se ha terminado.
     */
    public static void setDownload(boolean descargando){
    	if (descargando)
    		_descargando.push(true);
    	else
    		_descargando.pop();
    	
    }
    
    public static boolean isDowloading(){
    	return !_descargando.isEmpty();
    }

	public static String get_idioma() {
		return _idioma;
	}

	public static void set_idioma(String idioma) {
		_idioma = idioma;
	}
	
	public static PerfilModel getPerfil(){
		if (_perfil == null)
			try {
				_perfil = LocalStorageServices.DatabaseService().getPerfil();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return _perfil;
	}
	public static void setPerfil(PerfilModel perfil){
		_perfil = perfil;
	}
    
	/**
	 * Obtiene un id local para una experiencia, actividad, o tarea.
	 * @return		Id local a utilizar.
	 */
	public static int getLocalId(){
		int localId = -2;
		SharedPreferences preferences = ApplicationService.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
		localId = preferences.getInt("localId", -2);
		localId--;
		preferences.edit().putInt("localId",localId).commit();
		return localId;
	}

}
