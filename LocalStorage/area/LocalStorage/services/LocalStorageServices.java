package area.LocalStorage.services;

import net.sqlcipher.database.SQLiteDatabase;
import area.LocalStorage.implementacion.DatabaseService;
import area.LocalStorage.implementacion.ExternalStorageService;
import area.LocalStorage.interfaces.IExternalStorageService;
/**
 * Clase donde se definen los servicios que se usarán en localStorage
 * @author luis
 *
 */
public class LocalStorageServices {

	
	private static DatabaseService _databaseService;
	private static IExternalStorageService _externalStorage;
	
	
	public static DatabaseService DatabaseService(){
		
		if(_databaseService == null){
			SQLiteDatabase.loadLibs(ApplicationService.getContext());
			_databaseService = new DatabaseService(ApplicationService.getContext(), DatabaseService.DATABASE_NAME,
													null, DatabaseService.DATABASE_VERSION);
		_databaseService.open();
		}
		return _databaseService;
	}
	
	public static IExternalStorageService ExternalStorage(){
		if(_externalStorage == null) _externalStorage = new ExternalStorageService();
		return _externalStorage;
	}
	
}
