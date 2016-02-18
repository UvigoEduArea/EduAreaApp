package area.communication.implementation;

import org.json.JSONObject;

import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.interfaces.ISincronizador;
import area.communication.services.WebServiceServices;
import area.domain.modelo.CommandModel;


/**
 * 
 * @author luis
 *
 */
public class SincronizadorService implements ISincronizador{

	private static final long START_SLEEP_TIME = 5000;
	private static final long MAX_SLEEP_TIME = 160000;
	
	private boolean _detener = false;
	private boolean _iniciado = false;
	private long _sleepTime = 0;
	
	/**
	 * Constructor
	 */
	public SincronizadorService(){
		
	}

	

	@Override
	public void iniciar() {
		_detener = false;
		if(_iniciado) return;
		new Thread(this).start();
		_iniciado = true;
		
	}

	@Override
	public void parar() {
		_detener = true;
		_iniciado = false;
	}
	
	
	
	
	@Override
	public void run() {
		if (_detener) return;
		
		
		if(!_detener){
			
			try {
				
				//TODO Lo que haya que hacer en la sincronización
				
				
				if(ApplicationService.redDisponible()){
					
					CommandModel comando = LocalStorageServices.DatabaseService().getCommand();
					
					if(comando == null){
						parar();
						return;
					}
								
					if(comando != null && comando.getCommand().equals(WebService._CMD_SET_COMMENT)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().setComment(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_PUT_EXPERIENCE)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().putExperience(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_ADD_RECORD)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().createRecord(js) != -1)
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_UPDATE_RECORD)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().updateRecord(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_DELETE_RECORD)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().deleteRecord(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_ADD_ACTIVITY)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().addActivity(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_PUT_ACTIVITY)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().putActivity(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_ADD_SUBMISSION)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().addEntrega(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_PUT_SUBMISSION)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().putEntrega(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_ADD_EXPERIENCE)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().addExperience(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
					if(comando != null && comando.getCommand().equals(WebService._CMD_DELETE_ELEMNT)){
						JSONObject js = new JSONObject(comando.getData());
						if(WebServiceServices.Web().delete(js))
							LocalStorageServices.DatabaseService().deleteCommand(comando);
					}
				
				}
				else{
					//_sleepTime = _sleepTime == 0 ? START_SLEEP_TIME : _sleepTime * 2;
					//_sleepTime = _sleepTime == 0 ? START_SLEEP_TIME : _sleepTime + 2000;
					_sleepTime = 4000;
					if(_sleepTime == MAX_SLEEP_TIME){
						parar();
						return;
					}
					Thread.sleep(_sleepTime);
				}
				new Thread(SincronizadorService.this).start();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				parar();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				parar();
			}
			
			
			
		}
		
	}



	
	
	
}
