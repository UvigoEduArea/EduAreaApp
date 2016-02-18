package area.communication.implementation;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import area.LocalStorage.services.ApplicationService;
import area.LocalStorage.services.LocalStorageServices;
import area.communication.interfaces.ISincronizador;
import area.communication.services.WebServiceServices;
import area.experiencias.tfg.Experience;

public abstract class CompruebaSincronizacion implements ISincronizador {

	private static final long _sleepTime = 1000;
	
	private boolean _detener = false;
	private Context _context;
	private MenuItem _icono;
	private boolean ahora = true;
	
	
	public CompruebaSincronizacion() {
		
	}
	
	@Override
	public void iniciar() {
		_detener = false;
		new Thread(this).start();
		
	}

	@Override
	public void parar() {
		_detener = true;
	}
	
	public abstract void avisar(boolean desincronizado);
	
	public abstract void descargando(boolean descargando);
	
	public abstract void modoLocal(boolean modoLocal);
	
	public void updated(boolean isUpdated){};
	
	
	
	@Override
	public void run() {
		
		if(!_detener){
			try {
				avisar(LocalStorageServices.DatabaseService().getCommand() != null);
				descargando(ApplicationService.isDowloading());
				modoLocal(ApplicationService.redDisponible());
				if(!ApplicationService.isDowloading() && ApplicationService.redDisponible()){
					updated(WebServiceServices.Web().getUpdatedAt(LocalStorageServices.DatabaseService().getExperiencesIds(Experience.ALL)));
				}
				ahora = !ahora;
				Thread.sleep(_sleepTime);
				new Thread(CompruebaSincronizacion.this).start();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}


	
	
	
	

}
